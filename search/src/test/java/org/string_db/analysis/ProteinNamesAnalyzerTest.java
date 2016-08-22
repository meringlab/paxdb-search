package org.string_db.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.junit.Test;
import org.string_db.search.SearchTestIndex;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.string_db.search.ProteinSearcherTest.makeSet;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class ProteinNamesAnalyzerTest {

    @Test
    public void main_analyzer() throws IOException {
        Analyzer analyzer = IndexConfig.getConfig().getAnalyzer();
        assertTokens(analyzer, "ID field should only be lowercased", "123.Aj042FG", makeSet("123.aj042fg"), "ID");
        assertTokens(analyzer, "PARTIAL MATCH field should omit the original term", "Aj042FG", Collections.<String>emptySet(), IndexConfig.Fields.PARTIAL_MATCH_NAME.toString());
        assertTokens(analyzer, "PARTIAL MATCH field should omit the original term", "Aj042FG.1", SearchTestIndex.makeSet("aj042fg1", "aj042fg"), IndexConfig.Fields.PARTIAL_MATCH_NAME.toString());
    }


    @Test
    public void synonyms_analyzer() throws IOException {
        Analyzer cut = new SynonymsOnlyAnalyzer(false);
        assertTokens(cut, "should include the actual term", "smo_human", makeSet("smo_human", "smo", "human", "smohuman"), "name_synonyms");
        assertTokens(cut, "", "cdc-6", makeSet("cdc", "cdc6", "cdc-6"), "name_synonyms");
        assertTokens(cut, "add both parts of a name separated by split characters",
                "Hox-4Ik", makeSet("hox", "4ik", "hox4ik", "hox-4ik"), "name_synonyms");
        assertTokens(cut, "add both parts of a name separated by split characters",
                "Hox.4Ik", makeSet("hox", "4ik", "hox4ik", "hox.4ik"), "name_synonyms");

        assertTokens(cut, "strip versioning numbers", "word.12", makeSet("word", "word12", "word.12"), "name_synonyms");
        assertTokens(cut, "strip versioning numbers", "word.12b.1", makeSet("word", "12b", "word12b1", "word.12b.1"), "name_synonyms");


        assertTokens(cut, "cvs list of names", "AJ250839, 12919292, A8K9I1_HUMAN", makeSet("a8k9i1_human", "a8k9i1", "a8k9i1human", "human", "aj250839"), "name_synonyms");
    }


    @Test
    public void synonyms_only_analyzer() throws IOException {
        Analyzer cut = new SynonymsOnlyAnalyzer(true);
        assertTokens(cut, "must not emit tokens for 'normal' words", "trpA", Collections.<String>emptySet(), "name_synonyms");
        assertTokens(cut, "must not emit tokens for 'normal' words", "1342", Collections.<String>emptySet(), "name_synonyms");
        assertTokens(cut, "must not emit tokens for 'normal' words", "AC009336", Collections.<String>emptySet(), "name_synonyms");
        assertTokens(cut, "must not emit tokens for 'normal' words", "AC009336", Collections.<String>emptySet(), "name_synonyms");


        assertTokens(cut, "should only emit synonyms, not the actual term", "smo_human", makeSet("smo", "human", "smohuman"), "name_synonyms");
        assertTokens(cut, "", "cdc-6", makeSet("cdc", "cdc6"), "name_synonyms");
        assertTokens(cut, "add both parts of a name separated by split characters",
                "Hox-4Ik", makeSet("hox", "4ik", "hox4ik"), "name_synonyms");
        assertTokens(cut, "add both parts of a name separated by split characters",
                "Hox.4Ik", makeSet("hox", "4ik", "hox4ik"), "name_synonyms");

        assertTokens(cut, "strip versioning numbers", "word.12", makeSet("word", "word12"), "name_synonyms");
        assertTokens(cut, "strip versioning numbers", "word.12b.1", makeSet("word", "12b", "word12b1"), "name_synonyms");

//            "A-116A10.1" "MU-RMS-40.16A", "scl18933.27.1_48-S"

    }

    private void assertTokens(Analyzer cut, String msg, String input, Set<String> expected, String field) throws IOException {
        TokenStream ts = cut.tokenStream(field, new StringReader(input));
        CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute posIncAtt = ts.addAttribute(PositionIncrementAttribute.class);

        Set<String> tokens = new HashSet<String>();
        int position = 0;
        assertTrue("position must monoton. increase: " + input + ", syn: " + term.buffer() + ", pos: " + position + ", was: " +
                posIncAtt.getPositionIncrement(), position <= posIncAtt.getPositionIncrement());
        while (ts.incrementToken()) {
            tokens.add(new String(term.buffer(), 0, term.length()));
            position = posIncAtt.getPositionIncrement();
        }

        assertEquals(msg, expected, tokens);
    }

    @Test
    public void test_position_increments() throws IOException {
        Map<String, Integer> increments = new HashMap();
        increments.put("aj250839", 1);
        increments.put("a8k9i1_human", 1);
        increments.put("aah38238.1", 1);
        increments.put("human", 0);
        increments.put("a8k9i1", 0);
        increments.put("a8k9i1human", 0);
        increments.put("aah38238", 0);
        increments.put("aah382381", 0);

        Analyzer cut = new SynonymsOnlyAnalyzer(false);
        TokenStream ts = cut.tokenStream("f", new StringReader("AJ250839, A8K9I1_HUMAN, AAH38238.1"));


        TermAttribute term = ts.addAttribute(TermAttribute.class);
        PositionIncrementAttribute posIncAtt = ts.addAttribute(PositionIncrementAttribute.class);
        OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
        boolean runOnce = false;
        while (ts.incrementToken()) {
            assertEquals("position must monoton. increase: " + term.term(), increments.get(term.term()).intValue(),
                    posIncAtt.getPositionIncrement());
            runOnce = true;
        }
        assertTrue("no terms emitted", runOnce);
    }

    @Test
    public void test_position_increments_synonymOnly() throws IOException {
        Analyzer cut = new SynonymsOnlyAnalyzer(true);

        TokenStream ts = cut.tokenStream("f", new StringReader("AJ250839, A8K9I1_HUMAN, AAH38238.1"));

        CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute posIncAtt = ts.addAttribute(PositionIncrementAttribute.class);

        assertTrue("no terms emitted!", ts.incrementToken());
        int inc = posIncAtt.getPositionIncrement();
        assertEquals(term.buffer() + " posInc bad: ", 1, inc);

        assertTrue(ts.incrementToken());
        inc = posIncAtt.getPositionIncrement();
        assertEquals(term.buffer() + " posInc bad: ", 0, inc);

        assertTrue(ts.incrementToken());
        inc = posIncAtt.getPositionIncrement();
        assertEquals(term.buffer() + " posInc bad: ", 0, inc);

        assertTrue(ts.incrementToken());
        inc = posIncAtt.getPositionIncrement();
        assertEquals(term.buffer() + " posInc bad: ", 1, inc);

        assertTrue(ts.incrementToken());
        inc = posIncAtt.getPositionIncrement();
        assertEquals(term.buffer() + " posInc bad: ", 0, inc);

    }

}
