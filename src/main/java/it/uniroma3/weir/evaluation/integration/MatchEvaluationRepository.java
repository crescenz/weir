package it.uniroma3.weir.evaluation.integration;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.model.Attribute;

import java.util.Collections;
import java.util.LinkedList;

public class MatchEvaluationRepository extends MatchRepository {

	static final private long serialVersionUID = 4257536423546302916L;
	
	static final private HypertextualLogger log = HypertextualLogger.getLogger();
	
	static final private class MatchEvaluationBuilder extends MatchBuilder {

		static final private long serialVersionUID = -6436586389878830190L;

		final private MatchEvaluator matchEvaluator;
		
		public MatchEvaluationBuilder(MatchEvaluator mode) {
			this.matchEvaluator = mode; 
		}

		@Override
		public Match createPair(Attribute a_i, Attribute a_j) {
			// produce matches only between attributes from the same site
			if (a_i.sameWebsiteAs(a_j) ) {
				final double score = getScore(a_i,a_j);
				if (score>0)
					return new Match(a_i,a_j, score) ; 
			}
			return null;
		}

		private double getScore(Attribute golden, Attribute output) {
			// we assume the attributes from the same website: no linkage needed
			return this.matchEvaluator.score(golden,output);
			// N.B. evaluation score / distance used during the algorithm 
			//      not necessarily the same as that used during the evaluation
		}
		
	}

	public MatchEvaluationRepository(MatchEvaluator mode) {
		super(new MatchEvaluationBuilder(mode));
	}

	public Match findBestMatching(final Attribute golden) {
		final LinkedList<Match> fromSamesite = new LinkedList<>(this.getPairs(golden));
		if (fromSamesite.isEmpty()) {
			log.trace("none attribute (with score>0) as "+golden+" from "+golden.getWebsite());
			return null;
		}
		Collections.sort(fromSamesite); // CHECK use matchEvaluator to order here
		final Match best = fromSamesite.getFirst();
		// N.B. if the l.c.a. holds, at most one attribute from each site
		if (fromSamesite.size()>1) {
			log.warn("multiple same-site attributes as "+golden	+" - keep the best");
			fromSamesite.remove(best);
			this.removeAll(fromSamesite);/* side-effects here */
		}
		// usually we got just one match
		return best;
	}

}
