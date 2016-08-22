package org.string_db.search;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class SearchRelevanceTest extends SearchTestIndex {


    @Test
    public void all_other_vs_annotation() throws IOException {
        //TODO
    }


    @Test
    public void preferred_name_match_over_other_names() throws IOException {
        checkScoring("hla", 6L, 5L, "match over preferred_name must have bigger score: ");
    }

    @Test
    public void full_name_hit_vs_partial_name_hit() throws IOException {
        checkScoring("A8K9I1", 4L, 1L, "full match must have bigger score");
    }

    private void checkScoring(String query, long id_moreRelevant, long id_lessRelevant, String message) {
        SearchResult results = searcher.search(query);
        final Float score = results.getHit(id_moreRelevant).getScore();
        final Float lessRelevantScore = results.getHit(id_lessRelevant).getScore();
        assertTrue(message + score + " > " + lessRelevantScore, score > lessRelevantScore);
    }

    @Test
    public void field_length_is_irrelevant() throws IOException {
        SearchResult results = searcher.search("cDc");
        //DefaultSimilarity gives higher scores to shorter fileds:
        assertTrue("field length should not affect scores", results.getHit(2l).getScore() >= results.getHit(5l).getScore());
    }
}
