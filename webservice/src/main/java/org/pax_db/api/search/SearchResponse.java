package org.pax_db.api.search;

import org.pax_db.api.Response;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

/**
 * The jaxb annotated response to a search request. On every xml output
 * backward incompatible change, resulting from any of the both directly
 * and indirectly referenced classes, the version should be increased.
 * <p/>
 * Query can contain multiple items (comma/newline.. separated) and
 * each will have its own <code>Hits</code> object.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchResponse")
@XmlRootElement(name = "searchResponse")
public class SearchResponse implements Response {
    @XmlElement(required = true)
    protected ArrayList<Hits> hits;

    @XmlAttribute
    protected final Integer version = 3;

    @XmlAttribute
    private String searchTime;

    public SearchResponse() {

    }

    public SearchResponse(ArrayList<Hits> results, String searchTime) {
        this.hits = results;
        this.searchTime = searchTime;
    }

    /**
     * @return false if there's an input that has more than one match
     */
    public boolean isResolved() {
        for (Hits hit : hits) {
            // 11 matches with a page size of 10 and currentPage == 2 equals 1
            // and we need .
            // hit.getCurrentPage() != 1 in addition to hits.size
            if (hit.getHits().size() != 1 || hit.getCurrentPage() != 1) {
                return false;
            }
        }
        // and they all need to belong to the same species
        // Long speciesId = null;
        // for (Hits hit : results.values()) {
        // for (SearchResultId searchResultId : hit.getHits()) {
        // if (null == speciesId) {
        // speciesId = searchResultId.getSpeciesId();
        // } else if (! speciesId.equals(searchResultId.getSpeciesId())) {
        // return false;
        // }
        //
        // }
        // }
        return true;
    }

    public String getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(String searchTime) {
        this.searchTime = searchTime;
    }

    public ArrayList<Hits> getResults() {
        return hits;
    }

    public void setResults(ArrayList<Hits> results) {
        this.hits = results;
    }

    @Override
    public String toString() {
        return hits.toString();
    }

    private static final long serialVersionUID = 1989456548006663617L;

}
