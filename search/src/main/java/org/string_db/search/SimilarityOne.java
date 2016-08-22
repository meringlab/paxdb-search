package org.string_db.search;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.DefaultSimilarity;

/**
 * Default similarity with the constant length norm.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class SimilarityOne extends DefaultSimilarity {
    /**
     * field length is not relevant at all in our case (i.e. the fact that
     * one protein has 10 names and another 100, or that it has one line annotation
     * and the other 3 lines, doesn't make the first protein more relevant).
     *
     * @return just the field boost
     */
    @Override
    public float computeNorm(String field, FieldInvertState state) {
        return state.getBoost();
    }
}
