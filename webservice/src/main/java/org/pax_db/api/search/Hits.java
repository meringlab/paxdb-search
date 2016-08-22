package org.pax_db.api.search;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Search results for one query item.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hits", propOrder = {"pageSize", "currentPage", "totalResults", "speciesId", "query", "hits"})
public class Hits implements Serializable {
    @XmlTransient
    private static final long serialVersionUID = -4215722423988064332L;

    @XmlAttribute(name = "totalHits", required = true)
    private Integer totalResults;
    @XmlAttribute(required = true)
    private Integer currentPage;
    @XmlAttribute(required = true)
    private Integer pageSize;
//TODO prev/next page for xml/json

    @XmlAttribute
    private String query;
    @XmlAttribute
    private Long speciesId = null;

    @XmlElement(name = "protein", required = true)
    private ArrayList<SearchResultId> hits;

    public Hits() {
        this(null, null, null, 0, 1, 10);
    }

    public Hits(String query, Long speciesId, ArrayList<SearchResultId> hits, int totalResults,
                int currentPage, int pageSize) {
        super();
        this.query = query;
        this.speciesId = speciesId;
        this.hits = hits;
        this.totalResults = totalResults;
        assert (currentPage > 0);
        this.currentPage = currentPage;
        assert (pageSize > 0);
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        int total = totalResults / pageSize;
        if (totalResults % pageSize > 0) {
            total++;
        }
        return total;
    }

    public String getDisplayMessage() {
        if (totalResults == 1) {
            return "Showing 1 match";
        }
        String msg = "Showing ";
        if (totalResults < pageSize) {
            msg += totalResults;
        } else {
            msg += getFirstResultPosition() + " - " + getLastResultPosition() + " of "
                    + totalResults;
        }
        msg += " matches";
        return msg;
    }

    public Long getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(Long speciesId) {
        this.speciesId = speciesId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getFirstResultPosition() {
        return (currentPage - 1) * pageSize + 1;
    }

    public Integer getLastResultPosition() {
        return getFirstResultPosition() + hits.size() - 1;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setHits(ArrayList<SearchResultId> hits) {
        this.hits = hits;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalResults(int total) {
        this.totalResults = total;
    }

    public ArrayList<SearchResultId> getHits() {
        return hits;
    }

    @Override
    public String toString() {
        return "Hits [hits=" + hits + ", totalResults=" + totalResults + ", currentPage="
                + currentPage + ", pageSize=" + pageSize + "]";
    }

}
