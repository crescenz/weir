package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.fixture.Asserts.assertDoubleEquals;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.vector.value.Date;

import org.junit.Test;

public class DateTypeTest {

	private static void assertExpectedExtraction(Date expected, String value) {
		assertEquals(expected, Type.DATE.tryCastNonNull(value));
	}

	private static void assertDateDistance(double expected, Date value1, Date value2) {
		assertDoubleEquals(expected, Type.DATE.distance(value1, value2));
	}

	/* --- Tests for extraction  --- */
	
	@Test
	public void testTryCast_day_month_year(){
		assertExpectedExtraction(new Date(15,12,2001), "15 12 2001");
	}

	@Test
	public void testTryCast_month_day_year(){
		assertExpectedExtraction(new Date(15,12,2001), "12 15 2001");
	}

	@Test
	public void testTryCast_year_day_month(){
		assertExpectedExtraction(new Date(15,12,2001), "2001 15 12");
	}

	@Test
	public void testTryCast_year_month_day(){
		assertExpectedExtraction(new Date(15,12,2001), "2001 12 15");
	}

	@Test
	public void testTryCast_ambiguos_date(){
		assertExpectedExtraction(new Date(11,5,2001), "05 11 2001");
	}

	@Test
	public void testTryCast_second_ambiguos_date(){
		assertExpectedExtraction(new Date(5,11,2001), "11 05 2001");
	}

	@Test
	public void testTryCast_date_with_minus_sign(){
		assertExpectedExtraction(new Date(15,12,2001), "15-12-2001");
	}

	@Test
	public void testTryCast_date_with_slash(){
		assertExpectedExtraction(new Date(12,1,1990), "01/12/1990");
	}

	@Test
	public void testTryCast_month_as_string(){
		assertExpectedExtraction(new Date(15,12,2001), "15 December 2001");
	}

	@Test
	public void testTryCast_date_with_short_month_format(){
		assertExpectedExtraction(new Date(15,12,2001), "Dec 15 2001");
	}

	@Test
	public void testTryCast_date_with_short_month_format_and_comma(){
		assertExpectedExtraction(new Date(15,12,2001), "Dec 15, 2001");
	}
	
	@Test
	public void testTryCast_date_with_comma(){
		assertExpectedExtraction(new Date(15,12,2001), "December 15, 2001");
	}

	@Test
	public void testTryCast_string_contains_birthdate_and_bornplace(){
		assertExpectedExtraction(new Date(4,3,1982), "03/04/1982 in Salvador De Bahia");
	}

	@Test
	public void testTryCast_string_contains_birthdate_and_missing_bornplace(){
		assertExpectedExtraction(new Date(4,3,1982), "03/04/1982 in");
	}

	@Test
	public void testTryCast_date_without_day_and_month_as_word(){
		assertExpectedExtraction(new Date(12,2001), "December 2001");
	}

	@Test
	public void testTryCast_date_without_day_and_month_as_number(){
		assertExpectedExtraction(new Date(12,2001), "2001-12");
	}

	@Test
	public void testTryCast_date_with_only_year_as_number(){
		assertExpectedExtraction(new Date(2001), "2001");
	}

	@Test
	public void testTryCast_date_with_zeros(){
		assertExpectedExtraction(new Date(0,0,0), "0/0/0000");
	}

	@Test
	public void testTryCast_date_of_birth_long_format(){
		assertExpectedExtraction(new Date(3,1,1979), "January 3, 1979 (1979-01-03) (age 28)");
	}

	@Test
	public void testTryCast_date_of_birth_long_format_day_before_month(){
		assertExpectedExtraction(new Date(3,1,1979), "3 January, 1979 (1979-01-03) (age 28)");
	}

	@Test
	public void testTryCast_date_of_birth_with_th(){
		assertExpectedExtraction(new Date(13,8,1979), "13th August 1979");
	}

	@Test
	public void testTryCast_date_of_birth_with_rd(){
		assertExpectedExtraction(new Date(3,12,1979), "3rd December 1979");
	}

	@Test
	public void testTryCast_date_of_birth_with_nd(){
		assertExpectedExtraction(new Date(22,8,1979), "22nd August 1979");
	}

	@Test
	public void testTryCast_date_of_birth_with_st(){
		assertExpectedExtraction(new Date(31,1,1979), "31st January 1979");
	}

	@Test
	public void testTryCast_date_with_day_of_week_and_hour(){
		assertExpectedExtraction(new Date(7,12,2010), "Tuesday, December 7, 2010 2:31");
	}

	@Test
	public void testTryCast_date_with_year_first_and_hour(){
		assertExpectedExtraction(new Date(7,12,2010), "2010-12-07 2:31");
	}

	@Test
	public void testTryCast_date_with_month_slash_year(){
		assertExpectedExtraction(new Date(9,1996), "9/1996");
	}

	@Test
	public void testTryCast_empty_string(){
		assertExpectedExtraction(null, "");
	}
	
	@Test
	public void testTryCast_string(){
		assertExpectedExtraction(null, "a");
	}

	@Test
	public void testTryCast_number_with_comma(){
		assertExpectedExtraction(null, "1.1");
	}

	@Test
	public void testTryCast_number_with_dot(){
		assertExpectedExtraction(null, "1,1");
	}
	
	@Test
	public void testTryCast_number(){
		assertExpectedExtraction(null, "15122001");
	}
	
	@Test
	public void testTryCast_missing_month(){
		assertExpectedExtraction(null, "15 2001");
	}

	@Test
	public void testTryCast_date_with_wrong_month(){
		assertExpectedExtraction(null, "15 15 2001");
	}

	@Test
	public void testTryCast_size_in_not_a_date(){
		assertExpectedExtraction(null, "3.94 x 6.85 x 0.39");
	}

	@Test
	public void testTryCast_string_contains_a_standard_date_with_month_as_word_is_not_a_date(){
		assertExpectedExtraction(null, "HarperCollins UK (June 1, 1995)"); //for www.amozon.com
	}

	@Test
	public void testTryCast_string_contains_a_month_year_date_with_moth_as_word_is_not_a_date(){
		assertExpectedExtraction(null, "Abridged edition (January 2000)"); //for www.amozon.com
	}

	@Test
	public void testTryCast_string_contains_a_year_date_is_not_a_date(){
		assertExpectedExtraction(null, "Harper Audio (1996)"); //for www.amozon.com
	}

	@Test
	public void testTryCast_string_contains_a_month_year_date_is_not_a_date(){
		assertExpectedExtraction(null, "HarperCollins (UK) - 1998-06");
	}

	@Test
	public void testTryCast_string_contains_a_standard_date_is_not_a_date(){
		assertExpectedExtraction(null, "HarperCollins UK - 1995-06-01");
	}

	/* --- Tests for distance (standard date) --- */

	@Test
	public void testGetDistance_equal_standard_dates(){
		assertDateDistance(0.0, new Date(1,1,2001), new Date(1,1,2001));
	}

	@Test
	public void testGetDistance_different_standard_dates(){
		assertDateDistance(1.0, new Date(1,1,2001), new Date(2,2,2002));
	}

	@Test
	public void testGetDistance_equal_standard_date_and_date_without_day(){
		assertDateDistance(0.25, new Date(1,1,2001), new Date(1,2001));
	}

	@Test
	public void testGetDistance_different_standard_date_and_date_without_day(){
		assertDateDistance(1.0, new Date(1,1,2001), new Date(2,2002));
	}

	@Test
	public void testGetDistance_equal_standard_date_and_date_with_only_year(){
		assertDateDistance(0.25, new Date(1,1,2001), new Date(2001));
	}

	@Test
	public void testGetDistance_different_standard_date_and_date_with_only_year(){
		assertDateDistance(1.0, new Date(1,1,2001), new Date(2002));
	}

	/* --- Tests for distance (date without day) --- */

	@Test
	public void testGetDistance_from_standard_date_equal_months_and_years(){
		assertDateDistance(0.25, new Date(1,2001), new Date(1,1,2001));
	}

	@Test
	public void testGetDistance_from_standard_date_equal_months_different_years(){
		assertDateDistance(1.0, new Date(1,2001), new Date(1,1,2002));
	}

	@Test
	public void testGetDistance_from_standard_date_equal_years_different_months(){
		assertDateDistance(0.25, new Date(1,2001), new Date(2,1,2001));
	}

	@Test
	public void testGetDistance_from_standard_date_different_months_and_years(){
		assertDateDistance(1.0, new Date(1,2001), new Date(2,2,2002));
	}

	
	
	@Test
	public void testGetDistance_from_date_without_day_equal_months_and_years(){
		assertDateDistance(0.0, new Date(1,2001), new Date(1,2001));
	}

	@Test
	public void testGetDistance_from_date_without_day_equal_months_different_years(){
		assertDateDistance(1.0, new Date(1,2001), new Date(1,2002));
	}

	@Test
	public void testGetDistance_from_date_without_day_equal_years_different_months(){
		assertDateDistance(1.0, new Date(1,2001), new Date(2,2001));
	}

	@Test
	public void testGetDistance_from_date_without_day_different_months_and_years(){
		assertDateDistance(1.0, new Date(1,2001), new Date(2,2002));
	}

		
	
	@Test
	public void testGetDistance_from_date_with_only_year_equal_years(){
		assertDateDistance(0.25, new Date(1,2001), new Date(2001));
	}

	@Test
	public void testGetDistance_from_date_with_only_year_different_years(){
		assertDateDistance(1.0, new Date(1,2001), new Date(2002));
	}

	
	
	
	/* --- Tests for distance (date with only year) --- */
	
	@Test
	public void testGetDistance_equal_dates_with_only_year(){
		assertDateDistance(0.0, new Date(2001), new Date(2001));
	}

	@Test
	public void testGetDistance_different_dates_with_only_year(){
		assertDateDistance(1.0, new Date(2001), new Date(2002));
	}

	@Test
	public void testGetDistance_equal_date_with_only_year_and_standard_date(){
		assertDateDistance(0.25, new Date(2001), new Date(1,1,2001));
	}

	@Test
	public void testGetDistance_different_date_with_only_year_and_standard_date(){
		assertDateDistance(1.0, new Date(2001), new Date(2,2,2002));
	}

	@Test
	public void testGetDistance_equal_date_with_only_year_and_date_without_day(){
		assertDateDistance(0.25, new Date(2001), new Date(1,2001));
	}

	@Test
	public void testGetDistance_different_date_with_only_year_and_date_without_day(){
		assertDateDistance(1.0, new Date(2001), new Date(2,2002));
	}

}
