package org.string_db.analysis;


import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.util.Collection;
import java.util.Stack;

/**
 * This class is based on the example from Lucene in Action, chapter 4.
 * It adds an option to exclude original terms.
 *
 * @author Manuel Weiss
 */
final class SynonymFilter extends TokenFilter {
    public static final String TOKEN_TYPE_SYNONYM = "SYNONYM";

    private final Stack<String> synonymStack = new Stack<String>();
    private final CharTermAttribute termAttr;
    private final PositionIncrementAttribute posIncAtt;
    private SynonymEngine engine;
    private AttributeSource.State current;
    /**
     * if true, only synonyms will be emitted (original terms will be omitted).
     */
    private boolean excludeOriginalTerms;

    /*
#1 Synonym buffer
#2 Pop buffered synonyms
#3 Read next token
#4 Push synonyms of current token onto stack
#5 Return current token
#6 Retrieve synonyms
#7 Push synonyms onto stack
#8 Set position increment to zero
*/

    /**
     * @param in
     * @param engine
     * @param excludeOriginalTerms if true, only synonyms will be emitted (original terms will be omitted).
     */
    public SynonymFilter(TokenStream in, SynonymEngine engine, boolean excludeOriginalTerms) {
        super(in);
        this.engine = engine;
        this.excludeOriginalTerms = excludeOriginalTerms;
        this.termAttr = addAttribute(CharTermAttribute.class);
        this.posIncAtt = addAttribute(PositionIncrementAttribute.class);
    }

    public boolean incrementToken() throws IOException {
        if (!synonymStack.isEmpty()) {       //#2
            String syn = synonymStack.pop(); //#2
            restoreState(current);             //#2
            termAttr.copyBuffer(syn.toCharArray(), 0, syn.length());
            posIncAtt.setPositionIncrement(0);
            return true;
        }

        while (synonymStack.isEmpty()) {
            if (!input.incrementToken()) {  //#3
                return false;
            }
            if (addAliasesToStack()) {  //#4
                current = captureState();
                //skip this term, only add attributes
                if (excludeOriginalTerms) {
                    final String lastSyn = synonymStack.pop();
                    termAttr.copyBuffer(lastSyn.toCharArray(), 0, lastSyn.length());
                }
                return true;       //#5
            }
            if (!excludeOriginalTerms) {
                //there's stil the original term to be emitted
                return true;
            }
        }
//        //no more tokens and no more synonyms?
        return true;
    }

    private boolean addAliasesToStack() throws IOException {
        Collection<String> synonyms = engine.getSynonyms(new String(termAttr.buffer(), 0, termAttr.length()));   //#6
        if (synonyms == null || synonyms.isEmpty()) {
            return false;
        }
        for (String synonym : synonyms) {
            // TODO figure out whether we have to increase position for multi-word synonyms or not...
            for (String s : synonym.split(" ")) {    // we might be dealing with a multi-word synonym
                synonymStack.push(s);
            }
        }
        return !synonymStack.isEmpty();
    }
}