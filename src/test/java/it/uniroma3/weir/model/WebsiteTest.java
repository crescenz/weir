package it.uniroma3.weir.model;

import static it.uniroma3.weir.fixture.AttributeFixture.createAttribute;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class WebsiteTest {
	
	private Website ws;
	
	@Before
	public void beforeTest() {
		ws = new Website("www.site.com");
	}

	@Test
	public void testGetSitename() {
		assertEquals("www.site.com", ws.getName());
	}

	@Test
	public void testNumberOfPagesAfterCreation() {
		assertEquals(0, ws.getOverlappingPages().size());
	}

	@Test
	public void testAddPage() {
		Webpage p1 = new Webpage("001");
		ws.addPage(p1);
		assertEquals(1, ws.getOverlappingPages().size());
		assertEquals(p1, ws.getOverlappingPages().get(0));
	}

	@Test
	public void testNumberOfAttributesAfterCreation() {
		assertEquals(0, ws.getAttributes().size());
	}

	@Test
	public void testAddAttribute() {
		Attribute attr = createAttribute("a", "b");
		ws.addAttribute(attr);
		assertEquals(1, ws.getAttributes().size());
		assertEquals(attr, ws.getAttributes().get(0));
	}

}
