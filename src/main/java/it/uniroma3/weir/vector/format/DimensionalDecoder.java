package it.uniroma3.weir.vector.format;

import static it.uniroma3.weir.vector.format.Decoders.NUMBER_DECODER;
import static it.uniroma3.weir.vector.format.FormatRule.letterRule;
import static it.uniroma3.weir.vector.format.FormatRule.numberRule;
import it.uniroma3.weir.vector.unitmeasure.UnitMeasure;
import it.uniroma3.weir.vector.unitmeasure.UnitMeasureGroup;
import it.uniroma3.weir.vector.value.Dimensional;
import it.uniroma3.weir.vector.value.Number;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DimensionalDecoder implements TypeDecoder {

	private UnitMeasureGroup unitMeasureGroup;

	private String referenceMarker;
	
	public DimensionalDecoder(UnitMeasureGroup umg) {
		this.unitMeasureGroup = umg;
		this.referenceMarker  = umg.getReferenceUnit().getUnitMarkers()[0];
	}
	
	@Override
	public Dimensional decode(String value) {
		/* view a value candidate dimensional as set of numbers and words */
		final LinkedList<String> numbs = numberRule.extractAll(value);
		final LinkedList<String> words = letterRule.extractAll(value); // was: LETTER_SYMBOLS

		/* check for degenerate cases, i.e., no number, too many/few words */
		if (numbs.isEmpty() && IsThereAunitMarker(words))
			return new Dimensional(0, referenceMarker);

		if (numbs.isEmpty() || numbs.size()!=words.size())
			return null;

		/* at least one number and the same number of words */
		/* ??? */
/*		if (value.contains("/")) { //VC: overfitting su NBAPlayer qui?
			final Double casted = findUnitMeasureAndCast(numbers.getFirst(), words.getFirst());
			if (casted!=null) {
				return new Dimensional(casted, referenceMarker);
			}
			return null;
		}*/

		/* - cumulative - e.g., 8kg 3gr ; oppure 3" 2'; */
		return cumulateDimensionals(numbs, words);
	}

	private Dimensional cumulateDimensionals(final List<String> numbers, final List<String> words) {
		/* process PCDATA by accumulating multiple dimensional numbers if needed 
		 * e.g., '8kg 3gr.' */
		final Iterator<String> nIt = numbers.iterator(); // these are two lists...
		final Iterator<String> wIt = words.iterator();   // ...of the same length
		double result = 0d;
		int counter = 0;
		while (nIt.hasNext()) {
			final String n = nIt.next();
			final String w = wIt.next();
			
			final Double casted = findUnitMeasureAndCast(n, w);
			if (casted!=null) { 
				result += casted;
				counter++;
			} else break; // otherwise, to get something anyway: return null;
		}
		if (counter>0) {
			return new Dimensional(result, referenceMarker);
		}
		return null;
	}
	
	private boolean IsThereAunitMarker(List<String> words) {
		for (String word : words) {
			if (this.unitMeasureGroup.findUnitMarker(word)!=null)
				return true;
		}
		return false;
	}

	private Double findUnitMeasureAndCast(String number, String word) {
		final UnitMeasure unit = this.unitMeasureGroup.findUnitMarker(word);
		if (unit!=null) {
			final Number n = (Number) NUMBER_DECODER.decode(number);
			return n.getValue() * unit.getRatio();
		} else return null;
	}
	
}
