package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.configuration.Constants.DATATYPES;
import static it.uniroma3.weir.vector.format.TypeDecoder.IDENTITY;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.vector.DistanceFunction;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.format.TypeDecoder;
import it.uniroma3.weir.vector.value.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A type is a singleton object that allows the interpretation of 
 * string values as {@link Object}s.
 * 
 * Check {@link MoneyType, MassType, URLType, etc. etc.}
 * for an example of handled types.
 * 
 * The types are organized into an hierarchy with
 * {@link StringType} as root, i.e., the most generic
 * available type ({@see #getParent()).
 * 
 * Notice that: (i) we do not introduce another interface just
 * for value's interpretation and use {@link Object} to ease 
 * the implementation; (ii) <tt>null</tt> is used as the only
 * null-value for interpreted strings, i.e., independently
 * from the type.
 * 
 */
public abstract class Type implements Serializable {

	static final private long serialVersionUID = 4303086356028987828L;
	
	/**
	 * @return the list of all available types from the most
	 *         specific one to least one
	 */
	static public List<Type> availableDatatypes() {
		return valueOf(WeirConfig.getList(DATATYPES));
	}
	
	static final public StringType STRING = new StringType();
	static final public NumberType NUMBER = new NumberType();
	static final public DateType     DATE = new DateType();
	static final public ISBNType     ISBN = new ISBNType();
	static final public PhoneType   PHONE = new PhoneType();
	static final public URLType       URL = new URLType();
	static final public DimensionalType 
								DIMENSIONAL 
										  = new DimensionalType();
	static final public SpaceType   SPACE = new SpaceType();
	static final public MoneyType   MONEY = new MoneyType();
	static final public MassType     MASS = new MassType();
	
	static public List<Type> valueOf(List<String> typenames) {
		final List<Type> datatypes = new LinkedList<>();
		for (String typename : typenames) {
			datatypes.add(valueOf(typename));
		}
		return datatypes;
	}
	
	static public Type valueOf(String typeAsString) {
		Type type = null;
		try {
			type = (Type) Type.class.getDeclaredField(typeAsString).get(null);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unknown type name" +typeAsString,e);
		}
		return type;
	}
	
	static public Type rootType() { return STRING; }
	
	static public Type getCommonAncestor(Type t1, Type t2) {
		List<Type> anc1 = t1.getAllAncestors();
		List<Type> anc2 = t2.getAllAncestors();
		for (Type a : anc1) {
			if (anc2.contains(a)) {
				return a;
			}
		}
//		return null;
		throw new IllegalStateException("The types hierarchy must be single rooted at "+rootType());
	}
		
	private List<Type> getAllAncestors() {
		final List<Type> ancestors = new ArrayList<Type>();
		Type current = this;
		do {
			ancestors.add(current);			
			current = current.getParent();
		} while (current!=null);
		return ancestors;
	}
	
	/**
	 * 
	 * @return the parent {@link Type} of this type
	 */
	public abstract Type getParent();
	
	/**
	 * Establishes a reflexive subtype relation
	 * @param type - the candidate supertype
	 * @return true iff this type is a subtype of the passed one
	 */
	public boolean isSubtypeOf(Type supertype) {
		Type current = this;
		do {
			if (this.equals(supertype)) return true;
			current = current.getParent();
		} while (current!=null);
		return false;
	}
	
	/**
	 * Check whether a given string value can be casted to this type
	 * @param the value to cast
	 * @return true iff the given value can be casted to this type
	 */
	public boolean instanceOf(String value) {
		if (value==null) return true;
		return ( guardedTryCastNonNull(value)!=null );
	}
	
	/**
	 * n.b. by using {@link Object} to represent interpreted
	 *              strings we decouple {@link Type} from {@link Value}
	 * @param the string value to be interpreted by means of this type
	 * @return null if either the cast is not possible, or the given
	 *         <tt>value</tt> was null.
	 */
	public Object tryCast(String value) {
		if (value==null) return null;
		return guardedTryCastNonNull(value);
	}
	
	private Object guardedTryCastNonNull(String value) {
		try {
			return tryCastNonNull(value);
		} catch (RuntimeException e) {
			/* a casting attempt might fail by raising an exception */
			return null;
		}
	}
	
	protected Object tryCastNonNull(String value) {
		try {
			return decoder().decode(value);
		}
		catch (RuntimeException e) {
			throw new RuntimeException("Cannot cast \'+value\' to type "+this, e);
		}
	}

	public Object cast(String value) {
		if (value==null) return null;
		return castNonNull(value);
	}
	
	protected Object castNonNull(String value) {
		Object result=null;
		try {
			result = guardedTryCastNonNull(value);
			if (result==null) 
				throw new IllegalTypeCastException(this,value);
		} catch (RuntimeException e) {
			throw new IllegalTypeCastException(this,value,e);
		}
		return result;
	}

	/**
	 * A type-aware distance between two values
	 * @param value1
	 * @param value2
	 * @return the distance between the two values
	 */
	public abstract double distance(Object value1, Object value2);
	
	
	/**
	 * Create a type-aware distance function between 
	 * vectors of all values of a certain type
	 */
	public DistanceFunction createDistanceFunction() {
		final DistanceFunction df = DistanceFunction.MEAN;
		df.init(this);
		return df;
	}
	
	public Vector createTypedVector(ExtractedVector extracted, String... values) {
		return new Vector(extracted, this, values);		
	}
	
	/**
	 * @return a numbersDecoder to create object instances of 
	 * 	       this type from string representations
	 */
	public TypeDecoder decoder() {
		return IDENTITY;
	}
	
	/**
	 * Find the most specific type to cast all the strings of an array
	 * 
	 * @param the array of values to cast
	 * @return the most specific type to cast all the values in the given array
	 */
	static public Type findMostSpecificType(String...values) {
		Type result =  findType(availableDatatypes(), values);
		if (result==null)
			throw new IllegalArgumentException("Even the root type does not apply");
		return result;
	}	
	
	/**
	 * Find the first type to cast all the strings of an array
	 * 
	 * @param the array of values to cast
	 * @return the first type to cast all the values in the given array
	 */
	static public Type findType(Iterable<Type> types, String...values) {
		// iterate from the most specific to the most general type
		for(Type type : types) {
			if (type.allInstanceOf(values))
				return type;
		}
		return null;
	}
	
	public boolean allInstanceOf(String...values) {
		for(String value : values) {
			if (!this.instanceOf(value)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Type))
			return false;
		Type that = (Type)obj;
		return this.getClass()==that.getClass();
	}

	@Override
	public String toString() {
		final String className = this.getClass().getSimpleName().toUpperCase();
		return className.substring(0, className.length()-"Type".length());
	}
	
}
