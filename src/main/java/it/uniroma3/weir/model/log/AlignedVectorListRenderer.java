package it.uniroma3.weir.model.log;

import it.uniroma3.hlog.render.IterableRenderer;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.hiddenrelation.AbstractInstance;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.value.Value;

import java.util.*;
/**
 * An {@link IterableRenderer} to render a list of {@link Vector}s
 * aligned by means of a list of {@link AbstractInstance}s.
 */
public class AlignedVectorListRenderer extends VectorListRenderer
	   implements IterableRenderer<Vector, Iterable<Vector>> {
	
	private SortedSet<AbstractInstance> instances;

	public AlignedVectorListRenderer(Set<AbstractInstance> instances) {
		this.instances = orderBySize(instances);
	}
	
	private SortedSet<AbstractInstance> orderBySize(Set<AbstractInstance> instances) {
		final SortedSet<AbstractInstance> result = new TreeSet<>(AbstractInstance.COMPARATOR_BY_SIZE);
		result.addAll(instances);
		return result;
	}
	
	protected int numberOfColumns(Vector extracted) {
		return ( extracted==null ? 0 : Math.min(this.instances.size(),MAX_NUMBER_VALUES) );
	}

	/* n.b. value can be null is the site does not provide a page */
	//there was:final Webpage page = ai.firstNonNullPage();
	
	/**
	 * We are assuming all vectors are properly aligned by means of this method
	 * 
	 * @param vector
	 * @return
	 */
	@Override
	protected Iterable<Value> enumerateColumnValues(Iterable<Value> iterable) {
		final Vector vector = (Vector)iterable;
		final List<Value> result = new ArrayList<>(vector.size());
		final Website site = vector.getWebsite();
		for(AbstractInstance ai : this.instances) {
			/* n.b. value can be null is the site does not provide a page */
			final Webpage page = ai.from(site);
			final Value value = vector.get(page);
			result.add(value);
			if (result.size()>=MAX_NUMBER_VALUES) break;
		}
		return result;
	}

}
