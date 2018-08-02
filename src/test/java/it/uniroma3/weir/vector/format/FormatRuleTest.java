package it.uniroma3.weir.vector.format;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class FormatRuleTest {

	private static final String REGEX_FOR_WORD = "[a-zA-Z]+";
	private static final String REGEX_FOR_NUMBER = "\\d+";

	private void assertExtractValue(String value, String regex, String expected) {
		FormatRule fr = new FormatRule(regex);
		assertEquals(expected, fr.extract(value));
	}

	private void assertExtractValue(String value, String regex, int index, String expected) {
		FormatRule fr = new FormatRule(regex, index);
		assertEquals(expected, fr.extract(value));
	}

	private void assertExtractValues(String value, String regex, String... expected) {
		FormatRule fr = new FormatRule(regex);
		assertEquals(Arrays.asList(expected), fr.extractAll(value));
	}

	/* --- Tests for extract a value using a FormatRule without index --- */
	
	@Test
	public void testExtract_word_string_word_regex() {
		assertExtractValue("abc", REGEX_FOR_WORD, "abc");
	}

	@Test
	public void testExtract_number_string_number_regex() {
		assertExtractValue("123", REGEX_FOR_NUMBER, "123");
	}

	@Test
	public void testExtract_string_with_word_and_number_word_regex() {
		assertExtractValue("abc 123", REGEX_FOR_WORD, "abc");
	}
	
	@Test
	public void testExtract_string_with_word_and_number_number_regex() {
		assertExtractValue("abc 123", REGEX_FOR_NUMBER, "123");
	}

	@Test
	public void testExtract_in_more_matchs_returns_first_result() {
		assertExtractValue("a b c", REGEX_FOR_WORD, "a");
	}
	
	@Test
	public void testExtract_empty_string() {
		assertExtractValue("", REGEX_FOR_WORD, null);
	}

	@Test
	public void testExtract_word_string_number_regex() {
		assertExtractValue("abc", REGEX_FOR_NUMBER, null);
	}

	@Test
	public void testExtract_number_string_word_regex() {
		assertExtractValue("123", REGEX_FOR_WORD, null);
	}

	@Test
	public void testExtract_empty_string_with_index() {
		assertExtractValue("", REGEX_FOR_WORD, 1, null);
	}

	/* --- Tests for extract a value using a FormatRule with index --- */
	
	@Test
	public void testExtract_with_index() {
		assertExtractValue("a b c", REGEX_FOR_WORD, 2, "c");
	}
	
	@Test
	public void testExtract_with_out_of_range_index() {
		assertExtractValue("a b c", REGEX_FOR_WORD, 3, null);
	}

	/* --- Tests for extract all values using a FormatRule --- */
	
	@Test
	public void testExtractAll_only_string() {
		assertExtractValues("a 1 b 2 c 3", REGEX_FOR_WORD, "a", "b", "c");
	}

	@Test
	public void testExtractAll_only_number() {
		assertExtractValues("a 1 b 2 c 3", REGEX_FOR_NUMBER, "1", "2", "3");
	}
	
	@Test
	public void testExtractAll_empty_string() {
		assertExtractValues("", REGEX_FOR_WORD, new String[]{});
	}
	
}
