package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.vector.format.Decoders.NUMBER_DECODER;
import static it.uniroma3.weir.vector.value.ValueDistances.NUMBER_DISTANCE;
import it.uniroma3.weir.vector.DistanceFunction;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.NumericVector;
import it.uniroma3.weir.vector.format.TypeDecoder;

/**
 * 
 * A {@link Type} for representing numbers.
 *
 */
public class NumberType extends Type {

	static final private long serialVersionUID = -8027306138243762948L;

	/**
	 * Create a type-aware distance function between 
	 * numeric vectors
	 */
	@Override
	public DistanceFunction createDistanceFunction() {
		final DistanceFunction df = DistanceFunction.EUCLIDEAN;
		df.init(this);
		return df;
	}
	
	@Override
	public NumericVector createTypedVector(ExtractedVector extracted, String... values) {
		return new NumericVector(extracted, this, values);		
	}
	
	@Override
	public double distance(Object value1, Object value2) {
		return NUMBER_DISTANCE.distance(value1, value2);
	}

	@Override
	public TypeDecoder decoder() {
		return NUMBER_DECODER;
	}

	@Override
	public Type getParent() {
		return Type.STRING;
	}

}
