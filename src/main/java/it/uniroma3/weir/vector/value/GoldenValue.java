package it.uniroma3.weir.vector.value;

import it.uniroma3.weir.model.Webpage;

/**
 * Represents a <em>golden</em> value, 
 * i.e., a given string value considered as correct.
 * 
 * Used during experimental evaluation to load
 * golden datasets
 */
public class GoldenValue extends ExtractedValue {
	
	static final private long serialVersionUID = -4675878756025828014L;
	
	public GoldenValue(Webpage page, String value) {
		super(page,value);
	}

}
