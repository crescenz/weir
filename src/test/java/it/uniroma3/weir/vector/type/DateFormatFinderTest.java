package it.uniroma3.weir.vector.type;

import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.vector.format.DateFormatFinder;
import it.uniroma3.weir.vector.format.FormatRule;

import java.util.List;

import org.junit.Test;

public class DateFormatFinderTest {

	private static final String REGEX_FOR_TOKEN_SEARCH = "\\w+";
	private static final FormatRule RULE_FOR_TOKEN = new FormatRule(REGEX_FOR_TOKEN_SEARCH);
	
	private static void assertDayIndex(int expected, String value) {
		List<String> tokenizedValue = RULE_FOR_TOKEN.extractAll(value);
		assertEquals(expected, DateFormatFinder.indexOfDay(tokenizedValue));
	}

	private static void assertMonthIndex(int expected, String value) {
		List<String> tokenizedValue = RULE_FOR_TOKEN.extractAll(value);
		assertEquals(expected, DateFormatFinder.indexOfMonth(tokenizedValue));
	}

	private static void assertYearIndex(int expected, String value) {
		List<String> tokenizedValue = RULE_FOR_TOKEN.extractAll(value);
		assertEquals(expected, DateFormatFinder.indexOfYear(tokenizedValue));
	}

	private static void assertMonthNumber(int expected, String value) {
		assertEquals(expected, DateFormatFinder.getMonth(value));
	}

	/* --- Tests for find the day index  --- */
	
	@Test
	public void testSearchDayIndex_in_more_matchs_returns_first_result(){
		assertDayIndex(0, "1 1 2001");
	}
	
	@Test
	public void testSearchDayIndex_day_not_present(){
		assertDayIndex(-1, "a");
	}
	
	@Test
	public void testSearchDayIndex_empty_string(){
		assertDayIndex(-1, "");
	}

	/* --- Tests for find the month index  --- */
	
	@Test
	public void testSearchMonthIndex_month_as_word(){
		assertMonthIndex(1, "1 January 2001");
	}
	
	@Test
	public void testSearchMonthIndex_date_non_ambiguous(){
		assertMonthIndex(1, "15 12 2001");
	}
	
	@Test
	public void testSearchMonthIndexmonth_as_word_in_more_matchs_returns_first_result(){
		assertMonthIndex(0, "January February");
	}
	
	@Test
	public void testSearchMonthIndex_month_as_number_in_more_matchs_returns_first_result(){
		assertMonthIndex(0, "1 1 2001");
	}
	
	@Test
	public void testSearchMonthIndex_empty_string(){
		assertMonthIndex(-1, "");
	}
	
	@Test
	public void testSearchMonthIndex_month_not_present(){
		assertMonthIndex(-1, "a");
	}

	/* --- Tests for find the year index  --- */
	
	@Test
	public void testSearchYearIndex_complete_date(){
		assertYearIndex(2, "1 1 2001");
	}
	
	@Test
	public void testSearchYearIndex_in_more_matchs_returns_first_result(){
		assertYearIndex(0, "2001 2002");
	}
	
	@Test
	public void testSearchYearIndex_empty_string(){
		assertYearIndex(-1, "");
	}

	@Test
	public void testSearchYearIndex_year_not_present(){
		assertYearIndex(-1, "a");
	}

	/* --- Tests for find the month number given a String  --- */
	
	@Test
	public void testToMonthNumber_month_as_word(){
		assertMonthNumber(1, "January");
	}

	@Test
	public void testToMonthNumber_month_as_number(){
		assertMonthNumber(1, "1");
	}

	@Test
	public void testToMonthNumber_empty_(){
		assertMonthNumber(-1, "");
	}

	@Test
	public void testToMonthNumber_wrong_string(){
		assertMonthNumber(-1, "a");
	}
	
}
