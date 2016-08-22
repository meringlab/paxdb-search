package org.string_db.analysis;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Emits only synonyms, omits the original term.
 * <p/>
 * preferred_name regex: <pre> db.proteins.find({"name" : {"$not" : /^[0-9a-zA-Z.\-\x27_():+*@\/<>\[\]#,]+$/}})</pre>
 */
class SynonymOnlyEngine implements SynonymEngine {

    private static final Logger log = Logger.getLogger(SynonymOnlyEngine.class);
    final String split_chars = "[\\[\\]\\_\\-(),;:.]";
    final String synonym_pattern = ".+" + split_chars + ".*";

    @Override
    public Collection<String> getSynonyms(String s) throws IOException {

        if (!s.matches(synonym_pattern)) {
            return null;
        }

        final Set<String> synonyms = new HashSet<String>();
        String var = s.replaceAll(split_chars, " ");
        synonyms.addAll(Arrays.asList(var.split(" ")));
        var = var.replace(" ", "");
        synonyms.add(var);
        //exclude the original term
        synonyms.remove(s);
        if (log.isDebugEnabled()) {
            log.debug(s + " synonyms: " + synonyms);
        }
        return synonyms;
    }
}
