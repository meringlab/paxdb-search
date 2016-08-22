package org.string_db.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public final class SynonymsOnlyAnalyzer extends Analyzer {

    private boolean excludeOriginalTerms;

    private final Set<String> stopSet = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "-", "protein", "uncharacterized", "putative",
            "of", "the", "in", "and", "a", "an", "to", "by", "for", "may", "similarity", "involved",
            "is", "be", "like", "with", "as", "that", "family", "precursor", "member", "isoform", "role", "cell")));
    ;

    public SynonymsOnlyAnalyzer(boolean excludeOriginalTerms) {
        this.excludeOriginalTerms = excludeOriginalTerms;
    }

    @Override
    public final TokenStream tokenStream(String fieldName, Reader reader) {
        return new JunkTermsFilter(
                new RemoveTrailingDotFilter(
                        new StopFilter(IndexConfig.getConfig().LUCENE_VERSION,
                                new SynonymFilter(
                                        new LowerCaseFilter(IndexConfig.getConfig().LUCENE_VERSION,
                                                new WhitespaceComaTokenizer(reader)), new SynonymOnlyEngine(), excludeOriginalTerms), stopSet)));
    }
}
