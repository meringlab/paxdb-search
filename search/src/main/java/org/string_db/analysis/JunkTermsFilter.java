package org.string_db.analysis;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;

/**
 * Removes numbers and too short terms (<=2 chars by default) from a token stream.
 * Based on a stop filter.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class JunkTermsFilter extends TokenFilter {

    private CharTermAttribute termAtt;
    private PositionIncrementAttribute posIncrAtt;
    private static final int MIN_LENGTH = 2;

    /**
     * Construct a token stream filtering the given input.
     *
     * @param input Input TokenStream
     */
    public JunkTermsFilter(TokenStream input) {
        super(input);
        termAtt = addAttribute(CharTermAttribute.class);
        posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    }

    /**
     * Returns the next input Token whose term() is not a number or too short (<MIN_LENGTH)
     */
    @Override
    public final boolean incrementToken() throws IOException {
        // return the first non-stop word found
        int skippedPositions = 0;
        while (input.incrementToken()) {
            if (termAtt.length() > MIN_LENGTH &&
                    !isDecimalNumber(new String(termAtt.buffer(), 0, termAtt.length()))) {

                posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement() + skippedPositions);
                return true;
            }
            skippedPositions += posIncrAtt.getPositionIncrement();
        }
        // reached EOS -- return false
        return false;
    }

    public static boolean isDecimalNumber(String term) {
        if (term.matches("[ 0-9]+")) {
            return true;
        }
        return false;
    }
}
