package it.uniroma3.weir.dscd;

import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.MatchRepository.MatchBuilder;
import it.uniroma3.weir.MatchRepository.MatchWeighter;
import it.uniroma3.weir.integration.AttributePair;
import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.integration.WeakRuleRemoval;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.structures.PairRepositories;
import it.uniroma3.weir.model.Experiment;

/**
 * An enumeration of possibile strategies to create
 * candidate matches for DSCDAlgorithm.
 *
 */
public enum DSCDMatches {
	/**
	 * All matches.
	 */
	ALL {

		@Override
		public MatchRepository createMatches(Website w1, Website w2) {
			final MatchRepository repository = new MatchRepository();
			repository.addReflexiveCartesianProduct(w1.getAttributes(), w2.getAttributes());
			return repository;
		}

	},
	/**
	 * All matches cleaned by weak-rules removal procedure
	 */
	ALL_WRR {

		@Override
		public MatchRepository createMatches(Website w1, Website w2) {
			final MatchRepository matches = ALL.createMatches(w1, w2);			
			new WeakRuleRemoval(matches).removeWeakRules();
			return matches;
		}

	},
	/**
	 * Top-k matches after weak-rule removal
	 */
	TOP_K_WRR {

		static final private int TOP_K_MATCHES = 15;

		@Override
		public MatchRepository createMatches(Website w1, Website w2) {
			final MatchRepository matches = new MatchRepository();

			PairRepositories.addTopKCartesianProduct(TOP_K_MATCHES, matches, 
					new MatchBuilder(),
					new MatchWeighter(),
					w1.getAttributes(), w2.getAttributes());

			new WeakRuleRemoval(matches).removeWeakRules();
			return matches;
		}

	},
	/**
	 * Matches weighted on the top-k linkages
	 */
	TOP_K_LINKED {

		static final private int TOP_K = 40;

		@Override
		public MatchRepository createMatches(Website w1, Website w2) {
			DomainLinkage linkages = Experiment.getInstance().getLinkages();
			final WebsiteLinkage topK = linkages.get(w1, w2).top(TOP_K);

			MatchRepository matches = new MatchRepository(new MatchBuilder() {

				static final private long serialVersionUID = -7554391770660559245L;

				@Override
				public Match createPair(Attribute a1, Attribute a2) {
					final double distance = new AttributePair(a1,a2).distance(topK);
					return new Match(a1,a2,distance);
				}
			});

			matches.addCartesianProduct(w1.getAttributes(), w2.getAttributes());

			return matches;
		}
	};
	
	/* TODO GOLDEN */

	abstract public MatchRepository createMatches(Website w1, Website w2);

}
