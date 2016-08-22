package org.string_db.analysis;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 * This filter is just a hack to remove trailing dots ('.') left behind by OurTokenizer
 * we could also use it to normalize other protein ID types...
 *
 * @author Manuel Weiss
 */
final class RemoveTrailingDotFilter extends TokenFilter {

    private CharTermAttribute termAttr;

    public RemoveTrailingDotFilter(TokenStream in) {
        super(in);
        termAttr = (CharTermAttribute) addAttribute(CharTermAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken() || termAttr.length() == 0)
            return false;
        // remove trailing '.'
        if (termAttr.buffer()[termAttr.length() - 1] == '.')
            termAttr.setLength(termAttr.length() - 1);
        return true;
    }

}
