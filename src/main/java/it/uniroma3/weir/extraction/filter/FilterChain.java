package it.uniroma3.weir.extraction.filter;


import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import static it.uniroma3.weir.Formats.percentage;
import static it.uniroma3.weir.configuration.Constants.EXTRACTION_RULES_FILTERS;
import static it.uniroma3.weir.model.log.WeirStyles.header;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.hlog.Logpage;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An {@link ExtractionRule} filter implemented as a chain of filters
 * over the {@link ExtractedVector}s.
 */
public class FilterChain implements VectorFilter {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	private List<VectorFilter> filters;

	public FilterChain() {
		this(makeFilters());
	}

	public FilterChain(List<VectorFilter> filters) {
		this.filters = filters;
	}

	private static List<VectorFilter> makeFilters() {
		final List<VectorFilter> filters = new ArrayList<>();

		final List<FilterType> filterTypes = WeirConfig.getEnumList(FilterType.class, EXTRACTION_RULES_FILTERS);
		for (FilterType filterType : filterTypes) {
			filters.add(filterType.getFilter());
		}
		log.trace("enabled filters: "+filters);
		return filters;
	}

	public void filter(List<? extends Vector> vectors) {
		final int before = vectors.size();
		log.trace("number of vectors to filter: " + before);
		int counter = 0;
		Iterator<? extends Vector> it = vectors.iterator();
		log.newTable();
		log.trace(header(" "),header("in/out"),header("vector"));
		
		while (it.hasNext()) {
			final Vector vector = it.next();
			final Logpage logpage = log.newPage();			
			final boolean result = filter(vector);
			if (!result) it.remove();
			log.endPage();
			log(counter++, vector, logpage, result);
		}
		log.endTable();
		final int after = vectors.size();
		log.trace("vectors before filtering: " + before);
		final double perc = (double)after/before;
		log.trace(after + " remaining rules ("+percentage.format(perc)+")");
	}

	private void log(int counter, Vector vector, Logpage logpage, boolean result) {
		log.trace(counter,
				  linkTo(logpage).withAnchor(result ? "accepted" : "discarded"), 
				  vector
				 );
	}

	@Override
	public boolean filter(Vector vector) {
		boolean result = true;
		String filterName = null;
		log.trace(vector);
		for (VectorFilter filter : this.filters) {
			filterName = filter.getClass().getSimpleName();
			log.trace("checking with " + filterName);
			if (!filter.filter(vector)) {
				log.trace("discarded");
				result = false;
				break;
			} else log.trace("accepted");
			log.trace();
		}
		return result;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + this.filters;
	}

}
