package it.uniroma3.weir.vector.type;


import static it.uniroma3.weir.vector.format.Regexps.URL_REGEX;
import static it.uniroma3.weir.vector.value.ValueDistances.JSDISTANCE;
import it.uniroma3.weir.vector.format.FormatRule;
/**
 * 
 * A {@link Type} for representing URLs
 *
 */
public class URLType extends Type {

	static final private long serialVersionUID = 5744560439745828280L;
	
	static final private FormatRule URL_RULE = new FormatRule(URL_REGEX);

	@Override
	public Object tryCastNonNull(String value) {
		return URL_RULE.extract(value);
	}
	
	@Override
	public double distance(Object value1, Object value2) {
		return JSDISTANCE.distance(value1, value2);
	}
		
	@Override
	public Type getParent() {
		return Type.STRING;
	}
	
	@Override
	public String toString() {
		return "URL";
	}

}
