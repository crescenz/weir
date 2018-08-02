package it.uniroma3.weir.integration.lc;

import static it.uniroma3.hlog.HypertextualUtils.format;
import static it.uniroma3.hlog.HypertextualUtils.lazyPopup;
import static it.uniroma3.weir.Formats.thousandth;
import static it.uniroma3.weir.configuration.Constants.LOCAL_CONSISTENCY_THRESHOLD;
import static it.uniroma3.weir.model.log.WeirStyles.header;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.model.log.VectorListRenderer;
import it.uniroma3.weir.vector.Label;
import it.uniroma3.weir.vector.Vector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/**
 *
 * Group {@link Vector}s into clusters by similarity.
 *
 * Then choose a {@link Vector} to represent each cluster,
 * discarding all the others in the group, after grabbing
 * their {@link Label}s to save them in the new representative.
 *
 */
public class VectorClustering {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	final private double threshold;

	final private LinkedList<Vector> available;
	
	public VectorClustering(List<Vector> normalized) {
		this.threshold = WeirConfig.getDouble(LOCAL_CONSISTENCY_THRESHOLD);
		log.trace("LC distance threshold=" + format(thousandth,this.threshold));
		this.available = new LinkedList<>(normalized);
		log.trace(normalized.size() + " vectors to cluster");
	}

	public Vector getNextPivot() {
		if (this.available.isEmpty()) return null;
		return this.available.getFirst();
	}
	
	static final private VectorListRenderer vlr = new VectorListRenderer().enableIdColumn().enableRuleColumn();
	
	public List<Vector> groupBySimilarity(final Vector pivot) {
		log.trace("selecting the most similar vectors "
				+ "to the pivot over "+this.available.size()+" candidates");
		log.trace("Pivot:");
		log.trace(pivot);
		log.trace("<hr/>");
		log.trace("Candidates:");
		final List<Vector> similars = this.findSimilar(pivot, this.available);
		log.trace("<hr/>");
		if (!similars.isEmpty()) {
			log.trace("List of all similar vectors found:");
			log.trace(vlr.toHTMLstring(similars));
		} else log.trace("No other similar vectors found");

		similars.add(pivot);

		available.removeAll(similars);

		return similars;
	}

	/* find all similar vectors within the LC threshold */
	private List<Vector> findSimilar(Vector pivot, List<Vector> candidates) {
		log.newTable();
		log.trace(header("id"), header("d"), header("similar"));		
		final List<Vector> similars = new ArrayList<>();
		for (final Vector candidate : candidates) {
			if (isSimilar(pivot, candidate)) {
				similars.add(candidate);
			}
		}
		log.endTable();
		return similars;
	}

	private boolean isSimilar(Vector pivot, final Vector candidate) {
		final double distance = pivot.distance(candidate);
		final boolean isSimilar = 
				pivot.getType().equals(candidate.getType()) &&  // CHECK to relax?
				( /*1*/ distance<=this.threshold
				||             
				  /*2*/ pivot.equalsIgnoringNulls(candidate) );
		// CHECK which of the two (1) vs (2), or both?
		log(isSimilar, distance, candidate);
		return isSimilar;
	}

	private void log(boolean similar, double d, Vector c) {
		log.trace(lazyPopup(c.getWeirId(),c), format(thousandth,d), similar);
	}

}
