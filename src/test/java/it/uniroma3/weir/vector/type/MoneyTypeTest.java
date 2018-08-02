package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.fixture.Asserts.assertDoubleEquals;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.vector.value.Dimensional;

import org.junit.Test;

public class MoneyTypeTest {

	private static void assertExpectedExtraction(String expected, String value) {
		assertEquals(expected, Type.MONEY.tryCastNonNull(value).toString());
	}

	private static void assertNullExtraction(String value) {
		assertEquals(null, Type.MONEY.tryCastNonNull(value));
	}

	private static void assertDimensionalDistance(double expected, Dimensional value1, Dimensional value2) {
		assertDoubleEquals(expected, Type.MONEY.distance(value1, value2));
	}

	/* --- Tests for extraction  --- */
	
	@Test
	public void testTryCast_dollar(){
		assertExpectedExtraction("1 $", "1 $");
	}

	@Test
	public void testTryCast_dollar_with_dot(){
		assertExpectedExtraction("1,1 $", "1.1 $");
	}

	@Test
	public void testTryCast_dollar_with_comma(){
		assertExpectedExtraction("1,1 $", "1,1 $");
	}

	@Test
	public void testTryCast_dollar_with_dot_and_comma(){
		assertExpectedExtraction("1397,7 $", "1,397.70 $");
	}

	@Test
	public void testTryCast_dollar_with_multiple_comma(){
		assertExpectedExtraction("1397712 $", "1,397,712 $");
	}

	@Test
	public void testTryCast_euro(){
		assertExpectedExtraction("1,3 $", "1 €");
	}
	
	@Test
	public void testTryCast_pound(){
		assertExpectedExtraction("1,6 $", "1 £");
	}
	
	@Test
	public void testTryCast_inverted_order(){
		assertExpectedExtraction("1 $", "$ 1");
	}

	@Test
	public void testTryCast_no_space_between_number_and_tag(){
		assertExpectedExtraction("1 $", "1$");
	}

	@Test
	public void testTryCast_with_decimal(){
		assertExpectedExtraction("1,1 $", "$ 1.1");
	}
	
	@Test
	public void testTryCast_extracted_value_of_two_or_dimension_is_the_sum_of_them(){
		assertExpectedExtraction("2,3 $", "1$ 1€");
	}
	
	@Test
	public void testTryCast_slash_means_that_the_value_is_expressed_in_more_unit_measures(){
		assertExpectedExtraction("1 $", "1 $ / 1.3 €");
	}
	
	@Test
	public void testTryCast_the_number_in_the_brackets_is_another_way_to_express_the_values(){
		assertExpectedExtraction("1 $", "1 $ (1.3€)");
	}

	@Test
	public void testTryCast_dollar_without_number(){
		assertExpectedExtraction("0 $", ": $");
	}

	@Test
	public void testTryCast_empty_string(){
		assertNullExtraction("");
	}

	@Test
	public void testTryCast_string(){
		assertNullExtraction("a");
	}
	
	@Test
	public void testTryCast_integer(){
		assertNullExtraction("1");
	}
	
	@Test
	public void testTryCast_dollar_string(){
		assertNullExtraction("1 dollars");
	}

	@Test
	public void testTryCast_not_well_formed(){
		assertNullExtraction("1 $h");
	}

	@Test
	public void testTryCast_too_many_words(){
		assertNullExtraction("1 xx $ f");
	}

	@Test
	public void testTryCast_too_many_numbers(){
		assertNullExtraction("$ 1 1");
	}

	/* Test for distance */

	@Test
	public void testGetDistance_equal_money(){
		assertDimensionalDistance(0.0, new Dimensional(80, "$"), new Dimensional(80, "$"));
	}

	@Test
	public void testGetDistance_different_money(){
		assertDimensionalDistance(0.33, new Dimensional(80, "$"), new Dimensional(40, "$"));
	}

	@Test
	public void testGetDistance_money_vs_space(){
		assertDimensionalDistance(1.0, new Dimensional(80, "$"), new Dimensional(80, "m"));
	}

	@Test
	public void testGetDistance_money_vs_mass(){
		assertDimensionalDistance(1.0, new Dimensional(80, "$"), new Dimensional(80, "Kg"));
	}

}
