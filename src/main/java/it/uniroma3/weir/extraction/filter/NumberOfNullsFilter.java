package it.uniroma3.weir.extraction.filter;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.vector.Vector;
import static it.uniroma3.weir.configuration.Constants.MAX_PERCENTAGE_NULLS;

public class NumberOfNullsFilter implements VectorFilter {
	
	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	final private double maxNullsPerc;
	
	public NumberOfNullsFilter() {
		this((double)WeirConfig.getInteger(MAX_PERCENTAGE_NULLS) / 100);
	}

	public NumberOfNullsFilter(double maxPercentageOfNullElements) {
		this.maxNullsPerc = maxPercentageOfNullElements;
	}

	public boolean filter(Vector vector) {
		long maxNulls = Math.round(vector.size() * this.maxNullsPerc);

		int nullsCounter = vector.nulls();
		
		log.trace("max null elements: " + vector.size() + " * " + this.maxNullsPerc + " = " + maxNulls);
		log.trace("null elements: " + nullsCounter);

		return ( nullsCounter < maxNulls ) ;
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + " - max nulls perc.: " + this.maxNullsPerc ;
	}

}
