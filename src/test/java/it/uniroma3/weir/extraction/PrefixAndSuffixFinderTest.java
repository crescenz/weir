package it.uniroma3.weir.extraction;

import static it.uniroma3.weir.fixture.VectorFixture.createVector;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.vector.PrefixAndSuffixFinder;
import it.uniroma3.weir.vector.Vector;

import org.junit.Test;

public class PrefixAndSuffixFinderTest extends WeirTest {
	
	static private void assertEmptyPrefix(String... values) {
		assertPrefix("", values);
	}
	
	static private void assertPrefix(String expected, String... values) {
		final Vector vector = createVector(values);
		final PrefixAndSuffixFinder finder = new PrefixAndSuffixFinder(vector);
		assertEquals(expected, finder.getPrefix());
	}

	static private void assertSuffix(String expected, String... values) {
		final Vector toCheck = createVector(values);
		final PrefixAndSuffixFinder finder = new PrefixAndSuffixFinder(toCheck);
		assertEquals(expected, finder.getSuffix());
	}

	static private void assertEmptySuffix(String... values) {
		assertSuffix("", values);
	}
	
	/* --- Tests for strings not having a prefix --- */

	@Test
	public void testGetPrefix_nulls() {
		assertEmptyPrefix(null, null);
	}

	@Test
	public void testGetPrefix_null_and_empty() {
		assertEmptyPrefix(null, "");
	}
	
	@Test
	public void testGetPrefix_empty() {
		assertEmptyPrefix("", "");
	}

	@Test
	public void testGetPrefix_empty_and_simple_string() {
		assertEmptyPrefix("", "a");
	}
	
	@Test
	public void testGetPrefix_equal_simple_string() {
		assertEmptyPrefix("a", "a");
	}
	
	@Test
	public void testGetPrefix_equal_long_string() {
		assertEmptyPrefix("aaa", "aaa");
	}
	
	@Test
	public void testGetPrefix_different_string() {
		assertEmptyPrefix("a", "b");
	}
	
	@Test
	public void testGetPrefix_different_long_string() {
		assertEmptyPrefix("aaa", "bbb");
	}
	@Test
	public void testGetPrefix_equal_number() {
		assertEmptyPrefix("1", "1");
	}

	@Test
	public void testGetPrefix_different_number() {
		assertEmptyPrefix("1", "2");
	}

	@Test
	public void testGetPrefix_equal_number_with_dot() {
		assertEmptyPrefix("1.11", "1.11");
	}

	@Test
	public void testGetPrefix_numbers_with_whitespace() {
		assertEmptyPrefix("1 1", "1 2");
	}

	@Test
	public void testGetPrefix_kilogram() {
		assertEmptyPrefix("kg 1", "kg 1");
	}

	@Test
	public void testGetPrefix_pound_in_lb() {
		assertEmptyPrefix("lb 1", "lb 1");
	}

	@Test
	public void testGetPrefix_pound_in_lbs() {
		assertEmptyPrefix("lbs 1", "lbs 1");
	}

	@Test
	public void testGetPrefix_pound_in_pound() {
		assertEmptyPrefix("pound 1", "pound 1");
	}

	@Test
	public void testGetPrefix_pound_in_pounds() {
		assertEmptyPrefix("pounds 1", "pounds 1");
	}

	@Test
	public void testGetPrefix_ounce() {
		assertEmptyPrefix("ounce 1", "ounce 1");
	}

	@Test
	public void testGetPrefix_ounces() {
		assertEmptyPrefix("ounces 1", "ounces 1");
	}

	@Test
	public void testGetPrefix_stone() {
		assertEmptyPrefix("st 1", "st 1");
	}

	@Test
	public void testGetPrefix_meter() {
		assertEmptyPrefix("m 1", "m 1");
	}

	@Test
	public void testGetPrefix_centimeter() {
		assertEmptyPrefix("cm 1", "cm 1");
	}

	@Test
	public void testGetPrefix_inches() {
		assertEmptyPrefix("in 1", "in 1");
	}

	@Test
	public void testGetPrefix_inches_short() {
		assertEmptyPrefix("\" 1", "\" 1");
	}

	@Test
	public void testGetPrefix_foot() {
		assertEmptyPrefix("ft 1", "ft 1");
	}

	@Test
	public void testGetPrefix_foot_short() {
		assertEmptyPrefix("' 1", "' 1");
	}

	@Test
	public void testGetPrefix_dollar() {
		assertEmptyPrefix("$ 1", "$ 1");
	}

	@Test
	public void testGetPrefix_euro() {
		assertEmptyPrefix("€ 1", "€ 1");
	}

	@Test
	public void testGetPrefix_pound_money() {
		assertEmptyPrefix("£ 1", "£ 1");
	}
	
	@Test
	public void testGetPrefix_dimensional_string_with_dot() {
		assertEmptyPrefix("$ 1.1", "$ 1.1");
	}

	@Test
	public void testGetPrefix_simple_urls() {
		assertEmptyPrefix("www.site.com", "www.site.com");
	}

	@Test
	public void testGetPrefix_urls_with_http() {
		assertEmptyPrefix("http://www.site.com", "http://www.site.com");
	}

	@Test
	public void testGetPrefix_urls_with_https() {
		assertEmptyPrefix("https://www.site.com", "https://www.site.com");
	}
	
	@Test
	public void testGetPrefix_phone_string() {
		assertEmptyPrefix("111-1111", "111-2222");
	}

	@Test
	public void testGetPrefix_isbn_string() {
		assertEmptyPrefix("111-1111111111", "111-2222222222");
	}

	@Test
	public void testGetPrefix_strings_begins_with_aus_sign() {
		assertEmptyPrefix("-1.0", "-1.0");
	}
	
	@Test
	public void testGetPrefix_strings_begins_with_plus_sign() {
		assertEmptyPrefix("+1.0", "+1.0");
	}
	
	@Test
	public void testGetPrefix_simple_date() {
		assertEmptyPrefix("1/1/2001", "1/1/2001");
	}
	
	@Test
	public void testGetPrefix_date_with_whitespace() {
		assertEmptyPrefix("1 1 2001", "1 1 2001");
	}

	@Test
	public void testGetPrefix_date_with_month_name() {
		assertEmptyPrefix("january 1 2001", "January 2 2002");
	}

	@Test
	public void testGetPrefix_date_with_short_month_name() {
		assertEmptyPrefix("jan 1 2001", "Jan 2 2002");
	}

	@Test
	public void testGetPrefix_strings_with_non_word_character_in_the_middle_without_prefix() {
		assertEmptyPrefix("a!a", "b!a");
	}

	@Test
	public void testGetPrefix_string_ends_with_non_word_character() {
		assertEmptyPrefix("pre!", "pre!");
	}

	@Test
	public void testGetPrefix_non_word_character() {
		assertEmptyPrefix("!", "!");
	}

	/* --- Tests for strings having a prefix --- */

	@Test
	public void testGetPrefix_string_with_non_word_character() {
		assertPrefix("!", "!a", "!");
	}

	@Test
	public void testGetPrefix_same_string_with_non_word_character() {
		assertPrefix("!", "!a", "!a");
	}

	@Test
	public void testGetPrefix_string_with_whitespace() {
		assertPrefix("a ", "a b", "a b");
	}

	@Test
	public void testGetPrefix_strings_begins_with_non_word_character() {
		assertPrefix("!", "!a", "!b");
	}

	@Test
	public void testGetPrefix_strings_begins_with_a_sequence_of_non_word_character() {
		assertPrefix("!#?", "!#?a", "!#?b");
	}

	@Test
	public void testGetPrefix_strings_with_non_word_character_in_the_middle() {
		assertPrefix("pre!", "pre!a", "pre!b");
	}

	@Test
	public void testGetPrefix_strings_with_aus_sign_in_the_middle() {
		assertPrefix("pre-", "pre-a", "pre-b");
	}

	@Test
	public void testGetPrefix_strings_with_double_points_in_the_middle() {
		assertPrefix("pre:", "pre:a", "pre:b");
	}

	@Test
	public void testGetPrefix_strings_with_prefix_and_nulls() {
		assertPrefix("!", "!a", null, "!");
	}

	/* --- Tests for strings not having a suffix --- */

	@Test
	public void testGetSuffix_nulls() {
		assertEmptySuffix(null, null);
	}

	@Test
	public void testGetSuffix_null_and_empty() {
		assertEmptySuffix(null, "");
	}
	
	@Test
	public void testGetSuffix_empty() {
		assertEmptySuffix("", "");
	}

	@Test
	public void testGetSuffix_empty_and_simple_string() {
		assertEmptySuffix("", "a");
	}
	
	@Test
	public void testGetSuffix_equal_simple_string() {
		assertEmptySuffix("a", "a");
	}
	
	@Test
	public void testGetSuffix_equal_long_string() {
		assertEmptySuffix("aaa", "aaa");
	}
	
	@Test
	public void testGetSuffix_different_string() {
		assertEmptySuffix("a", "b");
	}
	
	@Test
	public void testGetSuffix_different_long_string() {
		assertEmptySuffix("aaa", "bbb");
	}
	@Test
	public void testGetSuffix_equal_number() {
		assertEmptySuffix("1", "1");
	}

	@Test
	public void testGetSuffix_different_number() {
		assertEmptySuffix("1", "2");
	}

	@Test
	public void testGetSuffix_equal_number_with_dot() {
		assertEmptySuffix("1.11", "1.11");
	}

	@Test
	public void testGetSuffix_numbers_with_whitespace() {
		assertEmptySuffix("1 1", "1 2");
	}

	@Test
	public void testGetSuffix_kilogram() {
		assertEmptySuffix("kg 1", "kg 1");
	}

	@Test
	public void testGetSuffix_pound_in_lb() {
		assertEmptySuffix("lb 1", "lb 1");
	}

	@Test
	public void testGetSuffix_pound_in_lbs() {
		assertEmptySuffix("lbs 1", "lbs 1");
	}

	@Test
	public void testGetSuffix_pound_in_pound() {
		assertEmptySuffix("pound 1", "pound 1");
	}

	@Test
	public void testGetSuffix_pound_in_pounds() {
		assertEmptySuffix("pounds 1", "pounds 1");
	}

	@Test
	public void testGetSuffix_ounce() {
		assertEmptySuffix("ounce 1", "ounce 1");
	}

	@Test
	public void testGetSuffix_ounces() {
		assertEmptySuffix("ounces 1", "ounces 1");
	}

	@Test
	public void testGetSuffix_stone() {
		assertEmptySuffix("st 1", "st 1");
	}

	@Test
	public void testGetSuffix_meter() {
		assertEmptySuffix("m 1", "m 1");
	}

	@Test
	public void testGetSuffix_centimeter() {
		assertEmptySuffix("cm 1", "cm 1");
	}

	@Test
	public void testGetSuffix_inches() {
		assertEmptySuffix("in 1", "in 1");
	}

	@Test
	public void testGetSuffix_inches_short() {
		assertEmptySuffix("\" 1", "\" 1");
	}

	@Test
	public void testGetSuffix_foot() {
		assertEmptySuffix("ft 1", "ft 1");
	}

	@Test
	public void testGetSuffix_foot_short() {
		assertEmptySuffix("' 1", "' 1");
	}

	@Test
	public void testGetSuffix_dollar() {
		assertEmptySuffix("$ 1", "$ 1");
	}

	@Test
	public void testGetSuffix_euro() {
		assertEmptySuffix("€ 1", "€ 1");
	}

	@Test
	public void testGetSuffix_pound_money() {
		assertEmptySuffix("£ 1", "£ 1");
	}
	
	@Test
	public void testGetSuffix_dimensional_string_with_dot() {
		assertEmptySuffix("$ 1.1", "$ 1.1");
	}

	@Test
	public void testGetSuffix_urls_with_com_suffix() {
		assertEmptySuffix("www.site1.com", "www.site2.com");
	}

	@Test
	public void testGetSuffix_urls_with_it_suffix() {
		assertEmptySuffix("www.site1.it", "www.site2.it");
	}

	@Test
	public void testGetSuffix_urls_with_net_suffix() {
		assertEmptySuffix("www.site1.net", "www.site2.net");
	}

	@Test
	public void testGetSuffix_urls_with_http() {
		assertEmptySuffix("http://www.site.com", "http://www.site.com");
	}

	@Test
	public void testGetSuffix_urls_with_https() {
		assertEmptySuffix("https://www.site.com", "https://www.site.com");
	}
	
	@Test
	public void testGetSuffix_phone_string() {
		assertEmptySuffix("111-1111", "111-2222");
	}

	@Test
	public void testGetSuffix_isbn_string() {
		assertEmptySuffix("111-1111111111", "111-2222222222");
	}

	@Test
	public void testGetSuffix_strings_begins_with_aus_sign() {
		assertEmptySuffix("-1.0", "-1.0");
	}
	
	@Test
	public void testGetSuffix_strings_with_aus_sign_in_the_middle() {
		assertEmptySuffix("pre-a", "pre-b");
	}

	@Test
	public void testGetSuffix_strings_begins_with_plus_sign() {
		assertEmptySuffix("+1.0", "+1.0");
	}
	
	@Test
	public void testGetSuffix_simple_date() {
		assertEmptySuffix("1/1/2001", "1/1/2001");
	}
	
	@Test
	public void testGetSuffix_date_with_whitespace() {
		assertEmptySuffix("1 1 2001", "1 1 2001");
	}

	@Test
	public void testGetSuffix_date_with_month_name() {
		assertEmptySuffix("january 1 2001", "January 2 2002");
	}

	@Test
	public void testGetSuffix_date_with_short_month_name() {
		assertEmptySuffix("jan 1 2001", "Jan 2 2002");
	}

	@Test
	public void testGetSuffix_strings_with_non_word_character_in_the_middle_without_suffix() {
		assertEmptySuffix("a!a", "a!b");
	}

	@Test
	public void testGetSuffix_string_begins_with_non_word_character() {
		assertEmptySuffix("!pre", "!pre");
	}

	@Test
	public void testGetSuffix_non_word_character() {
		assertEmptySuffix("!", "!");
	}

	@Test
	public void testGetSuffix_not_all_elements_have_suffix() {
		assertEmptySuffix("a,", "a", "a,");
	}

	@Test
	public void testGetSuffix_brackets_with_dimensional() {
		assertEmptySuffix("1.65 m (5 ft 5 in)", "1.85 m (6 ft 1 in)");
	}

	@Test
	public void testGetSuffix_brackets() {
		assertEmptySuffix("2001: A Space Odyssey (1968)", "Blade Runner (1982)");
	}

	@Test
	public void testGetPrefix_dimensional_marker() {
		assertEmptySuffix("50 Kg", "40 Kg");
	}

	@Test
	public void testGetPrefix_strings_contains_dimensional_marker() {
		assertEmptySuffix("50 Kg.", "40 Kg.");
	}

	@Test
	public void testGetPrefix_height() {
		assertEmptySuffix("6' 10\"", "6' 5\"");
	}

	/* --- Tests for strings having a suffix --- */

	@Test
	public void testGetSuffix_string_with_non_word_character() {
		assertSuffix("!", "a!", "!");
	}

	@Test
	public void testGetSuffix_same_string_with_non_word_character() {
		assertSuffix("!", "a!", "a!");
	}

	@Test
	public void testGetSuffix_string_with_whitespace() {
		assertSuffix(" b", "a b", "a b");
	}

	@Test
	public void testGetSuffix_strings_ends_with_non_word_character() {
		assertSuffix("!", "a!", "b!");
	}

	@Test
	public void testGetSuffix_strings_ends_with_a_sequence_of_non_word_character() {
		assertSuffix("!#?", "a!#?", "b!#?");
	}

	@Test
	public void testGetSuffix_strings_with_non_word_character_in_the_middle() {
		assertSuffix("!suff", "a!suff", "b!suff");
	}

	@Test
	public void testGetSuffix_movie_title() {
		assertSuffix(" - Movie Info - Yahoo! Movies", "2001: A Space Odyssey (1968) - Movie Info - Yahoo! Movies", "Body Shots (1999) - Movie Info - Yahoo! Movies");
	}

}
