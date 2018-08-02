package it.uniroma3.weir.integration.lc;

import it.uniroma3.weir.integration.AbstractIntegration;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.model.hiddenrelation.AbstractAttribute;
/**
 * <p>The <em>local consistency assumption</em> is defined as follows:
 * 
 * A {@link Website} cannot publish twice the same source {@link Attribute}
 * with different values. It is assumed to be <em>locally consistent</em>, 
 * i.e., it publishes each {@link AbstractAttribute} (was: <em>conceptual</em> )
 * just once (possibly in the form of many <em>identical</em> source 
 * {@link Attribute}s).
 * </p>
 * 
 * <p>This interface models the condition originally proposed to detect whether
 * two distinct source {@link Attribute}s should be associated with the same
 * {@link AbstractAttribute}.
 * </p>
 * 
 * <p>In a new, and <i>relaxed</i> model, the {@link Website}s can publish 
 * several unequal and distinct source {@link Attribute}s related to the same
 * {@link AbstractAttribute} as long as they are correlated: for example all 
 * the values of an attribute contains the corresponding values of the other
 * one.
 * </p>
 * 
 * <p>
 * This assumption is fundamental for the integration algorithm that refrains
 * from merging {@link Mapping}s which otherwise would violate it: without
 * this assumption, the integration algorithm could not decide when to stop
 * the mergings. (See {@link AbstractIntegration} for the detail of the 
 * integration algorithm).
 * </p> 
 * 
 * <HR/>
 * @see <a href="http://www.vldb.org/pvldb/vol6/p805-bronzi.pdf">
 * <i>Extraction and integration of partially overlapping web sources</i></a>.
 * <br/>
 * Mirko Bronzi, Valter Crescenzi, Paolo Merialdo, Paolo Papotti.<br/>
 * Proceedings of the VLDB Endowment VLDB.<br/>
 * Volume 6 Issue 10, August 2013 (pages 805-816).<br/>
 * <em>Section 2.</em>
 * <p>{@link StrictLocalConsistency}, {@link Correlation} implements
 *      the logics need to deal with the <em>local consistency assumption</em>.
 *    {@link AbstractIntegration} implements the integration algorithm based
 *    on this assumption.
 * </p>
 * 
 */
public interface LocalConsistency {
	
	/**
	 * This method sets the basic semantic of the local
	 * consistency assumption.
	 * @param a first attribute
	 * @param b second attribute 
	 * @return true iff a {@link Mapping} with the two given {@link Attribute}s
	 *         would violate the <em>local consistency assumption</em>
	 */
	public boolean violatedBy(Attribute a, Attribute b);
	
}
