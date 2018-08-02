package it.uniroma3.weir.vector.format;

import java.util.List;

import static it.uniroma3.weir.vector.format.Regexps.*;
/**
 * 
 * Find the correct format for extracting a date from a string
 *
 */
public class DateFormatFinder {

	/* min-max bounds for date components */
	static private final int[] YEAR_BOUNDS  = {1800,2020}; /*!!*/	
	static private final int[] MONTH_BOUNDS = {1, 	  12};
	static private final int[] DAY_BOUNDS   = {1, 	  31};

	static public int indexOfMonth(List<String> tokens) {
		int index=indexOfMonthName(tokens);
		return ( index==-1 ? indexOfMonthNumber(tokens) : index );
	}

	static private int indexOfMonthName(List<String> tokenizedDate) {
		for (int i = 0; i < tokenizedDate.size(); i++) {
			int month = getMonthNameByNumber(tokenizedDate.get(i));
			if (month != -1) {
				return i;
			}
		}
		return -1;
	}
	
	static private int indexOfMonthNumber(List<String> dateTokens) {
		return indexOfNumberWithinRange(dateTokens, MONTH_BOUNDS[0], MONTH_BOUNDS[1]);	
	}

	static public int indexOfDay(List<String> dateTokens) {
		return indexOfNumberWithinRange(dateTokens, DAY_BOUNDS[0], DAY_BOUNDS[1]);
	}

	static public int indexOfYear(List<String> dateTokens) {
		return indexOfNumberWithinRange(dateTokens, YEAR_BOUNDS[0],YEAR_BOUNDS[1]);
	}

	static private int indexOfNumberWithinRange(List<String> tokens, int low, int high) {
		for (int i=0; i<tokens.size(); i++) {
			final String token = tokens.get(i);
			if (isNumberWithinRange(token, low, high)) {
				return i;
			}
		}
		return -1;
	}

	static private boolean isNumberWithinRange(final String token, int low, int high) {
		if (token==null) return false;
		final Integer number = extractNumber(token);
		return (number!=null && number>=low && number<=high);
	}

	static private Integer extractNumber(String value) {
		final String s = FormatRule.numberRule.extract(value);
		return (s!=null ? Integer.valueOf(s) : null);
	}


	static public int getMonth(String s) {
		/* is it a jan | january spelled month ?*/
		int month = getMonthNameByNumber(s);
		if (month==-1) {
			// no: get it as 1..12 month number index
			Integer number = extractNumber(s);
			month = (number==null ? -1 : number);
		}
		return month;
	}

	static private int getMonthNameByNumber(String token) {
		for (int i=0; i<12; i++) {
			if (token.equalsIgnoreCase(MONTHS[i]) ||
				token.equalsIgnoreCase(MONTHS_ABBR[i])) {
				return i + 1;
			}
		}
		return -1;
	}
	
}
