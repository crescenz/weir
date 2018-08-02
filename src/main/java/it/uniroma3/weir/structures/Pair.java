package it.uniroma3.weir.structures;

import java.util.*;
import java.io.Serializable;

/**
 * <u>Unordered</u> pairs of elements of the same type T.
 * <br/>
 * Notice that given two objects <code>min, max</code> of type T 
 * such that:
 * <BR/>
 * <code>!min.equals(max)</code>, then:
 * <BR/>
 * <code> new Pair(min,max).equals(new Pair(max,min))</code>.
 *
 * @param T the type of both the elements in the pair
 */
public class Pair<T extends Comparable<? super T>> implements Serializable {

	static final private long serialVersionUID = 3893124093157664614L;

	static final private String NULL_MSG = "Unordered pairs cannot host null elements";
	
	static final public <T extends Comparable<? super T>> String toString(Pair<T> p) {
		return toString(p.getMin(), p.getMax());
	}
	
	static final public <T extends Comparable<? super T>> String toString(T a, T b) {
		return "("+a +","+b+")";
	}
	
	final protected T min;
	final protected T max;

	public Pair(T a, T b) {
		Objects.requireNonNull(a,NULL_MSG);
		Objects.requireNonNull(b,NULL_MSG);
		this.min = ( a.compareTo(b)<=0 ? a : b );
		this.max = ( a.compareTo(b)<=0 ? b : a );;
	}

	final public T getMin() {
		return this.min;
	}

	final public T getMax() {
		return this.max;
	}
	
	final public T getMate(T a) {
		if ( a.compareTo(this.getMin())==0 ) 
			return this.getMax();
		if ( a.compareTo(this.getMax())==0 ) 
			return this.getMin();
		throw new NoSuchElementException(a + " is not in "+toString());
	}

	final public boolean contains(T wanted) {
		return getMin().equals(wanted) || getMax().equals(wanted);
	}
	
	/**
	 * Return the first element in the given collection which is
	 * also present in this pair
	 * @param wanted the collection of elements to search for
	 * @return the first element
	 */
	final public T retain(Collection<? extends T> wanted) {
		for(T e : wanted) {
			if (this.contains(e))
				return e;
		}
		throw new NoSuchElementException(wanted + " are not in "+toString());
	}
	
	final public SortedSet<T> getAll() {
		return Collections.unmodifiableSortedSet(new TreeSet<T>() {
			static final private long serialVersionUID = 1L; {
			add(getMin());
			add(getMax());
		}});
	}
	
	static final protected 
	<T extends Comparable<? super T>>
	int compare(Pair<T> thisP, Pair<T> thatP) {
		int cmp = thisP.getMin().compareTo(thatP.getMin());
		
		if (cmp==0)
			cmp = thisP.getMax().compareTo(thatP.getMax());
		
		return cmp;
	}

	@Override
	final public int hashCode() {
		return Objects.hashCode(getMin())+Objects.hashCode(getMax());
	}
	
	@Override
	final public boolean equals(Object obj) {
		if (obj == null || this.getClass()!=obj.getClass()) return false;
		Pair<?> that = (Pair<?>)obj;
		return	Objects.equals(this.getMin(),that.getMin()) && Objects.equals(this.getMax(),that.getMax());// || 
//was:			Objects.equals(this.getMin(),that.getMax()) && Objects.equals(this.getMax(),that.getMin());
	}
		
	@Override
	public String toString() {
		return toString(this);
	}

}
