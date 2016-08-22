package org.string_db.analysis;

import org.apache.lucene.analysis.*;
import org.apache.lucene.index.IndexWriter;
import org.string_db.analysis.IndexConfig.Fields;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Field specific analyzer.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public final class ProteinAnalyzer extends Analyzer {

    private final LimitTokenCountAnalyzer analyzer;

    public ProteinAnalyzer() {
        Map analyzerPerField = new HashMap();

        analyzerPerField.put(Fields.SPECIES.toString(), new KeywordAnalyzer());
        analyzerPerField.put(Fields.PARTIAL_MATCH_NAME.toString(), new SynonymsOnlyAnalyzer(true));
        analyzerPerField.put(Fields.PARTIAL_MATCH_OTHER_NAMES.toString(), new SynonymsOnlyAnalyzer(true));
        analyzerPerField.put(Fields.OTHER_NAMES_HIGHLIGHT.toString(), new SynonymsOnlyAnalyzer(false));
        analyzerPerField.put(Fields.ANNOTATION.toString(), new ProteinAnnotationAnalyzer(false));

        final PerFieldAnalyzerWrapper perFieldAnalyzerWrapper = new PerFieldAnalyzerWrapper(/* default analyzer */new LowerCaseAnalyzer(), analyzerPerField);
        analyzer = new LimitTokenCountAnalyzer(perFieldAnalyzerWrapper, IndexWriter.MaxFieldLength.LIMITED.getLimit());
    }

    @Override
    public final TokenStream tokenStream(String fieldName, Reader reader) {
        return analyzer.tokenStream(fieldName, reader);
    }
//    @Override
//    public int getPositionIncrementGap(String fieldName) {
//        /**
//         * for multivalued fields, we need to increase the gap
//         * enough for queries not to match across two instances
//         */
//        if ("id".equals(fieldName) || "name".equals(fieldName)) {
//            return 100;
//        }
//        return super.getPositionIncrementGap(fieldName);
//    }
}