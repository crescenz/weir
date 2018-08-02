package it.uniroma3.weir.vector;

import static it.uniroma3.util.CollectionsCSVUtils.csv2collection;
import static it.uniroma3.weir.vector.type.Type.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import it.uniroma3.weir.vector.type.Type;

import org.junit.Test;

public class VectorCasterTest {
	
	static final private String AVAILABLE_TYPES = "STRING,NUMBER,MONEY,SPACE,MASS,DATE,PHONE,ISBN,URL";
	
	static private void assertType(Type expected, String... values) {
		final List<String> typenames = new ArrayList<>(csv2collection(AVAILABLE_TYPES));
		final VectorCaster caster = new VectorCaster(Type.valueOf(typenames));
		final Type actual = caster.findMostSpecificType(values);
		assertEquals(expected, actual);
	}

	/* --- Tests for casting a vector having all the elements of the same Type --- */
	
	@Test
	public void test_urls() {
		assertType(URL, "http://www.site1.com", "http://www.site2.com");
	}

	@Test
	public void test_isbn() {
		assertType(ISBN, "111-1111111111", "222-2222222222");
	}

	@Test
	public void test_phone() {
		assertType(PHONE, "111) 111-1111", "222) 222-2222");
	}

	@Test
	public void test_date() {
		assertType(DATE, "1 1 2001", "2 2 2002");
	}

	@Test
	public void test_mass() {
		assertType(MASS, "1 Kg", "2 Kg");
	}

	@Test
	public void test_space() {
		assertType(SPACE, "1 m", "2 m");
	}

	@Test
	public void test_money() {
		assertType(MONEY, "1 $", "2 $");
	}

	@Test
	public void test_number() {
		assertType(NUMBER, "1", "2");
	}

	@Test
	public void test_string() {
		assertType(STRING, "a", "b");
	}

	/* --- Tests for casting a vector having a STRING element, returning a STRING vector --- */

	@Test
	public void testTryCast_one_date() {
		assertType(STRING, "1 1 2001", "a");
	}

	@Test
	public void testTryCast_one_isbn() {
		assertType(STRING, "111-1111111111", "a");
	}

	@Test
	public void testTryCast_one_mass() {
		assertType(STRING, "1 Kg", "a");
	}

	@Test
	public void testTryCast_one_money() {
		assertType(STRING, "1 $", "a");
	}

	@Test
	public void testTryCast_one_number() {
		assertType(STRING, "1", "a");
	}

	@Test
	public void testTryCast_one_phone() {
		assertType(STRING, "111-1111", "a");
	}

	@Test
	public void testTryCast_one_space() {
		assertType(STRING, "1 m", "a");
	}

	@Test
	public void testTryCast_one_url() {
		assertType(STRING, "http://www.site.com", "a");
	}

	/* --- Tests for casting a vector having different Type elements, returning a STRING vector --- */

	@Test
	public void testTryCast_date_number() {
		assertType(STRING, "1 1 2001", "1");
	}

	@Test
	public void testTryCast_date_isbn() {
		assertType(STRING, "1 1 2001", "111-1111111111");
	}

	@Test
	public void testTryCast_date_url() {
		assertType(STRING, "1 1 2001", "http://www.site.com");
	}

	@Test
	public void testTryCast_date_phone() {
		assertType(STRING, "1 1 2001", "111-1111");
	}

	@Test
	public void testTryCast_number_isbn() {
		assertType(STRING, "1", "111-1111111111");
	}

	@Test
	public void testTryCast_number_url() {
		assertType(STRING, "1", "http://www.site.com");
	}

	@Test
	public void testTryCast_number_phone() {
		assertType(STRING, "1", "111-1111");
	}

	@Test
	public void testTryCast_isbn_url() {
		assertType(STRING, "111-1111111111", "http://www.site.com");
	}

	@Test
	public void testTryCast_isbn_phone() {
		assertType(STRING, "111-1111111111", "111-1111");
	}

	@Test
	public void testTryCast_url_phone() {
		assertType(STRING, "http://www.site.com", "111-1111");
	}

	@Test
	public void testTryCast_mass_space() {
		assertType(STRING, "1 Kg", "1 m");
	}

	@Test
	public void testTryCast_mass_money() {
		assertType(STRING, "1 Kg", "1 $");
	}

	@Test
	public void testTryCast_space_money() {
		assertType(STRING, "1 m", "1 $");
	}
	
	@Test
	public void testTryCast_mass_date() {
		assertType(STRING, "1 Kg", "1 1 2001");
	}

	@Test
	public void testTryCast_mass_isbn() {
		assertType(STRING, "1 Kg", "111-1111111111");
	}

	public void testTryCast_mass_number() {
		assertType(STRING, "1 Kg", "1.0");
	}

	@Test
	public void testTryCast_mass_phone() {
		assertType(STRING, "1 Kg", "111-1111");
	}

	@Test
	public void testTryCast_mass_url() {
		assertType(STRING, "1 Kg", "http://www.site.com");
	}

	@Test
	public void testTryCast_money_date() {
		assertType(STRING, "1 $", "1 1 2001");
	}

	@Test
	public void testTryCast_money_isbn() {
		assertType(STRING, "1 $", "111-1111111111");
	}

	@Test
	public void testTryCast_money_number() {
		assertType(STRING, "1 $", "1.0");
	}

	public void testTryCast_money_phone() {
		assertType(STRING, "1 $", "111-1111");
	}

	@Test
	public void testTryCast_money_url() {
		assertType(STRING, "1 $", "http://www.site.com");
	}

	@Test
	public void testTryCast_space_date() {
		assertType(STRING, "1 m", "1 1 2001");
	}

	@Test
	public void testTryCast_space_isbn() {
		assertType(STRING, "1 m", "111-1111111111");
	}

	@Test
	public void testTryCast_space_number() {
		assertType(STRING, "1 m", "1.0");
	}

	public void testTryCast_space_phone() {
		assertType(STRING, "1 m", "111-1111");
	}

	@Test
	public void testTryCast_space_url() {
		assertType(STRING, "1 m", "http://www.site.com");
	}
	
}
