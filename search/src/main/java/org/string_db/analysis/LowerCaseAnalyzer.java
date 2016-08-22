package org.string_db.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public final class LowerCaseAnalyzer extends Analyzer {
    @Override
    public final TokenStream tokenStream(String fieldName, Reader reader) {
        return new LowerCaseFilter(IndexConfig.getConfig().LUCENE_VERSION, new WhitespaceComaTokenizer(reader));
    }
}

class WhitespaceComaTokenizer extends CharTokenizer {

    public WhitespaceComaTokenizer(Reader input) {
        super(IndexConfig.getConfig().LUCENE_VERSION, input);
    }

    public WhitespaceComaTokenizer(Version matchVersion, Reader input) {
        super(matchVersion, input);
    }

    public WhitespaceComaTokenizer(Version matchVersion, AttributeSource source, Reader input) {
        super(matchVersion, source, input);
    }

    public WhitespaceComaTokenizer(Version matchVersion, AttributeFactory factory, Reader input) {
        super(matchVersion, factory, input);
    }

    /**
     * split on whitespace and ignore comas.
     */
    @Override
    protected boolean isTokenChar(int c) {
        return !Character.isWhitespace(c) && ',' != c;
    }
}