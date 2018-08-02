package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.fixture.Asserts.assertDoubleEquals;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.vector.value.Dimensional;

import org.junit.Test;

public class MassTypeTest {

	private static void assertExpectedExtraction(String expected, String value) {
		assertEquals(expected, Type.MASS.tryCastNonNull(value).toString());
	}

	private static void assertNullExtraction(String value) {
		assertEquals(null, Type.MASS.tryCastNonNull(value));
	}

	private static void assertDimensionalDistance(double expected, Dimensional value1, Dimensional value2) {
		assertDoubleEquals(expected, Type.MASS.distance(value1, value2));
	}

	/* --- Tests for extraction  --- */

	@Test
	public void testTryCast_kilogram(){
		assertExpectedExtraction("1 kg", "1 kg");
	}

	@Test
	public void testTryCast_kilogram_with_dot(){
		assertExpectedExtraction("1,1 kg", "1.1 kg");
	}

	@Test
	public void testTryCast_kilogram_with_comma(){
		assertExpectedExtraction("1,1 kg", "1,1 kg");
	}

	@Test
	public void testTryCast_kilogram_with_dot_and_comma(){
		assertExpectedExtraction("1397,7 kg", "1,397.70 kg");
	}

	@Test
	public void testTryCast_kilogram_with_multiple_comma(){
		assertExpectedExtraction("1397712 kg", "1,397,712 kg");
	}

	@Test
	public void testTryCast_kilogram_case_zero(){
		assertExpectedExtraction("0 kg", "0 kg");
	}
	
	@Test
	public void testTryCast__kilogram_inverted_order(){
		assertExpectedExtraction("1 kg", "kg 1");
	}

	@Test
	public void testTryCast__kilogram_upper_case(){
		assertExpectedExtraction("1 kg", "Kg 1");
	}

	@Test
	public void testTryCast__no_space_between_number_and_unit_measure(){
		assertExpectedExtraction("1 kg", "1.00kg");
	}
	
	@Test
	public void testTryCast_pound_as_lb(){
		assertExpectedExtraction("0,454 kg", "1 lb");
	}
	
	@Test
	public void testTryCast_pound_as_lbs(){
		assertExpectedExtraction("0,454 kg", "1 lbs");
	}

	@Test
	public void testTryCast_pound_as_puond(){
		assertExpectedExtraction("0,454 kg", "1 pound");
	}

	@Test
	public void testTryCast_pound_as_pounds(){
		assertExpectedExtraction("0,454 kg", "1 pounds");
	}

	@Test
	public void testTryCast_ounce(){
		assertExpectedExtraction("0,028 kg", "1 ounce");
	}
	
	@Test
	public void testTryCast_ounces(){
		assertExpectedExtraction("0,028 kg", "1 ounces");
	}
	
	@Test
	public void testTryCast_stones(){
		assertExpectedExtraction("6,35 kg", "1 st");
	}
	
	@Test
	public void testTryCast_extracted_value_of_two_or_dimension_is_the_sum_of_them(){
		assertExpectedExtraction("6,804 kg", "1st 1lbs");
	}
	
	@Test
	public void testTryCast_slash_means_that_the_value_is_expressed_in_more_unit_measures(){
		assertExpectedExtraction("1 kg", "1 kg / 0.45359237 lbs");
	}
	
	@Test
	public void testTryCast_the_number_in_the_brackets_is_another_way_to_express_the_values(){
		assertExpectedExtraction("6,804 kg", "1st 1lbs (6.8 kg)");
	}
	
	@Test
	public void testTryCast_kilogram_without_number(){
		assertExpectedExtraction("0 kg", ": kg");
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
	public void testTryCast_without_unit_measure(){
		assertNullExtraction("1");
	}

	@Test
	public void testTryCast_numer_with_dot(){
		assertNullExtraction("1.1");
	}

	@Test
	public void testTryCast_numer_with_comma(){
		assertNullExtraction("1,1");
	}

	@Test
	public void testTryCast_not_well_formed(){
		assertNullExtraction("1 kgh");
	}

	@Test
	public void testTryCast_too_many_words(){
		assertNullExtraction("1 xx kg f");
	}

	@Test
	public void testTryCast_too_many_numbers(){
		assertNullExtraction("kg 100 25");
	}
	
	/* Test for distance */

	@Test
	public void testGetDistance_equal_mass(){
		assertDimensionalDistance(0.0, new Dimensional(80, "Kg"), new Dimensional(80, "Kg"));
	}

	@Test
	public void testGetDistance_different_mass(){
		assertDimensionalDistance(0.33, new Dimensional(80, "Kg"), new Dimensional(40, "Kg"));
	}

	@Test
	public void testGetDistance_mass_vs_space(){
		assertDimensionalDistance(1.0, new Dimensional(80, "Kg"), new Dimensional(80, "m"));
	}

	@Test
	public void testGetDistance_mass_vs_money(){
		assertDimensionalDistance(1.0, new Dimensional(80, "Kg"), new Dimensional(80, "$"));
	}

}
