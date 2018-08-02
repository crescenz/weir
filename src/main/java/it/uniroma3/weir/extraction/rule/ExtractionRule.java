package it.uniroma3.weir.extraction.rule;

import static javax.xml.xpath.XPathConstants.NODESET;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.WeirId;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.value.ExtractedValue;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.List;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * An extraction rule to locate a textual leaf (i.e., a node {@link Text}) from a {@link Document}.
 * 
 * The considered rules are based on {@link XPathExpression}.
 * 
 * Several occurrences of the same text are disambiguated by means of
 * {@link #getOccurrenceMark(Text)}
 *
 */
public class ExtractionRule extends WeirId implements Serializable {

	static final private long serialVersionUID = 4940567786894867322L;
	
	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	static final private XPathFactory FACTORY = XPathFactory.newInstance();

	private transient XPathExpression xpath;

	final private String xpathString;
	
	final private ExtractionRuleClass ruleClass;

	private Website website;
	
	public ExtractionRule(ExtractionRuleClass clazz, String xpath) {
		super(nextIdByClass(ExtractionRule.class));
		this.ruleClass = clazz;
		this.xpathString = xpath;
		this.xpath = compileXPath(xpath);
	}
	
	public Website getWebsite() {
		return this.website;
	}

	public void setWebsite(Website site) {
		this.website = site;
	}
	
	
	public ExtractionRuleClass getExtractionRuleClass() {
		return this.ruleClass;
	}
	
	private XPathExpression compileXPath(String exp) {
		try {
			return FACTORY.newXPath().compile(exp);
		} catch (XPathExpressionException e) {
			throw new IllegalStateException("error compiling XPath expression " + this.xpathString + "\n" + e);
		}
	}

	public String getXPath() {
		return this.xpathString;
	}
	
	public XPathExpression getXPathExpression() {
		return this.xpath;
	}
	
	public ExtractedValue applyTo(Webpage page) {
		final Document document = page.getDocument();
		if (document==null)
			throw new IllegalStateException("Webpage "+page+" has not been loaded yet");
		synchronized (document) { 
			// synch since ExtractedValue access document
			// to extract text value and occurrence mark
			return extract(page, document);			
		}
	}

	/**
	 * @return a rule from which this rule derive, if any,
	 *         the rule itself otherwise
	 */
	public ExtractionRule getOriginatingRule() {
		return this;
	}

	protected ExtractedValue extract(Webpage page, final Document document) {
		return new ExtractedValue(page, (NodeList)applyTo(document));
	}
	
	public ExtractedVector applyTo(List<Webpage> pages) {
		return applyTo(pages,0);
	}
	
	public ExtractedVector applyTo(List<Webpage> pages, int offset) {
		final int npages = pages.size();
		final ExtractedValue[] values = new ExtractedValue[npages];

		for(int i=0; i<npages; i++) {
			final int index = (i+offset) % npages; // so they'll work on different docs
			values[index] = this.applyTo(pages.get(index));
		}

		return new ExtractedVector(values, this);		
	}
	
	public Object applyTo(Document document) {
		// N.B. xerces DOM impl. requires clients to serialize 
		//      accesses because of lazy inits in the DOM trees
		// https://xerces.apache.org/xerces2-j/faq-dom.html#faq-1
		synchronized (document) {
			try {
				return evaluate(document);
			} catch (XPathExpressionException e) {
				log.warn(e.getMessage()+" during "+ this + " application");
				throw new ExtractionException(e);
			}		
		}
	}

	protected Object evaluate(Document document) throws XPathExpressionException {
		/* N.B.: this requires the input document to have been already normalized,
		 *       i.e., contiguous text siblings should have been already merged
		 */
		final NodeList nodeList = (NodeList) this.xpath.evaluate(document, NODESET);
		return nodeList;
//was:	return nodeList.item(0); // return *only* the first node
	}
	
	/*
	 * This method is useful to serialize/deserialize an ExtractionRule 
	 * which was serialized avoiding to write the compiled
	 * {@link XPathExpression}
	 */
	private Object readResolve() throws ObjectStreamException {
		compileXPath(getXPath());
		return this;
	}
	
	@Override
	public int hashCode() {
		return this.getXPath().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o==null || !(o instanceof ExtractionRule)) return false;
		
		final ExtractionRule that = (ExtractionRule)o;
		return this.getXPath().equals(that.getXPath());
	}
	
	@Override
	public String getWeirId() {
		final int siteId = this.getWebsite().getIndex();
		return super.getWeirId()+"<sup>" + siteId + "</sup>";
	}

	@Override
	public String toString() { 
		return this.getXPath(); 
	}
	
}
