package it.uniroma3.weir.vector.type;

public class IllegalTypeCastException extends RuntimeException {

	static final private long serialVersionUID = -2002848024054912524L;

	public IllegalTypeCastException(Type type, String value) {
		super("Cannot cast "+value+" to "+type);
	}

	public IllegalTypeCastException(Type type, String value, Exception e) {
		super("Cannot cast "+value+" to "+type, e);
	}

}
