package org.string_db.analysis;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class ProteinAnnotationSynonymEngine implements SynonymEngine {

    private static final Logger log = Logger.getLogger(ProteinAnnotationSynonymEngine.class);

    @Override
    public Collection<String> getSynonyms(String s) throws IOException {

        if (!s.matches(".+[\\[\\]\\-(),;:.].*"))
            return null;
        final Set<String> synonyms = new HashSet<String>();
        if (log.isDebugEnabled()) {
            log.debug("adding synonym " + s.replaceAll("[\\[\\]\\-(),;:.]", " ") + " for " + s);
        }
        s = s.replaceAll("[\\[\\]\\-(),;:.]", " ");
        synonyms.add(s);
        if (log.isDebugEnabled()) {
            log.debug("adding synonym " + s.replace(" ", "") + " for " + s);
        }
        s = s.replace(" ", "");
        synonyms.add(s);

        return synonyms;
    }
}
