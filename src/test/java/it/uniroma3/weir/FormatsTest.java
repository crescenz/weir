package it.uniroma3.weir;

import static it.uniroma3.weir.Formats.percentage;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FormatsTest {

	@Test
	public void testPercentage() {
		assertEquals("15,6%",percentage.format(0.156d));
		assertEquals("99,9%",percentage.format(0.999d));
		assertEquals("99,99%",percentage.format(0.9999d));
		assertEquals("100%",percentage.format(0.99999d));
		assertEquals("5,3%",percentage.format(0.053));
	}

}
