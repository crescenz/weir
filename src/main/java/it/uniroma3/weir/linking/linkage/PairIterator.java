package it.uniroma3.weir.linking.linkage;

import it.uniroma3.weir.structures.Pair;

import java.util.Iterator;

/** 
 * An {@link Iterator} over {@link Pair}s.
 * 
 * The implementations must also host the current pair, 
 * i.e., the last pair  returned by {@linkplain PairIterator#next()};
 * they are also retrievable by means of these getters methods:
 * {@linkplain PairIterator#getMin()}, and {@linkplain PairIterator#getMax()}.
 * 
 */
public abstract class PairIterator<T extends Comparable<? super T>> implements Iterator<Pair<T>> {

	protected Pair<T> pair;
	
	abstract public Pair<T> next();

	abstract public boolean hasNext();

	public T getMin() { return this.pair.getMin(); }

	public T getMax() { return this.pair.getMax(); }
	
	@Override
	public void remove() { throw new UnsupportedOperationException(); }

}