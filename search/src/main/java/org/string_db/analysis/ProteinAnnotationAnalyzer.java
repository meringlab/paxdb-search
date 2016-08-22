package org.string_db.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ProteinAnnotationAnalyzer extends Analyzer {

    private boolean excludeOriginalTerms = false;

    private Set<String> stopSet = new HashSet<String>(Arrays.asList("-", "protein", "uncharacterized", "putative",
            "of", "the", "in", "and", "a", "an", "to", "by", "for", "may", "similarity", "involved",
            "is", "be", "like", "with", "as", "that", "family", "precursor", "member", "isoform", "role", "cell"));
    ;

    public ProteinAnnotationAnalyzer(Set<String> stopWords, boolean excludeOriginalTerms) {
        this.excludeOriginalTerms = excludeOriginalTerms;
        stopSet = new HashSet<String>(stopWords);
    }

    public ProteinAnnotationAnalyzer(boolean excludeOriginalTerms) {
        this.excludeOriginalTerms = excludeOriginalTerms;
    }

    public ProteinAnnotationAnalyzer() {

    }

    @Override
    public final TokenStream tokenStream(String fieldName, Reader reader) {
        return new JunkTermsFilter(new SynonymFilter(
                new StopFilter(IndexConfig.getConfig().LUCENE_VERSION, new RemoveTrailingDotFilter(new ProteinAnnotationTokenizer(reader)), stopSet),
                new ProteinAnnotationSynonymEngine(), excludeOriginalTerms));
    }

//    @Override
//    public int getPositionIncrementGap(String fieldName) {
//        /**
//         * since id & name are multivalued fields, we need to increase the gap
//         * enough for queries not to match across two instances
//         */
//        if ("id".equals(fieldName) || "name".equals(fieldName)) {
//            return 100;
//        }
//        return super.getPositionIncrementGap(fieldName);
//    }
}

