package it.uniroma3.weir.extraction;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static it.uniroma3.weir.model.log.WeirStyles.header;
import static it.uniroma3.weir.model.log.WeirStyles.nullStyled;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.Label;
import static java.lang.Math.min;
/**
 *
 * Choose {@link Label}s for a collection of {@link ExtractedVector}:
 * based on an heuristics that looks for textual template nodes
 * working as pivots within the {@link ExtractionRule}.
 *
 * @see <a href="http://www.vldb.org/pvldb/vol6/p805-bronzi.pdf">
 * <i>Extraction and integration of partially overlapping web sources</i></a>.
 * <br/>
 * Mirko Bronzi, Valter Crescenzi, Paolo Merialdo, Paolo Papotti.<br/>
 * Proceedings of the VLDB Endowment VLDB.<br/>
 * Volume 6 Issue 10, August 2013 (pages 805-816).<br/>
 * <em>Section 5.3</em>
 *
 */
public class Labeler {

	private static final int NUMBER_OF_BEST_LABELS = 15;

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	static final private String CONTAINS_KEYWORD = "contains";

	static final private char   XPATH_STEP       = '/';

	final private SortedSet<Label> labels;
	
	public Labeler() {
		this.labels = new TreeSet<>();
	}
	
	public SortedSet<Label> getLabels() {
		return labels;
	}

	public List<Label> getBestLabels(int n) {
		final int size = this.getLabels().size();
		return new ArrayList<>(this.getLabels()).subList(0, min(size,n));
	}

	public SortedSet<Label> label(List<ExtractedVector> vectors) {
		log.newTable();
		log.trace(header("label"), header("distance"), header("extracted vector"));
		for (final ExtractedVector extracted : vectors) {
			final Label label = label(extracted);
			if (label!=null)
				this.labels.add(label);
		}
		log.endTable();
		log.trace("<hr/>");
		final int n = Math.min(NUMBER_OF_BEST_LABELS,this.getLabels().size());
		final List<Label> bestLabels = this.getBestLabels(n);
		log.trace( n + " best labels found:");
		log.trace(bestLabels);
		return this.labels;
	}

	public Label label(ExtractedVector vector) {
		final String xpath = vector.getExtractionRule().getXPath();
		final String meaningfulLabel = this.findPivotingLabel(xpath);
		Label label = null;
		if (meaningfulLabel!=null) {
			final int d = this.pivot2ValueDistance(xpath);
			label = new Label(meaningfulLabel, d);
			vector.addLabel(label);
			log.trace("@"+meaningfulLabel, d, vector);
		} else log.trace(nullStyled("None"), nullStyled(" "), vector);
		return label;
	}

	private String findPivotingLabel(String xpath) {
		if (!xpath.contains(CONTAINS_KEYWORD)) return null;
		final String label = this.locatePivotInvariant(xpath);
		return this.sanitize(label);
	}

	private String locatePivotInvariant(String xpath) {
		String label;
		try {
			label = xpath.split("text\\(\\),")[1].split("\\)\\]")[0].toLowerCase();
		} catch (final ArrayIndexOutOfBoundsException e) {
			label = xpath.split(CONTAINS_KEYWORD + "\\(\\.,")[1].split("\\)\\]")[0].toLowerCase();
		}
		return label;
	}

	private String sanitize(String label) {
		return label.replaceAll("^\\W*", "").replaceAll("\\W*$", "");
	}

	private int pivot2ValueDistance(String xpath) {
		return ( !xpath.contains(CONTAINS_KEYWORD) ? 0 : this.countXPathSteps(xpath)-3 );
	}

	private int countXPathSteps(String xpath) {
		int fromIndex = 0;
		int counter = 0;
		do {
			fromIndex = xpath.indexOf(XPATH_STEP, fromIndex);
			counter++;
		} while (fromIndex++ !=-1) ;

		return counter;
	}

}
