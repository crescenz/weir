package it.uniroma3.weir.vector.type;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TypeTest {
	
	private static void assertParent(Type exp, Type actual) {
		assertEquals(exp, actual.getParent());
	}
	
	private static void assertCommonAncestor(Type exp, Type t1, Type t2) {
		assertEquals(exp, Type.getCommonAncestor(t1, t2));
	}

	/* --- Tests for parent --- */
	
	@Test
	public void testParent_string_type() {
		assertParent(null, Type.STRING);
	}

	@Test
	public void testParent_isbn_type() {
		assertParent(Type.STRING, Type.ISBN);
	}

	@Test
	public void testParent_url_type() {
		assertParent(Type.STRING, Type.URL);
	}

	@Test
	public void testParent_phone_type() {
		assertParent(Type.STRING, Type.PHONE);
	}

	@Test
	public void testParent_date_type() {
		assertParent(Type.STRING, Type.DATE);
	}

	@Test
	public void testParent_number_type() {
		assertParent(Type.STRING, Type.NUMBER);
	}

	@Test
	public void testParent_mass_type() {
		assertParent(Type.DIMENSIONAL, Type.MASS);
	}

	@Test
	public void testParent_money_type() {
		assertParent(Type.DIMENSIONAL, Type.MONEY);
	}

	@Test
	public void testParent_space_type() {
		assertParent(Type.DIMENSIONAL, Type.SPACE);
	}
	
	/* --- Tests for ancestor --- */
	
	@Test
	public void testCommonAncestor_string_with_string() {
		assertCommonAncestor(Type.STRING, Type.STRING, Type.STRING);
	}

	@Test
	public void testCommonAncestor_number_with_number() {
		assertCommonAncestor(Type.NUMBER, Type.NUMBER, Type.NUMBER);
	}
	
	@Test
	public void testCommonAncestor_number_with_string() {
		assertCommonAncestor(Type.STRING, Type.NUMBER, Type.STRING);
	}

	@Test
	public void testCommonAncestor_descendant_of_string_with_descendant_of_string() {
		assertCommonAncestor(Type.STRING, Type.NUMBER, Type.DATE);
	}

	@Test
	public void testCommonAncestor_dimensional_with_descendant_of_string() {
		assertCommonAncestor(Type.STRING, Type.MASS, Type.DATE);
	}

	@Test
	public void testCommonAncestor_dimensional_with_number() {
		assertCommonAncestor(Type.NUMBER, Type.MASS, Type.NUMBER);
	}
	
	@Test
	public void testCommonAncestor_dimensional_with_dimensional() {
		assertCommonAncestor(Type.DIMENSIONAL, Type.MASS, Type.SPACE);
	}

}
