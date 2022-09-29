package org.string_db.index;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.string_db.analysis.IndexConfig;
import org.string_db.analysis.JunkTermsFilter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class ProteinIndexer {
    private static final Logger log = Logger.getLogger(ProteinIndexer.class);
    private final IndexConfig conf = IndexConfig.getConfig();
    private final IndexWriter writer;
    private final DataSource dataSource;

    /**
     * Opens the default index, defined in <code>IndexConfig</code>.
     *
     * @throws IOException
     */
    public ProteinIndexer() throws IOException {
        this(new JdbcDataSource());
    }

    public ProteinIndexer(DataSource ds) throws IOException {
        this(NIOFSDirectory.open(IndexConfig.getConfig().getIndexDirectory()), ds);
    }

    public ProteinIndexer(Directory index) {
        this(index, null);
    }

    public ProteinIndexer(Directory index, DataSource ds) {
        this.dataSource = ds;
        try {
            IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_36, conf.getAnalyzer());
            writerConfig.setSimilarity(IndexConfig.getConfig().DEFAULT_SIMILARITY);
            writerConfig.setRAMBufferSizeMB(64);
            writer = new IndexWriter(index, writerConfig);
            /* compound file format trades number of files for performance.
             the total number of files in our case is constant and pretty low,
             we can go for speed here.
            */
//          TODO  writer.setUseCompoundFile(false);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        log.info("initialized, index: " + index);
    }


    /**
     * * <p>
     * TODO HitCollector and FieldCache to maintain a mapping between protein ids and lucene doc ids
     * </p>
     * <p>
     * there's different (preferred) name classes:
     * <pre>
     * > db.proteins.find({"name" : /[^a-zA-Z]+/, "name": /[^0-9]+/, "name" : /[^a-zA-Z0-9\.\-]+/},{"name":1}).count()
     * 3536
     * > var c = db.proteins.find({"name" : /[^a-zA-Z]+/, "name": /[^0-9]+/, "name" : /[^a-zA-Z0-9\.\-]+/},{"name":1})
     * > c.next()
     * { "ADE5,7" , "ARG5,6" , "B'ALPHA", "CE7X_3.2" , "Cw*03", "DIM+10","DRB4*", "DUR1,2", "Dmel/GFAT2", "E(Pc)" ...
     * </pre>
     * </p>
     *
     * @param id
     * @param extId
     * @param preferredName
     * @param otherNames
     * @param annotation
     */
    public void indexProtein(Long id, Long speciesId, String extId, String preferredName, Set<String> otherNames, String annotation) {
        Document doc = new Document();
        try {
            doc.add(conf.makeField(IndexConfig.Fields.ID, String.valueOf(id)));
            doc.add(conf.makeField(IndexConfig.Fields.EXT_ID, extId));
            doc.add(conf.makeField(IndexConfig.Fields.NAME, preferredName));
            doc.add(conf.makeField(IndexConfig.Fields.PARTIAL_MATCH_NAME, preferredName));
            doc.add(conf.makeField(IndexConfig.Fields.SPECIES, speciesId.toString()));
            doc.add(conf.makeField(IndexConfig.Fields.ANNOTATION, annotation));

            StringBuffer sb = new StringBuffer();
            Iterator<String> iterator = otherNames.iterator();
            /**
             * boosts from multiple fields with the same name get multiplied and we don't want that.
             * This variable will allow name_synonyms to be boosted only once.
             * @see http://lucene.apache.org/java/3_0_3/api/core/org/apache/lucene/search/Similarity.html#formula_norm
             */
            boolean boostedOnce = false;
            while (iterator.hasNext()) {
                String otherName = iterator.next();
                if (JunkTermsFilter.isDecimalNumber(otherName)) {
                    continue;
                }
                sb.append(otherName);
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
                // todo name can contain coma(s) and LowerCaseAnalyzer will split on them
                Field anotherName = conf.makeField(IndexConfig.Fields.OTHER_NAMES, otherName);
                Field synField = conf.makeField(IndexConfig.Fields.PARTIAL_MATCH_OTHER_NAMES, otherName);

                if (boostedOnce) {
                    anotherName.setBoost(1.0f);
                    synField.setBoost(1.0f);
                }
                boostedOnce = true;
                doc.add(anotherName);
                doc.add(synField);
//                doc.add(conf.makeField(IndexConfig.Fields.OTHER_NAMES_HIGHLIGHT, String.valueOf(otherName)));
            }
            doc.add(conf.makeField(IndexConfig.Fields.OTHER_NAMES_HIGHLIGHT, sb.toString()));
//            doc.add(conf.makeField(IndexConfig.Fields.PARTIAL_MATCH_OTHER_NAMES, sb.toString()));
            writer.addDocument(doc);
        } catch (Exception e) {
            log.error("error indexing: " + doc.toString(), e);
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public void indexAll() {
        long start = System.currentTimeMillis();
        List<Long> speciesIds = dataSource.getSpeciesIds();
        log.info("total species: " + speciesIds.size());
        for (int i = 0; i < speciesIds.size(); i++) {
            log.info("indexing: " + speciesIds.get(i) + " [" + (i + 1) + ". out of " + speciesIds.size() + "]");
            indexSpecies(speciesIds.get(i));
        }
        log.info("indexing all species done in " + ((System.currentTimeMillis() - start) / 1000) + "sec");
        //assert .numDocs() equals total proteins
    }

    public void indexSpecies(Long speciesId) {
        long start = System.currentTimeMillis();
        try {
            Map<Long, Set<String>> proteinNames = dataSource.getProteinNames(speciesId);
            List<String> records = dataSource.getProteinsTsv(speciesId);
            for (String r : records) {
                String[] col = r.split("\t");
                if (col.length != 4) {
                    log.warn("some records missing: " + col);
                } else {
                    indexProtein(Long.valueOf(col[0]), speciesId, col[1], col[2], proteinNames.get(Long.valueOf(col[0])), col[3]);
                }
            }
            writer.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            log.info("done in [ms]: " + (System.currentTimeMillis() - start));
        }
    }


    public static void main(String[] args) throws IOException {
        File indexDirectory = IndexConfig.getConfig().getIndexDirectory();
        if (indexDirectory.exists()) {
            log.info("\nusing existing index at: " + indexDirectory);
            return;
        }
        log.info("\nmaking a new index at: " + indexDirectory);
        long start = System.currentTimeMillis();
        ProteinIndexer proteinIndexer = new ProteinIndexer(new JdbcDataSource());
        try {
            proteinIndexer.indexAll();
        } finally {
            try {
                proteinIndexer.close();
            } finally {
                log.info("exec time: " + (System.currentTimeMillis() - start));
            }
        }
    }

}
