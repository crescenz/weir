package it.uniroma3.weir.vector.type;

import static org.junit.Assert.assertEquals;
import static it.uniroma3.weir.fixture.Asserts.assertDoubleEquals;

import org.junit.Test;

public class ISBNTypeTest {

	private static void assertExpectedExtraction(String expected, String value) {
		assertEquals(expected, Type.ISBN.tryCastNonNull(value));
	}

	private static void assertISBNDistance(double expected, String value1, String value2) {
		assertDoubleEquals(expected, Type.ISBN.distance(value1, value2));
	}

	/* --- Tests for extraction  --- */
	
	@Test
	public void testTryCast_isbn_with_10_numbers(){
		assertExpectedExtraction("1111111111", "1111111111");
	}
	
	@Test
	public void testTryCast_isbn_with_13_numbers(){
		assertExpectedExtraction("1111111111111", "1111111111111");
	}

	@Test
	public void testTryCast_isbn_with_minus_sign(){
		assertExpectedExtraction("1111111111111", "111-1111111111");
	}

	@Test
	public void testTryCast_isbn_with_slash_sign(){
		assertExpectedExtraction("9780002200141", "9780002200141 / 0002200147");
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
	public void testTryCast_number(){
		assertExpectedExtraction(null, "1");
	}
	
	@Test
	public void testTryCast_string_similar_to_isbn(){
		assertExpectedExtraction(null, "xxx111-1111111111xxx");
	}

	/* --- Tests for distance  --- */
	
	@Test
	public void testGetDistance_equal_elements(){
		assertISBNDistance(0.0, "1111111111", "1111111111");
	}

	@Test
	public void testGetDistance_equal_elements_without_minus_sign(){
		assertISBNDistance(1.0, "978-0001049895", " 9780001049895");
	}
	
	@Test
	public void testGetDistance_similar_elements(){
		assertISBNDistance(1.0, "1111111111", "1111111112");
	}
	
	@Test
	public void testGetDistance_different_elements(){
		assertISBNDistance(1.0, "1111111111", "2222222222");
	}

}
