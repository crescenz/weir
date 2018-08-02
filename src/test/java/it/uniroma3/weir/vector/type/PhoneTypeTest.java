package it.uniroma3.weir.vector.type;

import static it.uniroma3.weir.fixture.Asserts.assertDoubleEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PhoneTypeTest {

	private static void assertExpectedExtraction(String expected, String value) {
		assertEquals(expected, Type.PHONE.tryCastNonNull(value));
	}

	private static void assertNumberDistance(double expected, String value1, String value2) {
		assertDoubleEquals(expected, Type.PHONE.distance(value1, value2));
	}

	/* --- Tests for extraction  --- */
	
	

	@Test
	public void testTryCast_phone(){
		assertExpectedExtraction("317-940-8100", "317-940-8100");
	}

	@Test
	public void testTryCast_phone_with_bracket(){
		assertExpectedExtraction("800-654-3210", "800) 654-3210");
	}

	@Test
	public void testTryCast_phone_with_slash(){
		assertExpectedExtraction("317-940-8100", "317/940-8100");
	}

	@Test
	public void testTryCast_phone_with_slash_and_ext(){
		assertExpectedExtraction("317-940-8100", "317/940-8100 Ext. 4803");
	}

	@Test
	public void testTryCast_phone_with_slash_and_other_info(){
		assertExpectedExtraction("317-940-8100", "317/940-8100 Papa Haydn; 503/222-0048 Jo Bar");
	}

	@Test
	public void testTryCast_phone_list(){
		assertExpectedExtraction("317-940-8100", "317/940-8100; 907/274-9797; 800/764-1001");
	}

	@Test
	public void testTryCast_phone_without_space(){
		assertExpectedExtraction("317-940-8100", "3179408100");
	}

	
//	@Test
//	public void testTryCast_simple_phone(){
//		assertExpectedExtraction("111-1111", "111-1111");
//	}
//
//	@Test
//	public void testTryCast_phone_with_space(){
//		assertExpectedExtraction("111-1111", "111 -  1111");
//	}
//
//	@Test
//	public void testTryCast_empty_string(){
//		assertExpectedExtraction(null, "");
//	}
//	
//	@Test
//	public void testTryCast_phone_without_minus_sign(){
//		assertExpectedExtraction(null, "1111111");
//	}
//
//	@Test
//	public void testTryCast_phone_without_first_part(){
//		assertExpectedExtraction(null, "-1111");
//	}
//
//	@Test
//	public void testTryCast_phone_without_second_part(){
//		assertExpectedExtraction(null, "111-");
//	}
//	
//	@Test
//	public void testTryCast_string(){
//		assertExpectedExtraction(null, "a");
//	}
//
//	@Test
//	public void testTryCast_string_similar_to_phone(){
//		assertExpectedExtraction(null, "xxx111-1111xxx");
//	}

	/* --- Tests for distance  --- */
	
	@Test
	public void testGetDistance_equal_elements(){
		assertNumberDistance(0.0, "1111111", "1111111");
	}

	@Test
	public void testGetDistance_similar_elements(){
		assertNumberDistance(1.0, "1111111", "1111112");
	}
	
	@Test
	public void testGetDistance_different_elements(){
		assertNumberDistance(1.0, "1111111", "2222222");
	}

}
