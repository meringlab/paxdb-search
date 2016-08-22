package org.string_db.search;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.ScoreOrderFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.string_db.analysis.IndexConfig;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class HighlighterTest extends SearchTestIndex /* will also inherit all the tests..*/ {

    //        String q = "APC";
    String q = "AAH38238.1";
    //        String q = "A8K9I1_HUMAN";
//        String q = "AJ250839";
//    final String q = "A8K9I1";
    final String NAMES_HIGHLIGHT_FIELD = IndexConfig.Fields.OTHER_NAMES_HIGHLIGHT.toString();
    final String NAME_FIELD = IndexConfig.Fields.OTHER_NAMES.toString();
    //    final Query query = new TermQuery(new Term(ALL_NAMES_FIELD, q.toLowerCase()));
    final Query query = new TermQuery(new Term(NAME_FIELD, q.toLowerCase()));
    final Query highlight_query = new TermQuery(new Term(NAMES_HIGHLIGHT_FIELD, q.toLowerCase()));
    static IndexReader reader;

    @BeforeClass
    public static void setupHighlighterTest() throws IOException {
        reader = IndexReader.open(index);
    }

    @AfterClass
    public static void afterHighlighterTest() throws IOException {
        reader.close();
    }

    @Test
    public void name_contains_special_characters() {
        String hit = searcher.search("BEST:GH24664:1").getHit(6L).getHighlighted();
        assertTrue(hit.contains("BEST:GH24664:1"));
    }

    @Test
    public void annotation_field_only_stopwords() {
        String hit = searcher.search("7").getHit(7L).getHighlighted();
        assertTrue(hit, hit.contains("annotation_snippet"));
    }

    @Test
    public void highlight_partial_hit() {
        String hit = searcher.search("A8K9I1", 1234L).getHit(1L).getHighlighted();
        assertTrue(hit, hit.contains("also known as"));
        assertTrue(hit, hit.contains("<span class='search_hit'>A8K9I1_HUMAN</span>"));
    }

    @Test
    public void highlighting() {
        String hit = searcher.search("APC", 1234L).getHit(1L).getHighlighted();
        assertTrue(hit, hit.contains("also known as"));
        assertTrue(hit, hit.contains("<span class='search_hit'>APC</span>"));
        hit = searcher.search("AAH38238.1", 1234L).getHit(1L).getHighlighted();
        assertTrue(hit, hit.contains("also known as"));
        assertTrue(hit, hit.contains("<span class='search_hit'>AAH38238.1</span>"));
        hit = searcher.search("YANK2", 1234L).getHit(1L).getHighlighted();
        assertFalse(hit, hit.contains("also known as"));
        assertTrue(hit, hit.contains("<span class='search_hit'>YANK2</span>"));
        assertTrue(hit, hit.contains("<div class='annotation_snippet'>"));
        hit = searcher.search("YANK2 or APC", 1234L).getHit(1L).getHighlighted();
        assertTrue(hit, hit.contains("also known as"));
        assertTrue(hit, hit.contains("<span class='search_hit'>APC</span>"));
        assertTrue(hit, hit.contains("<span class='search_hit'>YANK2</span>"));
    }

    @Test
    public void demo_simple_highlighter_static_text() throws IOException, InvalidTokenOffsetsException {
        for (String q : new String[]{"APC", "AAH38238.1", "AJ250839"}) {
//            Query qu = new TermQuery(new Term(NAME_FIELD, q.toLowerCase()));
            Query hq = new TermQuery(new Term(NAMES_HIGHLIGHT_FIELD, q.toLowerCase()));
            QueryScorer scorer = new QueryScorer(hq, NAMES_HIGHLIGHT_FIELD);
            Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter(), new SimpleHTMLEncoder(), scorer);
            highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, 50));

            TokenStream stream = IndexConfig.getConfig().getAnalyzer().tokenStream(NAMES_HIGHLIGHT_FIELD, new StringReader(names));
            String bestFragment = highlighter.getBestFragment(stream, names);
            assertNotNull("no fragment highlighted", bestFragment);
            assertTrue(bestFragment, bestFragment.contains("<B>" + q));
            System.out.println("highlighter on static text: " + bestFragment);
        }

    }

    @Test
    public void demo_fastVector_highlighter_static_text() throws IOException, InvalidTokenOffsetsException {
        FastVectorHighlighter highlighter = new FastVectorHighlighter(true, true, new SimpleFragListBuilder(),
                new ScoreOrderFragmentsBuilder(new String[]{"<B>"}, new String[]{"</B>"}));
        int docId = search().scoreDocs[0].doc;
        String bestFragment = highlighter.getBestFragment(highlighter.getFieldQuery(highlight_query), reader, docId, NAMES_HIGHLIGHT_FIELD, 40);
        assertNotNull(bestFragment);
        assertTrue(bestFragment, bestFragment.contains("<B>" + q));
        System.out.println("fastVectorHighlighter: " + bestFragment);
    }


    @Test
    public void demo_simple_highlight_search_result() throws IOException, InvalidTokenOffsetsException {
        QueryScorer scorer = new QueryScorer(highlight_query, NAMES_HIGHLIGHT_FIELD);
        Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter(), new SimpleHTMLEncoder(), scorer);
        highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, 40));

        int docId = search().scoreDocs[0].doc;
        Document document = reader.document(docId);
        String originalText = document.get(NAMES_HIGHLIGHT_FIELD);
        TokenStream stream = TokenSources.getAnyTokenStream(reader, docId, NAMES_HIGHLIGHT_FIELD, null);
        String bestFragment = highlighter.getBestFragment(stream, originalText);
        assertNotNull(bestFragment);
        assertTrue(q + " not highlighted: " + bestFragment, bestFragment.contains("<B>" + q));
        System.out.println("highlighter on search results: " + bestFragment);
    }

    private TopDocs search() throws IOException {
        return searcher.searcher.search(query, 10);
    }
}
