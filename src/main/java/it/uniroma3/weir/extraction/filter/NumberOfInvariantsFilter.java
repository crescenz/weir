package it.uniroma3.weir.extraction.filter;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.value.Value;

import java.util.HashMap;
import java.util.Map;

import static it.uniroma3.weir.configuration.Constants.MAX_PERCENTAGE_EQUALS;

public class NumberOfInvariantsFilter implements VectorFilter {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	// max percentage of the most frequently occurring value
	final private double maxPercInv;

	public NumberOfInvariantsFilter() {
		this((double)WeirConfig.getInteger(MAX_PERCENTAGE_EQUALS) / 100);
	}

	public NumberOfInvariantsFilter(double max) {
		this.maxPercInv = max;
	}

	@Override
	public boolean filter(Vector extractedVector) {
		final Map<String, Integer> value2occurrenceCounter = new HashMap<>();
		int nonNulls =	extractedVector.countNonNulls();
		long maxOccs = Math.max(Math.round(nonNulls * this.maxPercInv), 1);//>0


		log.trace("size (without nulls): " + nonNulls);
		log.trace("max occurrences: " + nonNulls + " * " + this.maxPercInv + " ~= " + maxOccs);

		for (Value element : extractedVector.getElements()) {
			if (!element.isNull()) {
				String value = element.toString();
				Integer counter = value2occurrenceCounter.get(value);
				if (counter==null) {
					counter = 0;
				}

				if (counter+1>maxOccs) {
					log.trace("the value \'" + element + "\' has at least "+counter+" occurrences");
					return false;
				}

				value2occurrenceCounter.put(value, counter+1);
			}
		}
		return true;
	}

	public String toString() {
		return this.getClass().getSimpleName() + " - max occs %: " +	this.maxPercInv ;
	}

}
