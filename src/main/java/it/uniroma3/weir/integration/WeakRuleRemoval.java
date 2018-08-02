package it.uniroma3.weir.integration;

import static it.uniroma3.hlog.HypertextualUtils.format;
import static it.uniroma3.hlog.HypertextualUtils.lazyPopup;
import static it.uniroma3.weir.Formats.percentage;
import static it.uniroma3.weir.Formats.ten_thousandth;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.log.MatchListRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
/**
 * Remove the <em>weak</em>  {@link ExtractionRule}s, as follows:
 * <ul>
 * <li> <b>for each</b> pair of rules ordered by descending distance:</li>
 * <ol> <b>if</b> no rule in the pair <i>overlaps</i> with a rule already marked as <i>correct</i><br/>
 *     <ol><b>then</b> mark both as correct<br/>
 *         <b>else</b> delete the pair<br/>
 *     </ol>
 *  </ol>
 *  </ul>
 * 
 */
public class WeakRuleRemoval {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	private MatchRepository matches;
	
	private Set<Attribute> markedAsCorrect; // rules currently marked as correct
		
	// attribute marked as correct and involved in the last overlap detected
	private Match overlap;  
	
	public WeakRuleRemoval(MatchRepository matches) {
		this.matches = matches;
		this.markedAsCorrect = new TreeSet<>();
		this.overlap = null;
	}
	
	private boolean isMarkedAsCorrect(Attribute a) {
		return this.markedAsCorrect.contains(a);
	}
	
	private void markAsCorrect(Attribute a) {
		this.markedAsCorrect.add(a);
		a.markAsCorrect();
	}
	
	private Set<Attribute> getMarkedAsCorrect() {
		return this.markedAsCorrect;
	}
	
	public List<Match> removeWeakRules() {
		log.newPage("weak-rules removal");
		
		final List<Match> ordered = matches.order();
		log.trace("weak-rules removal processing");
		log.trace("number of pairs to process: " + ordered.size());
		
		log.newTable();
		log.trace("<i>d</i>","<i>pair</i>","<i>overlap</i>","");
		for (Match match : ordered) {

			final Attribute a = match.getMin();
			final Attribute b = match.getMax();
			if (!existsOverlappingCorrectRule(match)) {
				markAsCorrect(a);   // First mark, then log..
				markAsCorrect(b);
				log(match, false);  /* do NOT overlap */
			} else
				log(match, true);   /* do     overlap */
		}
		log.endTable();
		log.endPage();
		
		final List<Match> correct = saveWeakRulesFreeMatches(ordered);
				
		final double removed = 1 - (double)correct.size() / ordered.size();
		log.newPage("remaining matches: " + correct.size() + 
				  " (" + format(percentage,removed) + " removed)");
		log.trace(new MatchListRenderer().toHTMLstring(correct));
		log.endPage();
		return correct;
	}

	private void log(Match match, boolean overlap) {
		/* relog list of correct rules only if it has changed */
		String logMsg = null;
		if (overlap) {	// FIXME per non dover fare un AttributePairRenderer sopra		
			logMsg = lazyPopup("overlap detected " + this.overlap.toString(), this.overlap);
		} else {
			logMsg = lazyPopup("correct rules", this.markedAsCorrectList());
		}
		log.trace(format(ten_thousandth,match.distance()), pairPopup(match), overlap ? "yes" : "no", logMsg);
	}

	static private String pairPopup(Match match) {
		return lazyPopup("("+match.getMin()+","+match.getMax()+")", match);
	}

	private String markedAsCorrectList() {
		final StringBuilder result = new StringBuilder();
		for(Attribute a : getMarkedAsCorrect()) {
			result.append(lazyPopup(a.getId(), a));
			result.append(" ");
		}
		return result.toString();
	}

	private boolean existsOverlappingCorrectRule(Match match) {
		final Attribute a = match.getMin();
		final Attribute b = match.getMax();
		return existsOverlappingCorrectRule(a) || existsOverlappingCorrectRule(b);
	}

	private boolean existsOverlappingCorrectRule(Attribute a) {
		/* since it's already been marked as correct, 
		 * its distance is smaller than the match's    */
		for (Attribute correct : getMarkedAsCorrect()) {
			if (correct.overlap(a)) {
				this.overlap = new Match(a,correct); // FIXME per non dover fare un AttributePairRenderer sopra
				return true;
			}			
		}
		this.overlap = null;
		return false;
	}

	@SuppressWarnings("unused")
	private boolean noTransitivityAssumedExistsCorrectOverlapping(Attribute a, Match match) {
		for (Attribute correct : getMarkedAsCorrect()) {
			if (a.overlap(correct) && otherCorrectAnotherDistance(a, correct, match)) {//???
				return true;
			}
		}
		return false;
	}

	private boolean otherCorrectAnotherDistance(Attribute a, Attribute overlapping, Match m) {
		final double dAB = m.distance();
		for (Attribute correct : getMarkedAsCorrect()) {
			if (correct.equals(overlapping) || correct.equals(a)) continue;

			final Double dCO = this.matches.getDistance(correct, overlapping);
			final Double dCA = this.matches.getDistance(correct, a);
			// N.B. Forse non vale la transitività per la nostra distanza normalizzata!
			if (dCO <= dAB && dCO < dCA && dCA <= dAB) { // => dCO < dAB !!! per la carenza di transitività???
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the list of {@link Match}, composed of rules both marked
	 *         marked as correct, i.e., not including any weak-rule
	 */
	private List<Match> saveWeakRulesFreeMatches(final List<Match> ordered) {
		final List<Match> correct = new ArrayList<>();	
		for (Match match : ordered) {
			final Attribute a = match.getMin();
			final Attribute b = match.getMax();
			if (!a.sameWebsiteAs(b) && isWeakRuleFreePair(a, b)) {
				correct.add(match);
			} else {
				this.matches.remove(match);
			}
		}
		return correct;
	}
	
	private boolean isWeakRuleFreePair(final Attribute a, final Attribute b) {
		return isMarkedAsCorrect(a) && isMarkedAsCorrect(b);
	}

}
