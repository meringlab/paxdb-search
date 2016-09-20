package org.string_db.search;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.string_db.analysis.IndexConfig;
import org.string_db.analysis.IndexConfig.Fields;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class ProteinSearcher implements Searcher {
    /**
     * maximum number of queries
     */
    public static final int MAX_QUERIES = 100;
    IndexSearcher searcher;
    public final Integer DEFAULT_PAGE = 1;
    public final Integer DEFAULT_PAGE_SIZE = 10;
    final ProteinQueryParser parser = IndexConfig.getConfig().getParser();
    final QueryParser highlightParser = IndexConfig.getConfig().getHighlightParser();

    private static final Logger log = Logger.getLogger(ProteinSearcher.class);

    public ProteinSearcher(Directory index) {
        try {
            IndexReader reader = IndexReader.open(index);
//            searcher = new IndexSearcher(index, true);
            searcher = new IndexSearcher(reader);
            searcher.setSimilarity(IndexConfig.getConfig().DEFAULT_SIMILARITY);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public SearchResult search(String query) {
        return search(query, null);
    }

    @Override
    public SearchResult search(String query, Long spcId) {
        return search(query, spcId, DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
    }

    @Override
    public SearchResult search(String query, Integer page, Integer pageSize) {
        return search(query, null, page, pageSize);
    }

    public List<SearchResult> search(List<String> queries, Long spcId) {
        return search(queries, spcId, DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
    }

    public List<SearchResult> search(List<String> queries, Integer page, Integer pageSize) {
        return search(queries, null, page, pageSize);
    }

    @Override
    public List<SearchResult> search(List<String> queries, Long spcId, Integer page, Integer pageSize) {
        if (queries.size() > MAX_QUERIES) {
            throw new IllegalArgumentException("can't submit more than " +
                    MAX_QUERIES + " queries at the time");
        }
        List<SearchResult> results = new ArrayList<SearchResult>(queries.size());
        for (String query : queries) {
            results.add(search(query, spcId, page, pageSize));
        }
        return results;
    }


    @Override
    public SearchResult search(String query, Long spcId, Integer page, Integer pageSize) {
        if (page < 1 || pageSize < 1) {
            throw new IllegalArgumentException(page + "-" + pageSize);
        }
        final int start = getStartIndex(page, pageSize);
        final int end = getEndIndex(page, pageSize);
        if (log.isDebugEnabled()) {
            log.debug("start-end: " + start + "-" + end);
        }
        try {
            Query q = parser.parse(query);
            /* filters can alternatively be implemented as a required clause of a BooleanQuery.
                There are some differences: IDF can be quite different (with a BooleanQuery all documents
                containing the terms contribute to the score, whereas a filter will exclude some documents);
                BooleanQuery may work better with a QueryParser.
                */
            Filter speciesFilter = spcId == null ? null :
                    new QueryWrapperFilter(new TermQuery(new Term(Fields.SPECIES.toString(), spcId.toString())));
            log.debug(q + ", filter: " + speciesFilter);
            TopDocs topDocs = searcher.search(q, speciesFilter, end);
            List<ProteinHit> results = new ArrayList<ProteinHit>();
            ScoreDoc[] hits = topDocs.scoreDocs;

            if (null != hits && hits.length > 0 && start < hits.length) {
                for (int i = start; i < hits.length && i < end; i++) {
                    int docId = hits[i].doc;
                    //for debugging:
//                Explanation explanation = searcher.explain(q, docId); System.out.println(explanation);
                    Document document = searcher.getIndexReader().document(docId);
                    String highlightedSnippet = getHighlightedSnippet(query, q, docId, document);

                    results.add(new ProteinHit(Long.valueOf(document.get(Fields.ID.toString())),
                            document.get(Fields.NAME.toString()),
                            document.get(Fields.ANNOTATION.toString()),
                            hits[i].score,
                            highlightedSnippet,
                            document.get(Fields.SPECIES.toString())));
                }
//                    if (log.isDebugEnabled()) {
//                        log.debug("'" + protein + "' hits: " + candidates);
//                    }

            } else {
                if (log.isDebugEnabled()) {
                    log.debug(q + " - no results");
                }

            }
            return new SearchResult(query, results, topDocs.totalHits, page, pageSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getHighlightedSnippet(String query, Query parsed, int docId, Document document) throws IOException {
        String highlightedSnippet = null;
        try {
            QueryScorer scorer = new QueryScorer(highlightParser.parse(QueryParser.escape(query)), Fields.OTHER_NAMES_HIGHLIGHT.toString());
            Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter("<span class='search_hit'>", "</span>"), new SimpleHTMLEncoder(), scorer);
            highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, 50));

            String originalText = document.get(Fields.OTHER_NAMES_HIGHLIGHT.toString());
            TokenStream stream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), docId, Fields.OTHER_NAMES_HIGHLIGHT.toString(), null, null);
            highlightedSnippet = highlighter.getBestFragment(stream, originalText);
            if (highlightedSnippet != null) {
                highlightedSnippet = "<div class='name_snippet'>also known as:" + highlightedSnippet + "</div>";
            }

            scorer = new QueryScorer(parsed, Fields.ANNOTATION.toString());
            highlighter = new Highlighter(new SimpleHTMLFormatter("<span class='search_hit'>", "</span>"), new SimpleHTMLEncoder(), scorer);
            highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, 50));

            highlightedSnippet = highlightedSnippet != null ? highlightedSnippet : "";
            //annotation can be composed of only stop words, for example "Putative uncharacterized protein"
            //and in that case the TFVector is null.
            originalText = document.get(Fields.ANNOTATION.toString());
            String annSnippet = null;
            TermFreqVector tfv = searcher.getIndexReader().getTermFreqVector(docId, Fields.ANNOTATION.toString());
            if (tfv != null) {
                if (tfv instanceof TermPositionVector) {
                    stream = TokenSources.getTokenStream((TermPositionVector) tfv, false);
                    annSnippet = highlighter.getBestFragment(stream, originalText);
                }
            }
            if (annSnippet == null) {
                annSnippet = (originalText.length() > WordBoundaryPreservingFragmenter.DEFAULT_SNIPPET_LENGTH) ?
                        originalText.substring(0, WordBoundaryPreservingFragmenter.DEFAULT_SNIPPET_LENGTH) + "..." : originalText;
            }

            highlightedSnippet += "<div class='annotation_snippet'>" + annSnippet + "</div>";

        } catch (ParseException e) {
            log.error("cant parse highlight query: " + query, e);
        } catch (InvalidTokenOffsetsException e) {
            log.error("origText and indexed differ: " + query, e);
        } catch (Exception e) {
            log.error("cant highlight query: '" + query + "', parsed: '" + parsed + "', doc: " + document, e);
        }
        return highlightedSnippet;
    }

    public void close() {
        try {
            searcher.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private int getEndIndex(int page, int pageSize) {
        return getStartIndex(page, pageSize) + pageSize;
    }

    private int getStartIndex(int page, int pageSize) {
        return (page - 1) * pageSize;
    }

}
