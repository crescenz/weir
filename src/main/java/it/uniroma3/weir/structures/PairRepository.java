package it.uniroma3.weir.structures;

import java.io.Serializable;
import java.util.*;

/**
 * An interface to mark structures to set up, update, 
 * and access min set of <em>unordered</em> pairs ({@link Pair<T>}) 
 * indexed by the two unordered elements of the pairs.
 * 
 * @param <P> the type of the unordered pairs hosted in the repository
 * @param <T> the type of the elements in the unordered pairs
 */
public interface PairRepository<P extends Pair<T>, 
								T extends Comparable<? super T>>
	   extends Iterable<P> {

	public boolean isEmpty();
	
	public int size();
	
	public P add(P p);
	
	public P get(T a, T b);
	
	public boolean contains(T a, T b);
	
	public boolean remove(P p);
	
	public Collection<? extends P> getAll();
	
	public boolean addAll(Collection<? extends P> c);
	
	public boolean removeAll(Collection<? extends P> c);
	@Override
	public Iterator<P> iterator();
	
	public void forEach(PairProcessor<P,T> processor);
		
	static public interface PairProcessor<P extends Pair<T>, 
										  T extends Comparable<? super T>> {
		
		public void process(P pair);

	}	

	static public interface PairBuilder<P extends Pair<T>,
										T extends Comparable<? super T>> {
		/**
		 * Create a new {@link Pair} of elements
		 * @param a - one element of the pair
		 * @param b - the other element of the pair
		 * @return the new pair or null if it should not be created
		 */
		public P createPair(T a, T b);
	
		/**
		 * Create a list of new {@link Pair}s by combining
		 * a given pivot with all the elements in a given collection
		 * @param p the pivot
		 * @param c the collection
		 * @return the list composed of all the pairs obtained 
		 * 		   by combining the pivot with every element in the 
		 * 		   collection <tt>c</tt> passed as parameter
		 */
		public List<P> createPairs(T p, Collection<T> c);

		/**
		 * Create a list of new <em>unordered</em> {@link Pair}s 
		 * by combining all the elements in a given collection 
		 * with the themselves, non reflexively.  
		 * @param c the collection
		 * @return the list composed of all the unordered pairs
		 * 		   obtained by combining every element in the 
		 * 		   collection <tt>c</tt> passed as parameter
		 */
		public List<P> createPairs(Collection<T> c);
		
		/**
		 * Create a list of new {@link Pair}s by Cartesian product
		 * of all the elements in two given collections
		 * @param c1 the collection
		 * @param c2 the collection
		 * @return the list of pairs by Cartesian product
		 */
		public List<P> createPairs(Collection<T> c1, Collection<T> c2);
		
	}

	static public abstract class AbstractPairBuilder<P extends Pair<T>, 
													 T extends Comparable<? super T>>
						   implements PairBuilder<P,T> , Serializable {
		
		static final private long serialVersionUID = -8862363883043937047L;
		
		/**
		 * Create a new {@link Pair} of elements
		 * @param a - one element of the pair
		 * @param b - the other element of the pair
		 * @return the new pair or null if it should not be created
		 */
		@Override
		abstract public P createPair(T a, T b);

		@Override
		public List<P> createPairs(T p, Collection<T> c) {
			return createPairs(Collections.singleton(p),c);
		}

		@Override
		public List<P> createPairs(Collection<T> c) {
			final List<P> result = new ArrayList<>(c.size()*(c.size()-1)/2);
			final List<T> list = new ArrayList<>(c);
			for (int i=0; i<list.size(); i++) {
				for (int j=i+1; j<list.size(); j++) {
					final P pair = this.createPair(list.get(i),list.get(j));
					if (pair!=null)
						result.add(pair);
				}
			}
			return result;
		}		
		@Override
		public List<P> createPairs(Collection<T> c1, Collection<T> c2) {
			final List<P> result = new ArrayList<>(c1.size()*c2.size());
			for(T e1 : c1) {
				for(T e2 : c2) {
					final P pair = this.createPair(e1, e2);
					if (pair!=null)
						result.add(pair);
				}
			}
			return result;
		}
	}
	
	
	static public interface PairWeighter<P extends Pair<T>,
	   							  		 T extends Comparable<? super T>> {

			// this version allows to save the weight into the pair
			public double weight(P pair);
			
			// this version does not require the pair to have been already built
			public double weight(T a, T b);
			
	}

	static public abstract class AbstractPairWeighter<P extends Pair<T>,
		 											  T extends Comparable<? super T>> 
						   implements PairWeighter<P,T>, Serializable {

		static final private long serialVersionUID = 7421039224949056050L;
		
		@Override
		public double weight(P pair) {
			return weight(pair.getMin(), pair.getMax());
		}
		@Override
		public double weight(T a, T b) {
			throw new UnsupportedOperationException();
		}		
	}
	
}
