package org.string_db.search;

import org.apache.lucene.search.Query;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public interface ProteinQueryParser {
    Query parse(String query);
}
