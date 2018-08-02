package it.uniroma3.weir.structures;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * An abstract class to factorize bulk methods and other facility
 * methods based on other, more fundamentals methods.

 * @param <P> the type of the unordered pairs hosted in the repository
 * @param <T> the type of the elements in the unordered pairs
 */
public abstract class AbstractPairRepository<P extends Pair<T>, 
											 T extends Comparable<? super T>> 
				implements PairRepository<P,T> {

	@Override
	public boolean isEmpty() { 
		return ( size()==0 ) ; 
	}
	@Override
	public Set<P> getAll() {
		final Set<P> result = new LinkedHashSet<>();
		this.forEach(new PairProcessor<P, T>() {			
			@Override
			public void process(P pair) {
				result.add(pair);
			}
		});
		return result;
	}
	@Override
	public boolean addAll(Collection<? extends P> c) {
		boolean result = false;
		for(P pair : c) {
			if (pair!=this.add(pair)) 
				result = true;
		}
		return result;
	}
	@Override	
	public boolean removeAll(Collection<? extends P> c) {
		boolean result = false;
		for(P pair : c) {
			if (this.remove(pair)) 
				result = true;
		}
		return result;
	}

	@Override
	public Iterator<P> iterator() {
		return this.getAll().iterator();
	}
	
}
