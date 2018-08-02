package it.uniroma3.weir.model;

import static it.uniroma3.weir.fixture.AttributeFixture.createAttribute;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class MappingTest {
	
	private Mapping mapping;
	
	@Before
	public void beforeTest() {
		mapping = new Mapping();
	}

	@Test
	public void testIsCompleteAfterCreation() {
		assertFalse(mapping.isComplete());
	}

	@Test
	public void testMarkAsComplete() {
		mapping.markAsComplete();
		assertTrue(mapping.isComplete());
	}

	@Test
	public void testAttributesSizeAfterCreation() {
		assertEquals(0, mapping.size());
	}

	@Test
	public void testAddAtribute() {
		Attribute attr = createAttribute("a", "b");
		mapping.add(attr);
		assertEquals(1, mapping.size());
		assertEquals(attr, mapping.getAttributes().iterator().next());
	}
	
}
