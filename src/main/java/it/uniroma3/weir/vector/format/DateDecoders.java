package it.uniroma3.weir.vector.format;

import java.util.List;
import java.util.regex.Pattern;

import it.uniroma3.weir.vector.value.Date;
import static it.uniroma3.weir.vector.format.Regexps.*;
import static it.uniroma3.weir.vector.format.FormatRule.*;
import static it.uniroma3.weir.vector.format.DateFormatFinder.*;

public enum DateDecoders implements TypeDecoder {

	STANDARD(STANDARD_DATE_REGEXP) {}, // e.g., 21 october 2015
	NODAY(NODAY_DATE_REGEXP) {},       // e.g., jan 2007
	YEAR(YEAR_DATE_REGEXP) {};         // e.g., 2008 
	
	static final private Pattern cardinals = Pattern.compile("th|rd|nd|st");
	
	static private String stripCardinals(String s) {
		return cardinals.matcher(s).replaceAll("");
	}
	
	final protected FormatRule rule;
	
	private DateDecoders(String regex) {
		this(regex,0);
	}

	private DateDecoders(String regex, int index) {
		this.rule = new FormatRule(regex, index);
	}
	
	/**
	 * 
	 * @return the {@link Date} in this format or null
	 *         if this format does not apply
	 */
	@Override
	public Date decode(String value) {
		final String extracted = this.rule.extract(value);
		if (extracted==null) return null;

		final List<String> tokens = tokenRule.extractAll(extracted);			
		return this.parseDateComponents(tokens);
	}

	private Date parseDateComponents(List<String> tokens) {
		/* search month; day; year (following this order: from most to least
		 * specific syntax); remove tokens once parse make easier the search
		 * of the next token.
		 */
		int day =-1, month=-1, year=-1;
		int index = -1;
		index = indexOfMonth(tokens);
		if (index!=-1) {
			final String token = tokens.set(index, null);
			month = getMonth(token);			
		}
		index = indexOfDay(tokens);
		if (index!=-1) {
			final String token = tokens.set(index, null);
			day = Integer.valueOf(stripCardinals(token));
		}
		index = indexOfYear(tokens);
		if (index!=-1) {
			final String token = tokens.set(index, null);
			year = Integer.valueOf(token);
		}
		return new Date(day,month,year);
	}
	
}
