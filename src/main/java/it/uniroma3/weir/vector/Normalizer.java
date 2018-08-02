package it.uniroma3.weir.vector;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.value.ExtractedValue;
import it.uniroma3.weir.vector.value.Value;

import java.util.*;
import java.util.regex.Pattern;

/**
 * This class is in charge of finding a common {@link Type} to
 * an {@link ExtractedVector} of  {@link ExtractedValue}s once
 * they have been clean up from common prefixes, suffixes, and 
 * preserving only letters, whitespaces, numbers, currencies, 
 * and punctuation symbols.
 * 
 * Sequence of whitespaces will be reduced to a single space.
 * 
 * An alternative name for this class could be NormalizedVectorBuilder.
 * 
 */
public class Normalizer {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	// preserve letters, whitespaces, numbers, currencies, and punctuations. That's all!
	static final private String CANONICAL_CHARS_REGEX = "[^\\p{L}\\p{Z}\\p{N}\\p{Sc}\\p{P}]";
	static final private String SEQUENCES_OF_WHITESPACES = "[\\p{Z}\\s]+";
	
	static final private Pattern SPACES_PATTERN = Pattern.compile(SEQUENCES_OF_WHITESPACES);
	static final private Pattern CHARS_PATTERN  = Pattern.compile(CANONICAL_CHARS_REGEX);
	
	private Map<Type, Integer> type2freq;

	private VectorCaster caster;

	public Normalizer() {
		this.caster = new VectorCaster();
		this.type2freq = new HashMap<>();		
	}

	public Vector normalize(ExtractedVector extracted) {
		PrefixAndSuffixFinder finder = new PrefixAndSuffixFinder(extracted);
		final String prefix = finder.getPrefix();
		final String suffix = finder.getSuffix();
		log.trace("Prefix found: \'"+prefix+"\'");
		log.trace("Suffix found: \'"+suffix+"\'");
		final String[] values = normalizeValues(extracted.getElements(), prefix, suffix);
		log.trace();
		log.trace("Looking for a type:");
		final Type type = this.caster.findMostSpecificType(values);
		log.trace("Casting to "+type);
		incrementTypeFrequency(type);
		return type.createTypedVector(extracted, values);
	}

	private String[] normalizeValues(Value[] values, String prefix, String suffix) {

		/* make a simple regexp to strip prefixes and/or suffixes */
		// CHECK why regex just to strip prefixes and suffixes?
		final Pattern stripPattern = makeStrippingPattern(prefix, suffix); 
		
		final String[] normalized = new String[values.length];
		for(int i=0; i<values.length; i++) {
			if (values[i].isNull()) {
				normalized[i] = null;
			} else {
				final String value = values[i].toString();
				/* strip prefixes & suffixes */
				String stripped = null;
				if (stripPattern!=null)
					stripped = stripPattern.matcher(value).replaceAll("");
				else
					stripped = value;
				/* keep only a certain subset of chars */
				final String cleaned = CHARS_PATTERN.matcher(stripped).replaceAll("");
				
				/* normalize sequences of whitespaces as a single blank space */
				final String spaced = SPACES_PATTERN.matcher(cleaned).replaceAll(" ");
				
				/* the empty string is a null marker */
				normalized[i] = emptyStringAsNullMarker(spaced);
				logValue(normalized, i, value);
			}
		}
		return normalized;
	}

	private void logValue(final String[] normalized, int i, final String value) {
		if (!Objects.equals(value,normalized[i]))
			log.trace(i+"-th value \'"+value+"\' normalized to \'"+normalized[i]+"\'");
	}

	private Pattern makeStrippingPattern(String prefix, String suffix) {
		if (prefix.isEmpty() && suffix.isEmpty()) return null;
		
		String regex = null;		
		if (prefix.isEmpty() && !suffix.isEmpty()) {
			regex = regex(suffix) + "$";
		}
		if (!prefix.isEmpty() && suffix.isEmpty()) {
			regex = "^"+regex(prefix) ;
		}
		if (!prefix.isEmpty() && !suffix.isEmpty()) {
			regex = "(^"+regex(prefix) +"|"+regex(suffix) + "$)";
		}
		return Pattern.compile(regex);
	}

	final private String emptyStringAsNullMarker(String s) {
		return ( s.trim().isEmpty() ? null : s );
	}

	private String regex(String regex) {
		StringBuilder result = new StringBuilder();
		for (int i=0; i<regex.length(); i++) {
			char character = regex.charAt(i);
			if (isCharToEscapeInRegex(character)) {
				result.append(escapeInRegex(character));
			} else {
				result.append(character);
			}
		}
		return result.toString();
	}

	final private String escapeInRegex(char character) {
		return "\\" + character;
	}

	static final private char[] CHARS_TO_ESCAPE_IN_REGEX = {
		'\\','(','[','{','^','-','|',']','}',')','?','*','+','.'
	};


	final private boolean isCharToEscapeInRegex(char character) {
		for(char c : CHARS_TO_ESCAPE_IN_REGEX)
			if (c==character)
				return true;
		return false;
	}

	private void incrementTypeFrequency(Type type) {
		if (type2freq.containsKey(type)) {
			type2freq.put(type, type2freq.get(type) + 1);
		} else {
			type2freq.put(type, 1);
		}
	}

}
