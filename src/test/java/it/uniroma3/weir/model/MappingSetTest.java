package it.uniroma3.weir.model;

import static it.uniroma3.weir.fixture.AttributeFixture.createRandomAttribute;
import static it.uniroma3.weir.fixture.MappingFixture.createMapping;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MappingSetTest {
	
	private MappingSet mapSet;
	
	@Before
	public void beforeTest() {
		mapSet = new MappingSet();
	}

	@Test
	public void testSizeAfterCreation() {
		assertEquals(0, mapSet.size());
	}

	@Test
	public void testAddMapping() {
		Mapping m = createMapping(createRandomAttribute(2));
		mapSet.addMapping(m);
		assertEquals(1, mapSet.size());
		assertEquals(m, mapSet.getMappings().iterator().next());
	}

	@Test
	public void testRemoveMapping() {
		Mapping m = createMapping(createRandomAttribute(2));
		mapSet.addMapping(m);
		mapSet.removeMapping(m);
		assertEquals(0, mapSet.size());
	}

	@Test
	public void testGetMappingByAttribute() {
		Attribute attr = createRandomAttribute(2);
		Mapping m = createMapping(attr);
		mapSet.addMapping(m);
		assertEquals(m, mapSet.getMappingByAttribute(attr));
	}

}
