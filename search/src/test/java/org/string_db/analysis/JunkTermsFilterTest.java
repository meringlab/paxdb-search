package org.string_db.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class JunkTermsFilterTest {

    @Test
    public void test_filter() throws Exception {
        final JunkTermsFilter filter = new JunkTermsFilter(new TestTokenStream("protein", "217", "aa", "non_junk"));
        assertTrue(filter.incrementToken());
        final CharTermAttribute termAtt = filter.getAttribute(CharTermAttribute.class);
        assertEquals("protein", new String(termAtt.buffer(), 0, termAtt.length()));
        assertTrue(filter.incrementToken());
        assertEquals("non_junk", new String(termAtt.buffer(), 0, termAtt.length()));
        assertFalse(filter.incrementToken());
    }


    @Test
    public void isDecimalNumber() throws Exception {
        assertTrue(JunkTermsFilter.isDecimalNumber("1"));
        assertTrue(JunkTermsFilter.isDecimalNumber("123"));
        assertTrue(JunkTermsFilter.isDecimalNumber(" 123 "));
        assertFalse(JunkTermsFilter.isDecimalNumber("123a"));
        assertFalse(JunkTermsFilter.isDecimalNumber("abc "));
    }
}

class TestTokenStream extends TokenStream {

    protected int index = 0;
    protected String[] testToken;
    protected final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    public TestTokenStream(String... testToken) {
        super();
        this.testToken = testToken;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        if (index < testToken.length) {
            termAtt.setEmpty().append(testToken[index++]);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void reset() {
        index = 0;
    }
}
