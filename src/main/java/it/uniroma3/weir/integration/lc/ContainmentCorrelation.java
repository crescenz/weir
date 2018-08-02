package it.uniroma3.weir.integration.lc;

import static it.uniroma3.weir.configuration.Constants.MIN_LOOSE_LC_RATIO;
import static it.uniroma3.weir.configuration.Constants.MIN_LOOSE_LC_VALUES;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.integration.AttributePair;
import it.uniroma3.weir.linking.linkage.PageLinkageIterator;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.vector.format.FormatRule;
import it.uniroma3.weir.vector.value.Value;

import java.util.List;
/**
 * Consider two {@link Attribute}s related whenever there is a relevant
 * overlap among their non-null values. The overlap is estimated by
 * splitting the values in word tokens, and checking whether a value contain
 * all the tokens of the other.
 *
 */
public class ContainmentCorrelation implements Correlation {

	private double minLCratio;
	private double minLCvalues;
	
	public ContainmentCorrelation() {
		this.minLCratio = WeirConfig.getDouble(MIN_LOOSE_LC_RATIO);
		this.minLCvalues = WeirConfig.getInteger(MIN_LOOSE_LC_VALUES);
	}
	
	@Override
	public boolean areCorrelated(Attribute a, Attribute b) {
		
		int counter = 0; // total number of non-null values pairs available
		int overlap = 0; // total number of non-null overlapping pairs
		
		final PageLinkageIterator it = new AttributePair(a,b).iterator();
		while (it.hasNext()) {
			it.next();
			final Value v1 = it.getMin(), v2 = it.getMax();
			
			if (v1.isNull() || v2.isNull()) continue; // skip nulls
			
			if (overlap(v1,v2)) {
				overlap++;
			}
			
			counter++; 
		}
		
		return isOverlapEvidenceEnough(counter, overlap) ;
	}

	private boolean isOverlapEvidenceEnough(int counter, int overlap) {
		return overlap >= this.minLCvalues && overlap >= counter*this.minLCratio;
	}

	private boolean overlap(Value v1, Value v2) {
		List<String> tokens1 = getTokens(v1), tokens2 = getTokens(v2) ;
		return tokens1.containsAll(tokens2) || tokens2.containsAll(tokens1);
	}

	static final private FormatRule WORDasTOKEN = new FormatRule("\\w+");

	private List<String> getTokens(Value v) {
		return WORDasTOKEN.extractAll(v.toString().toLowerCase());
	}

}
