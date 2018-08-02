package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures._HTML_TREE_;
import static it.uniroma3.weir.extraction.wrapper.DocumentUtils.getElement;
import static it.uniroma3.weir.extraction.wrapper.DocumentUtils.getTextByContent;
import static it.uniroma3.weir.extraction.wrapper.Navigation.navigation;
import static it.uniroma3.weir.extraction.wrapper.Step.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import it.uniroma3.weir.fixture.WeirTest;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class NavigationTest extends WeirTest {

	private static final Navigation EMPTY_NAVIGATION = navigation();

	private Document document;
	
	private Node text;
	
	private Node hr;

	private Node span;

//	private Node pre;
	
	@Before
	public void setUp() {
		this.document = DocumentFixtures.document(
			_HTML_TREE_("<SPAN>pre<BR/>text<HR/>post<BR/>other</SPAN>")
		);
		this.text = getTextByContent(this.document, "text");
		this.hr = getElement(this.document, "HR", 0);
		this.span = getElement(this.document, "SPAN", 0);
//		this.pre = getTextByContent(this.document, "pre");
	}

	@Test
	public void testHasKnot_1_step() {
		assertFalse(EMPTY_NAVIGATION.hasKnot(this.text, UP));
		assertFalse(EMPTY_NAVIGATION.hasKnot(this.text, DX));
		assertFalse(EMPTY_NAVIGATION.hasKnot(this.span, DWT(2)));
		assertFalse(EMPTY_NAVIGATION.hasKnot(this.text, SX));
	}
	
	@Test
	public void testHasKnot_2_steps() {
		assertTrue(navigation(SX).hasKnot(this.text, DX));
		assertTrue(navigation(DX).hasKnot(this.text, SX));
	}

	@Test
	public void testOpposite_UP_and_DOWN() {
		assertTrue(navigation(UP).hasKnot(this.text, DWT(2)));
		assertTrue(navigation(DWT(2)).hasKnot(this.span, UP));
		
		assertFalse(navigation(UP).hasKnot(this.text, DWT(1)));// reach "pre"
		
		assertFalse(navigation(UP).hasKnot(this.hr, DWE(1))); // reach <BR/>		
	}	
	
	@Test
	public void testHasKnot_3_steps() {
		assertTrue(navigation(UP,DWE(2)).hasKnot(this.text, SX));
		assertFalse(navigation(UP,DWE(2)).hasKnot(this.text, DX));
	}

}