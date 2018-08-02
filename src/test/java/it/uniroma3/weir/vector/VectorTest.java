package it.uniroma3.weir.vector;

import static it.uniroma3.weir.fixture.Asserts.assertDoubleEquals;
import static it.uniroma3.weir.fixture.VectorFixture.createVector;
import static it.uniroma3.weir.vector.type.Type.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import it.uniroma3.weir.vector.type.IllegalTypeCastException;
import it.uniroma3.weir.vector.type.Type;

import java.util.Arrays;

import org.junit.Test;


public class VectorTest {
	
	static public void assertTypeCast(Type type, String... values) {
		assertEquals(createVector(type,values).getType(), type);
	}

	static public void assertNotTypeCast(Type type, String... values) {
		try {
			createVector(type, values);
		}
		catch (IllegalTypeCastException itce) {
			return;
		}
		fail("Casting "+values+" to " + type + " should raise "+IllegalTypeCastException.class);
	}
	
	static public void assertIdenticalVectorsAtDistance0(Type type, String... values) {
		Vector v1 = createVector(type, values);
		Vector v2 = createVector(type, values);
		assertDoubleEquals(0, v1.distance(v2));
	}

	static public void assertTypedVectorsDistance(double expexted, Type type, String... values) {
		Vector v1 = createVector(type,firstHalf(values));
		Vector v2 = createVector(type,secondHalf(values));
		assertDoubleEquals(expexted, v1.distance(v2));
	}

	static private String[] firstHalf(String... values) {
		return Arrays.copyOfRange(values, 0, values.length/2);
	}

	static private String[] secondHalf(String... values) {
		return Arrays.copyOfRange(values, values.length/2, values.length);
	}

	static public void assertTypedVectorDistance(double expexted, Type t1, Type t2, String... values) {
		Vector v1 = createVector(t1, firstHalf(values));
		Vector v2 = createVector(t2, secondHalf(values));
		assertDoubleEquals(expexted, v1.distance(v2));
	}

	/* --- Tests for casting a vector from STRING to any Type --- */
	
	@Test
	public void testTryCast_string() {
		assertTypeCast(STRING, "a", "b");
	}

	@Test
	public void testTryCast_date() {
		assertTypeCast(DATE, "1 1 2001", "2 2 2002");
	}

	@Test
	public void testTryCast_isbn() {
		assertTypeCast(ISBN, "111-1111111111", "222-2222222222");
	}

	@Test
	public void testTryCast_mass() {
		assertTypeCast(MASS, "1 Kg", "2 lb");
	}

	@Test
	public void testTryCast_money() {
		assertTypeCast(MONEY, "1 $", "2 €");
	}

	@Test
	public void testTryCast_number() {
		assertTypeCast(NUMBER, "1", "2.0");
	}

	@Test
	public void testTryCast_phone() {
		assertTypeCast(PHONE, "111-111-1111", "222-222-2222");
	}

	@Test
	public void testTryCast_space() {
		assertTypeCast(SPACE, "1 m", "2 cm");
	}

	@Test
	public void testTryCast_url() {
		assertTypeCast(URL, "http://www.site.com", "www.site.com");
	}
	
	/* --- Tests for casting a vector from STRING to any Type, failing because one element have different Type --- */
	
	@Test
	public void testTryCast_one_date() {
		assertNotTypeCast(DATE, "1 1 2001", "string");
	}

	@Test
	public void testTryCast_one_isbn() {
		assertNotTypeCast(ISBN, "111-1111111111", "string");
	}

	@Test
	public void testTryCast_one_mass() {
		assertNotTypeCast(MASS, "1 Kg", "string");
	}

	@Test
	public void testTryCast_one_money() {
		assertNotTypeCast(MONEY, "1 $", "string");
	}

	@Test
	public void testTryCast_one_number() {
		assertNotTypeCast(NUMBER, "1", "string");
	}

	@Test
	public void testTryCast_one_phone() {
		assertNotTypeCast(PHONE, "111-1111", "string");
	}

	@Test
	public void testTryCast_one_space() {
		assertNotTypeCast(SPACE, "1 m", "string");
	}

	@Test
	public void testTryCast_one_url() {
		assertNotTypeCast(URL, "http://www.site.com", "string");
	}

	/* --- Tests for distance between two vectors of same Type, having the same elements --- */
	
	@Test
	public void testGetDistance_string() {
		assertIdenticalVectorsAtDistance0(STRING, "a", "b");
	}

	@Test
	public void testGetDistance_date() {
		assertIdenticalVectorsAtDistance0(DATE, "1 1 2001", "2 2 2002");
	}

	@Test
	public void testGetDistance_isbn() {
		assertIdenticalVectorsAtDistance0(ISBN, "111-1111111111", "222-2222222222");
	}

	@Test
	public void testGetDistance_mass() {
		assertIdenticalVectorsAtDistance0(MASS, "1 Kg", "2 lb");
	}

	@Test
	public void testGetDistance_money() {
		assertIdenticalVectorsAtDistance0(MONEY, "1 $", "2 €");
	}

	@Test
	public void testGetDistance_number() {
		assertIdenticalVectorsAtDistance0(NUMBER, "1", "2.0");
	}

	@Test
	public void testGetDistance_phone() {
		assertIdenticalVectorsAtDistance0(PHONE, "111-1111", "222-2222");
	}

	@Test
	public void testGetDistance_space() {
		assertIdenticalVectorsAtDistance0(SPACE, "1 m", "2 cm");
	}

	@Test
	public void testGetDistance_url() {
		assertIdenticalVectorsAtDistance0(URL, "http://www.site.com", "www.site.com");
	}
	
	/* --- Tests for distance between two vectors of same Type, having one different elements (the last) --- */

	@Test
	public void testGetDistance_two_string() {
		assertTypedVectorsDistance(0.5, STRING, "1", "2", "1", "3");
	}

	@Test
	public void testGetDistance_two_date() {
		assertTypedVectorsDistance(0.5, DATE, "1 1 2001", "2 2 2002", "1 1 2001", "3 3 2003");
	}

	@Test
	public void testGetDistance_two_isbn() {
		assertTypedVectorsDistance(0.5, ISBN, "111-1111111111", "222-2222222222", "111-1111111111", "333-3333333333");
	}

	@Test
	public void testGetDistance_two_mass() {
		assertTypedVectorsDistance(0.1, MASS, "1 Kg", "2 Kg", "1 Kg", "3 Kg");
	}

	@Test
	public void testGetDistance_two_money() {
		assertTypedVectorsDistance(0.1, MONEY, "1 $", "2 $", "1 $", "3 $");
	}

	@Test
	public void testGetDistance_two_number() {
		assertTypedVectorsDistance(0.1, NUMBER, "1", "2", "1", "3");
	}

	@Test
	public void testGetDistance_two_phone() {
		assertTypedVectorsDistance(0.5, PHONE, "111-1111", "222-2222", "111-1111", "333-3333");
	}

	@Test
	public void testGetDistance_two_space() {
		assertTypedVectorsDistance(0.1, SPACE, "1 m", "2 m", "1 m", "3 m");
	}

	@Test
	public void testGetDistance_two_url() {
		assertTypedVectorsDistance(0.125, URL, "www.site1.com", "www.site2.com", "www.site1.com", "www.site3.com");
	}

	/* --- Tests for distance between two vectors of different Type --- */

	@Test
	public void testGetDistance_number_string() {
		assertTypedVectorDistance(0.5, NUMBER, STRING, "1", "2", "1", "a");
	}

	@Test
	public void testGetDistance_descendant_of_string_descendant_of_string() {
		assertTypedVectorDistance(0.19, NUMBER, DATE, "1", "2", "1-1-2001", "2-2-2002");
	}

	@Test
	public void testGetDistance_dimensional_descendant_of_string() {
		assertTypedVectorDistance(0.42, SPACE, DATE, "1 m", "2 m", "1 1 2001", "2 2 2002");
	}

	@Test
	public void testGetDistance_dimensional_number() {
		assertTypedVectorDistance(0.0, SPACE, NUMBER, "1 m", "2 m", "1", "2");
	}

	@Test
	public void testGetDistance_dimensional_dimensional() {
		assertTypedVectorDistance(1.0, SPACE, MASS, "1 m", "2 m", "1 Kg", "2 Kg");
	}

	@Test
	public void testGetDistance_dimensional_string() {
		assertTypedVectorDistance(0.0, SPACE, STRING, "1 m", "2 m", "1 m", "2 m");
	}

	/* --- Tests distance between null vectors */

	@Test
	public void testGetDistance_between_two_null_vector() {
		assertTypedVectorsDistance(0.0, STRING, null, null, null, null);
	}

	@Test
	public void testGetDistance_between_one_null_vector_and_one_not() {
		assertTypedVectorsDistance(1.0, STRING, null, "a", null, null, null, "a");
	}

	@Test
	public void testGetDistance_equal_vector_without_nulls() {
		assertTypedVectorsDistance(0.0, STRING, "a", "a");
	}
	
	@Test
	public void testGetDistance_equal_vector_nulls() {
		assertTypedVectorsDistance(0.0, STRING, null, "a", null, "a");
	}

	@Test
	public void testGetDistance_different_vector_without_nulls() {
		assertTypedVectorsDistance(1.0, STRING, "a", "b");
	}
	
	@Test
	public void testGetDistance_nulls_not_influence_distance() {
		assertTypedVectorsDistance(1.0, STRING, null, "a", null, "b");
	}

	@Test
	public void testGetDistance_between_date_vector_and_string_not_without_itersection() {
		assertTypedVectorDistance(1.0, DATE, STRING, 
				"1-12-1999", 	null,	null, 	"11-1998", "1997", 	null,
				null,			null,	"a",	null, 		null,	null);
	}
	
	/* --- Tests distance between date vectors */

	@Test
	public void testGetDistance_equal_vector_various_date_format() {
		assertIdenticalVectorsAtDistance0(DATE, "1-12-1999", "11-1998", "1997");
	}

	@Test
	public void testGetDistance_vector_various_date_format() {
		assertIdenticalVectorsAtDistance0(DATE, "10/1998","6/8/1995","4/18/1996","2/1999","2/1999","5/18/2000","1/1999","1/1999","11/2000","6/1999","1/2000","9/1/1995","12/4/1995","7/1/1996",null,"5/31/1994","1998","2/1999","7/6/1998","9/1999","8/6/2001","11/9/1995","3/11/1996","6/1998","7/6/1998","5/1998","2/1999","3/2000","1/1999","7/3/2000","6/7/1999","11/1/2001","4/2001","4/6/1998","9/1999","6/1/1998","4/6/1999","8/7/2000","2/7/2000","4/1/2000","1/1/2000","10/23/1995","10/23/1995","8/21/1995","1/2000","1/2000","1/2000","2/19/1996","9/16/1996","5/1/1996","1/1996","3/2000","12/11/1995","12/1995","11/27/1995","5/20/1996","11/3/1997","1/1/1999","5/2/2000","5/20/1996","3/18/1996","2/19/1996","9/1/1996","5/1/1996","5/4/1999","4/1997","1996","1997","1997",null,"12/4/1995","4/1996","2/19/1996","2/19/1996","5/20/1996","1997","9/16/1996","10/4/1999","10/4/1999");
	}
	
}
