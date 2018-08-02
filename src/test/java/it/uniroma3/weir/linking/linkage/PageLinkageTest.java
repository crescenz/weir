package it.uniroma3.weir.linking.linkage;

import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.model.Webpage;

import org.junit.Before;
import org.junit.Test;
import static it.uniroma3.weir.fixture.Asserts.assertDoubleEquals;

public class PageLinkageTest {
	
	private PageLinkage pageLinkage;
	
	@Before
	public void beforeTest() {
		this.pageLinkage = new PageLinkage(new Webpage("010"), new Webpage("011"), 0.9);
	}

	@Test
	public void testGetPage1() {
		assertEquals(new Webpage("010"), pageLinkage.getMin());
	}

	@Test
	public void testGetPage2() {
		assertEquals(new Webpage("011"), pageLinkage.getMax());
	}

	@Test
	public void testGetSimilarity() {
		assertDoubleEquals(0.9, pageLinkage.getSimilarity());
	}

}
