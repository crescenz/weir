package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.vector.format.Decoders.DATE_DECODER;
import static it.uniroma3.weir.vector.value.ValueDistances.DATE_DISTANCE;
import it.uniroma3.weir.vector.format.TypeDecoder;
/**
 *
 * The {@link} representing dates
 *
 */
public class DateType extends Type {

	static final private long serialVersionUID = 5132451201226458118L;

	@Override
	public double distance(Object value1, Object value2) {
		return DATE_DISTANCE.distance(value1, value2);
	}


	@Override
	public TypeDecoder decoder() {
		return DATE_DECODER;
	}

	@Override
	public Type getParent() {
		return Type.STRING;
	}

}
