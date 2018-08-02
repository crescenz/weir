package it.uniroma3.weir.integration.lc;

import static it.uniroma3.hlog.HypertextualUtils.lazyPopup;
import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import static it.uniroma3.weir.Formats.thousandth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.hlog.Logpage;
import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.model.MappingSet;
import it.uniroma3.weir.model.Website;
/**
 * Solve <em>loose</em> conflicts remaining in the {@link Mapping}s: split all
 * {@link Mapping}s containing multiple unequal  {@link Attribute}s from the
 * same {@link Website}.
 * <p>
 * A {@link Mapping} satisfies the
 * <ul><li><em>strong</em> local consistency assumption: if it does not 
 * 		   contain two {@link Attribute}s from the same {@link Website}
 *     <li><em>loose</em> local consistency assumption: if it does contain
 *         only correlated {@link Attribute}s from the same {@link Website}
 * </ul>
 * The latter can be reduced to semantically homogeneous {@link Mapping}s 
 * either by spreading {@link Attribute}s from the same {@link Website} into
 * separate singleton {@link Mapping}s, and then rearranging other 
 * {@link Attribute}s around their closest mapping; or by deleting all
 * conflicting {@link Attribute}s but one, choosing that better matching with
 * the others.
 * </p>
 * <p>
 * This class implements the latter solution, since the former could
 * erroneously distribute semantically equivalent {@link Attribute}s from 
 * distinct {@link Website}s into separate {@link Mapping}s only because they 
 * are less similar to each other than the conflicting (unequal and correlated)
 * {@link Attribute}s.
 * </p>
 * @see {@link LocalConsistencyEnforcer}, {@link StrictLocalConsistency},
 * 		{@link Correlation}.
 */
public class LooseLocalConsistencySolver {
	
	static final private HypertextualLogger log = HypertextualLogger.getLogger();
	
	final private MatchRepository matches;
	
	public LooseLocalConsistencySolver(MatchRepository matches) {
		this.matches = matches;
	}

	public MappingSet solveConflicts(MappingSet set) {
		MappingSet result = new MappingSet();
		//final MatchRepository matches = exp.getDomain().getMatches();
		log.trace("resolving loose conflicts in output mappings");
		log.newTable();
		log.trace("<i>mapping</i>","<i>l.c. violation</i>","<i>decomposition</i>");
		for (final Mapping mapping : set) {
			log.newPage();
			final Mapping solved = this.solveConflicts(mapping);
			result.addMapping(solved);
			final Logpage logpage = log.endPage();
			if (solved!=mapping) {
				log.trace( mapping.toString(), linkTo(logpage).withAnchor("yes"), lazyPopup(result) );
			} else {
				log.trace( mapping.toString(), "no" );
			}
		}
		log.endTable();
		return result;
	}
	
	public Mapping solveConflicts(Mapping mapping) {// -> AbstractRelation?
		final Set<Attribute> conflicting = mapping.getConflictingAttributes();	
		
		if (conflicting.isEmpty()) return mapping;
		
		log.trace("these attributes "+conflicting+" violates the l.c.a.");
		
		final Set<Attribute> survivors = new HashSet<>(mapping.getAttributes());		
		while (!conflicting.isEmpty()) {			
			final Map<Attribute, Double> map = getDistanceMap(matches, conflicting, survivors);
			final Attribute farthest = getFarthest(map);
			final Double farthestDist = map.get(farthest);
			log.trace("attribute " + farthest + " is the farthest away: " + thousandth.format(farthestDist) );
			
			final Attribute better = findBest(map, farthest);
			
			if (better!=null) {
				final Double betterDist = map.get(better);
				log.trace(better + " is better: " + thousandth.format(betterDist));
				log.trace("removing " + farthest);
				conflicting.remove(farthest);
				survivors.remove(farthest);
			} else {
				log.trace("all attributes from site " + farthest.getWebsite()
						+ " at the same distance: removing all but one attribute");
				final Set<Attribute> fromSameSource = mapping.fromSource(farthest.getWebsite());
				conflicting.removeAll(fromSameSource);
				survivors.removeAll(fromSameSource);
				survivors.add(farthest);
			}
		}
		
		final Mapping result = new Mapping(survivors);
		log.trace(result);
		return result;
	}

	private Map<Attribute, Double> getDistanceMap(MatchRepository matches, Set<Attribute> conflicting, Set<Attribute> nonConflicting) {
		Map<Attribute, Double> distanceMap = new HashMap<>();
		for (Attribute c : conflicting) {
			double distance = 0.0;
			for (Attribute nc : nonConflicting) {
				if (/*!c.equals(nc) && */!c.sameWebsiteAs(nc)) {
					double d = matches.getDistance(c,nc);
					distance +=  d > 1.0 ? 1000 : d; /* VC: ??? */
				}
			}
			distanceMap.put(c, distance);
		}
		return distanceMap;
	}
	
	private Attribute getFarthest(Map<Attribute, Double> map) {
		Attribute farthest = null;
		double farthestDistance = -1;
		for (Attribute a : map.keySet()) {
			double d = map.get(a);
			if (d>farthestDistance) {
				farthestDistance = d;
				farthest = a;
			}
		}
		return farthest;
	}

	private Attribute findBest(Map<Attribute, Double> map, Attribute farthest) {
		double farthestDistance = map.get(farthest);
		for (Attribute a : map.keySet()) {
			double distance = map.get(a);
			if (!a.equals(farthest) && a.sameWebsiteAs(farthest) && distance<farthestDistance) {
				return a;
			}
		}
		return null;
	}

}
