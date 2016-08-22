package org.string_db.index;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public interface DataSource {
    List<Long> getSpeciesIds();

    Map<Long, Set<String>> getProteinNames(long speciesId);

    /**
     * @param speciesId
     * @return {id, external_id, preferred_name, annotation}
     */
    List<String> getProteinsTsv(Long speciesId);
}
