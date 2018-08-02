package it.uniroma3.weir.vector.format;


import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static it.uniroma3.weir.vector.format.Regexps.*;
/**
 *
 * Represents a formatting rule: a tool 
 * useful for extracting data from strings
 * representing values according to a
 * given syntactical {@link Pattern}.
 * 
 * @see java regular expressions syntax
 */
public class FormatRule {

	/* a rule to extract tokens as consecutive word-chars */
	static final public FormatRule tokenRule = new FormatRule(TOKEN_REGEXP);

	/* a rule to extract tokens as consecutive numbers */
	static final public FormatRule numberRule = new FormatRule(NUMBER_REGEXP);

	/* a rule to extract tokens as consecutive letters */
	static final public FormatRule letterRule = new FormatRule(LETTER_REGEXP);

	
	final private Pattern pattern; // a java regex making us of group

	final private int matchIndex;  // the index of the group to extract, if any

	public FormatRule(String regex) {
		this(regex, 0);
	}

	public FormatRule(String regex, int index) {
		this.pattern = Pattern.compile(regex);
		this.matchIndex = index;
	}

	private FormatRule(Pattern pattern, int index) {
		this.pattern = pattern;
		this.matchIndex = index;
	}

	public FormatRule atIndex(int index) {
		return new FormatRule(this.pattern, index);
	}
	
	public String extract(String s) {
		if (this.matchIndex==-1) return null;
		final Matcher matcher = this.pattern.matcher(s);
		matcher.find();
		for (int i=0; i<this.matchIndex; i++) {
			matcher.find();
		}
		try {
			return matcher.group();
		}
		catch (final IllegalStateException e) {
			// no match means that the rule does not apply
			return null;
		}
	}

	public LinkedList<String> extractAll(String s) {
		final LinkedList<String> values = new LinkedList<>();
		try {
			final Matcher matcher = this.pattern.matcher(s);
			while (matcher.find())
				values.add(matcher.group());
		}
		catch (final IllegalStateException e) {
			// no match means that the rule does not apply
		}
		return values;
	}	
	
}
