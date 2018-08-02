package it.uniroma3.weir.integration.lc;

import it.uniroma3.weir.integration.AbstractIntegration;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.hiddenrelation.AbstractAttribute;

/**
 * This interface models the algorithm to detect whether two
 * given {@link Attribute}s, from the same {@link Website} are 
 * correlated, and  can be considered as two unequal versions
 * of the same {@link AbstractAttribute}.
 * 
 * @see {@link LocalConsistency}, {@link AbstractIntegration}
 */
public interface Correlation {

	public boolean areCorrelated(Attribute a, Attribute b);
	
}
