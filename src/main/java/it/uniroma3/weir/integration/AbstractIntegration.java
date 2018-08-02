package it.uniroma3.weir.integration;


import static it.uniroma3.hlog.HypertextualUtils.*;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.integration.lc.ContainmentCorrelation;
import it.uniroma3.weir.integration.lc.Correlation;
import it.uniroma3.weir.integration.lc.LocalConsistency;
import it.uniroma3.weir.integration.lc.StrictLocalConsistency;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.model.MappingSet;
import it.uniroma3.weir.model.Website;

import java.util.List;
import java.util.Set;

/**
 * An integration algorithm specialized to the setting in which the local
 * consistency (see {@link LocalConsistency}) of the source 
 * {@link Website}s is used to decide when to stop grouping the 
 * {@link Attribute}s into {@link Mapping}s. 
 * <br/>
 * <p>
 * The integration algorithm starts with singleton {@link Mapping}s, each
 * one including a single source {@link Attribute}. 
 * It processes {@link Match}es at increasing distances merging the two
 * involved mappings into a new fresh mapping.
 * It stops to merge {@link Mapping}s as soon as it would produce a 
 * {@link Mapping} violating the local consistency of one of the sources 
 * producing the involved {@link Attribute}s, i.e., it stops as soon as 
 * the merged {@link Mapping} includes unequal {@link Attribute}s
 * from the same {@link Website}.
 * </p>
 * 
 * <p>
 * In this version, a few violations of the local consistency assumption are
 * allowed, as long as the violating {@link Attribute}s are correlated, as 
 * discovered by means of {@link Correlation#areCorrelated()}.
 * </p>
 * 
 * @see <a href="http://www.vldb.org/pvldb/vol6/p805-bronzi.pdf">
 * <i>Extraction and integration of partially overlapping web sources</i></a>.
 * <br/>
 * Mirko Bronzi, Valter Crescenzi, Paolo Merialdo, Paolo Papotti.<br/>
 * Proceedings of the VLDB Endowment VLDB.<br/>
 * Volume 6 Issue 10, August 2013 (pages 805-816).<br/>
 * <em>Section 4.1 - Listing 1.</em>
 * <p>{@link StrictLocalConsistency}, {@link Correlation}</em>.</p>
 */
public class AbstractIntegration {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();
	
	final private MappingSet mappingSet;

	final private LocalConsistency localConsistency;     // Local Consistency

	final private Correlation correlation; // Correlation
	
	public AbstractIntegration() {
		this.mappingSet  = new MappingSet();
		this.localConsistency = new StrictLocalConsistency();
		this.correlation = new ContainmentCorrelation();
	}

	public MappingSet integrate(MatchRepository repository) {
		List<Match> matches = repository.order();
		log.newPage("integration");
		
		log.newTable();
		log.trace("<I>(a,b)</I>",
				  "<I>m<SUB>a</SUB></I>",
				  "<I>m<SUB>b</SUB></I>",
				  "<I>action</I>",
				  "<I>result</I>");
		for(Match match : matches) {

			Attribute a = match.getMin();
			Attribute b = match.getMax();
			
			Mapping m_a = findOrCreateNewSingletonMapping(a);
			Mapping m_b = findOrCreateNewSingletonMapping(b);

			handlePair(match, m_a, m_b);
		}
		log.endTable();
		log.endPage();
		return this.mappingSet;
	}

	private Mapping findOrCreateNewSingletonMapping(Attribute a) {
		Mapping m_a = this.mappingSet.getMappingByAttribute(a);
		if (m_a==null) {
			m_a = new Mapping(a);
			this.mappingSet.addMapping(m_a);
		}
		return m_a;
	}

	private void handlePair(Match pair, Mapping m_a, Mapping m_b) {
		
//		log.trace(pair + " involving mappings " + m_a + " and " + m_b);
		
		if (m_a.equals(m_b)) {
			log(pair, m_a, m_b, "same","");
			return;
		}
		
		if (m_a.isComplete() || m_b.isComplete()) {
			if (!m_a.isComplete()) {
				log(pair, m_a, m_b, m_b+" complete", m_a+" completed");
				m_a.markAsComplete();
			}
			
			if (!m_b.isComplete()) {
				log(pair, m_a, m_b, m_a+" complete", m_b+" completed");
				m_b.markAsComplete();
			}
		} else if (aMergeWouldViolateLC(pair, m_a, m_b)) {
			if (!areCorrelated(pair)) {
//				log.trace("marking both mappings as complete");
				m_a.markAsComplete();
				m_b.markAsComplete();
				log(pair, m_a, m_b, "l.c.!","marked completed");
			} else {
//				log.trace("however, it is not a strict violation: "+	
//						  "the violating attributes "+pair+" are unequal but correlated");
				log(pair, m_a, m_b, "l.c.!","<i>loose</i> violation");
			}
		} else {
			this.mappingSet.removeMapping(m_a);
			this.mappingSet.removeMapping(m_b);
			
			Mapping mNew = new Mapping(m_a, m_b);
			this.mappingSet.addMapping(mNew);
			log(pair, m_a, m_b, "merge", mNew);
		}
	}

	static final private void log(Match pair, Mapping m_a, Mapping m_b, String msg, Object result) {
		log.trace(
				lazyPopup(pair),
				objectPopup(id(m_a), m_a), 
				objectPopup(id(m_b), m_b), 
				nbsp(msg), result
		);
	}

	static private String id(Mapping m_a) {
		return ( m_a.isComplete() ? "<I>"+m_a.toString()+"</I>" : m_a.toString() );
	}

//	static private String pairPopup(Match match) {
//		return lazyPopup("("+match.getMin()+","+match.getMax()+")", match);
//	}

	private boolean aMergeWouldViolateLC(Match pair, Mapping m_a, Mapping m_b) {
		if (m_a.equals(m_b)) return false;

		final Set<Website> violating = getCommonSites(m_a,m_b);
		
		if (!violating.isEmpty()) {
			//log.trace(m_a+" and "+m_b+" both have attributes from: "+violating);
			for(Website site : violating) {
				final Set<Attribute> m_a_fromSite = m_a.fromSource(site);
				final Set<Attribute> m_b_fromSite = m_b.fromSource(site);
				//log.trace("attributes coming from "+site+": "+m_a_fromSite+" and "+m_b_fromSite);
				if (aMergeWouldViolateLC(m_a_fromSite, m_b_fromSite)) 
					return true;
			}
		}
//		log.trace("merging "+m_a+" and "+m_b+" does not lead "
//				+ "to local consistency assumption violation");
		return false;
	}
	
	private Set<Website> getCommonSites(Mapping m_a, Mapping m_b) {
		final Set<Website> intersection = m_a.getSourceSites();
		intersection.retainAll(m_b.getSourceSites());
		return intersection;
	}
	
	private boolean aMergeWouldViolateLC(Set<Attribute> fromA, Set<Attribute> fromB) {
		for(Attribute a : fromA)
			for(Attribute b : fromB)
				if (this.localConsistency.violatedBy(a, b)) {
					//log.trace("local consistency is violated by the attributes "+a+" and "+b);
					return true;
				}
		return false;
	}

	private boolean areCorrelated(Match pair) {
		return ( this.correlation.areCorrelated(pair.getMin(), pair.getMax()) ) ;
	}

}
