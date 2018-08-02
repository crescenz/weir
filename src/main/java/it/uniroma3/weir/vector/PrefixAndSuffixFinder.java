package it.uniroma3.weir.vector;

import it.uniroma3.weir.vector.format.Regexps;
import it.uniroma3.weir.vector.format.FormatRule;
import it.uniroma3.weir.vector.unitmeasure.UnitMeasure;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * 
 * Find a common prefix and suffix for the values of a vector
 *
 */
public class PrefixAndSuffixFinder {

	static final private String PREFIX_SEPARATOR = "[ :!\\?\\*\\+\\.\\|\\-\\^\\{\\}\\[\\]]"; //[ :!?*+.|-^{}[]];
	static final private Pattern PREFIX_PATTERN = Pattern.compile(PREFIX_SEPARATOR);
	static final private String NUMBER_REGEX_PATTERN = "\\d";
	static final private FormatRule RULE_FOR_NUMBER = new FormatRule(NUMBER_REGEX_PATTERN);

	private Vector vector;
	
	// suffixes are searched as prefixes over reversed values
	private boolean reverseMode;	

	public PrefixAndSuffixFinder(Vector vector) {
		this.vector = vector;		
		this.reverseMode = false;
	}
		
	private boolean isReverseModeEnabled() {
		return reverseMode;
	}

	private void setReverseMode(boolean reverseMode) {
		this.reverseMode = reverseMode;
	}
	
	/**
	 * was getPrefixWithoutSplittingWords()
	 * @return
	 */
	public String getPrefix() {		
		final String pivot = findFirstNonNullValue();
		if (pivot==null) return "";

		String prefix = null;
		
		for(int len=1; len<=pivot.length(); len++) {
			final String candidatePrefix = pivot.substring(0, len);
			final char charAt = candidatePrefix.charAt(candidatePrefix.length()-1);
			if (PREFIX_PATTERN.matcher(Character.toString(charAt)).matches()) {
				if (isAcommonPrefix(candidatePrefix)) {
					prefix = candidatePrefix;
				} else break;
			}
		}
		return finalCheckOverWhitelistedPrefix(prefix);
	}
	
	private String findFirstNonNullValue() {
		Iterator<String> valuesIt = stringValueIterator();
		while (valuesIt.hasNext()) {
			String value = valuesIt.next();
			if (value!=null)
				return value;
		}
		return null;
	}

	private boolean isAcommonPrefix(String prefix) {
		Iterator<String> valuesIt = stringValueIterator();
		while (valuesIt.hasNext()) {
			String value = valuesIt.next();
			if (value!=null && !value.startsWith(prefix)) {
				return false;
			}
		}
		return true;
	}

	private String finalCheckOverWhitelistedPrefix(String ffix) {
		final String first = findFirstNonNullValue();
		// cannot discard prefix=value
		if (ffix==null || first==null || ffix.equals(first) ) return "";
		
		/* if looking for suffixes, reverse the string befor checking */
		if (isReverseModeEnabled()) ffix = reverse(ffix);
		
		// there are pre/suffixes we want to keep, e.g., http://
		return !isPrefixSuffixToPreserve(ffix.trim()) ? ffix : "";
	}
	
	/**
	 * was getSuffixWithoutSplittingWords()
	 * @return
	 */
	public String getSuffix() {
		setReverseMode(true);
		final String suffix =  getPrefix();
		setReverseMode(false);
		return suffix;
	}

	final private String reverse(String s) {
		if (s==null) return null;
		StringBuilder result = new StringBuilder(s);
		return result.reverse().toString();
	}

	private Iterator<String> stringValueIterator() {
		final Vector vector = this.vector;
		if (isReverseModeEnabled()) {
			return new Iterator<String>() {
				final Iterator<String> innerIt = vector.stringsIterator();
				@Override
				public boolean hasNext() {
					return innerIt.hasNext();
				}
				@Override
				public String next() {
					return reverse(innerIt.next());
				}
				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
		else return vector.stringsIterator();
	}
	
	/**
	 * TODO introduce regexp here.
	 * 
	 * heuristic safe-guards to choose which prefixes and suffixes 
	 * can be removed and which cannot
	 */
	final private boolean isPrefixSuffixToPreserve(String ffix) {
		return 	   ffix.equals("") 
				|| ffix.equals("-")
				|| ffix.startsWith("http")
				|| ffix.startsWith("www.")
				|| ffix.endsWith(".com")
				|| ffix.endsWith(".it")
				|| ffix.endsWith(".net")
				|| containsNumbers(ffix) 
				|| containsUnitMeasureTags(ffix)
				|| isAMonth(ffix);
	}

	private boolean containsNumbers(String prefix) {
		return (RULE_FOR_NUMBER.extractAll(prefix).size() != 0) ;
	}

	static final private Pattern UNIT_MEASURE_REGEXP = Pattern.compile("[^\\d\\w'\"$€£]");

	private boolean containsUnitMeasureTags(final String candidateMarker) {
		final String marker = candidateMarker.toLowerCase().trim();
		final String prefix = UNIT_MEASURE_REGEXP.matcher(marker).replaceAll("");
		for (UnitMeasure um : UnitMeasure.values()) {
			for (String unitMarker : um.getUnitMarkers()) {
				if (prefix.equals(unitMarker))
					return true;
			}
		}
		return false;
	}

	private boolean isAMonth(String prefix) {
		String lowerCase = prefix.toLowerCase();
		for (String m : Regexps.MONTHS) {
			if (lowerCase.trim().equals(m))
				return true;
		}
		for (String m : Regexps.MONTHS_ABBR) {
			if (lowerCase.trim().equals(m))
				return true;
		}
		return false;
	}

}
