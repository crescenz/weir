package it.uniroma3.weir.vector.format;

public interface Regexps {

	/* general-use */
	static final public String TOKEN_REGEXP  = "\\w+";

	static final public String LETTER_REGEXP = "[A-Za-z'\"$€£]+";

	static final public String NUMBER_REGEXP = "(-)?(\\d+(,\\d+)*(\\.\\d+)?|\\.\\d+)";

	/* numbers */
	static final public String EXP_SUFFIX_REGEXP  = "[eE](-)?\\d{1,4}";
	static final public String EXPONENTIAL_REGEXP = NUMBER_REGEXP + EXP_SUFFIX_REGEXP;
	static final public String MILLION_REGEXP     = NUMBER_REGEXP + " Mil";
	static final public String KILO_REGEXP        = NUMBER_REGEXP + "[kK]";
	static final public String HUNDREDS_REGEXP    = "(-)?\\d+,\\d{3}";	
	static final public String FOOT_INCHES_REGEXP = "\\d-\\d{1,2}";
	
	/* phone */
	static final public String US_PHONE_REGEXP = "\\d{3,4}";

	/* url */
	static final public String URL_REGEX = "(^(http(s)?://)?www([.][^.,]{1,48}){1,4})";

	/* isbn */
	static final public String ISBN_REGEXP = "\\d{13}|\\d{10}";

	/* date */
	static final public String STANDARD_DATE_REGEXP = 
			"\\d{1,2}\\W\\d{1,2}\\W\\d{4}"+"|"+      // 01-12-1999,	1/12/1999,	1 12 1999
			"\\d{4}\\W\\d{1,2}\\W\\d{1,2}"+"|"+      // 1999-12-01,	1999/12/1,	1999 12 1
			"\\d{1,2}\\W\\w{3}\\W{1,2}\\d{4}"+"|"+   // 01 Dec 1999
			"\\d{1,2}\\W\\w{3,9}\\W{1,2}\\d{4}"+"|"+ // 01 December 1999
			"\\w{3,9} \\d{1,2}\\W{1,2}\\d{4}"+"|"+   // Dec 01, 1999 December 01, 1999
			"\\w{6,9}, \\w{3,9} \\d{1,2}, \\d{4} \\d{1,2}:\\d{1,2}"+"|"+ // Tuesday, December 7, 2010 2:31
			"\\d{4}\\W\\d{1,2}\\W\\d{1,2} \\d{1,2}:\\d{1,2}"+"|"+        // 2010-12-07 2:31
			"\\d{1,2}\\W\\d{1,2}\\W\\d{4} in.*"+"|"+ // 01/12/1999 in Rome
			"\\d{1,2}(th|rd|nd|st) \\w{3,9} \\d{4}"+"|"+ // 13th August 1979,	3rd December 1979,	22nd August 1979,	31st January 1979
			"\\w{3,9} \\d{1,2}, \\d{4} \\(\\d{4}-\\d{2}-\\d{2}\\) \\(age \\d{2}\\)?"+"|"+ // December 1, 1999 (1999-01-12) (age 28)
			"\\d{1,2} \\w{3,9}\\W{1,2}\\d{4} \\(\\d{4}-\\d{2}-\\d{2}\\) \\(age \\d{2}\\)?";	// 1 December, 1999 (1999-01-12) (age 28)
	
	static final public String NODAY_DATE_REGEXP = 	
			"\\d{4}\\W\\d{1,2}"+"|"+	// 1999 12
			"\\d{1,2}\\W\\d{4}"+"|"+	// 12/1999
			"\\w{3} \\d{4}"+"|"+		// Dec 1999
			"\\w{3,9} \\d{4}";			// December 1999
	
	static final public String YEAR_DATE_REGEXP =
			"^\\d{4}$";					// ^1999$

	static final public String[] MONTHS      = {"january","february","march","april","may","june","july","august","september","october","november","december"};

	static final public String[] MONTHS_ABBR = {"jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};
	
}
