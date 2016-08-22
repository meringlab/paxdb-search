package org.string_db.search;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.string_db.index.DataSource;
import org.string_db.index.ProteinIndexer;

import java.io.IOException;
import java.util.*;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class SearchTestIndex {
    public static final String names = "STK32B, AJ250839, 12919292, A8K9I1_HUMAN, APC, AAH38238.1," +
            " Serine/threonine-protein kinase 32B, GI_8923753-S";
    static Directory index;
    static ProteinIndexer indexer;
    static ProteinSearcher searcher;

    public static Set<String> makeSet(String... strs) {
        Set<String> res = new LinkedHashSet<String>(strs.length);
        for (String str : strs) {
            res.add(str);
        }
        return res;
    }

    /**
     * ?? is it possible to get fields that have been hit by query?
     */

    @AfterClass
    public static void tearDownBaseSearchTest() throws IOException {
        searcher.close();
    }

    @BeforeClass
    public static void setupBaseSearchTest() throws IOException {
        index = new RAMDirectory();
        indexer = new ProteinIndexer(index, new DataSource() {
            final List<Long> species = Collections.unmodifiableList(Arrays.asList(new Long[]{1234L, 4932L, 7227L, 4321L,}));
            final Map<Long, Map<Long, Set<String>>> proteinNames = new HashMap();
            final Map<Long, List<String>> proteinTSVRecords = new HashMap();

            {
                Map<Long, Set<String>> n = new HashMap();
                n.put(1L, makeSet(names.split(",")));
                proteinNames.put(1234L, n);
                n = new HashMap();
                n.put(2L, makeSet("cdc"));
                proteinNames.put(4932L, n);
                n = new HashMap();
                n.put(3L, makeSet("STK32B"));
                n.put(5L, makeSet("CDC"));
                n.put(6L, makeSet("BRP", "BEST:GH24664:1"));
                proteinNames.put(7227L, n);
                n = new HashMap();
                n.put(4L, makeSet("A8K9I1"));
                n.put(7L, makeSet("UNKNOWNPROTEIN"));
                proteinNames.put(4321L, n);
                proteinTSVRecords.put(1234L, Collections.unmodifiableList(Arrays.asList(new String[]{
                        "1\t1234.AJ250839\tST32B_HUMAN\tSerine/threonine-protein kinase 32B (EC 2.7.11.1) (YANK2) CONTROLWORD"
                })));

                proteinTSVRecords.put(4932L, Collections.unmodifiableList(Arrays.asList(new String[]{
                        "2\t4932.YGL003C\tAT.I.24-9\tadditional degradation signal termed the KEN box including ASE1, CDC20, the B-type cyclins CLB2 and CLB3, the polo-like kinase CDC5 and HSL1 CONTROLWORD"
                })));

                proteinTSVRecords.put(7227L, Collections.unmodifiableList(Arrays.asList(new String[]{
                        "3\t7227.STK32B\tAPC\tAPC/C activator protein CDH1; regulates the ubiquitin ligase activity of the anaphase promoting complex/cyclosome (APC/C). CONTROLWORD",
                        "5\t7227.CG5363-PA\tHLA-Cw*07\tCell division control protein 2 homolog (EC 2.7.11.22) (p34 protein kinase) CONTROLWORD",
                        "6\t7227.CG5432-PA\tHLA\tmore random crap CONTROLWORD"
                })));
                proteinTSVRecords.put(4321L, Collections.unmodifiableList(Arrays.asList(new String[]{
                        "4\t4321.HJQPA\tsu(w[a])\trandom crap CONTROLWORD",
                        "7\t4321.QWERTY\tQWERTZ\tPutative uncharacterized protein"
                })));
            }

            @Override
            public List<Long> getSpeciesIds() {
                return species;
            }

            @Override
            public Map<Long, Set<String>> getProteinNames(long speciesId) {
                return proteinNames.get(speciesId);
            }

            @Override
            public List<String> getProteinsTsv(Long speciesId) {
                return proteinTSVRecords.get(speciesId);
            }
        });

//        indexer.indexProtein(1l, 1234L, "1234.AJ250839", "ST32B_HUMAN", makeSet(names.split(",")),
//                "Serine/threonine-protein kinase 32B (EC 2.7.11.1) (YANK2) CONTROLWORD");
//        indexer.indexProtein(2l, 4932L, "4932.YGL003C", "AT.I.24-9", makeSet("cdc"),
//                "additional degradation signal termed the KEN box including ASE1, CDC20, the B-type cyclins CLB2 and CLB3, the polo-like kinase CDC5 and HSL1 CONTROLWORD");
//        indexer.indexProtein(3l, 7227L, "7227.STK32B", "APC", makeSet("STK32B"),
//                "APC/C activator protein CDH1; regulates the ubiquitin ligase activity of the anaphase promoting complex/cyclosome (APC/C). CONTROLWORD");
//        indexer.indexProtein(4l, 4321L, "4321.HJQPA", "su(w[a])", makeSet("A8K9I1"),
//                "random crap CONTROLWORD");
//        indexer.indexProtein(5l, 7227L, "7227.CG5363-PA", "HLA-Cw*07", makeSet("CDC"),
//                "Cell division control protein 2 homolog (EC 2.7.11.22) (p34 protein kinase) CONTROLWORD");
//        indexer.indexProtein(6l, 7227L, "7227.CG5432-PA", "HLA", makeSet("BRP"),
//                "more random crap CONTROLWORD");
        indexer.indexAll();
        indexer.close();
        searcher = new ProteinSearcher(index);
    }
}
