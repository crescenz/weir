package it.uniroma3.weir.extraction.wrapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import static org.w3c.dom.Node.*;
import static it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates.*;

public class PositionalXPathBuilder {

	public String getXPath(Text text) {
		return findXPath(text.getParentNode()) + "/text()[" + findTextXPathPosition(text) + "]";
	}

	private String findXPath(Node node) {
		/* positional rules rooted on the HTML node */
		if (node == null || "HTML".equalsIgnoreCase(node.getNodeName())){
			return "/HTML[1]";
		}

		/* positional rules rooted on template nodes with an 'id' attribute */
		if (isNodeWithId(node)) {
			final Node id = node.getAttributes().getNamedItem(ID_ATTRIBUTE);
			if (id!=null)
				return "//" + node.getNodeName() + "[@id='" + id.getNodeValue() + "']";			
		}

		/* positional rules rooted somewhere above here */
		final String parentXPath = findXPath(node.getParentNode());
		final int position = findElementXPathPosition((Element) node);
		return parentXPath + "/" + node.getNodeName() + "[" + position + "]";
	}

	private int findElementXPathPosition(Element wanted) {
		final NodeList siblings = wanted.getParentNode().getChildNodes();
		int position = 1;
		for (int i=0; i<siblings.getLength(); i++) {
			final Node current = siblings.item(i);
			if (current.getNodeType()!=ELEMENT_NODE) continue;
			
			if (wanted==current) {
				return position;
			}

			if (wanted.getNodeName().equals(current.getNodeName())) { // same tag name
				position++;
			}
		}
		return -1;
	}

	private int findTextXPathPosition(Text wanted) {
		final NodeList siblings = wanted.getParentNode().getChildNodes();
		boolean textSeparated = true;
		int position = 0;
		for (int i=0;i<siblings.getLength(); i++) {
			final Node current = siblings.item(i);
			if (!isText(current)) {
				textSeparated = true; // deal with denormalized DOM with texts
				continue;             // spread over several contiguous nodes
			}
			
			// it does not matter which text value: any text matches
			if (textSeparated) {
				position++;
				textSeparated = false;
			}

			if (wanted==current) {
				return position;
			}
		}
		return -1;
	}

}
