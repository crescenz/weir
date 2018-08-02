package it.uniroma3.weir.extraction;

import static it.uniroma3.weir.fixture.VectorFixture.createExtractedVector;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.Normalizer;
import it.uniroma3.weir.vector.Vector;

import java.util.Arrays;

import org.junit.Test;

public class NormalizerTest extends WeirTest {
	

	static private void assertPrefixRemoved(String prefix, String... values) {
		final ExtractedVector actual   = createExtractedVector(firstHalf(values));
		final ExtractedVector expected = createExtractedVector(secondHalf(values));
		final Normalizer normalizer = new Normalizer();
		final Vector actualNormalized   = normalizer.normalize(actual);
		final Vector expectedNormalized = normalizer.normalize(expected);
		assertEquals(expectedNormalized, actualNormalized);
	}

	static private String[] firstHalf(String... values) {
		return Arrays.copyOfRange(values, 0, values.length/2);
	}

	static private String[] secondHalf(String... values) {
		return Arrays.copyOfRange(values, values.length/2, values.length);
	}

	@Test
	public void testRemovePrefix_string_null() {
		assertPrefixRemoved("!", null,/*->*/null);
	}

	@Test
	public void testRemovePrefix_string_empty() {
		assertPrefixRemoved("!", "",/*->*/"");
	}
	
	@Test
	public void testRemovePrefix_string_without_prefix() {
		assertPrefixRemoved("!", "a",/*->*/"a");
	}
	
	@Test
	public void testRemovePrefix_string_with_prefix() {
		assertPrefixRemoved("!", "!a",/*->*/"a");
	}

	@Test
	public void testRemovePrefix_strings_without_prefix() {
		assertPrefixRemoved("!", "a", "b",/*->*/"a", "b");
	}
	
	@Test
	public void testRemovePrefix_strings_with_prefix() {
		assertPrefixRemoved("!", "!a", "!b",/*->*/"a", "b");
	}
	
	@Test
	public void testRemovePrefix_strings_with_prefix_and_nulls() {
		assertPrefixRemoved("!", "!a", null, "!b",/*->*/"a", null, "b");
	}

}
