package it.uniroma3.weir.vector.format;


public interface Decoders {

	static final public TypeDecoder ISBN_DECODER = new ISBNDecoder();
	
	static final public TypeDecoder NUMBER_DECODER = new UnionDecoder(NumberDecoders.values());
	
	static final public TypeDecoder DATE_DECODER = new UnionDecoder(DateDecoders.values());

	static final public TypeDecoder PHONE_DECODER = new PhoneDecoder();

}
