package it.uniroma3.weir.vector;

import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.cache.Fingerprinted;
import it.uniroma3.weir.cache.Fingerprinter;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.linking.linkage.PageLinkageIterator;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.WeirId;
import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.value.ExtractedValue;
import it.uniroma3.weir.vector.value.Value;

import java.io.Serializable;
import java.util.*;

/**
 * A vector of {@link Value}s associated with a { @link Type} such that
 * every element in the vector is of that type. The values have been
 * extracted from {@link Webpage}s and then eventually normalized
 * @see Normalizer, {@link ExtractedVector}
 * 
 */
public class Vector extends WeirId implements Iterable<Value>, Serializable, Fingerprinted {

	static final private long serialVersionUID = -3455939240963536316L;

	final private Type type;

	protected Value[] elements;
	
	protected Map<Webpage, Value> page2value; // this is an indexed vector

	private ExtractedVector originating;

	final private SortedSet<Label> labels;
	
	protected Vector(Type type) {
		super(nextIdByClass(Vector.class));
		this.type = type;
		this.labels = new TreeSet<>();
		this.elements = null;
		this.page2value = new HashMap<>();
		this.originating = null;
	}

	protected Vector(Vector vector) {
		this(vector.getType());
		this.originating = vector.getOriginatingVector();
		this.elements = vector.getElements();
		this.page2value = vector.getPage2Value();
	}

	/**
	 * Create a new vector generated with the values coming from
	 * a {@link ExtractedVector}, and then normalized to an array
	 * of strings.
	 * @param original
	 * @param type
	 * @param values coming from the extracted vector
	 */
	public Vector(ExtractedVector original, Type type, String...values) {
		this(type);
		this.originating = original;
		this.elements = castValues(original, type, values);
		this.labels.addAll(original.getLabels());
	}

	private Value[] castValues(ExtractedVector original, Type type, String...values) {
		final Value[] casted = new Value[values.length];
		for (int i=0; i<values.length; i++) {
			final Webpage page = original.get(i).getPage();
			final Object normalized = type.cast(values[i]);
			final Value value = new Value(page,normalized);
			this.page2value.put(page, value);
			casted[i] = value;
		}
		return casted;
	}	
	
	protected void initIndexPage2Value(Value[] elements) {
		for(Value value : elements)
			this.page2value.put(value.getPage(), value);
	}

	/**
	 * Return the {@link Type} of this vector.
	 * Notice that the type <tt>t</tt> must be 
	 * such that for every non-null {@link Value} element 
	 * <tt>v</tt> in the vector, it results that 
	 * {@link Type#tryCast(v.getValue())!=null}.
	 *  
	 * @return the {@link Type} of this vector.
	 */
	public Type getType() {
		return this.type;
	}

	public Value[] getElements() {
		return this.elements;
	}

	public Map<Webpage,Value> getPage2Value() {
		return this.page2value;
	}
	
	public Value get(int index) {
		return this.elements[index];
	}
	
	/**
	 * @param  page
	 * @return the {@link Value} indexed from this page, or null
	 *         if the page is not indexed by the {@link Website}
	 */
	public Value get(Webpage page) {
		return this.page2value.get(page);
	}

	/**
	 * @param value -  A value from this vector
	 * @return the corresponding extracted value from the originating vector
	 */
	public ExtractedValue getExtractedValue(Value value) {
		if (value==null) return null;
//		Objects.requireNonNull(value);
		final ExtractedVector originating = this.getOriginatingVector();
		if (originating==null) return null;
		return (ExtractedValue) originating.get(value.getPage());
	}
	
	public int size() {
		return this.elements.length;
	}

	public boolean isEmpty() {
		return ( this.size()==0 );
	}

	public int countNonNulls() {
		int nonNullCounter = 0;
		for (int i=0; i<this.size(); i++) {
			if (!this.get(i).isNull()) {
				nonNullCounter++;
			}
		}
		return nonNullCounter;
	}

	public int nulls() {
		return this.size() - this.countNonNulls();
	}

	public boolean allNulls() {
		return ( this.countNonNulls()==0 );
	}

	public void addLabel(Label label) {
		this.labels.add(label);
	}

	public void addLabels(Collection<Label> labels) {
		this.labels.addAll(labels);
	}

	public SortedSet<Label> getLabels() {
		return this.labels;
	}

	/**
	 * @return the vector of values as originally extracted
	 *         from the starting pages (i.e., before any other
	 *         processing step such as normalization).
	 */
	public ExtractedVector getOriginatingVector() {
		if (this.originating==null) return null;
		else return this.originating.getOriginatingVector();
	}

	public ExtractionRule getExtractionRule() {
		final ExtractedVector originating = this.getOriginatingVector();
		// n.b. it might be null for golden vectors
		return ( originating==null ? null : originating.getExtractionRule() ); 
	}

	public Website getWebsite() {
		return getOriginatingVector().getWebsite();
	}
	
	public boolean isInstanceof(Type type) {
		for (Value value : this) {
			if (value.isNull()) continue;

			if (!type.instanceOf(value.toString()))
				return false;
		}				
		return true;
	}

	public boolean equalsIgnoringNulls(Vector that) {
		for (int i=0; i<this.size(); i++) {
			// FIXME use linkage! non è un problema perché al momento 
			// è usato solo dentro il clustering intrasite
			final Value thisValue = this.get(i), thatValue = that.get(i);
			if (thisValue.isNull() || thatValue.isNull()) continue;
			if (!Objects.equals(thisValue.toString(), thatValue.toString())) {
				return false;
			}
		}
		return true;
	}

	public Vector copy() {
		// N.B. without labels
		return new Vector(this);
	}
	
	@Override
	public Iterator<Value> iterator() {
		return Arrays.asList(getElements()).iterator();
	}

	public Iterator<String> stringsIterator() {
		return new Iterator<String>() {
			final Iterator<Value> innerIt = iterator();

			@Override
			public boolean hasNext() {
				return innerIt.hasNext();
			}

			@Override
			public String next() {
				return Objects.toString(innerIt.next(),null);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	// FIXME Il codice legacy di DSCDAlgorithm materializzava i linkage per l'occasione
	public double distance(Vector that) {
		return DistanceSupport.distance(PageLinkageIterator.pairwiseIterator(this, that));
	}
	
	@Override
	public Fingerprint getFingerprint() {
		final Fingerprinter printer = new Fingerprinter();
		printer.fingerprint(Integer.toString(this.size()));
		for(Value value : this.getElements()) {
			 /* n.b. String.valueOf(value.toString())  
			  *      may produce null values          ! */
			if (!value.isNull())
				printer.fingerprint(String.valueOf(value));
		}
		return printer.getFingerprint("vect");
	}

	@Override
	public String getWeirId() {
		final int siteId = this.getWebsite().getIndex();
		return super.getWeirId()+"<sup>" + siteId + "</sup>";
	}
	
	@Override
	public int hashCode() {
		return this.getType().hashCode()+Arrays.hashCode(this.getElements());
	}

	@Override
	public boolean equals(Object object) {
		if (object==null || !(object instanceof Vector)) return false;

		final Vector that = (Vector)object;
		return this.getType().equals(that.getType()) &&
			   Arrays.equals(this.getElements(), that.getElements());
	}

	@Override
	public String toString() {
		return super.toString()+"<sup>"+getWebsite().getId()+"</sup>: "+
			   this.getType()+"["+this.size()+"]"+"\t"+Arrays.toString(getElements());
	}

}