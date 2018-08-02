package it.uniroma3.weir.structures;

import java.io.Serializable;
import java.util.*;

/**
 * A data structure to set up, update, and access a set of 
 * <em>unordered</em> {@link Pair}s indexed by two
 * {@link Comparable} elements in the pairs.
 * 
 * @param <P> the type of the pairs hosted in the min2pairs
 * @param <T> the type of the elements in the pairs
 */
public class MapPairRepository<P extends Pair<T>, 
							   T extends Comparable<? super T>>
			 extends AbstractPairRepository<P,T>
             implements PairRepository<P,T>, Serializable {

	static final private long serialVersionUID = -5380575322388969906L;
	
	private Map<T,Map<T,P>> min2pairs; 

	private PairBuilder<P,T> builder;
	
	public MapPairRepository(PairBuilder<P,T> builder) {
		this(new HashMap<T,Map<T,P>>(),builder);
	}
	
	/* MGC */
	protected MapPairRepository(Map<T,Map<T,P>> min2pairs, PairBuilder<P,T> builder) {
		this.min2pairs = min2pairs;	
		this.builder = builder;
	}

	/* make it public for every type of repository or delete this method: CHECK */
	protected PairBuilder<P,T> getPairBuilder() {
		return this.builder;
	}
	
	/**
	 * Populate the repository by adding a <i>pyramid</i>
	 * of all the unordered pairs formed with a given
	 * collection of elements.
	 * @param <T> all elements to form the pairs
	 * @return the {@link PairRepository} once populated
	 */
	public MapPairRepository<P,T> addAllUnorderedPairs(Collection<T> all) {
		this.addAll(getPairBuilder().createPairs(all));
		return this;
	}

	/**
	 * Populate the repository with all the pairs obtained by performing 
	 * the Cartesian product of the two given collections of elements.
	 * @param all1 first collection of elements
	 * @param all2 second collection of elements
	 */
	public MapPairRepository<P,T> addCartesianProduct(Collection<T> all1, 
												      Collection<T> all2) {
		this.addAll(getPairBuilder().createPairs(all1,all2));
		return this;
	}
	
	public MapPairRepository<P,T> addReflexiveCartesianProduct(Collection<T> all1, 
			   												   Collection<T> all2) {
		this.addAll(getPairBuilder().createPairs(all1,all2));
		this.addAllUnorderedPairs(all1);
		this.addAllUnorderedPairs(all2);
		return this;
	}

	public MapPairRepository<P,T> reflexiveClosure() {
		for(P p : this.getAll())
			for(T e : p.getAll())
				this.add(this.builder.createPair(e, e));
		return this;
	}
	
	@Override
	public int size() {
		int result = 0;
		for(Map<T,P> map : this.min2pairs.values())
			result += map.size();
		return result;
	}

	@Override
	public P get(T a, T b) {
		
		if (a.compareTo(b)>0) {
			/* swap if min>max */
			T tmp = b;
			b = a;
			a = tmp;
		}
		
		return getPair(a,b);
	}
	
	@Override
	public boolean contains(T a, T b) {
		return ( get(a,b)!=null );
	}
	
	private P getPair(T min, T max) {
		Map<T, P> max2pair = this.min2pairs.get(min);
		if (max2pair==null) return null;
		return max2pair.get(max);		
	}
	
	@Override
	public P add(P pair) {
		T min = pair.getMin();
		T max = pair.getMax();
		
		Map<T, P> max2pair = this.min2pairs.get(min);
		
		if (max2pair==null) {
			max2pair = createInnerMap();
			this.min2pairs.put(min, max2pair);
		}
		
		return max2pair.put(max,pair);
	}

	protected Map<T, P> createInnerMap() {
		return new HashMap<T, P>();	
	}

	public boolean remove(P pair) {
		final T min = pair.getMin();
		final T max = pair.getMax();
		
		final Map<T, P> max2pair = this.min2pairs.get(min);
		
		if (max2pair!=null) {
			max2pair.remove(max);
			if (max2pair.isEmpty())
				this.min2pairs.remove(min);
			return true;
		}
		
		return false;
	}

	/**
	 * Remove all the pairs containing the given element
	 * @param a
	 */
	public void removeAll(T a) {
		this.removeAll(this.getPairs(a));
	}
	

	/**
	 * Get all pairs containing the given element
	 * @param a the element to look for in the pairs
	 * @return the set of pairs containing the element
	 */
	public Set<P> getPairs(T a) {
		final Set<P> result = new HashSet<>();
		result.addAll(getPairsByMin(a));
		result.addAll(getPairsByMax(a));
		return result;
	}

	public Set<T> getMates(T a) {
		final Set<T> mates = new HashSet<>();
		for(P pair : getPairs(a)) {
			mates.add(pair.getMate(a));
		}
		return mates;
	}

	private Set<P> getPairsByMin(T min) {
		if (this.min2pairs.containsKey(min))
			 return new HashSet<>(this.min2pairs.get(min).values());
		return Collections.emptySet();
	}

	private Set<P> getPairsByMax(T max) {
		Set<P> result = new HashSet<>();
		for(Map<T,P> max2pairs : this.min2pairs.values()) {
			if (max2pairs.containsKey(max))
				 result.add(max2pairs.get(max));
		}
		return result;
	}
	
	public Set<P> getPairs(Collection<? extends T> c) {
		final Set<P> result = new HashSet<>();
		for(T a : c)
			result.addAll(getPairs(a));
		return result;
	}

	public Set<T> getMates(Collection<? extends T> c) {
		final Set<T> mates = new HashSet<>();
		for(T a : c) {
			mates.addAll(getMates(a));
		}
		return mates;
	}

	@Override
	public void forEach(PairProcessor<P, T> processor) {
		for(Map<T, P> max2pair : this.min2pairs.values())
			for(P pair : max2pair.values())
				processor.process(pair);
	}

}
