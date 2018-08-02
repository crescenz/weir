package it.uniroma3.weir.extraction.filter;

import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.fixture.VectorFixture;
import it.uniroma3.weir.vector.Vector;

import org.junit.Test;

public class NumberOfInvariantFilterTest {

	private static void assertChecking(boolean expectedCheck, double percentage, String... values) {
		Vector extractedVector = VectorFixture.createVector(values);
		NumberOfInvariantsFilter filter = new NumberOfInvariantsFilter(percentage);
		assertEquals(expectedCheck, filter.filter(extractedVector));
	}

	@Test
	public void testCheck_minimum_percentage_with_single_element() {
		assertChecking(false, 0.0, "a");
	}

	@Test
	public void testCheck_a_percentage_greater_than_zero_with_single_element() {
		assertChecking(true, 0.5, "a");
	}

	@Test
	public void testCheck_maximum_percentage_with_single_element() {
		assertChecking(true, 1.0, "a");
	}
	
	@Test
	public void testCheck_minimum_percentage_with_all_null_elements() {
		assertChecking(true, 0.0, null, null);
	}

	@Test
	public void testCheck_a_percentage_with_all_null_elements() {
		assertChecking(true, 0.5, null, null);
	}

	@Test
	public void testCheck_maximum_percentage_with_all_null_elements() {
		assertChecking(true, 1.0, null, null);
	}

	@Test
	public void testCheck_minimum_percentage_with_all_equal_elements() {
		assertChecking(false, 0.0, "a", "a");
	}

	@Test
	public void testCheck_a_percentage_with_all_equal_elements() {
		assertChecking(false, 0.5, "a", "a");
	}

	@Test
	public void testCheck_maximum_percentage_with_all_equal_elements() {
		assertChecking(true, 1.0, "a", "a");
	}

	@Test
	public void testCheck_minimum_percentage_with_all_different_elements() {
		assertChecking(false, 0.0, "a", "b");
	}
	
	@Test
	public void testCheck_percentage_smaller_than_the_number_of_equal_elements() {
		assertChecking(false, 0.1, "a", "a", "b");
	}

	@Test
	public void testCheck_percentage_equals_to_the_number_of_equal_elements() {
		assertChecking(true, 0.5, "a", "b");
	}

	@Test
	public void testCheck_percentage_greater_than_the_number_of_equal_elements() {
		assertChecking(true, 0.7, "a", "b");
	}

	@Test
	public void testCheck_maximum_percentage_with_all_different_elements() {
		assertChecking(true, 1.0, "a", "b");
	}

	@Test
	public void testCheck_percentage_smaller_than_the_number_of_equal_elements_different_from_null() {
		assertChecking(false, 0.1, null, null, null, null, "a", "a", "b");
	}

	@Test
	public void testCheck_percentage_equals_to_the_number_of_equal_elements_different_from_null() {
		assertChecking(true, 0.5, null, null, null, null, "a", "b");
	}

	@Test
	public void testCheck_percentage_greater_than_the_number_of_equal_elements_different_from_null() {
		assertChecking(true, 0.7, null, null, null, null, "a", "b");
	}

}
