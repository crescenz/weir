package it.uniroma3.weir.integration.lc;

import static it.uniroma3.weir.model.log.WeirStyles.header;
import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import static it.uniroma3.weir.configuration.Constants.LOCAL_CONSISTENCY_THRESHOLD;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.hlog.HypertextualUtils.Link;
import it.uniroma3.hlog.Logpage;
import it.uniroma3.weir.cache.CachedComputation;
import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.log.VectorListRenderer;
import it.uniroma3.weir.vector.Label;
import it.uniroma3.weir.vector.Vector;

import java.util.*;
/**
 * 
 * Enforce Local Consistency over a list of {@linkplain Vector}s
 * from the same {@link Website}.
 * 
 * Group vectors into clusters of similar vectors, then save only the most 
 * "representative" vector for each cluster, saving their original labels
 * (from the corresponding {@link ExtractionRule})
 */
public class LocalConsistencyEnforcer extends CachedComputation<List<Vector>, List<Vector>> {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	public List<Vector> enforceLocalConsistency(List<Vector> normalized) {
		return cachedComputation(normalized);
	}

	@Override
	public List<Vector> uncachedComputation(List<Vector> normalized) {
		log.trace("Local Consistency Enforcement");
		final VectorClustering clustering = new VectorClustering(normalized);
		final List<Vector> result = new ArrayList<>(); // chosen representative
		Vector pivot = null; // the vector around which the cluster is built

		log.trace("the first vector of each cluster is the chosen"
				+ " representative and minimizes the null values");

		log.newTable();
		log.trace(header("pivot id"), header("repr id"), header("labels"), header("cluster"));
		while ( (pivot=clustering.getNextPivot())!=null ) {
			log.newPage();
			final List<Vector> cluster = clustering.groupBySimilarity(pivot);
			final Vector representative = findRepresentative(cluster);
			result.add(representative);
			logCluster(log.endPage(), pivot, representative, cluster);
		}
		log.endTable();
		return result;
	}

	static final private VectorListRenderer vlr = 
			new VectorListRenderer().enableIdColumn();

	private void logCluster(Logpage page, Vector pivot, Vector repr, List<Vector> cluster) {
		final Link link = linkTo(page).withAnchor("details "+pivot.getWeirId());		
		log.trace(link, repr.getWeirId(), repr.getLabels(), vlr.toHTMLstring(repr,cluster));
	}

	static final private Comparator<Vector> numberOfNullComparator = new Comparator<Vector>() {
		@Override
		public int compare(Vector v1, Vector v2) {
			return Integer.compare(v1.countNonNulls(), v2.countNonNulls());
		}
	};

	private Vector findRepresentative(List<Vector> group) {
		if (group.size()>1) {
			final Vector best = Collections.max(group, numberOfNullComparator);

			final Vector result = best.copy(); // n.b. labels are not copied 
			final Set<Label> labels = mergeVectorLabels(group);
			result.addLabels(labels);
			return result;
		} else {
			return group.get(0);
		}		
	}

	private Set<Label> mergeVectorLabels(List<Vector> group) {
		/* merge all labels from the group */
		Set<Label> labels = Collections.emptySet();
		for (Vector vector : group) {
			labels = Label.merge(labels, vector.getLabels());
		}
		return labels;
	}

	@Override
	public Fingerprint fingerprint(List<Vector> vectors) {
		fingerprint(WeirConfig.getString(LOCAL_CONSISTENCY_THRESHOLD));
		for(Vector v : vectors)
			fingerprint(v.getFingerprint());

		return getFingerprint("lce");
	}

}