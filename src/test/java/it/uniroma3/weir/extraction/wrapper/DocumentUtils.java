package it.uniroma3.weir.extraction.wrapper;


import static javax.xml.xpath.XPathConstants.NODESET;
import static org.junit.Assert.*;
import it.uniroma3.util.MarkUpUtils;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.*;


public class DocumentUtils {

	static public Element getUniqueElement(Document doc, String name) {
		final NodeList nodes = doc.getElementsByTagName(name);
		assertFalse(msg("None element found by name=",name,doc), nodes.getLength()==0); 
		assertEquals(msg("Multiple elements found by name=",name,doc), 1, nodes.getLength()); 
		final Node found = nodes.item(0);
		assertNotNull(msg("Cannot find an element with name=",name,doc),found);
		return (Element)found;
	}

	static public Element getElement(Document doc, String name, int index) {
		final NodeList nodes = doc.getElementsByTagName(name);
		assertFalse(msg("None element found by name=",name,doc), nodes.getLength()==0); 
		final Node found = nodes.item(index);
		assertNotNull(msg("Cannot find a node with index=",index,doc),found);
		return (Element)found;
	}

	static public Text getTextByContent(Document doc, String content) {
		NodeList nodes = null;
		try {
			nodes = evaluateXPath(doc, "//text()[contains(.,'"+content+"')]");
		} catch (XPathExpressionException e) {
			fail("Cannot access text by content="+content);
		}
		assertFalse(msg("None text found by content=",content,doc), nodes.getLength()==0); 
		assertEquals(msg("Multiple texts found by content=",content,doc), 1, nodes.getLength()); 
		final Node found = nodes.item(0);
		assertTrue("Found a node that is not an text ", found instanceof Text);
		return (Text) found;
	}
	
	static public Text getText(Document doc, String parentName, int parentIndex, int textIndex) {
		final NodeList elements = doc.getElementsByTagName(parentName);
		assertFalse(msg("None element found by name=",parentName,doc), elements.getLength()==0); 
		final Node textParent = elements.item(parentIndex);
		assertTrue("Found an element without children", textParent.hasChildNodes());
		final Node found = textParent.getChildNodes().item(textIndex);
		assertNotNull(msg("Cannot find a text with index=",textIndex,doc),found);
		assertTrue("Found a node, but it is not an text ", found instanceof Text);
		return ((Text)found);
	}
	
	public static Element getElementById(Document doc, String id) {
		/* we cannot use doc.getElementById(). Check:
		 * http://stackoverflow.com/questions/3423430/java-xml-dom-how-are-id-attributes-special
		 */
        NodeList nodes = null;
		try {
	        nodes = evaluateXPath(doc, "//*[@id = '"+id+"']");
		} catch (XPathExpressionException e) {
			fail(msg("Cannot access elements by id=",id,doc));
		}
		assertFalse(msg("None element found by id=",id,doc), nodes.getLength()==0); 
		assertEquals(msg("Multiple elements found by id=",id,doc), 1, nodes.getLength()); 
		final Node found = nodes.item(0);
		assertTrue("Found a node that is not an element ", found instanceof Element);
        return (Element) found;
	}

	public static NodeList evaluateXPath(Document doc, String xpath)
			throws XPathExpressionException {
		final XPath xPath = XPathFactory.newInstance().newXPath();
		final Element root = doc.getDocumentElement();
		return (NodeList)xPath.evaluate(xpath, root, NODESET);
	}
	
	public static Node getUniqueByXPath(Document doc, String xpath)
			throws XPathExpressionException {
		NodeList nodes = evaluateXPath(doc, xpath);
		assertFalse(msg("None found by XPath: ",xpath,doc), nodes.getLength()==0); 
		assertEquals(msg("Multiple nodes found by XPath: ",xpath,doc), 1, nodes.getLength());
		return nodes.item(0);
	}
	
	static final String msg(String msg, Object o, Document doc) {
		return msg + o + " over\n" + MarkUpUtils.dumpTree(doc);
	}
	
}
