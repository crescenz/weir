package it.uniroma3.weir.model;

import static it.uniroma3.weir.fixture.VectorFixture.createVector;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class AttributeTest {
	
	private Attribute attribute;
	
	@Before
	public void setUp() {
		this.attribute = new Attribute(createVector("a", "b"));
	}

	@Test
	public void testGetVector() {
		assertEquals(createVector("a", "b"), attribute.getVector());
	}
	
	
	

}
