package it.uniroma3.weir.vector.format;

/**
 * A {@link TypeDecoder} defined as union 
 * of decoders, the first that works is used.
 */
public class UnionDecoder implements TypeDecoder {

	private TypeDecoder[] decoders;
	
	public UnionDecoder(TypeDecoder[] decoders) {
		this.decoders = decoders;
	}
	
	@Override
	public Object decode(String string) {
		Object result = null;
		for(TypeDecoder format : this.decoders) {
			result = format.decode(string);
			if (result!=null) break;
		}
		return result;
	}


}
