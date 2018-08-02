package it.uniroma3.weir.extraction.wrapper.pcdata;

import static it.uniroma3.token.Constants.SEPARATOR_CHARS;
import static it.uniroma3.weir.fixture.WebpageFixture.webpage;
import static javax.xml.xpath.XPathConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.extraction.wrapper.DocumentUtils;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.model.Webpage;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.*;


/**
 * How does the XPath evaluation behave over denormalized documents
 * as those created during Weir's processing ? 
 * 
 */
public class XPathAssumesNormalizedDocumentsSpike extends WeirTest {

	private XPathFactory xpathFactory = XPathFactory.newInstance();

	@Before
	public void setUp() {
		WeirConfig.getInstance().setProperty(SEPARATOR_CHARS, "");
		this.xpathFactory = XPathFactory.newInstance();
	}

	@Test
	public void spike_XPathOverDenormalizedDocuments() throws Exception {
		final Webpage page = webpage("<HTML><BODY>a b</BODY></HTML>");

		/* Let's assume and double-check that the DOM representation is NOT normalized */		
		final XPathExpression bodyXPath = this.xpathFactory.newXPath().compile("//BODY");
		final Node body = (Node)bodyXPath.evaluate(page.getDocument(), NODE);
		// The BODY element has got more than just one text child so...
		assumeTrue(body.getChildNodes().getLength()>1); //...it is NOT normalized

		/* ...the XPath evalution engine assume the normalized version of the document ... */
		final XPathExpression textXPath = this.xpathFactory.newXPath().compile("/HTML/BODY/text()");
		
		/* ...and it keeps on working as long as we evaluate XPath as STRINGs ...*/
		final String text = (String)textXPath.evaluate(page.getDocument(), STRING);
		assertEquals("a b", text);
		
		/* However, as we evaluate as NODESET something strange happens... */
		NodeList nodes = (NodeList)textXPath.evaluate(page.getDocument(), NODESET);
		assumeNotNull(nodes);
		/* ... The XPath evaluation engine returns on the first text node! */
		assertEquals(1, nodes.getLength());
		assertEquals("a",nodes.item(0).getNodeValue()); // the first text node contains "a"

		/* Now let's double-check that it's due to normalization: */
		/* Let's normalize the DOM and re-evaluate same XPath exp */
		page.getDocument().normalize();
		nodes = (NodeList)textXPath.evaluate(page.getDocument(), NODESET);
		assumeNotNull(nodes);
		assertEquals(1, nodes.getLength());
		assertEquals("a b",nodes.item(0).getNodeValue()); 
		/*  Now it works ! */
	}
	
	@Test
	public void spike_normalizingInPresenceOfUserDataLossesUserData() {
		final Document doc = webpage("<HTML><BODY>ab</BODY></HTML>").getDocument();
		final Element body = DocumentUtils.getElement(doc, "BODY", 0);
		assumeTrue(body.getChildNodes().getLength()==1);
		final Text text = (Text) body.getFirstChild();
		text.splitText(1);
		assumeTrue(body.getChildNodes().getLength()==2);
		final Text first = (Text) body.getFirstChild();
		assertEquals("a",first.getNodeValue());
		final Text last = (Text) body.getLastChild();
		assertEquals("b",last.getNodeValue());
		first.setUserData("AnnoFirst", "X", null);
		last.setUserData("AnnoLast", "Y", null);
		assertEquals("X",first.getUserData("AnnoFirst"));
		assertEquals("Y",last.getUserData("AnnoLast"));
		doc.normalize();
		assertEquals(1,body.getChildNodes().getLength());
		final Text merged = (Text) body.getFirstChild();
		assertEquals("X",merged.getUserData("AnnoFirst"));
		/* Unfortunately, we lost last annotation! */
		assertNull(merged.getUserData("AnnoLast"));
	}

}
