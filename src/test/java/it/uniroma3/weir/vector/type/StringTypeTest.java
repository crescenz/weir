package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.fixture.Asserts.assertDoubleEquals;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.vector.format.Regexps;
import it.uniroma3.weir.vector.value.Date;

import java.util.Arrays;

import org.junit.Test;

public class StringTypeTest {

	private static void assertExpectedExtraction(String expected, String value) {
		assertEquals(expected, Type.STRING.tryCastNonNull(value));
	}

	private static void assertStringDistance(double expected, String value1, String value2) {
		assertDoubleEquals(expected, Type.STRING.distance(value1, value2));
	}

	/* --- Tests for extraction  --- */
	
	@Test
	public void testTryCast_empty_string(){
		assertExpectedExtraction("", "");
	}

	@Test
	public void testTryCast_simple_string(){
		assertExpectedExtraction("a", "a");
	}

	@Test
	public void testTryCast_string_with_space(){
		assertExpectedExtraction("a a", "a a");
	}

	@Test
	public void testTryCast_number(){
		assertExpectedExtraction("1", "1");
	}

	@Test
	public void testTryCast_space(){
		assertExpectedExtraction("1 m", "1 m");
	}

	@Test
	public void testTryCast_mass(){
		assertExpectedExtraction("1 lb", "1 lb");
	}

	@Test
	public void testTryCast_money(){
		assertExpectedExtraction("1 $", "1 $");
	}
	
	@Test
	public void testTryCast_url(){
		assertExpectedExtraction("http://www.site.com", "http://www.site.com");
	}

	@Test
	public void testTryCast_phone(){
		assertExpectedExtraction("111-1111", "111-1111");
	}

	@Test
	public void testTryCast_isbn(){
		assertExpectedExtraction("1111111111", "1111111111");
	}

	@Test
	public void testTryCast_date(){
		assertExpectedExtraction("1 1 2001", "1 1 2001");
	}

	/* --- Tests for distance  --- */

	@Test
	public void testGetDistance_equal_elements(){
		assertStringDistance(0.0, "a", "a");
	}

	@Test
	public void testGetDistance_equal_elements_without_whitespace(){
		assertStringDistance(0.0, " a ", "a");
	}

	@Test
	public void testGetDistance_equal_elements_without_whit(){
		assertStringDistance(0.0, "111-222-3333", "111) 222-3333");
	}

	@Test
	public void testGetDistance_isbn_with_different_formats(){
		assertStringDistance(1.0, "111-222222", "111222222");
	}

	@Test
	public void testGetDistance_double_number_vs_int_number(){
		assertStringDistance(0.31, "7.0", "7");
	}

	@Test
	public void testGetDistance_very_similar_elements(){
		test(0.0, "Michael Chinery", "Chinery, Michael");
		assertStringDistance(0.0, "Michael Chinery", "Chinery, Michael");
	}

	@Test
	public void testGetDistance_similar_elements(){
		test(0.59, "Bob Press", "J. R. Press");
		assertStringDistance(0.59, "Bob Press", "J. R. Press");
	}

	@Test
	public void testGetDistance_similar_elements_miss_some_word(){
		test(0.31, "Majesco Enterta", "Majesco");
		assertStringDistance(0.31, "Majesco Enterta", "Majesco");
	}

	@Test
	public void testGetDistance_similar_elements_but_different_entity(){
		test(0.5, "Eidos Interactive", "Empire Interactive");
		assertStringDistance(0.5, "Eidos Interactive", "Empire Interactive");
	}

	@Test
	public void testGetDistance_similar_elements_2(){
		test(0.31, "Paperback", "Paperback Textbook");
		assertStringDistance(0.31, "Paperback", "Paperback Textbook");
	}

	@Test
	public void testGetDistance_different_elements(){
		test(1.0, "W. Lippert", "D. Podlech");
		assertStringDistance(1.0, "W. Lippert", "D. Podlech");
	}

	@Test
	public void testGetDistance_normalized_date_vs_simple_date(){
		assertStringDistance(0.33, "8/21/1995", "21 August 1995");
		testWithDate(0.33, new Date(21, 8, 1995), "21 August 1995");
	}

	@Test
	public void testGetDistance_normalized_date_without_day_vs_simple_date(){
		assertStringDistance(0.59, "12/2000", "01 November 2000");
		testWithDate(0.59, new Date(12, 2000), "01 November 2000");
	}

	public void test(double oldDistance, String s1, String s2){
		String[] tokens1 = s1.split("\\W+|\\s+");
		String[] tokens2 = s2.split("\\W+|\\s+");
		System.out.println(Arrays.toString(tokens1));
		System.out.println(Arrays.toString(tokens2));
		
		int match = 0;
		for (int i = 0; i < tokens1.length; i++) {
			for (int j = 0; j < tokens2.length; j++) {
				if (tokens1[i].equals(tokens2[j])) {
					match++;
					tokens2[j] = null;
				}
			}
		}
		
		System.out.println(match);
		double maxLength = Math.max(tokens1.length, tokens2.length);
		double distance = ((maxLength - match) / maxLength);
		System.out.println(distance + " vs " + oldDistance);
		System.out.println();
		System.out.println();
	}
	

	public void testWithDate(double oldDistance, Date date, String s){
		String[] tokens1 = date.toString().split("\\W+|\\s+");
		String[] tokens2 = s.split("\\W+|\\s+");
		System.out.println(Arrays.toString(tokens1));
		System.out.println(Arrays.toString(tokens2));
		
		int match = 0;
		for (int i = 0; i < tokens1.length; i++) {
			for (int j = 0; j < tokens2.length; j++) {
				if (tokens1[i].equals(tokens2[j])) {
					match++;
					tokens2[j] = null;
					break;
				}
			}
		}
		
		if (match<3) {
			String shortMonth = Regexps.MONTHS_ABBR[date.getMonth() - 1];
			boolean found = false;
			for (String t : tokens2) {
				if (t != null && shortMonth.equals(t.toLowerCase())) {
					match++;
					found = true;
					break;
				}
			}
			
			if (!found) {
				String month = Regexps.MONTHS[date.getMonth() - 1];
				for (String t : tokens2) {
					if (t != null && month.equals(t.toLowerCase())) {
						match++;
						break;
					}
				}
			}
		}
		
		
		System.out.println(match);
		double maxLength = Math.max(tokens1.length, tokens2.length);
		double distance = ((maxLength - match) / maxLength);
		System.out.println(distance + " vs " + oldDistance);
		System.out.println();
		System.out.println();
	}
	

}
