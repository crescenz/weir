package it.uniroma3.weir;

import static it.uniroma3.hlog.HypertextualUtils.format;
import static it.uniroma3.weir.Formats.thousandth;
import static it.uniroma3.weir.configuration.Constants.GOOD_MATCH_THRESHOLD;
import static it.uniroma3.weir.configuration.Constants.PERFECT_MATCH_THRESHOLD;
import static it.uniroma3.weir.model.Attribute.COMPARATOR_BY_SITE;
import static java.util.Collections.sort;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.evaluation.integration.MatchEvaluator;
import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.model.MappingSet;

import java.util.*;
/**
 * Integrate output {@link Attribute}s after extraction by using golden 
 * {@link Mapping}s before attempting any serious integration algorithm
 * and without considering any constraint in the produced mappings. 
 * <BR/>
 * It works by setting two thresholds over the distance of
 * <em>perfect</em> matches and of <em>good</em> matches:
 * {@link PERFECT_MATCH_THRESHOLD} and  {@link GOOD_MATCH_THRESHOLD}.
 * Then it classifies the matches as:
 * <em>perfect</em> (d<=0.001); <em>good</em> (d<=0.1); <em>bad</em> (d>0.1).
 * <BR/>
 * Only <i>good</i> and <i>perfect</i> matches are considered to
 * aggregate {@link Attribute}s into {@link Mappings} to form the
 * resulting {@link MappingSet}.
 * <BR/> 
 * It sets an upper-bound on the best possible integration 
 * algorithm results given the input attributes produced by
 * the <em>extraction</em> phase.
 * 
 * <BR/> N.B. FIXME non usa lo stesso {@link MatchEvaluator} della valutazione successiva.
 */
public class OracleIntegrator {
	
	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	final private double goodThreshold;
	final private double perfThreshold;
	
	private int perfCounter;
	private int goodCounter;
	private int badCounter;
	
	private List<Attribute> withoutTarget;
	
	final private Experiment experiment;
	
	public OracleIntegrator(Experiment exp) {
		this.experiment = exp;
		// distance threshold for perfect matching
		this.perfThreshold = WeirConfig.getDouble(PERFECT_MATCH_THRESHOLD);
		// distance threshold for semi-perfect matching
		this.goodThreshold = WeirConfig.getDouble(GOOD_MATCH_THRESHOLD);
		//
		this.perfCounter = 0; // total number of perfect matches
		this.goodCounter = 0; // total number of good matches
		this.badCounter  = 0; // total number of bad matches
		//
		this.withoutTarget = new LinkedList<>();
	}

	public MappingSet integrateJustExtracted() {
		final MappingSet goldenMappingSet = this.experiment.getGoldenMappingSet();
		log.newPage("choosing the target attributes");
		log.trace("targets are the extracted attributes better matching with golden");
		log.trace("<i>perfect</i> match - distance threshold: "+perfThreshold);
		log.trace("<i>good</i> match - distance threshold: "+goodThreshold);
		final MappingSet targetMappingSet = new MappingSet();

		log.trace("matching output attributes with golden mappings");
		log.trace();
		log.trace("<hr/>");
		log.trace();
		for (Mapping golden : goldenMappingSet.getMappingsOrderedByLabel()) {
			log.trace("golden mapping: "+golden.getLabel());
			log.trace(golden);
			log.newPage(golden.getLabel()+" matching details");
			final Mapping targetMapping = findTargetMapping(golden);
			targetMappingSet.addMapping(targetMapping);
			log.endPage();
			if (targetMapping.isEmpty()) log.trace("<em>none matching mapping</em>");
			else {
				log.trace("target mapping:");
				log.trace(targetMapping);
			}
			log.trace();
			log.trace("<hr/>");
			log.trace();
		}
		
		log.endPage();
		logSummary();
		log.trace("<b>TODO log PRF of Oracle's Target Mappings as upper-bound</b>");// TODO
		return targetMappingSet;
	}

	//TODO refactor by using repositories
	private Mapping findTargetMapping(final Mapping goldenMapping) {
		final Mapping matching = new Mapping();
		for (Attribute golden : goldenMapping.getAttributesOrderedBySite()) {
			log.trace("choosing a target attribute to associate with "
					 + golden.getFirstLabel()+"'s golden attribute "
					 +" " + golden + " from "+golden.getWebsite());
			/* it processes the output attributes grouped by golden's site */
			final List<Attribute> output = golden.getWebsite().getAttributes();// FIXME ???
			final Match bestMatch = golden.findClosest(output);			
			if (bestMatch!=null) {
				final double distance = bestMatch.distance();
				final Attribute best = bestMatch.getMate(golden);
				if (distance<=perfThreshold) {
					perfCounter++;
					matching.add(best);
					/* mark it as a target attribute */
					best.setAsTarget(bestMatch);
					log.trace("has a perfect match with "+best);
				} else if (distance<=goodThreshold) {
					goodCounter++;
					matching.add(best);
					/* mark it as a target attribute */
					best.setAsTarget(bestMatch);
					log.trace("has only a good match with "+best);
				} else {
					badCounter++;
					log.trace("<em>doesn't match</em> with an extracted attribute.");
					this.withoutTarget.add(golden);
				}
				log.trace("the best match is with " + best + " at d=" + format(thousandth,distance));
				if (best.isTarget()) {
					log.trace("the extracted attribute "+best+" is chosen as a <em>target</em> attribute");
				}
				log.trace(bestMatch);
			} else { log.warn("...? No candidate match for "+golden); };
			log.trace("<HR>");
		}
		return matching;
	}
	
	public void logSummary() {
		final int totCounter =  perfCounter + goodCounter + badCounter;
		log.trace("Out of "+totCounter+" golden attributes...");
		logMatchCounter(perfCounter, "a perfect");
		logMatchCounter(goodCounter, "a good");
		logMatchCounter( badCounter, "not a");
		log.trace("... match with one of the extracted attributes");
		if (badCounter>0 || goodCounter>0) {
			log.trace("<em>not all</em> wrappers are complete");
			if (badCounter>0) {
				log.trace("these golden attributes do not match any extracted attribute:");
				sort(withoutTarget, COMPARATOR_BY_SITE);
				log.trace(withoutTarget);
			}
		}
		else
			log.trace("all wrappers are complete");
	}

	private void logMatchCounter(int count, String msg) {
		log.trace(" ..."+count + " have <i>"+msg+ "</i>... ");
	}
	
}
