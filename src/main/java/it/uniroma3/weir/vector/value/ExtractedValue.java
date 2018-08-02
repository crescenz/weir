package it.uniroma3.weir.vector.value;

import static org.w3c.dom.Node.TEXT_NODE;
import it.uniroma3.weir.extraction.wrapper.PositionalXPathBuilder;
import it.uniroma3.weir.model.Webpage;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * 
 * Represents an <em>occurrence</em> of a string value as 
 * extracted  from a @li{@link Node} of a {@link Webpage}. 
 * It preserves a link back to the page.
 * 
 * Instances of this class are immutable.
 *
 */
public class ExtractedValue extends Value implements Serializable {
	
	static final private long serialVersionUID = -5992470944526706405L;
	
	static public Value nullValue(Webpage page) {
		return new ExtractedValue(page, (NodeList)null);
	}
	
	/* a marker to disambiguate distinct occurrences of a string value */
	private String mark;
    
	/* Positional/Relative rule extraction */
    public ExtractedValue(Webpage page, NodeList nodes) {
    	this(page, textualValue(nodes), getOccurrenceMark(nodes));
    }

    /* SubPCDATA rule extraction */
    public ExtractedValue(Webpage page, String text, NodeList nodes) {
    	this(page, sanitize(text), getOccurrenceMark(nodes));
    }

	/**
	 * Use this when a disambiguation mark is not available 
	 * (e.g., for golden values)
	 * @param page
	 * @param text
	 */
	protected ExtractedValue(Webpage page, String text) {
		this(page,text,(String)null); 
	}

    /* golden-values: see a few lines below... */
	protected ExtractedValue(Webpage page, String text, String mark) {
		super(page,emptyStringAsNullMarker(sanitize(text)));
		this.mark = mark;
	}
	static private String emptyStringAsNullMarker(String value) {
		// the empty string is a null-marker
		return value==null || value.isEmpty() ? null : value.trim();
	}
	
	public String getOccurrenceMark() {
		return this.mark;
	}
	
	/**
	 * @return the {@link String} object as a typed 
	 *         interpretation of this extracted value
	 */
	@Override
	public String getValue() {
		return (String) super.getValue();
	}
    
	/* A regexp that catches all whitespace characters unicode + traditional
	   http://stackoverflow.com/questions/1822772/java-regular-expression-to-match-all-whitespace-characters */
	static final private Pattern ALL_WHITESPACES = Pattern.compile("[\\p{Z}\\s]");
	
	//FIXME centralize sanitization/normalization logics
	static private String textualValue(NodeList nodes) {
		if (nodes==null || nodes.getLength()==0) return null;
		final StringBuilder result = new StringBuilder();
		int i=0;
//		for(; i<nodes.getLength()-1; i++) {
//			final Node node = nodes.item(i);
//			appendTextContent(result, node);
//			result.append(" ");
//		}
		appendTextContent(result, nodes.item(i));
		return result.toString();
	}

	static private void appendTextContent(StringBuilder result, Node node) {
		if (node==null || node.getNodeType()!=TEXT_NODE) return;
		final String textualValue = node.getNodeValue();
		if (textualValue!=null) // avoid "null" strings
			result.append(textualValue);
	}

	static private String sanitize(final String text) {
		if (text==null) return null;
		return ALL_WHITESPACES.matcher(text).replaceAll(" ").trim();
	}

	static final public String OCCURRENCE_MARK = "mark";
	
	static private String getOccurrenceMark(NodeList nodes) {
		if (nodes==null || nodes.getLength()==0) return null;
		
		final Text text = (Text)nodes.item(0);
		
		/* cache it directly as user-data in the DOM tree */
		String mark = (String) text.getUserData(OCCURRENCE_MARK);
		if (mark==null) {
			mark = computeOccurrenceMark(text);
			for(int i=0; i<nodes.getLength(); i++) {
				final Node node = nodes.item(i);
				node.setUserData(OCCURRENCE_MARK, mark, null);
			}
		}
		return mark;
	}

	// a marker (actually the positional/absolute XPath of the DOM node is used) 
	// to disambiguate several occurrences of the same string value extracted 
	static final private PositionalXPathBuilder positionalXPathBuilder = new PositionalXPathBuilder();

	static final private String computeOccurrenceMark(Text text) {
		return positionalXPathBuilder.getXPath(text);
	}

}
