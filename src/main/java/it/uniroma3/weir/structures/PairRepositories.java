package it.uniroma3.weir.structures;

import static it.uniroma3.weir.structures.PairRepository.*;

import java.util.*;

/**
 * Factory methods to create most commonly used {@link PairRepository},
 * i.e. top-K Cartesian product repositories.
 */
public class PairRepositories {


//	// add top(int k) methods
// questa versione materializza le coppie prima di scartarle
//	static public 
//	<P extends Pair<T>, T extends Comparable<? super T>> 	
//	void addTopKCartesianProduct(
//			final int k,
//			final PairRepository<P,T> repository,
//			final PairBuilder<P,T> builder,
//			final PairWeighter<P,T> weighter,
//			final Collection<T> all1,
//			final Collection<T> all2) {
//		if (k<0) throw new IllegalArgumentException("k must be >=0");
//		if (k==0) return;
//
//		final List<P> pivoted = new ArrayList<P>(all2.size());
//		for (T pivot : all1) {
//			for (T e : all2) {
//				final P pair = builder.createPair(pivot,e);
//				if (pair!=null)
//					pivoted.add(pair);
//			}
//			Collections.sort(pivoted, weightComparator(weighter));
//			final int n = Math.min(k,pivoted.size()); 
//			repository.addAll(pivoted.subList(0, n)); // add only top-k
//			pivoted.clear();
//		}
//	}

	static public 
	<P extends Pair<T>, T extends Comparable<? super T>> 	
	void addTopKCartesianProduct(
			final int k,
			final PairRepository<P,T> repository,
			final PairBuilder<P,T> builder,
			final PairWeighter<P,T> weighter,
			final Collection<T> all1,
			final Collection<T> all2) {
		if (k<0) throw new IllegalArgumentException("k must be >=0");
		if (k==0) return;
		for (T pivot : all1) {
			repository.addAll(topK(k,builder,weighter,pivot,all2));// add only top-k
		}
	}
	
	static public
	<P extends Pair<T>, T extends Comparable<? super T>> 	
	List<P> topK(
			final int k,
			final PairBuilder<P,T> builder,
			final PairWeighter<P,T> weighter,
			final T pivot,
			final Collection<T> all) {
		if (k<0) throw new IllegalArgumentException("k must be >=0");
		if (k==0) return Collections.emptyList();
		final List<T> pivoted = new ArrayList<T>(all);
		Collections.sort(pivoted, pivotedComparator(pivot, weighter));
		final int n = Math.min(k,pivoted.size());  // select only top-k
		return builder.createPairs(pivot,pivoted.subList(0, n));		
	}

	static public 
	<P extends Pair<T>, T extends Comparable<? super T>> 
	Comparator<T> pivotedComparator(final T pivot, final PairWeighter<P,T> w) {
		return new Comparator<T>() {
			@Override
			public int compare(T a, T b) {
				/* order by weight */
				return Double.compare(w.weight(pivot,a),w.weight(pivot,b));
			}
		};
	}

	static public 
	<P extends Pair<T>, T extends Comparable<? super T>> 	
	Comparator<P> weightComparator(final PairWeighter<P,T> w) {
		return new Comparator<P>() {
			@Override
			public int compare(P p1, P p2) {
				/* the heavier the better */
				return Double.compare(w.weight(p2),w.weight(p1));
			}
		};
	}
}
