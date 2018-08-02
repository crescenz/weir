package it.uniroma3.weir.vector.value;

import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.type.Type;

import java.io.Serializable;
import java.net.URL;

/**
 * Abstract base class for different kind of values 
 * in a {@link Vector}.
 * 
 * Originally, these values come from a text extracted
 * from {@link Webpage} in the form of {@link ExtractedValue} 
 * objects. 
 * 
 * Further processing can occur ({@see Normalizer})
 * to clean up the extracted values. 
 *  
 * These values can then be interpreted by means of a {@link Type}
 * and seen as a subtype of {@link Object}, e.g., {@link Integer}s,
 * {@link String}, {@link URL}.
 * 
 */
public class Value implements Comparable<Value>, Serializable {

	static final private long serialVersionUID = -268261948803734994L;
	
	/** the value after its interpretation by means of 
	  a {@link Type}, e.g., as a NUMBER, URL etc., etc... 
	 */
	private Object value; 
	/**
	 * the {@link Webpage} from which this value has been extracted
	 */
    private Webpage page;

	public Value(Webpage page, Object value) {
		this.page  = page;
		this.value = value;
	}

	/**
	 * The object interpreting the value according to a {@link Type}
	 * @return the value as a generic 
	 *         {@link Object} interpreted by {@link Type}.
	 */
	public Object getValue() {
		return this.value;
	}
	
	/**
	 * Simple but useful method to get a numeric interpretation as a 
	 * primitive double of a normalized value.
	 * @return an interpretation of this value as a numeric double value
	 * @throws IllegalStateException if this values cannot be cast to a value
	 */
	public double getNumericValue() throws IllegalStateException {
		if (this.isNull()) throw new IllegalStateException(this+" cannot be cast to a number");
		final Number number = (Number)getValue();
		return number.getValue();
	}

	/**
	 * @return the {@link Webpage} from which this value comes from
	 */
    public Webpage getPage() {
        return this.page;
    }
    
	final public boolean isNull() { return getValue()==null; }
	
	static public double nonNullsTypedDistance(Type type, Value v1, Value v2) {
		if (v1.isNull() && v2.isNull()) 
			throw new IllegalArgumentException("The distance between two null values is undefined");
		return typedDistance(type, v1, v2);
	}

	static public double typedDistance(Type type, Value v1, Value v2) {
		if (v1.isNull() && v2.isNull()) return Double.NaN; 
		
		/* if only one value is null but the other is not, then return max distance */
		return (v1.isNull() ||  v2.isNull() ? 1.0d : type.distance(v1.getValue(), v2.getValue()) );
	}
	
	@Override
	final public int hashCode() {
		return java.util.Objects.hashCode(getValue());
	}

	@Override
	final public boolean equals(Object o) {
		if (o==null || this.getClass()!=o.getClass()) return false;
		
		Value that = (Value)o;
		
		return java.util.Objects.equals(this.getValue(), that.getValue());
	}
	
	final public String toLowerCase() {
		if (this.isNull()) return null;
		return this.getValue().toString().toLowerCase().trim();	
	}

	@Override
	public int compareTo(Value that) {
		int cmp = this.getPage().compareTo(that.getPage());
		if (cmp!=0) return cmp;
		if (this.isNull() && that.isNull()) return 0;
		if (that.isNull()) return +1;
		if (this.isNull()) return -1;
		return this.toString().compareTo(that.toString());
	}

	/**
	 * Check {@linkplain Type#tryCastNonNull(String)} signature that 
	 * converts a String into a typed object. 
	 * In order to preserve the invariant
	 * t.tryCast(v.toString()).equals(v.getValue())
	 * {@link #toString()} method returns null if {@link #isNull()}
	 * is true whenever v is a value of type t
	 * @return either null if this value {@link #isNull()}, or
	 *         its String representation
	 */
	@Override
	final public String toString() {
		if (this.isNull()) return null;
		return String.valueOf(this.getValue());
	}


}
