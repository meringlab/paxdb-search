package org.string_db.search;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.string_db.analysis.ProteinNamesAnalyzerTest;
import org.string_db.analysis.SynonymEngineTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ProteinSearcherTest.class,
        HighlighterTest.class,
        ProteinNamesAnalyzerTest.class,
        ProteinQueryParserTest.class,
        SearchRelevanceTest.class,
        SynonymEngineTest.class
})
public class ProteinSearchSuite {
}