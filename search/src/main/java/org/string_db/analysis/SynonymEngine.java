package org.string_db.analysis;

import java.io.IOException;
import java.util.Collection;

interface SynonymEngine {
    Collection<String> getSynonyms(String s) throws IOException;
}