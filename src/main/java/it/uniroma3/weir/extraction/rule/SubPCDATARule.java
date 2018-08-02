package it.uniroma3.weir.extraction.rule;

import static javax.xml.xpath.XPathConstants.STRING;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.value.ExtractedValue;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class SubPCDATARule extends ExtractionRule {

	static final private long serialVersionUID = 1582365421488659498L;

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	// the rule leading to the whole PCDATA and so "originating" this rule
	private ExtractionRule pcdataRule; 
	
	public SubPCDATARule(ExtractionRule rule, String xpath) {
		super(null, xpath);
		this.pcdataRule = rule;
	}

	@Override
	public Website getWebsite() {
		return getOriginatingRule().getWebsite(); 
	}	
	
	@Override
	public ExtractionRule getOriginatingRule() {
		return this.pcdataRule.getOriginatingRule();
	}

	@Override
	protected ExtractedValue extract(Webpage page, final Document document) {
		final ExtractionRule pcdataRule = this.getOriginatingRule();
		final NodeList pcdata = (NodeList) pcdataRule.applyTo(document);
		try {
			final String subpcdata = evaluate(document);
			return new ExtractedValue(page, subpcdata, pcdata);
		} catch (XPathExpressionException e) {
			log.warn(e.getMessage()+
					" during application of rule "+pcdataRule+" pcdataRule "+this);
			throw new ExtractionException(e);
		}
	}

	@Override
	protected String evaluate(Document document) throws XPathExpressionException {
		return (String) this.getXPathExpression().evaluate(document, STRING);
	}

}
