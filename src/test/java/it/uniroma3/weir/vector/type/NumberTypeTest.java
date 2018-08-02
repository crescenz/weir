package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.fixture.Asserts.assertDoubleEquals;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.vector.value.Number;

import org.junit.Test;

public class NumberTypeTest {

	private static void assertExpectedExtraction(String expected, String value) {
		assertEquals(expected, Type.NUMBER.tryCastNonNull(value).toString());
	}

	private static void assertNullExtraction(String value) {
		assertEquals(null, Type.NUMBER.tryCastNonNull(value));
	}

	private static void assertNumberDistance(double expected, double value1, double value2) {
		assertDoubleEquals(expected, Type.NUMBER.distance(new Number(value1), new Number(value2)));
	}

	/* --- Tests for extraction  --- */

	@Test
	public void testTryCast_integer_number(){
		assertExpectedExtraction("1", "1");
	}

	@Test
	public void testTryCast_integer_number_with_minus_sign(){
		assertExpectedExtraction("-1", "-1");
	}

	@Test
	public void testTryCast_number_with_comma(){
		assertExpectedExtraction("1,1", "1,1");
	}

	@Test
	public void testTryCast_number_with_comma_and_minus_sign(){
		assertExpectedExtraction("-1,1", "-1,1");
	}

	@Test
	public void testTryCast_number_with_dot(){
		assertExpectedExtraction("1,1", "1.1");
	}

	@Test
	public void testTryCast_number_with_dot_and_minus_sign(){
		assertExpectedExtraction("-1,1", "-1.1");
	}

	@Test
	public void testTryCast_number_with_commas_and_dot(){
		assertExpectedExtraction("100000", "100,000.00");
	}

	@Test
	public void testTryCast_number_with_commas_and_dot_and_minus_sign(){
		assertExpectedExtraction("-100000", "-100,000.00");
	}
	
	@Test
	public void testTryCast_plus_symbol(){
		assertExpectedExtraction("1", "+1.0");
	}
	
	@Test
	public void testTryCast_minus_symbol(){
		assertExpectedExtraction("-1", "-1.0");
	}

	@Test
	public void testTryCast_int_exponential(){
		assertExpectedExtraction("100", "1E2");
	}

	@Test
	public void testTryCast_double_exponential(){
		assertExpectedExtraction("111", "1.11E2");
	}

	@Test
	public void testTryCast_exponential_format_lower_case(){
		assertExpectedExtraction("111", "1.11e2");
	}
	
	@Test
	public void testTryCast_exponential_base_with_minus_sign(){
		assertExpectedExtraction("-111", "-1.11E2");
	}

	@Test
	public void testTryCast_exponential_exponent_with_minus_sign(){
		assertExpectedExtraction("1,11", "111E-2");
	}

	@Test
	public void testTryCast_exponential_base_and_exponent_with_minus_sign(){
		assertExpectedExtraction("-1,11", "-111E-2");
	}

	@Test
	public void testTryCast_hundreds_with_commas(){
		assertExpectedExtraction("1495100", "1,495,100");
	}

	@Test
	public void testTryCast_millions_with_commas(){
		assertExpectedExtraction("414500", "414,500");
	}

	@Test
	public void testTryCast_number_as_na(){
		assertExpectedExtraction("0", "N.A");
	}

	@Test
	public void testTryCast_number_as_unchanged(){
		assertExpectedExtraction("0", "unch");
	}

	@Test
	public void testTryCast_empty_string(){
		assertNullExtraction("");
	}

	@Test
	public void testTryCast_string_similar_to_exponential(){
		assertNullExtraction("N82E16830180316");
	}
	
	@Test
	public void testTryCast_string_without_numbers(){
		assertNullExtraction("a");
	}

	@Test
	public void testTryCast_date(){
		assertNullExtraction("12-10-1995");
	}

	@Test
	public void testTryCast_month_year_date(){
		assertNullExtraction("12-1995");
	}

	@Test
	public void testTryCast_more_than_one_number(){
		assertNullExtraction("12 1995");
	}

	@Test
	public void testTryCast_number_with_string(){
		assertNullExtraction("12 a");
	}
	
	/* --- Tests for distance  --- */
	
	@Test
	public void testGetDistance_equal_positive_elements(){
		assertNumberDistance(0.0, 1.0, 1.0);
	}

	@Test
	public void testGetDistance_similar_positive_elements(){
		assertNumberDistance(0.04, 1.0, 1.1);
	}

	@Test
	public void testGetDistance_different_positive_elements(){
		assertNumberDistance(0.33, 1.0, 2.0);
	}

	@Test
	public void testGetDistance_different_positive_elements_inverted_order(){
		assertNumberDistance(0.33, 2.0, 1.0);
	}

	@Test
	public void testGetDistance_big_positive_element(){
		assertNumberDistance(1.0, 1.0, 2000000000.0);
	}

	@Test
	public void testGetDistance_equal_negative_elements(){
		assertNumberDistance(0.0, -1.0, -1.0);
	}

	@Test
	public void testGetDistance_similar_negative_elements(){
		assertNumberDistance(0.04, -1.0, -1.1);
	}
	
	@Test
	public void testGetDistance_different_negative_elements(){
		assertNumberDistance(0.33, -1.0, -2.0);
	}
	
	@Test
	public void testGetDistance_different_negative_elements_inverted_order(){
		assertNumberDistance(0.33, -2.0, -1.0);
	}

	@Test
	public void testGetDistance_big_negative_element(){
		assertNumberDistance(1.0, -1.0, -2000000000.0);
	}

	@Test
	public void testGetDistance_zero_and_positive_elements(){
		assertNumberDistance(0.6, 0.0, 3.0);
	}

	@Test
	public void testGetDistance_positive_and_zero_elements(){
		assertNumberDistance(0.6, 3.0, 0.0);
	}
	
	@Test
	public void testGetDistance_zero_and_negative_elements(){
		assertNumberDistance(0.6, 0.0, -3.0);
	}

	@Test
	public void testGetDistance_negative_and_zero_elements(){
		assertNumberDistance(0.6, -3.0, 0.0);
	}

	@Test
	public void testGetDistance_negative_and_positive_elements(){
		assertNumberDistance(0.6, -1.0, 2.0);
	}

	@Test
	public void testGetDistance_positive_and_negative_elements(){
		assertNumberDistance(0.6, 1.0, -2.0);
	}

	@Test
	public void testGetDistance_big_positive_and_big_negative_elements(){
		assertNumberDistance(1.0, 1.0, 2000000000.0);
	}

	@Test
	public void testGetDistance_big_negative_and_big_positive_elements(){
		assertNumberDistance(1.0, 1.0, 2000000000.0);
	}
	
}
