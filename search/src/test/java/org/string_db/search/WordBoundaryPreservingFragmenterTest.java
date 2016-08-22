package org.string_db.search;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class WordBoundaryPreservingFragmenterTest {

    WordBoundaryPreservingFragmenter fragmenter = new WordBoundaryPreservingFragmenter(15);

    @Test
    public void test_cutoff_annotation_when_too_long() throws Exception {

        assertThat("cut off text after the specified limit (15)",
                fragmenter.cutoff("limit ends here density is high"), equalTo("limit ends here ..."));

        assertThat("it doesn't cut off short text",
                fragmenter.cutoff("limit ends here"), equalTo("limit ends here"));

    }

    /**
     * @see AnnotationNonLetterCharacters
     */
    @Test
    public void test_cutoff_at_word_boundary() throws Exception {
        assertThat(fragmenter.cutoff("limit ends here/COX "), equalTo("limit ends ..."));
    }

}
