package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.weir.extraction.wrapper.template.TemplateMarker.INVARIANT_MARKER;
import static it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates.isText;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class RelativeXPathBuilder {

	public String makeXPath(Node pivot, Navigation pivot2value) {
		final boolean textualPivot = ( isText(pivot) );
		final String result;
		/* the starting node can be: 
		 * either a node with an id attribute, 
		 * or an element that is the parent of a 
		 * PCDATA containing an invariant text */
		if (textualPivot) {
			// a text with an invariant label as content
			final Text starting = (Text)pivot;
			result = pivotXPath(starting) + 
					 pivot2valueXPath(pivot, pivot2value.stripFirstIfNotEmpty());
		} else {
			// an element with an id attribute
			final Element starting = (Element)pivot;
			result = pivotXPath(starting) + 
					 pivot2valueXPath(pivot, pivot2value);
		}
		if (!pivot2value.isEmpty())
		// pivot is not the target
			return result + "/self::text()" ; // only texts, please...
		// pivot is also the target (mixed variant and invariant PCDATA)
		else return result +"/text()" ;
	}

	/**
	 * see ...paper...
	 * @param textualPivot occurrence of the pivot node
	 * @return an XPath expression to find the pivot
	 */
	private String pivotXPath(Text textualPivot) {
		// a textual invariant label plays as pivot
		final Node parentNode = textualPivot.getParentNode();
		
		final String elementName = parentNode.getNodeName(); // e.g., TD
		final String label = getMeaningfulLabel(textualPivot);
		return "//" + elementName.toUpperCase() + "[contains(text(),'" + label + "')]";
	}
	
	private String pivotXPath(Element elementPivot) {
		// an element with an id attribute plays as pivot
		final String elementName = elementPivot.getNodeName().toUpperCase(); // e.g., DIV
		return "//" + elementName + "[@id='" + getIdValue(elementPivot) + "']";
	}
	
	private String getIdValue(Node pivot) {
		final Element element = (Element)pivot;
		return element.getAttribute("id");
	}

	private String getMeaningfulLabel(Text pivot) {
		return sanitize(extractLongestInvariant(pivot));
	}

	@SuppressWarnings("unchecked")
	private String extractLongestInvariant(Text pivot) {
		// choose longest invariant
		String result = null;
		final Object invariants = pivot.getUserData(INVARIANT_MARKER);
		if (invariants instanceof String) 
			return (String)invariants;
		for(Object inv : (List<Object>)invariants) {
			final String candidate = inv.toString();
			if (result==null || inv.toString().length()>result.length()) {
				result = candidate;
			}
		}
		
		return result;
	}

	private String sanitize(String label) {
//was:	return label.replaceAll("'", "\"").replace("\u00a0","").replaceAll("\\s+", " ");
		return label.replaceAll("'", "\"").replace("\u00a0","\\u00a0").trim();
	}

	private String pivot2valueXPath(Node pivot, Navigation pivot2value) {
		final StringBuilder result = new StringBuilder();
		for(Step d : pivot2value)
			result.append(d.xPathStep());
		return result.toString();
	}

}