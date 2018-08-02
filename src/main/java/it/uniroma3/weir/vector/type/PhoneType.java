package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.vector.format.Decoders.PHONE_DECODER;
import static it.uniroma3.weir.vector.value.ValueDistances.HAMMING;
import it.uniroma3.weir.vector.format.TypeDecoder;

/**
 * 
 * A type that represents a phone number
 *
 */
public class PhoneType extends Type {

	static final private long serialVersionUID = 554057955882487347L;
		
	@Override
	public TypeDecoder decoder() { return PHONE_DECODER; }
	
	@Override
	public double distance(Object value1, Object value2) {
		return HAMMING.distance(value1, value2);
	}
	
	@Override
	public String toString() {
		return "PHONE";
	}

	@Override
	public Type getParent() {
		return Type.STRING;
	}
	
}
