package org.string_db.analysis;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.string_db.search.ProteinQueryParser;
import org.string_db.search.ProteinSearcher;
import org.string_db.search.SimilarityOne;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Keeps index configuration: fields definitions, analyzer(s), etc.
 * Singleton class.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class IndexConfig {
    private static final Logger log = Logger.getLogger(IndexConfig.class);
    public static final String PAXDB_PROPS = "/opt/paxdb/v4.1/paxdb.properties";

    public final Version LUCENE_VERSION = Version.LUCENE_36;

    public final Similarity DEFAULT_SIMILARITY = new SimilarityOne();
    private final ProteinQueryParser parser = new ProteinQueryParserImpl();
    private final Analyzer analyzer = new ProteinAnalyzer();
    private final File indexDir;
    private QueryParser highlightParser = new QueryParser(LUCENE_VERSION, Fields.OTHER_NAMES_HIGHLIGHT.toString(), new LowerCaseAnalyzer());
    private ProteinSearcher searcher;

    /**
     * @return singleton index configuration
     */
    public static IndexConfig getConfig() {
        return instance;
    }

    public synchronized ProteinSearcher getSearcher() {
        if (searcher == null) {
            try {
                searcher = new ProteinSearcher(NIOFSDirectory.open(getIndexDirectory()));
            } catch (IOException e) {
                log.error("cant open lucene index " + IndexConfig.getConfig().getIndexDirectory(), e);
                throw new RuntimeException(e);
            }
        }
        return searcher;
    }


    /**
     * @return default analyzer
     */
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public ProteinQueryParser getParser() {
        return parser;
    }

    public File getIndexDirectory() {
        return indexDir;
    }

    public QueryParser getHighlightParser() {
        return highlightParser;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Index field names.
     */
    public enum Fields {
        ID, SPECIES, NAME, OTHER_NAMES, ANNOTATION, PARTIAL_MATCH_NAME, PARTIAL_MATCH_OTHER_NAMES, OTHER_NAMES_HIGHLIGHT
    }

    /**
     * Factory method to create field instances.
     *
     * @param id    field name
     * @param value field value
     * @return created field
     */
    public Field makeField(Fields id, String value) {
        Field field = new Field(id.toString(), value, STORE_OPTS.get(id), INDEX_OPTS.get(id), TERMV_OPTS.get(id));
        field.setBoost(BOOSTS.get(id));
        return field;
    }

    private static IndexConfig instance = new IndexConfig();

    private final Map<Fields, Field.Store> STORE_OPTS;
    private final Map<Fields, Field.Index> INDEX_OPTS;
    private final Map<Fields, Field.TermVector> TERMV_OPTS;
    private final Map<Fields, Float> BOOSTS;

    private IndexConfig() {
        log.info("initializing");

        try {
            indexDir = readIndexDir();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        Map<Fields, Field.Store> store = new HashMap();
        store.put(Fields.ID, Field.Store.YES);
        store.put(Fields.SPECIES, Field.Store.YES);
        store.put(Fields.NAME, Field.Store.YES);
        store.put(Fields.PARTIAL_MATCH_NAME, Field.Store.NO);
        store.put(Fields.OTHER_NAMES, Field.Store.NO);
        store.put(Fields.OTHER_NAMES_HIGHLIGHT, Field.Store.YES);
        store.put(Fields.PARTIAL_MATCH_OTHER_NAMES, Field.Store.NO);
        store.put(Fields.ANNOTATION, Field.Store.YES);
        STORE_OPTS = Collections.unmodifiableMap(store);

        Map<Fields, Field.Index> index = new HashMap();
//        index.put(Fields.ID, Field.Index.ANALYZED_NO_NORMS);
        index.put(Fields.ID, Field.Index.ANALYZED);//need to use NORMS to be able to boost
        index.put(Fields.SPECIES, Field.Index.NOT_ANALYZED_NO_NORMS);
        index.put(Fields.NAME, Field.Index.ANALYZED);
        index.put(Fields.PARTIAL_MATCH_NAME, Field.Index.ANALYZED);
        index.put(Fields.OTHER_NAMES, Field.Index.ANALYZED);
        index.put(Fields.OTHER_NAMES_HIGHLIGHT, Field.Index.ANALYZED);
        index.put(Fields.ANNOTATION, Field.Index.ANALYZED);
        /**
         * used only for highlighting, we don't need norms
         */
        index.put(Fields.PARTIAL_MATCH_OTHER_NAMES, Field.Index.ANALYZED); //need to use NORMS to be able to boost
        INDEX_OPTS = Collections.unmodifiableMap(index);

        /**
         * the boost values are just guesses, they only say which field is more relevant.
         * relevance tests are required before they can be fine tuned. maybe using Lucene's quality package?
         */
        Map<Fields, Float> boosts = new HashMap();
        boosts.put(Fields.ID, 128f);
        boosts.put(Fields.SPECIES, 1f);
        boosts.put(Fields.NAME, 64f);
        boosts.put(Fields.OTHER_NAMES, 32f);
        boosts.put(Fields.PARTIAL_MATCH_NAME, 16f);
        boosts.put(Fields.PARTIAL_MATCH_OTHER_NAMES, 4f);
        boosts.put(Fields.OTHER_NAMES_HIGHLIGHT, 1f);
        boosts.put(Fields.ANNOTATION, 1f);
        BOOSTS = Collections.unmodifiableMap(boosts);


        Map<Fields, Field.TermVector> termv = new HashMap();
        termv.put(Fields.ID, Field.TermVector.NO);
        termv.put(Fields.SPECIES, Field.TermVector.NO);
        termv.put(Fields.NAME, Field.TermVector.NO);
        termv.put(Fields.PARTIAL_MATCH_NAME, Field.TermVector.NO);
        termv.put(Fields.OTHER_NAMES, Field.TermVector.NO);
        termv.put(Fields.OTHER_NAMES_HIGHLIGHT, Field.TermVector.WITH_POSITIONS_OFFSETS);
        termv.put(Fields.PARTIAL_MATCH_OTHER_NAMES, Field.TermVector.NO);
        termv.put(Fields.ANNOTATION, Field.TermVector.WITH_POSITIONS_OFFSETS);
        TERMV_OPTS = Collections.unmodifiableMap(termv);

//        analyzer = new PerFieldAnalyzerWrapper(new LowerCaseAnalyzer());
//        analyzer.addAnalyzer(Fields.SPECIES.toString(), new KeywordAnalyzer());
//        analyzer.addAnalyzer(Fields.PARTIAL_MATCH_NAME.toString(), new SynonymsOnlyAnalyzer(true));
//        analyzer.addAnalyzer(Fields.PARTIAL_MATCH_OTHER_NAMES.toString(), new SynonymsOnlyAnalyzer(true));
//        analyzer.addAnalyzer(Fields.OTHER_NAMES_HIGHLIGHT.toString(), new SynonymsOnlyAnalyzer(false));
//        analyzer.addAnalyzer(Fields.ANNOTATION.toString(), new ProteinAnnotationAnalyzer(false));

    }

    /**
     * Read index dir location from the property file
     *
     * @return index location
     */
    private File readIndexDir() throws IOException {
        Properties props = new Properties();
        final FileInputStream inStream = new FileInputStream(PAXDB_PROPS);
        props.load(inStream);
        inStream.close();
        String root = props.getProperty("stringdb_index");
        File d = new File(root);
        log.info("indexDir: " + d);
        return d;
    }

    private class ProteinQueryParserImpl implements ProteinQueryParser {
        QueryParser idParser = new QueryParser(LUCENE_VERSION, Fields.ID.toString(), new LowerCaseAnalyzer());
        QueryParser nameParser = new QueryParser(LUCENE_VERSION, Fields.NAME.toString(), new LowerCaseAnalyzer());
        QueryParser namePartialMatchParser = new QueryParser(LUCENE_VERSION, Fields.PARTIAL_MATCH_NAME.toString(), new LowerCaseAnalyzer());
        QueryParser otherNamesParser = new QueryParser(LUCENE_VERSION, Fields.OTHER_NAMES.toString(), new LowerCaseAnalyzer());
        //         QueryParser otherNamePartialMatchParser = new QueryParser(Version.LUCENE_30, Fields.PARTIAL_MATCH_OTHER_NAMES.toString(), new SynonymsOnlyAnalyzer(true));
        QueryParser otherNamePartialMatchParser = new QueryParser(LUCENE_VERSION, Fields.PARTIAL_MATCH_OTHER_NAMES.toString(), new LowerCaseAnalyzer());
        QueryParser annotationParser = new QueryParser(LUCENE_VERSION, Fields.ANNOTATION.toString(), new LowerCaseAnalyzer());

        private final Logger log = Logger.getLogger(ProteinQueryParserImpl.class);

        /**
         * @param query
         * @return
         */
        @Override
        public Query parse(String query) {
            if (query == null || "".equals(query.trim())) {
                //or throw an IllegalArgumentException
                return new TermQuery(new Term("UNEXISTING_FIELD", "UNEXISTING_VALUE"));
            }
            query = query.trim();
            //If query matches (/number/ or /external_id/) only then adds this clause.
            if (JunkTermsFilter.isDecimalNumber(query) || isExternalId(query)) {
                return parse(idParser, query, Fields.ID);
            }
            query = QueryParser.escape(query).toLowerCase();
            BooleanQuery q = new BooleanQuery();
            q.add(parse(nameParser, query, Fields.NAME), BooleanClause.Occur.SHOULD);
            q.add(parse(namePartialMatchParser, query, Fields.PARTIAL_MATCH_NAME), BooleanClause.Occur.SHOULD);
            q.add(parse(otherNamesParser, query, Fields.OTHER_NAMES), BooleanClause.Occur.SHOULD);
            q.add(parse(otherNamePartialMatchParser, query, Fields.PARTIAL_MATCH_OTHER_NAMES), BooleanClause.Occur.SHOULD);
            q.add(parse(annotationParser, query, Fields.ANNOTATION), BooleanClause.Occur.SHOULD);

            return q;
        }

        private Query parse(QueryParser parser, String query, Fields field) {
            Query parsed;
            try {
                parsed = parser.parse(query);
            } catch (ParseException e) {
                log.error("can't parse as name: " + query, e);
                parsed = new TermQuery(new Term(field.toString(), query));
            }
            return parsed;
        }

        private boolean isExternalId(String query) {
            //dev note: to quickly check if this regex matches all external ids use mongo db:
            //db.proteins.find({"externalId" : {"$not" : /^\d+\.[0-9a-zA-Z._-]+$/}}).count()
            return query.matches("^\\d+\\.[a-zA-Z0-9._-]+$");
        }

    }

}
