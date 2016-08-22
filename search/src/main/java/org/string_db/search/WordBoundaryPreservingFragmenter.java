package org.string_db.search;

/**
 * Tries to extract a substring that preserves word boundaries found in protein annotation.
 * There's plenty of possible improvements but these are usually used to display search results,
 * so it's not so important.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class WordBoundaryPreservingFragmenter {
    public static final Integer DEFAULT_SNIPPET_LENGTH = 300;

    private final Integer maxSnippetLength;
    private final Integer minSnippetLength = 10;

    public WordBoundaryPreservingFragmenter(Integer maxSnippetLength) {
        if (maxSnippetLength < minSnippetLength) {
            throw new IllegalArgumentException("max snippet lenght must be bigger than " + minSnippetLength);
        }
        this.maxSnippetLength = maxSnippetLength;
    }

    public WordBoundaryPreservingFragmenter() {
        this(DEFAULT_SNIPPET_LENGTH);
    }

    public String cutoff(String string) {
        if (string.length() <= maxSnippetLength) {
            return string;
        }
        for (int i = maxSnippetLength; i >= minSnippetLength; i--) {
            if (Character.isWhitespace(string.charAt(i))
                    || Character.isSpaceChar(string.charAt(i))
                    || isPunctuationChar(string.charAt(i))) {
                return string.substring(0, i) + " ...";
            }
        }
        return string.substring(0, minSnippetLength) + " ...";
    }

    protected boolean isPunctuationChar(char c) {
        /* protein names sometimes contain dots, maybe we should split only when there's whitespace after a dot */

        return c == ','
                || c == '.'
                || c == '!'
                || c == '?'
                || c == ':'
                || c == ';'
                || c == '['
                || c == ']'
//                || c == ')'
//                || c == '('
                || c == '+'
                || c == '*'
                || c == '`'
//                || c == '%'
//                || c == '&'
//                || c == '#'
                || c == '"'
//                || c == '/'
                || c == ':'
                || c == '}'
                || c == '{'
//                || c == '|'
//                || c == '>'
//                || c == '<'
                || c == '?'
                || c == '='
//                || c == '~'
//                || c == '^'
                ;
    }

}
