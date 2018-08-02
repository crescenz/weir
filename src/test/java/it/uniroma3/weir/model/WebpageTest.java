package it.uniroma3.weir.model;

import static org.junit.Assert.assertEquals;
import it.uniroma3.util.FixtureUtils;

import java.io.File;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;

public class WebpageTest {

	private Webpage page;

	@Before
	public void beforeTest() {
		URI uri = FixtureUtils.makeTmpFile("<html></html>","page.html").toURI();
		page = new Webpage("001", uri);
	}

	@Test
	public void testGetIdentifier() {
		assertEquals("001", page.getId());
	}

	@Test
	public void testGetURI() {
		File f = new File(page.getURI());
		assertEquals(FixtureUtils.getTmpDirectory().toString(), f.getParentFile().getAbsolutePath());
	}

	@Test
	public void testGetWebsiteIndex() {
		assertEquals(0, page.getIndex());
	}

	@Test
	public void testGetWebsiteIndex_after_modification() {
		page.setWebsite(null, 1);
		assertEquals(1, page.getIndex());
	}

}
