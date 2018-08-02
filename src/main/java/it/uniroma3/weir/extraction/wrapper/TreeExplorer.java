package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.hlog.HypertextualLogger.getLogger;
import static it.uniroma3.weir.extraction.wrapper.StepFactories.availableDirections;
import static it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates.isSuitableLeafValue;
import static it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates.isSuitablePivotOccurrence;
import static org.w3c.dom.Node.TEXT_NODE;
import it.uniroma3.hlog.HypertextualLogger;

import java.util.LinkedHashSet;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * 
 */
public class TreeExplorer {

	static final private HypertextualLogger log = getLogger();

	final private Set<Navigation> navigations; // set of navs accumulated so fare
	
	final private int range; // exploration max range from starting node 	
	
	private Node starting;   // starting node from which to start the exploration
	
	public TreeExplorer(int range) {
		this.range  = range;
		this.navigations = new LinkedHashSet<>();
	}
	
	/**
	 * 
	 * @param from 	starting node from which to explore
	 * @return
	 */
	public Set<Navigation> explore(Node from) {
		log.trace("Looking for text values at max distance "+this.range+" from pivot occurrence "+from+"\n");
		this.starting = from;
		this.explore(from, new Navigation(), this.range);
		return this.navigations;
	}
	
	private void explore(
			Node current,   // current node, exploring away from the start node
			Navigation seq, // sequence of navigation steps that lead here
			int distance) { // nav steps still available
		log.trace("distance d=" + distance+"; current path="+seq+"; current node="+current);
		if (isSuitableLeafValue(current)) {
			log.trace("Found a suitable textual leaf value "+current);
			log.trace("Saving path "+seq);
			this.navigations.add(seq);
			return; //!? /* wherever we're going, we shouldn't cross a value? */
		}

		/* Other steps are still available ? */
		if (distance > 0) {
			log.newPage("expanding navigation: "+seq);
			
			/* move one step farther away to reach other values */			
			for (Step step : availableDirections(current)) {
				log.trace();
				log.trace("Trying to add "+step);
				final Node to = step.to(current);

				/* check it is not chasing its own footsteps */
				if (this.chasingItsOwnTail(seq, step)) {
					log.trace("Path cycle: giving up path "+seq+"+"+step);
					continue;
				}

				/* wherever we're going, there's a closer pivot to start from */
				if (isSuitablePivotOccurrence(to)) {
					log.trace("Met another suitable pivot occurrence\n"+to);
					log.trace("Giving up this path");
					continue;
				}
				this.explore(to, seq.append(step), distance - deltaDistance(current,to));
			}
			log.endPage();
		}
	}

	final private boolean chasingItsOwnTail(Navigation seq, Step next) {
		// e.g., for excluding a DX step when just moved to SX
		return seq.hasKnot(this.starting, next);
	}

	public int deltaDistance(Node from, Node to) {
		if (from==to.getParentNode() || from.getParentNode()==to)
			return onlyElementSon(from,to)? 0 : 1; /* UP or DW */
		return 1; /* SX or DX */
	}

	/* do not consider for distances vertical steps into an only-son element */
	private boolean onlyElementSon(Node from, Node to) {
		final Node parent = (from == to.getParentNode() ? from : to ); // UP or DOWN?
		final NodeList children = parent.getChildNodes();
		int numBrothers = 0;
		for (int i=0; i<children.getLength(); i++) {
			final Node child = children.item(i);
			if (child.getNodeType()==TEXT_NODE) { // skip indentation texts
				final Text text = (Text)child;
				if (!text.getTextContent().trim().isEmpty())
					numBrothers++;
			} else {
				numBrothers++;					
			}
		}
		return ( numBrothers==1 );
	}

}