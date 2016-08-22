package org.string_db.analysis;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNull;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class SynonymEngineTest {
    static ProteinAnnotationSynonymEngine cut = new ProteinAnnotationSynonymEngine();

    @Test
    public void skipRegularWords() throws IOException {
        assertNull(cut.getSynonyms("word"));
        assertNull(cut.getSynonyms("-word"));
    }
}
