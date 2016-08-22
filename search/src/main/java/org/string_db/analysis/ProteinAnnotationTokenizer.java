package org.string_db.analysis;

import org.apache.lucene.analysis.CharTokenizer;

import java.io.Reader;

class ProteinAnnotationTokenizer extends CharTokenizer {

    public ProteinAnnotationTokenizer(Reader reader) {
        super(IndexConfig.getConfig().LUCENE_VERSION, reader);
    }

    @Override
    protected boolean isTokenChar(int c) {
        return Character.isLetterOrDigit(c) || c == '-' || c == '.';
    }

    @Override
    protected int normalize(int c) {
        return Character.toLowerCase(c);
    }
}
