package it.uniroma3.weir.extraction.filter;

import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.fixture.VectorFixture;
import it.uniroma3.weir.vector.Vector;

import org.junit.Test;

public class LengthOfValuesFilterTest {

	static private void assertChecking(boolean expectedCheck, int maxLen, String... values) {
		Vector extractedVector = VectorFixture.createVector(values);
		LengthOfValuesFilter filter = new LengthOfValuesFilter(maxLen);
		assertEquals(expectedCheck, filter.filter(extractedVector));
	}

	@Test
	public void testCheck_zero_length_with_null_element() {
		assertChecking(true, 0, new String[]{null});
	}

	@Test
	public void testCheck_greater_than_zero_length_with_null_element() {
		assertChecking(true, 5, new String[]{null});
	}

	@Test
	public void testCheck_zero_length_with_single_element() {
		assertChecking(false, 0, "aaa");
	}

	@Test
	public void testCheck_smaller_length_with_single_element() {
		assertChecking(false, 1, "aaa");
	}

	@Test
	public void testCheck_appropriate_length_with_single_element() {
		assertChecking(true, 3, "aaa");
	}

	@Test
	public void testCheck_bigger_length_with_single_element() {
		assertChecking(true, 5, "aaa");
	}

	@Test
	public void testCheck_zero_length_with_elements() {
		assertChecking(false, 0, "aaa", "bb", "c");
	}

	@Test
	public void testCheck_smaller_length_with_elements() {
		assertChecking(false, 1, "aaa", "bb", "c");
	}

	@Test
	public void testCheck_appropriate_length_with_elements() {
		assertChecking(true, 3, "aaa", "bb", "c");
	}

	@Test
	public void testCheck_bigger_length_with_elements() {
		assertChecking(true, 5, "aaa", "bb", "c");
	}

	@Test
	public void testCheck_zero_length_with_null_elements() {
		assertChecking(true, 0, null, null, null);
	}

	@Test
	public void testCheck_greater_than_zero_length_with_null_elements() {
		assertChecking(true, 5, null, null, null);
	}

	@Test
	public void testCheck_zero_length_with_elements_and_nulls() {
		assertChecking(false, 0, "aaa", null, "bb", null, "c");
	}

	@Test
	public void testCheck_smaller_length_with_elements_and_nulls() {
		assertChecking(false, 1, "aaa", null, "bb", null, "c");
	}

	@Test
	public void testCheck_appropriate_length_with_elements_and_nulls() {
		assertChecking(true, 3, "aaa", null, "bb", null, "c");
	}

	@Test
	public void testCheck_bigger_length_with_elements_and_nulls() {
		assertChecking(true, 5, "aaa", null, "bb", null, "c");
	}

}
