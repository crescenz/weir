package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.vector.format.FormatRule.letterRule;
import static it.uniroma3.weir.vector.format.FormatRule.numberRule;
import static it.uniroma3.weir.vector.value.ValueDistances.DIMENSIONAL_DISTANCE;
import it.uniroma3.weir.vector.unitmeasure.UnitMeasure;
import it.uniroma3.weir.vector.unitmeasure.UnitMeasureGroup;
import it.uniroma3.weir.vector.value.Dimensional;
import it.uniroma3.weir.vector.value.Number;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/**
 * 
 * A dimensional {@link Type}, i.e., a number associated with an
 * {@link UnitMeasure}.
 * <BR/>
 * Example of dimensional types are the subclasses:
 * ({@link MassType}, or {@link SpaceType}, or {@link MoneyType}).
 *
 */
public class DimensionalType extends NumberType {

	static final private long serialVersionUID = -1096006719652280759L;

	private UnitMeasureGroup unitMeasureGroup;

	private String referenceMarker;

	public DimensionalType() {}

	public DimensionalType(UnitMeasureGroup umg) {
		this.unitMeasureGroup = umg;
		this.referenceMarker = this.unitMeasureGroup.getReferenceUnit().getUnitMarkers()[0];
	}
	
	public UnitMeasureGroup getUnitMeasureGroup() {
		return this.unitMeasureGroup;
	}
	
	public String getReferenceMarker() {
		return this.referenceMarker;
	}

	@Override
	public Dimensional tryCastNonNull(String value) {
		/* view a candidate dimensional value as set of numbers and words  */
		final LinkedList<String> numbers = numberRule.extractAll(value);
		final LinkedList<String> words   = letterRule.extractAll(value); // was: LETTER_SYMBOLS

		/* check for degenerate cases, i.e., no number, too many/few words */
		if (numbers.isEmpty() && containsUnitMarker(words))
			return new Dimensional(0, referenceMarker);

		if (numbers.isEmpty() || numbers.size()!=words.size())
			return null;

		/* at least 1 number and the same amount of words (unit-measures?) */
		/* ??? */
//		if (value.contains("/")) { //VC: overfitting su NBAPlayer qui?
//			final Double casted = findUnitMeasureAndCast(numbers.getFirst(), words.getFirst());
//			if (casted!=null) {
//				return new Dimensional(casted, referenceMarker);
//			}
//			return null;
//		}

		/* - cumulative - e.g., 8kg 3gr ; oppure 3" 2'; */
		return cumulateDimensionals(numbers, words);
	}

	private Dimensional cumulateDimensionals(final List<String> numbers, final List<String> words) {
		/* process PCDATA by accumulating several dimensional 
		 * numbers spread in it if needed, e.g.: '8kg 3gr.' */
		final Iterator<String> nIt = numbers.iterator(); // these are two lists...
		final Iterator<String> wIt = words.iterator();   // ...of the same length
		double result = 0d;
		int counter = 0;
		while (nIt.hasNext()) {
			final String n = nIt.next(); // iterate over each number and...
			final String w = wIt.next(); // ...the corresponding word
			
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

	private boolean containsUnitMarker(List<String> words) {
		for (String word : words) {
			if (this.unitMeasureGroup.findUnitMarker(word)!=null)
				return true;
		}
		return false;
	}

	private Double findUnitMeasureAndCast(String number, String word) {
		final UnitMeasure unit = this.unitMeasureGroup.findUnitMarker(word);
		if (unit!=null) {
			final Number n = (Number) super.tryCastNonNull(number);
			return n.getValue() * unit.getRatio();
		} else return null;
	}

	@Override
	public double distance(Object value1, Object value2) {
		return DIMENSIONAL_DISTANCE.distance(value1, value2);
	}

	@Override
	public Type getParent() {
		return Type.NUMBER;
	}

}
