package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.weir.extraction.wrapper.Step.normalizedPreviousSiblingNode;
import static it.uniroma3.weir.extraction.wrapper.Step.normalizedNextSiblingNode;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;
import it.uniroma3.weir.extraction.wrapper.Step.Dw;
import it.uniroma3.weir.extraction.wrapper.Step.DwElement;
import it.uniroma3.weir.extraction.wrapper.Step.DwText;

import java.util.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * 
 * Factories to create the set of possible {@link Step} from a start {@link Node}.
 * 
 */
public enum StepFactories {

	DX {
		@Override
		protected Set<? extends Step> from(Node current) {
			return horizontalStep(current, normalizedNextSiblingNode(current), Step.DX);
		}
	},	
	SX {
		@Override
		protected Set<? extends Step> from(Node current) {
			return horizontalStep(current, normalizedPreviousSiblingNode(current), Step.SX);
		}
	},
	UP {
		/** return the available moves from the current DOM node upwards */
		@Override
		public Set<? extends Step> from(Node current) {
			final Node parent = current.getParentNode();
			if (!isTextOrElement(parent)) return Collections.emptySet();
			/* discover which step downward from the parent node would
			 * lead back here to this node as one of its children
			 */			
//			Set<? extends Dw> stepsBackDownward = (Set<? extends Dw>) DW.from(parent);
//			Dw opposite = findOppositeDownward(current, stepsBackDownward);
//
//			return Collections.singleton(new Step.Up(opposite));
			return Collections.singleton(Step.UP);
		}

		@SuppressWarnings("unused")
		private Dw findOppositeDownward(Node child, Set<? extends Dw> backDownward) {
			for(Dw down : backDownward)
				if (child==down.getChild())
					return down;
			throw new IllegalStateException("Cannot find back-step");
		}
	},
	DW {
		/** return the available moves from the current DOM node downwards */
		@Override
		public Set<? extends Dw> from(Node parent) {
			if (parent.getFirstChild()==null) return Collections.emptySet();

			/* several children  downward,  either texts or elements */

			/* check if resulting downward steps have
			 * been already cached as  DOM user-data */
			final Map<Dw, Node> cached = getCachedDownSteps(parent);
			if (cached!=null) {
				return cached.keySet();
			}

			/* no cached result found: built the result and cache it */
			final NodeList children = parent.getChildNodes();

			final Map<Dw,Node> result = new LinkedHashMap<>();

			// Check whether a text is separated from its immediate siblings
			// All this work makes sense only when the DOM tree contains nodes
			// that are neither texts nor elements (e.g., HTML comments), and
			// the DOM is not normalized, i.e., there are adjacent text nodes.
			boolean textIsSeparated = true; // next text is well separated?
			int positionAsElem = 0; // XPath position() for child::*[pos] expr.
			int positionAsText = 0; // XPath position() for text()[pos]   expr.
			for (int i = 0; i < children.getLength(); i++) {
				final Node child = children.item(i);
				final short type = child.getNodeType();
				switch (type) {
				case TEXT_NODE:// ----- It's a text
					if (textIsSeparated) {
						positionAsText++;
						final DwText down = new DwText(positionAsText);
						down.setChild(child); // next node from parent
						result.put(down,child);
					}
					textIsSeparated = false;
					break;
				case ELEMENT_NODE:// -- It's an element
					positionAsElem++; // increment the position
					// wrt other elements
					final DwElement down = new DwElement(positionAsElem);
					down.setChild(child); // next node from parent
					result.put(down,child);
					// n.b. an element separates texts: fall-through the case
				default: // ----------- It's something else
					// e.g., an HTML comment <!-- -->;
					// we do not care what it is but it separates 
					// otherwise contiguous text nodes for sure.
					textIsSeparated = true;
				}
			}
			/* cache result as DOM user-data in this parent node */
			
			setCachedDownSteps(parent, result);
			return result.keySet();
		}
				
	};
	
	@SuppressWarnings("unchecked")
	static private Map<Dw, Node> getCachedDownSteps(Node current) {
		return (Map<Dw, Node>) current.getUserData(USERDATA_KEY_DOWNWARDSTEPS);		
	}

	@SuppressWarnings("unchecked")
	static private Map<Dw, Node> setCachedDownSteps(Node current, Map<Dw, Node> step2node) {
		return (Map<Dw, Node>) current.setUserData(USERDATA_KEY_DOWNWARDSTEPS, step2node, null);
	}
	
	static public Node getChildOfDownStep(Node current, Dw downStep) {
		DW.from(current); // compute the down-steps if not already cached
		return getCachedDownSteps(current).get(downStep);
	}
	
	static final public String USERDATA_KEY_DOWNWARDSTEPS = "_STEPS_DOWNWARDS_";


	/**
	 * Build the set of {@link Step} available from the given 
	 * {@link Node}
	 * @param current the node where the step starts from
	 * @return the set of available steps
	 */
	abstract protected Set<? extends Step> from(Node current) ;

	protected boolean isTextOrElement(Node to) {
		if (to == null) return false;

		final short type = to.getNodeType();
		return (type == ELEMENT_NODE || type == TEXT_NODE);
	}


	/** return next available step in this dir from the current node */
	protected Set<? extends Step> horizontalStep(Node current, Node to, Step step) {
		// CHECK TODO circumvent the comment nodes?
		/* move only toward non-null texts and elements */ 
		if (!isTextOrElement(to)) 
			return Collections.emptySet();

		return Collections.singleton(step);
	}	
	
	static public List<Step> availableDirections(Node current) {
		final List<Step> result = new LinkedList<>();

		for (StepFactories factory : values()) {
			result.addAll(factory.from(current));
		}
		return result;
	}

}
