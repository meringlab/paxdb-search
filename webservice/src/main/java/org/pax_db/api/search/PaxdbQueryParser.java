package org.pax_db.api.search;

import java.util.ArrayList;

/**
 * Free text search query parser.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class PaxdbQueryParser {

    /**
     * Makes a list of the subqueries contained in the query, using
     * comma and newline as the delimiter strings.
     *
     * @param query
     * @return
     */
    public ArrayList<String> split(String query) {
        ArrayList<String> input = new ArrayList<String>();
        if (null == query || query.trim().length() == 0) {
            return input;
        }
        String[] inputItems = query.split("\\n");
        for (String item : inputItems) {
            for (String i : item.split(",")) {
                i = i.trim();
                if (i.length() > 0 && !input.contains(i)) {
                    input.add(i);
                }
            }
        }
        return input;
    }
}
