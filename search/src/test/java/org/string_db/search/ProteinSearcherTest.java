package org.string_db.search;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class ProteinSearcherTest extends SearchTestIndex {
    /**
     * contains {query, expected results} tuples}
     */
    static LinkedHashMap<String, List<Long>> testSpec = new LinkedHashMap();

    /* test multivalued fields for correct positional increments? */
    @BeforeClass
    public static void setupProteinSearcher() throws IOException {
        //over ID
        testSpec.put("1", Arrays.asList(new Long[]{1l}));
        testSpec.put("2", Arrays.asList(new Long[]{2l}));
        //over external id
        testSpec.put("1234.AJ250839", Arrays.asList(new Long[]{1l}));
        testSpec.put("4932.YGL003C", Arrays.asList(new Long[]{2l}));

        //full match over name:
        testSpec.put("ST32B_HUMAN", Arrays.asList(new Long[]{1l}));
        testSpec.put("AT.I.24-9", Arrays.asList(new Long[]{2l}));
        testSpec.put("HLA-Cw*07", Arrays.asList(new Long[]{5l}));


        //partial match over name
        testSpec.put("HLA", Arrays.asList(new Long[]{5l, 6l}));

        //full match over other names
        testSpec.put("cdc", Arrays.asList(new Long[]{2l, 5l}));
        testSpec.put("STK32B", Arrays.asList(new Long[]{1l, 3l}));
        testSpec.put("AJ250839", Arrays.asList(new Long[]{1l}));
        testSpec.put("A8K9I1_HUMAN", Arrays.asList(new Long[]{1l}));
        testSpec.put("12919292", Collections.<Long>emptyList());
        //partial match over other names
        testSpec.put("A8K9I1", Arrays.asList(new Long[]{1l, 4l}));

        //annotation
        testSpec.put("ubiquitin", Arrays.asList(new Long[]{3l}));

    }


    @Test
    public void search_multiple_queries() throws IOException {
        List<SearchResult> r = searcher.search(Arrays.asList(new String[]{"apc", "HLA"}), 7227L);
        assertEquals(2, r.size());
        assertEquals("apc", r.get(0).getQuery());
        assertEquals("HLA", r.get(1).getQuery());
        assertSearchResult("apc", Arrays.asList(new Long[]{3l}), r.get(0));
        assertSearchResult("HLA", testSpec.get("HLA"), r.get(1));
    }


    @Test
    public void test_search() throws IOException {
        for (Map.Entry<String, List<Long>> entry : testSpec.entrySet()) {
            assertSearchResult(entry.getKey(), null, entry.getValue());
        }
    }

    @Test
    public void filter_by_species() {
        SearchResult results = searcher.search("cdc", 4932l);
        assertEquals(1, results.getTotalResults());
        assertNotNull(results.getHit(2l));
        results = searcher.search("cdc", 7227l);
        assertEquals(1, results.getTotalResults());
        assertNotNull(results.getHit(5l));
    }

    @Test
    public void testPaging() throws IOException {
        int numProteins = searcher.searcher.maxDoc() - 1;
        assertPaging("CONTROLWORD", 1, 10, numProteins);
        assertPaging("CONTROLWORD", 1, 2, 2);
        assertPaging("CONTROLWORD", 2, 2, 2);
        assertPaging("CONTROLWORD", 2, 5, numProteins - 5);
    }

    private void assertPaging(String query, int page, int pageSize, int expectedResults) {
        SearchResult h = searcher.search(query, page, pageSize);
        assertEquals(expectedResults, h.getResults().size());
    }

    private void assertSearchResult(String query, Long spcId, List<Long> expected) {
        assertSearchResult(query, expected, searcher.search(query, spcId));
    }

    private void assertSearchResult(String query, List<Long> expected, SearchResult result) {
        assertEquals(result.getTotalResults(), result.getResults().size());
        if (expected.size() != result.getTotalResults()) {
            fail(query + " query failed, total results: " + result.getTotalResults() + ", expected: " + expected.size());
        }
        for (Long eId : expected) {
            try {
                result.getHit(eId);
            } catch (Exception e) {
                fail(query + " failed, results: " + result.getResults() + ", expected: " + expected);
            }
        }
    }
    /* TODO */
    /* score results */
    /** highest score: full match over preferred name  **/
    /** second highest score: partial match over id/external id/preferred name, other names  **/
    /** third highest score: annotation match **/

    /* highlight results */
    /** name field **/
    /** annotation field**/


}