package org.string_db.search;

import java.util.List;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public interface Searcher {
    SearchResult search(String query);

    SearchResult search(String query, Long spcId);

    SearchResult search(String query, Integer page, Integer pageSize);

    SearchResult search(String query, Long spcId, Integer page, Integer pageSize);

    List<SearchResult> search(List<String> queries, Long spcId, Integer page, Integer pageSize);
}
