package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.vector.value.ValueDistances.JSDISTANCE;
/**
 * 
 * A type that represents a string
 *
 */
public class StringType extends Type {

	static final private long serialVersionUID = -8127151470743367308L;

	@Override
	public Object tryCastNonNull(String value) {
		return value;
	}

	@Override
	public double distance(Object value1, Object value2) {
		return JSDISTANCE.distance(value1, value2);
	}

	@Override
	public String toString() {
		return "STRING";
	}

	@Override
	public Type getParent() {
		return null; // that's the root of the hierarchy
	}

}
