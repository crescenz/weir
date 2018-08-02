package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures._HTML_TREE_;
import static it.uniroma3.weir.extraction.wrapper.DocumentUtils.getUniqueElement;
import static it.uniroma3.weir.fixture.WebpageFixture.webpage;
import static org.junit.Assert.*;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.model.Webpage;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class StepTest extends WeirTest {
	

	private Webpage page;
	
	private Document doc;
	
	@Before
	public void setUp() {
		this.page = webpage(_HTML_TREE_("<P><B>sx</B><BR/><I>dx</I></P>"));
		this.doc = page.getDocument();		
	}
	
	@Test
	public void test_UP() {
		final Element br = getUniqueElement(doc, "BR");

		final Node up = Step.UP.to(br);
		assertNotNull(up);
		assertEquals("P", up.getNodeName());
	}	
	
	@Test
	public void test_DX() {
		final Element b = getUniqueElement(doc, "B");

		final Node dx = Step.DX.to(b);
		assertNotNull(dx);
		assertEquals("BR", dx.getNodeName());
	}
	
	@Test
	public void test_SX() {
		final Element i = getUniqueElement(doc, "I");

		final Node sx = Step.SX.to(i);
		assertNotNull(sx);
		assertEquals("BR", sx.getNodeName());
	}

	@Test
	public void test_DWE() {
		final Element p = getUniqueElement(doc, "P");

		final Node br = Step.DWE(2).to(p);
		assertNotNull(br);
		assertEquals("BR", br.getNodeName());	
	}

}