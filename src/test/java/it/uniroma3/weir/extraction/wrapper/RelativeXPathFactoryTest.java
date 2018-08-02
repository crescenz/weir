package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures._HTML_TREE_;
import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures.document;
import static it.uniroma3.weir.extraction.wrapper.DocumentUtils.*;
import static it.uniroma3.weir.extraction.wrapper.Step.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import it.uniroma3.weir.fixture.WeirTest;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import static it.uniroma3.weir.extraction.wrapper.Navigation.navigation;

public class RelativeXPathFactoryTest extends WeirTest {

	private RelativeXPathBuilder factory;
	
	@Before
	public void setUp() throws Exception {
		this.factory = new RelativeXPathBuilder();
	}

	@Test
	public void testMakeXPath_pivotOnText() {
		generateAndTestXPath(
				 _HTML_TREE_("text<P>pivot</P>"), 
				 "pivot", "text", UP, SX );
		generateAndTestXPath(
				_HTML_TREE_("<P>pivot</P>text"), 
				 "pivot", "text", UP, DX );
	}

	@Test
	public void testMakeXPath_pivotOnNodeWithId() {
		generateAndTestXPath(_HTML_TREE_("<PIVOT id='p'/>target"), 
				 "<PIVOT>", "target", DX );
		generateAndTestXPath(_HTML_TREE_("target<PIVOT id='p'/>"), 
				 "<PIVOT>", "target", SX );
	}
	
	public void testMakeXPath_pivotOnText_distance2() {
		generateAndTestXPath(_HTML_TREE_("<P>pivot</P><BR/>target"), 
				 "pivot", "target", DX, DX );
		generateAndTestXPath(_HTML_TREE_("target<BR/><P>pivot</P>"), 
				 "pivot", "target", SX, SX );
		generateAndTestXPath(_HTML_TREE_("<P><B>pivot</B></B>target"), 
				 "pivot", "target", UP, DX );
		generateAndTestXPath(_HTML_TREE_("target<P><B>pivot</B></B>"), 
				 "pivot", "target", UP, SX );
	}
	
	@Test
	public void testMakeXPath_pivotOnNodeWithId_distance2() {
		generateAndTestXPath(_HTML_TREE_("target<PIVOT id='p'/>"), 
				 "<PIVOT>", "target", UP, DWT(1) );	
		generateAndTestXPath(_HTML_TREE_("<P><PIVOT id='p'/></P>target"), 
				 "<PIVOT>", "target", UP, DX );
		generateAndTestXPath(_HTML_TREE_("target<P><PIVOT id='p'/></P>"), 
				 "<PIVOT>", "target", UP, SX );
		generateAndTestXPath(_HTML_TREE_("<PIVOT id='p'/><HR/>target"), 
				 "<PIVOT>", "target", DX, DX );
		generateAndTestXPath(_HTML_TREE_("target<HR/><PIVOT id='p'/>"), 
				 "<PIVOT>", "target", SX, SX );
		generateAndTestXPath(_HTML_TREE_("<PIVOT id='p'><P>target</P></PIVOT>"), 
				 "<PIVOT>", "target", DWE(1), DWT(1) );		
		generateAndTestXPath(_HTML_TREE_("<PIVOT id='p'><HR/>target</PIVOT>"), 
				 "<PIVOT>", "target", DWE(1), DX );
		generateAndTestXPath(_HTML_TREE_("<PIVOT id='p'>target<HR/></PIVOT>"), 
				 "<PIVOT>", "target", DWE(1), SX );
	}	
	
	private void generateAndTestXPath(final String content, String p, String t, Step... p2t) {
		final Document doc = document( content );
		final Node pivotNode = findPivot(doc, p);
		final String xpath = this.factory.makeXPath(pivotNode, navigation(p2t));
		final Text expected = getTextByContent(doc, t);
		Node actual;
		try {
			actual = getUniqueByXPath(doc, xpath);
			assertSame("Wrongly selected: "+actual+" instead of "+expected, 
					expected, actual);	
		} catch (XPathExpressionException e) {
			fail(e.getMessage());
		}
	}

	static final private Node findPivot(final Document doc, String p) {
		if (p.startsWith("<")) // <PIVOT>
			return getUniqueElement(doc, p.substring(1, p.length()-1));// PIVOT
		return getTextByContent(doc, p);
	}
	
}
