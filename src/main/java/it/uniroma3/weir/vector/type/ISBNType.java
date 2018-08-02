package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.vector.format.Decoders.ISBN_DECODER;
import static it.uniroma3.weir.vector.value.ValueDistances.ISBN_DISTANCE;
import it.uniroma3.weir.vector.format.TypeDecoder;
/**
 *
 * A {@link Type} representing ISBN
 *
 */
public class ISBNType extends Type {

	static final private long serialVersionUID = -8770195775473828984L;

	@Override
	public double distance(Object value1, Object value2) {
		return ISBN_DISTANCE.distance(value1, value2);
	}

	@Override
	public TypeDecoder decoder() {
		return ISBN_DECODER;
	}
	
	@Override
	public Type getParent() {
		return Type.STRING;
	}

}
