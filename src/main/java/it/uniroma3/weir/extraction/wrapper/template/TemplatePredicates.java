package it.uniroma3.weir.extraction.wrapper.template;

import static it.uniroma3.weir.extraction.wrapper.template.TemplateMarker.isTemplateNode;
import static it.uniroma3.weir.extraction.wrapper.template.TemplateMarker.isVariantNode;
import static it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates.Direction.DX;
import static it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates.Direction.SX;
import static it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates.TargetPredicate.ISINVARIANT;
import static it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates.TargetPredicate.ISVARIANT;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;
import it.uniroma3.token.dom.DOMToken;
import it.uniroma3.token.dom.node.DOMNode;
import it.uniroma3.weir.configuration.Constants;
import it.uniroma3.weir.configuration.WeirConfig;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
/**
 * Classifies documents nodes as suitable pivots
 * and suitable extracted values.
 * <br/>
 * It classifies each node occurrence as either a template (invariant) node
 * or non-template (variant) node, after an ExAlg-like ECGM analysis 
 * (see <a href=http://ilpubs.stanford.edu:8090/548/1/2002-40.pdf"> 
 *    Extracting Structured Data from Web Pages</a>
 * )
 * <br/>
 * It marks the template token occurrences directly on the DOMs.
 * <br/>
 *  It is not coupled with the details of the ECGM analysis and
 * LFEQ representation, but it knows both {@link DOMToken} and [DOM]Node.
 */
public class TemplatePredicates {

	static final public String ID_ATTRIBUTE = "id";
	
	static final private int MIN_PIVOT_LENGTH = WeirConfig.getInteger(Constants.MIN_PIVOT_LENGTH);
	static final private int MAX_PIVOT_LENGTH = WeirConfig.getInteger(Constants.MAX_PIVOT_LENGTH);
	static final private int MAX_VALUE_LENGTH = WeirConfig.getInteger(Constants.MAX_VALUE_LENGTH);
	
	static public boolean isSuitablePivotToken(DOMToken token) {
		final DOMNode node = token.getDOMNode();
		//n.b. id attribute *values* are not in the DOMNode associated with Tokens
		return isTokenWithId(token) || 
			 ( isTemplateNode(node) && isTextualPivotOfAppropriateLength(token) );
	}

	/**
	 * @param node
	 * @return 
	 */
	static public boolean isSuitablePivotOccurrence(Node node) {
		return isNodeWithId(node) || 
			   isTextualPivotOfAppropriateLength(node) && isPartOfPCDATAwithInvariants(node) ;
	}

	static public boolean isSuitableLeafValue(Node node) {
		return isText(node) && isPartOfPCDATAwithVariants(node) &&
			   checkLength(node.getNodeValue(), 1, MAX_VALUE_LENGTH);
	}
	
	/**
	 * @param node
	 * @return true iff node is text containing at least a variant to extract
	 */
	static private boolean isPartOfPCDATAwithVariants(Node node) {
		/** look at the left && right hand side */
		return lookAtTheSide(node, SX, ISVARIANT) || lookAtTheSide(node.getNextSibling(), DX, ISVARIANT) ;
	}
	
	/**
	 * @param node
	 * @return true iff node is text containing at least an variant to use as pivot
	 */
	static private boolean isPartOfPCDATAwithInvariants(Node node) {
		/** look at the left && right hand side */
		return lookAtTheSide(node, SX, ISINVARIANT) || lookAtTheSide(node.getNextSibling(), DX, ISINVARIANT) ;
	}

	static private boolean lookAtTheSide(Node current, Direction dir, TargetPredicate predicate) {
		while (current!=null && isText(current)) {
			if (predicate.evaluate(current))
				return true;			
			current = dir.move(current);
		}
		return false;
	}
	
	static enum Direction {
		SX {@Override
			Node move(Node node) { return node.getPreviousSibling(); }
		},
		DX {
			@Override
			Node move(Node node) { return node.getNextSibling();     }
		};		
		abstract Node move(Node node);
	}
	
	static enum TargetPredicate {

		ISVARIANT {
			@Override
			boolean evaluate(Node node) {
				return ( isNonEmptyTextualNodeOfAppropriateLength(node) && isVariantNode(node) );
			}
		},
		ISINVARIANT {
			@Override
			boolean evaluate(Node node) {
				return ( isTemplateNode(node) && isTextualPivotOfAppropriateLength(node) );
			}
		};
		abstract boolean evaluate(Node node);
		
	}
	
	static public boolean isNonEmptyTextualNodeOfAppropriateLength(Node node) {
		return isText(node) && checkLength(node.getNodeValue(), 1, MAX_VALUE_LENGTH);
	}

	static public boolean isTextualPivotOfAppropriateLength(Node node) {
		return isText(node) && checkLength(node.getNodeValue(), MIN_PIVOT_LENGTH, MAX_PIVOT_LENGTH);
	}

	static public boolean isText(Node node) {
		return node.getNodeType()==TEXT_NODE;
	}
	
	static private boolean checkLength(String s, int min, int max) {
		final int length = s.trim().length();
		return min<=length && length<max;
	}

	static public boolean isNodeWithId(Node node) {
		return node.getNodeType()==ELEMENT_NODE && ((Element)node).hasAttribute(ID_ATTRIBUTE) ;
	}

	static private boolean isTextualPivotOfAppropriateLength(DOMToken token) {
		return token.isText() && checkLength(token.getValue(), MIN_PIVOT_LENGTH, MAX_PIVOT_LENGTH);
	}

	static private boolean isTokenWithId(DOMToken token) {
		return token.isTag() && token.getTag().getAttributes().containsKey(ID_ATTRIBUTE) ;
	}

}
