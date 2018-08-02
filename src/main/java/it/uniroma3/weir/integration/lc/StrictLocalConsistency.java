package it.uniroma3.weir.integration.lc;

import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.model.hiddenrelation.AbstractAttribute;

/**
 * <p>A {@link Website} cannot publish twice the same source {@link Attribute}
 * with different values. It is assumed to be <em>locally consistent</em>, 
 * i.e., it publishes each {@link AbstractAttribute} ( was: <em>conceptual</em> )
 * just once (possibly in the form of many <em>identical</em> source 
 * {@link Attribute}s).
 * </p>
 * 
 * <p>This class models the <em>strict</em> semantics of the assumption:
 * if two {@link Attribute}s from the same source are equal ignoring their null
 * values, then they do violate the assumption.
 * </p>
 * 
 * @see <a href="http://www.vldb.org/pvldb/vol6/p805-bronzi.pdf">
 * <i>Extraction and integration of partially overlapping web sources</i></a>.
 * <br/>
 * Mirko Bronzi, Valter Crescenzi, Paolo Merialdo, Paolo Papotti.<br/>
 * Proceedings of the VLDB Endowment VLDB.<br/>
 * Volume 6 Issue 10, August 2013 (pages 805-816).<br/>
 * <em>Section 2.</em>
 * 
 */
public class StrictLocalConsistency implements LocalConsistency {
	
	/**
	 * This method sets the basic semantic of the <em>strict</em> local
	 * consistency assumption.
	 * @param a first attribute
	 * @param b second attribute 
	 * @return true iff a {@link Mapping} with the two given {@link Attribute}s
	 *         would violate the <em>local consistency assumption</em>
	 * @see {@link LooseLocalConsistency}
	 * 		as for the <em>loose</em> local consistency assumption.
	 * 
	 */
	@Override
	public boolean violatedBy(Attribute a, Attribute b) {
		if (a.equals(b)) return false; // a=b does not count!
		return a.sameWebsiteAs(b);     // not from same source
		
//		// if ignoring their nulls they're equal they cannot be separated
//		return ( a.getVector().equalsIgnoringNulls(b.getVector()) ) ;
	}
	
}
