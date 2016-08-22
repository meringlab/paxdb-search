package org.string_db.search;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class SearchResult {
    protected int page;
    protected int pageSize;
    private String query;
    private int totalResults;
    /**
     * id/score map
     */
    private List<ProteinHit> results;

    public SearchResult() {
    }

    public SearchResult(String query, List<ProteinHit> results, int totalResults, int page, int pageSize) {
        this.query = query;
        this.results = results;
        this.totalResults = totalResults;
        this.page = page;
        this.pageSize = pageSize;
    }

    public ProteinHit getHit(Long id) {
        for (ProteinHit result : results) {
            if (result.getId().equals(id)) {
                return result;
            }
        }
        throw new NoSuchElementException("protein " + id + " is not in the results");
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<ProteinHit> getResults() {
        return results;
    }

    public void setResults(List<ProteinHit> results) {
        this.results = results;
    }
    //TODO equals & hashCode
}

