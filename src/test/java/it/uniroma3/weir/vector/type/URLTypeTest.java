package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.fixture.Asserts.assertDoubleEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class URLTypeTest {

	private static void assertExpectedExtraction(String expected, String value) {
		assertEquals(expected, Type.URL.tryCastNonNull(value));
	}

	private static void assertURLDistance(double expected, String value1, String value2) {
		assertDoubleEquals(expected, Type.URL.distance(value1, value2));
	}

	/* --- Tests for extraction  --- */
	
	@Test
	public void testTryCast_url_without_http(){
		assertExpectedExtraction("www.site.com", "www.site.com");
	}
	
	@Test
	public void testTryCast_complete_url_with_http() {
		assertExpectedExtraction("http://www.site.com", "http://www.site.com");
	}

	@Test
	public void testTryCast_complete_url_with_https() {
		assertExpectedExtraction("https://www.site.com", "https://www.site.com");
	}
	
	@Test
	public void testTryCast_url_with_one_dot() {
		assertExpectedExtraction("http://www.com", "http://www.com");
	}

	@Test
	public void testTryCast_empty_string(){
		assertExpectedExtraction(null, "");
	}
	
	@Test
	public void testTryCast_fake_url(){
		assertExpectedExtraction(null, "www-site-com");
	}
	
	@Test
	public void testTryCast_string_containing_url() {
		assertExpectedExtraction(null, "a b http://www.site.com");
	}

	@Test
	public void testTryCast_url_without_dot() {
		assertExpectedExtraction(null, "http://wwwsitecom");
	}
	
	@Test
	public void testTryCast_number_with_dot() {
		assertExpectedExtraction(null, "1.1");
	}
	
	@Test
	public void testTryCast_string_with_dot() {
		assertExpectedExtraction(null, "site.com");
	}

	@Test
	public void testTryCast_string_and_number_with_dot() {
		assertExpectedExtraction(null, "a1.0");
	}

	/* --- Tests for distance  --- */
	
	@Test
	public void testGetDistance_equal_elements() {
		assertURLDistance(0.0, "www.site1.com", "www.site1.com");
	}

	@Test
	public void testGetDistance_similar_elements(){
		assertURLDistance(0.25, "www.site1.com", "www.site2.com");
	}
	
	@Test
	public void testGetDistance_different_elements() {
		assertURLDistance(0.5, "www.site1.com", "www.site2.net");
	}

}
