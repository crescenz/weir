package it.uniroma3.weir.structures;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

/**
 * A structure to set up, update, and access a set of <tt>N*(N-1)/2</tt>
 * unordered {@link Pair}s indexed by two unordered integer indices 
 * ranging from <tt>0</tt> to <tt>N-1</tt>.
 * 
 * @param <P> the type of the pairs hosted in the repository
 * @param <T> the type of the elements in the pairs
 * @see {@link Pair}
 */
public abstract class ArrayPairRepository<P extends Pair<T>, 
										  T extends Comparable<? super T>>
				extends AbstractPairRepository<P,T>
                implements PairRepository<P,T>, Serializable {

	static final private long serialVersionUID = 1610131165514444270L;

	final private P[][] repository;
	
	final private int n; // index range: 0..n-1

	final private List<T> elements;

	final private Map<T, Integer> element2index; // index in this.elements
	
	protected ArrayPairRepository(Collection<T> all, Class<? extends P> cls) {
		this(all, cls, null);
	}
	
	protected ArrayPairRepository(Collection<T> all, 
								  Class<? extends P> cls, 
								  PairBuilder<? extends P,T> builder) {
		this.n = all.size();
		this.element2index = new HashMap<>();
		if (n<1) 
			throw new IllegalArgumentException(
					this.getClass() + " repository undersized:  n > 0"
			);
		this.elements = new ArrayList<>(all);
		Collections.sort(this.elements); // sort comparable elements
		this.repository = initArrays(cls, builder);
	}

	@SuppressWarnings("unchecked")
	private P[][] initArrays(Class<? extends P> cls, PairBuilder<? extends P, T> builder) {
		// an array hosting n arrays...
		final P[][] arrays = (P[][])Array.newInstance(cls, n, n);
		for(int i=0; i<this.n; i++) {
			arrays[i] = (P[]) Array.newInstance(cls, n);
			final T a = this.elements.get(i);
			for(int j=i; j<this.n; j++) {
				// ...each hosting n references to pairs ...
				final T b = this.elements.get(j);
				arrays[i][j] = ( builder!=null ? builder.createPair(a, b) : null );
			}
			this.element2index.put(a,i); // save its index
		}
		/* waste n * ( n - 1 ) / 2 array elements to simplify the access */
		return arrays;
	}
	
	public List<T> getIndexedElements() {
		return this.elements;
	}

	/**
	 * @param the index of an element
	 * @return the index of the element, i.e., an integer 
	 *         ranging from <tt>0</tt> to <tt>N-1</tt>
	 */
	public int getIndex(T element) {
		Objects.requireNonNull(element, "Cannot search null elements");
		
		final Integer index = this.element2index.get(element);
		if (index==null)
			throw new NoSuchElementException(element.toString());
		return index;
	}
	
	@Override
	public int size() { return this.n; }
	
	@Override
	public P add(P p) {
		final int i = getIndex(p.getMin());
		final int j = getIndex(p.getMax());
		final P result = this.repository[i][j];
		this.repository[i][j]=p;
		return result;
	}

	@Override
	public P get(T a, T b) {
		return get(getIndex(a), getIndex(b));
	}

	@Override
	public boolean contains(T a, T b) {
		return ( get(a,b)!=null );
	}
	
	public P get(int _i, int _j) {
		final int i = Math.min(_i, _j);
		final int j = Math.max(_i, _j);
		return this.repository[i][j];
	}

	public boolean remove(P p) {
		final int i = getIndex(p.getMin());
		final int j = getIndex(p.getMax());
		boolean result = ( this.repository[i][j] == null);
		this.repository[i][j] = null;
		return result;
	}
	
//	final private int getMinIndex(T a, T b) {
//		return Math.min(getIndex(a), getIndex(b));
//	}
//
//	final private int getMaxIndex(T a, T b) {
//		return Math.max(getIndex(a), getIndex(b));
//	}

	@Override
	public void forEach(PairProcessor<P,T> processor) {
		for(int i=0; i<this.n; i++) {
			for(int j=i; j<this.n; j++) {
				processor.process(this.repository[i][j]);
			}
		}
	}
	
}
