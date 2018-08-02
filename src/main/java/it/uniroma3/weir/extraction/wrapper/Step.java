package it.uniroma3.weir.extraction.wrapper;

import org.w3c.dom.Node;
import static org.w3c.dom.Document.TEXT_NODE;
import static it.uniroma3.weir.extraction.wrapper.StepFactories.getChildOfDownStep;

/**
 * Model navigation steps within the text and elements nodes 
 * of a document DOM tree representation.
 * <BR/>
 * The navigation steps need to be easily translated into XPath steps.
 * A sequence of steps will be translated into syntactically correct 
 * XPath expressions by concatenating the translation of each step.
 * <BR/>
 * Note that we are NOT assuming the DOM tree normalized, 
 * i.e., there might be several contiguous text nodes.
 */
public abstract class Step {
	
	static final Up UP = new Up();     /* up    */
	static final Sx SX = new Sx();     /* left  */
	static final Dx DX = new Dx();     /* right */
	/* down: since many DW objects are possible, 
	 * cannot handle this with a java enum :(   */	
	static final DwText    DWT(int pos) { return new DwText(pos);    }
	static final DwElement DWE(int pos) { return new DwElement(pos); }
	
	/**
	 * Move this {@link Step} from the given node
	 * @param current the {@link Node} from which move this {@link Step}
	 * @return the {@link Node} this step move into
	 */
	abstract public Node to(Node current);

	abstract public String xPathStep();
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o!=null && this.getClass()==o.getClass();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	static class No extends Step {
		@Override
		public Node to(Node current) {
			return current;
		}
		public String xPathStep() {
			return "/.";
		} /* same as "" */
	};

	static class Dx extends Step {
		@Override
		public Node to(Node current) {
			return normalizedNextSiblingNode(current);
		}

		@Override
		public String xPathStep() {
			return "/following-sibling::node()[1]";
		}
	};

	static class Sx extends Step {
		@Override
		public Node to(Node current) {
			return normalizedPreviousSiblingNode(current);
		}

		@Override
		public String xPathStep() {
			return "/preceding-sibling::node()[1]";
		}
	};

	static class Up extends Step {

		@Override
		public Node to(Node current) {
			return current.getParentNode();
		}

		@Override
		public String xPathStep() {
			return "/..";
		}
	};

	static class Dw extends Step {
				
		final private int childPos; // this is an XPath position
                                    // i.e., it starts from 1
		private Node child;
				
		protected Dw(int childPos) {
			this.childPos = childPos;
		}

		public Node getChild() {
			return this.child;
		}
		
		public Dw setChild(Node child) {
			this.child = child;
			return this;
		}
		
		public int position() {
			return this.childPos;
		}

		@Override
		public Node to(Node current) {
			if (this.getChild()==null) {
				final Node cachedChild = getChildOfDownStep(current, this);
				this.setChild(cachedChild);
			}
			return this.getChild();
		}
		
		@Override
		public String xPathStep() {	throw new UnsupportedOperationException(); }

		@Override
		public int hashCode() {
			return super.hashCode() + this.position();
		}

		@Override
		public boolean equals(Object o) {
			/* the class check implies the direction and the child type */
			return super.equals(o) && this.position() == ((Dw)o).position();
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + position();
		}
		
	} /* end of Dw class */
	
	/**
	 *  this is a step downward to a text node
	 */
	static class DwText extends Dw {
		
		DwText(int pos) {
			super(pos);
		}
		
		@Override
		public String xPathStep() {
			return "/text()[" + position() + "]" ;
		}

	}
	
	/** 
	 * this is a step downward to an element
	 */
	static class DwElement extends Dw {
		
		DwElement(int pos) {
			super(pos);
		}

		@Override
		public String xPathStep() {
			return "/child::*[" + position() + "]";
		}
	}

	static final private boolean isText(Node node) {
		return ( node!=null && node.getNodeType()==TEXT_NODE ) ;
	}

	/**
	 * Return next sibling node, dealing with denormalized PCDATA
	 * @param current the start node
	 * @return 
	 */
	static final public Node normalizedNextSiblingNode(Node current) {
		Node result = current.getNextSibling();
		if (isText(current)) {
			while (result!=null && isText(result))
				result = result.getNextSibling();
		}
		return result;
	}

	/**
	 * Return previous sibling node, dealing with denormalized PCDATA
	 * @param current the start node
	 * @return 
	 */
	static final public Node normalizedPreviousSiblingNode(Node current) {
		Node result = current.getPreviousSibling();
		if (isText(current)) {
			while (result!=null && isText(result))
				result = result.getPreviousSibling();
		}
		return result;
	}

}
