package it.uniroma3.weir.vector.format;

import static it.uniroma3.weir.vector.format.Regexps.ISBN_REGEXP;

public class ISBNDecoder implements TypeDecoder {

	static final private FormatRule ISBN_RULE = new FormatRule(ISBN_REGEXP);
	
	@Override
	public Object decode(String string) {
		final String minusFree = string.replaceAll("-", "");
		if (minusFree.length()==10 || minusFree.length()==13 || minusFree.contains("/")) {
			return ISBN_RULE.extract(minusFree);
		}
		return null;
	}

}
