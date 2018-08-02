package it.uniroma3.weir.extraction.filter;

import it.uniroma3.hlog.HypertextualLogger;
import static it.uniroma3.weir.configuration.Constants.MAX_VALUE_LENGTH;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.value.Value;
import static it.uniroma3.hlog.HypertextualLogger.getLogger;

public class LengthOfValuesFilter implements VectorFilter {

	static final private HypertextualLogger log = getLogger();
	
	final private int maxLength;
	
	public LengthOfValuesFilter() {
		this(WeirConfig.getInteger(MAX_VALUE_LENGTH));
	}

	public LengthOfValuesFilter(int max) {
		this.maxLength = max;
	}

	@Override
	public boolean filter(Vector extractedVector) {		
		for (Value v : extractedVector.getElements()) {
			if (!v.isNull() && v.toString().length()>this.maxLength) {
				log.trace("value \'" + v + "\' has length " + v.toString().length());
				return false;
			}
		}
		return true;
	}

	public String toString() {
		return this.getClass().getSimpleName() + " - max length: " + this.maxLength ;
	}

}
