package org.pax_db.api.rest.search;

import org.pax_db.api.search.Hits;
import org.pax_db.api.search.PaxdbQueryParser;
import org.pax_db.api.search.SearchResponse;
import org.pax_db.api.search.SearchResultId;
import org.string_db.search.ProteinHit;
import org.string_db.search.SearchResult;
import org.string_db.search.Searcher;

import javax.ws.rs.DefaultValue;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class SearchResourceRepository implements SearchResource {
    private final Searcher searcher;

    private final PaxdbQueryParser parser = new PaxdbQueryParser();

    public SearchResourceRepository(Searcher searcher) {
        this.searcher = searcher;
    }

    @Override
    public Object search(String q, Long species, @DefaultValue("1") Integer page, @DefaultValue("10") Integer pageSize) {
        long start = System.currentTimeMillis();
        final List<SearchResult> searchResults = searcher.search(parser.split(q), species, page, pageSize);
        final ArrayList<Hits> results = new ArrayList<Hits>();
        for (SearchResult hit : searchResults) {
            final ArrayList<SearchResultId> resultIds = toSearchResultIds(hit);
            results.add(new Hits(hit.getQuery(), species, resultIds, hit.getTotalResults(), page, pageSize));
        }

        return new SearchResponse(results, (System.currentTimeMillis() - start) + "ms");
    }

    private ArrayList<SearchResultId> toSearchResultIds(SearchResult hit) {
        final ArrayList<SearchResultId> resultIds = new ArrayList<SearchResultId>(hit.getResults().size());
        for (ProteinHit p : hit.getResults()) {
            resultIds.add(new SearchResultId(p.getId().toString(), p.getName(), p.getAnnotation(), p.getHighlighted()));
        }
        return resultIds;
    }

    @Override
    public Integer getVersion() {
        return 3;
    }
}
