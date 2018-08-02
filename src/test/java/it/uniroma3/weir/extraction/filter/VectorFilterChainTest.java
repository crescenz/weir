package it.uniroma3.weir.extraction.filter;

import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.fixture.VectorFixture;
import it.uniroma3.weir.vector.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class VectorFilterChainTest {
	
	private static void assertChecking(int expectedSize, String... values) {
		NumberOfInvariantsFilter f1 = new NumberOfInvariantsFilter(0.5);
		NumberOfNullsFilter f2 = new NumberOfNullsFilter(0.5);
		
		List<VectorFilter> filters = new ArrayList<>();
		filters.add(f1);
		filters.add(f2);
		FilterChain chain = new FilterChain(filters);

		List<Vector> vectors = new ArrayList<>();
		vectors.add(VectorFixture.createVector(Arrays.copyOfRange(values, 0, values.length/2)));
		vectors.add(VectorFixture.createVector(Arrays.copyOfRange(values, values.length/2, values.length)));
		
		chain.filter(vectors);
		assertEquals(expectedSize, vectors.size());
	}
	
	@Test
	public void testFilter_all_vectors_have_positive_check() {
		assertChecking(2, "a", "max", "a", "max");
	}

	@Test
	public void testFilter_one_vector_have_negative_check_from_invariant_filter() {
		assertChecking(1, "a", "a", "a", "max");
	}

	@Test
	public void testFilter_one_vector_have_negative_check_from_null_filter() {
		assertChecking(1, "a", "max", null, null);
	}

	@Test
	public void testFilter_all_vectors_have_negative_check_from_invariant_filter() {
		assertChecking(0, "a", "a", "max", "max");
	}

	@Test
	public void testFilter_all_vectors_have_negative_check_from_null_filter() {
		assertChecking(0, null, null, null, null);
	}

	@Test
	public void testFilter_all_vectors_have_negative_check_from_different_filters() {
		assertChecking(0, "a", "a", null, null);
	}

}
