package it.uniroma3.weir.evaluation.integration;

import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.structures.Pair;
import it.uniroma3.weir.structures.PairRepository.PairWeighter;
import it.uniroma3.weir.vector.value.Value;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Alternative evaluation modes for scoring {@link Match}es.
 * <br/>
 * The scores over a set of {@link Match}es are aggregated 
 * by {@link IntegrationEvaluator}.
 * 
 */
public enum MatchEvaluator implements PairWeighter<Match, Attribute> {
	
	/**
	 * Percentage of perfectly equals values between two {@link Attribute}s
	 * by ignoring the case of their values and trimming them.
	 * N.B.: Currently it is measured over the overlapping portion.
	 */
	EQUALITY {
		@Override
		public double score(Attribute golden, Attribute output) {
			final Website website = golden.getWebsite();
			if (!website.equals(output.getWebsite()))
				throw new IllegalArgumentException(this+" can only compare attributes from the same site");
			int equalityCounter = 0;
			// N.B. since golden and output are from the same
			// site no likage is needed but we currently restrict
			// the evaluation to the overlap!?
			final List<Webpage> pages  = website.getOverlappingPages();// ... .getWebpages();
			final Iterator<Webpage> it = pages.iterator();
					
			while (it.hasNext()) {
				final Webpage page = it.next();
				final Value g = golden.get(page);
				final Value v = output.get(page);
				if (Objects.equals(g.toLowerCase(), v.toLowerCase()))
					equalityCounter++;
			}
			return (double)equalityCounter / pages.size();
		}

	},
	
	/**
	 * Average <em>1-distance</em> between two {@link Attribute}s
	 */
	DISTANCE {
		@Override
		public double score(Attribute golden, Attribute output) {
			return ( 1 - golden.distance(output) );		
		}
	};

	
	/* DOM_NODE_EQUALITY */
	/**
	 * 
	 * @param golden A golden {@link Attribute} of correct values to extract
	 * @param output An output {@link Attribute} of extracted values
	 * @return a basic pair-wise score
	 */
	public abstract double score(Attribute golden, Attribute output);
	
	public double score(Pair<Attribute> pair) {
		return score(pair.getMin(),pair.getMax());
	}
	
	@Override
	public double weight(Match pair) {
		return score(pair.getMin(), pair.getMax());
	}
	
	@Override
	public double weight(Attribute a, Attribute b) 
	{ throw new UnsupportedOperationException(); }

}