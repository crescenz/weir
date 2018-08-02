package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.fixture.Asserts.assertDoubleEquals;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.vector.value.Dimensional;

import org.junit.Test;

public class SpaceTypeTest {

	private static void assertExpectedExtraction(String expected, String value) {
		assertEquals(expected, Type.SPACE.tryCastNonNull(value).toString());
	}

	private static void assertNullExtraction(String value) {
		assertEquals(null, Type.SPACE.tryCastNonNull(value));
	}

	private static void assertDimensionalDistance(double expected, Dimensional value1, Dimensional value2) {
		assertDoubleEquals(expected, Type.SPACE.distance(value1, value2));
	}

	/* --- Tests for extraction  --- */
	
	@Test
	public void testTryCast_simple(){
		assertExpectedExtraction("1 m", "1 m");
	}

	@Test
	public void testTryCast_meter_with_dot(){
		assertExpectedExtraction("1,1 m", "1.1 m");
	}

	@Test
	public void testTryCast_meter_with_comma(){
		assertExpectedExtraction("1,1 m", "1,1 m");
	}

	@Test
	public void testTryCast_meter_with_dot_and_comma(){
		assertExpectedExtraction("1397,7 m", "1,397.70 m");
	}

	@Test
	public void testTryCast_meter_with_multiple_comma(){
		assertExpectedExtraction("1397712 m", "1,397,712 m");
	}

	@Test
	public void testTryCast_centimeter(){
		assertExpectedExtraction("0,01 m", "1 cm");
	}

	@Test
	public void testTryCast_inches(){
		assertExpectedExtraction("0,025 m", "1in");
	}

	@Test
	public void testTryCast_inches_short(){
		assertExpectedExtraction("0,025 m", "1\"");
	}
	
	@Test
	public void testTryCast_feet(){
		assertExpectedExtraction("0,305 m", "1ft");
	}

	@Test
	public void testTryCast_feet_short(){
		assertExpectedExtraction("0,305 m", "1'");
	}

	@Test
	public void testTryCast_with_decimal(){
		assertExpectedExtraction("1,1 m", "1.1 m");
	}

	@Test
	public void testTryCast_case_zero(){
		assertExpectedExtraction("0 m", "0 m");
	}

	@Test
	public void testTryCast_no_space_between_tag_and_number(){
		assertExpectedExtraction("1 m", "1m");
	}

	@Test
	public void testTryCast_uppercase_tag(){
		assertExpectedExtraction("1 m", "1 M");
	}

	@Test
	public void testTryCast_extracted_value_of_two_or_dimension_is_the_sum_of_them(){
		assertExpectedExtraction("1,1 m", "1 m 10 cm");
	}
	
	@Test
	public void testTryCast_slash_means_that_the_value_is_expressed_in_more_unit_measures(){
		assertExpectedExtraction("1 m", "1 m / 100 cm");
	}

	@Test
	public void testTryCast_the_number_in_the_brackets_is_another_way_to_express_the_values(){
		assertExpectedExtraction("1,1 m", "1m 10cm (1.1m)");
	}

	@Test
	public void testTryCast_the_height_with_other_information(){
		assertExpectedExtraction("1,83 m", "1.83 m (6 ft 1/2 in) 181 cm : 75 kg");
	}

	@Test
	public void testTryCast_the_height_in_other_format(){
		assertExpectedExtraction("1,97 m", "1.97 m (6 ft 6 in)[1]");
	}

	@Test
	public void testTryCast_the_height_in_other_format_2(){
		assertExpectedExtraction("1,84 m", "1.84 m (6 ft 0.4 in) - 76 kg");
	}

	@Test
	public void testTryCast_the_height_in_other_format_3(){
		assertExpectedExtraction("1,8 m", "1.80 m : 74 kg");
	}

	@Test
	public void testTryCast_meter_without_number(){
		assertExpectedExtraction("0 m", ": m");
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
	public void testTryCast_not_well_formed(){
		assertNullExtraction("100 cmh");
	}

	@Test
	public void testTryCast_too_many_words(){
		assertNullExtraction("100 xx cm f");
	}

	@Test
	public void testTryCast_too_many_numbers(){
		assertNullExtraction("cm 100 25");
	}

	/* Test for distance */

	@Test
	public void testGetDistance_equal_space(){
		assertDimensionalDistance(0.0, new Dimensional(80, "m"), new Dimensional(80, "m"));
	}

	@Test
	public void testGetDistance_different_space(){
		assertDimensionalDistance(0.33, new Dimensional(80, "m"), new Dimensional(40, "m"));
	}

	@Test
	public void testGetDistance_space_vs_money(){
		assertDimensionalDistance(1.0, new Dimensional(80, "m"), new Dimensional(80, "$"));
	}

	@Test
	public void testGetDistance_space_vs_mass(){
		assertDimensionalDistance(1.0, new Dimensional(80, "m"), new Dimensional(80, "Kg"));
	}

}
