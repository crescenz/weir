package it.uniroma3.weir.integration;

import static it.uniroma3.hlog.HypertextualUtils.format;
import static it.uniroma3.hlog.HypertextualUtils.lazyPopup;
import static it.uniroma3.weir.Formats.percentage;
import static it.uniroma3.weir.Formats.thousandth;
import static it.uniroma3.weir.configuration.Constants.MAX_DISTANCE_THRESHOLD;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.MatchRepository.MatchBuilder;
import it.uniroma3.weir.cache.CachedComputation;
import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.structures.Pair;

/**
 * Create a <em>pyramid</em> of all the {@link Match}es
 * below the {@link MAX_DISTANCE_THRESHOLD}.
 */
public class PyramidBuilder extends CachedComputation<Experiment, MatchRepository> {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	static final private class FilteringMatchBuilder extends MatchBuilder {

		static final private long serialVersionUID = -8090059131697258219L;

		private int accepted;
		private int discarded;
		
		private double distanceThreshold;
		
		FilteringMatchBuilder(double th) {
			this.accepted  = 0;
			this.discarded = 0;
			this.distanceThreshold = th;
		}
		
		@Override
		public Match createPair(Attribute a_i, Attribute a_j) {
			final Match match = new Match(a_i, a_j);
			// that's too heavy for 10^6 matches
			final String pair = lazyPopup(Pair.toString(match), match);
			if (match.distance()<=distanceThreshold) {
				log.trace(pair, format(thousandth,match.distance()), "<em>accepted</em>");
				this.accepted++;
				return match;
			} else {
				log.trace(pair, format(thousandth,match.distance()), "<em>discarded</em>");
				this.discarded++;
				return null; // returning null means: get-rid-of-it!
			}
		}

		public double getDistanceThreshold() {
			return distanceThreshold;
		}

		public int getAccepted() {
			return this.accepted;
		}
		
		public int getDiscarded() {
			return this.discarded;
		}

	}

	private MatchRepository matches;

	
	private FilteringMatchBuilder filter;
	
	public PyramidBuilder() {
		this.filter = new FilteringMatchBuilder(WeirConfig.getDouble(MAX_DISTANCE_THRESHOLD));
	}

	@Override
	public MatchRepository uncachedComputation(Experiment experiment) {
		final Domain domain = experiment.getDomain();

		log.newPage();
		
		final String threshold = percentage.format(this.filter.getDistanceThreshold());
		log.trace("filtering matches below the distance threshold: "+threshold);

		log.newTable();
		this.matches = new MatchRepository(this.filter);
		// this repository constructor will call
		// the method {@link FilteringMatchBuilder#createPair()} above
		this.matches.addAllUnorderedPairs(domain.getAllAttributes());
		final int total = this.filter.getAccepted() + this.filter.getDiscarded();
		log.endTable();
		log.endPage("found "+this.matches.size()+" matches below the "
				+ "distance threshold="+threshold +" i.e., "
				+ "("+percentage.format((double)this.filter.getAccepted()/total)+" out of "+total+")");

		experiment.getAbstractRelation().setMatches(matches);

		return this.matches;
	}

	public MatchRepository buildPyramid(Experiment experiment) {
		return cachedComputation(experiment);
	}

	@Override
	public Fingerprint fingerprint(Experiment experiment) {
		/* we should just take into account everything ... */
		fingerprint(WeirConfig.getDouble(MAX_DISTANCE_THRESHOLD));
		fingerprint(experiment.getFingerprint());
		return getFingerprint("pyra");
	}

}
