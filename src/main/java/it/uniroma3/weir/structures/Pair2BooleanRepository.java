package it.uniroma3.weir.structures;

import java.util.Map;
import java.util.WeakHashMap;
/**
 * A cache representing a {@link Map}: Pair<T> -> Boolean and represented
 * by means of two separate {@link MapPairRepository}s over true and false
 * values.
 */
public class Pair2BooleanRepository<P extends Pair<T>, 
							        T extends Comparable<? super T>> {

	private static class PairCache<P extends Pair<T>, 
	  							   T extends Comparable<? super T>> 
	 					 extends MapPairRepository<P,T> {

		static final private long serialVersionUID = 1184238254573236734L;

		PairCache() {
			super(new WeakHashMap<T, Map<T, P>>(),null);
		}
		
		@Override
		public Map<T, P> createInnerMap() {
			return new WeakHashMap<T, P>();
		}
		
	}
	
	private PairCache<P, T> trueRepo;

	private PairCache<P, T> falseRepo ;
		
	public Pair2BooleanRepository() {
		this.trueRepo = new PairCache<P, T>();
		this.falseRepo = new PairCache<P, T>();
	}

	public boolean cache(P pair, boolean result) {
		( result ? trueRepo : falseRepo ).add(pair);
		return result;
	}
	
	public Boolean get(T a, T b) {
		if (this.falseRepo.contains(a, b)) return false;
		if (this.trueRepo.contains(a, b)) return true;
		
		return null;
//		throw new NoSuchElementException("Cannot find "+Pair.toString(a,b));
	}

}
