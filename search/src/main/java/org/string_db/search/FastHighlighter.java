package org.string_db.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.ScoreOrderFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;

import java.io.IOException;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class FastHighlighter {
    private final int ANNOTATION_SNIPPET_LENGTH = 300;
    FastVectorHighlighter highlighter = new FastVectorHighlighter(true, true, new SimpleFragListBuilder(),
            new ScoreOrderFragmentsBuilder(
                    new String[]{"<span class='search_hit'>"},
                    new String[]{"</span>"})
    );
    private final IndexSearcher indexSearcher;


    public FastHighlighter(IndexSearcher is) {
        this.indexSearcher = is;
    }

    public String getHighlightedSnippet(int docId, Query query) throws IOException {
        FieldQuery fieldQuery = highlighter.getFieldQuery(query);
//        String idFrag  = highlighter.getBestFragment(fieldQuery, luceneSearcher.getIndexReader(), docId, "id", 20);
//        idFrag = idFrag !=null ? "..." + idFrag + "..., " : null;
        String nameFrag = highlighter.getBestFragment(fieldQuery, indexSearcher.getIndexReader(), docId, "name", 40);
        String snippet = "";

        if (nameFrag == null) {
            nameFrag = highlighter.getBestFragment(fieldQuery, indexSearcher.getIndexReader(), docId, "id", 40);
            nameFrag = nameFrag != null ? "..." + nameFrag + "..., " : null;
        }
        if (nameFrag != null) {
            snippet = "<div class='name_snippet'>also known as: ..." + (nameFrag != null ? nameFrag : "") + "...</div>\n";
        }

        String annFrag = highlighter.getBestFragment(fieldQuery, indexSearcher.getIndexReader(),
                docId, "annotation", ANNOTATION_SNIPPET_LENGTH);
        if (annFrag == null) {
            Document doc = indexSearcher.doc(docId, new FieldSelector() {
                @Override
                public FieldSelectorResult accept(String fieldName) {
                    if ("annotation".equals(fieldName)) {
                        return FieldSelectorResult.LOAD;
                    }
                    return FieldSelectorResult.NO_LOAD;
                }
            });
            annFrag = doc.get("annotation");
            if (annFrag.length() > ANNOTATION_SNIPPET_LENGTH) {
                annFrag = annFrag.substring(0, ANNOTATION_SNIPPET_LENGTH) + "...";
            }
        } else {
            annFrag = "..." + annFrag + "...";
        }
        snippet += (annFrag != null) ? "<div class='annotation_snippet'>" + annFrag + "</div>" : "";
        return snippet;
    }


}
///**
// * An implementation of FragmentsBuilder that looks for whitespace or the beginning or end of the
// * source text for fragment boundaries, to avoid truncating words at the edges.
// *
// */
// class WhitespaceFragmentsBuilder extends BaseFragmentsBuilder {
//
//
//	/**
//	 * a constructor.
//	 */
//	public WhitespaceFragmentsBuilder(){
//		super();
//	}
//
//	/**
//	 * a constructor.
//	 *
//	 * @param preTags array of pre-tags for markup terms.
//	 * @param postTags array of post-tags for markup terms.
//	 */
//	public WhitespaceFragmentsBuilder( String[] preTags, String[] postTags ) {
//		super( preTags, postTags );
//	}
//
//	/**
//	 * do nothing. return the source list.
//	 */
//	public List<WeightedFragInfo> getWeightedFragInfoList(List<WeightedFragInfo> src) {
//		return src;
//	}
//
//	protected String makeFragment( StringBuilder buffer, int[] index, String[] values, WeightedFragInfo fragInfo ){
//		StringBuilder fragment = new StringBuilder();
//		String src = getFragmentSource( buffer, index, values, fragInfo);
//		final int s = fragInfo.startOffset;
//		int srcIndex = 0;
//		for( SubInfo subInfo : fragInfo.subInfos ){
//			for( Toffs to : subInfo.termsOffsets ){
//				fragment.append( src.substring( srcIndex, to.startOffset - s ) ).append( getPreTag( subInfo.seqnum ) )
//				.append( src.substring( to.startOffset - s, to.endOffset - s ) ).append( getPostTag( subInfo.seqnum ) );
//				srcIndex = to.endOffset - s;
//			}
//		}
//		fragment.append( src.substring( srcIndex ) );
//		return fragment.toString();
//	}
//
//	protected String getFragmentSource( StringBuilder buffer, int[] index, String[] values, WeightedFragInfo fragInfo ){
//		while( buffer.length() < fragInfo.endOffset && index[0] < values.length ){
//			if( index[0] > 0 && values[index[0]].length() > 0 )
//				buffer.append( ' ' );
//			buffer.append( values[index[0]++] );
//		}
//		while(fragInfo.startOffset>0 && !Character.isWhitespace(buffer.charAt(fragInfo.startOffset))){
//			fragInfo.startOffset--;
//		}
//		while(fragInfo.endOffset <  buffer.length() &&  !Character.isWhitespace(buffer.charAt(fragInfo.endOffset-1))){
//			fragInfo.endOffset++;
//		}
//		return buffer.substring( fragInfo.startOffset,  buffer.length() < fragInfo.endOffset ? buffer.length() : fragInfo.endOffset);
//	}
//
//}
