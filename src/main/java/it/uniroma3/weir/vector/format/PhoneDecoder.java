package it.uniroma3.weir.vector.format;

import static it.uniroma3.weir.vector.format.Regexps.US_PHONE_REGEXP;

import java.util.List;

public class PhoneDecoder implements TypeDecoder {

	static final private FormatRule US_PHONE_RULE = new FormatRule(US_PHONE_REGEXP);

	@Override
	public Object decode(String value) {
		if (hasPhoneFormat(value)) {
			final List<String> numbers = US_PHONE_RULE.extractAll(value);
			return numbers.get(0) + "-" + numbers.get(1) + "-" + numbers.get(2);
		}
		else if (value.matches("\\d{10}")) { // e.g., 2016597074
			return value.substring(0,3) + "-" + value.substring(3,6) + "-" + value.substring(6,10);
		}
		return null;
	}

	private boolean hasPhoneFormat(String value) {
		return  value.matches("\\d{3}-\\d{3}-\\d{4}") || 		// 201-659-7074
				value.matches("\\d{3}\\) \\d{3}-\\d{4}") ||		// 201) 659-7074
				value.matches("\\d{3}/\\d{3}-\\d{4}.*");		// 202/338-3830
	}

}
