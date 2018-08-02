package it.uniroma3.weir.extraction.wrapper.template;

import it.uniroma3.token.dom.DOMToken;
import it.uniroma3.token.dom.node.DOMNode;
import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.cache.Fingerprinted;
import it.uniroma3.weir.model.Webpage;

import java.util.List;
/**
 * It classifies each node occurrence as either a template (invariant) node
 * or non-template (variant) node, after an ExAlg-like ECGM analysis 
 * (see <a href=http://ilpubs.stanford.edu:8090/548/1/2002-40.pdf"> 
 *    Extracting Structured Data from Web Pages</a>
 * )
 * <br/>
 * It marks the template token occurrences directly on the DOMs.
 * <br/>
 * It is not coupled with the details of the ECGM analysis and
 * LFEQ representation, but it knows both {@link DOMToken} and [DOM]Node.
 */
public class TemplateFinder implements Fingerprinted {
	
	static private ECGMFacade facade;
		
	static private List<Webpage> lastAnalyzed = null;
	
	public TemplateFinder() {
	}
	
	public void findTemplateTokens(List<Webpage> samples) {
		if (samples.equals(lastAnalyzed)) return ;
		lastAnalyzed = samples;
		facade = new ECGMFacade();

		/* find template nodes */
		facade.analyze(samples);
		/* mark template/invariant nodes vs value/variant nodes */
		final TemplateMarker marker = new TemplateMarker(this);
		marker.markTokens(samples);
	}

	public List<DOMToken> getIntensionalTemplateTokens() {
		return facade.getTokensOfBinaryLFEQs();
	}
	
	public List<DOMToken> getExtensionalTemplateTokens() {
		return facade.getOccurrencesOfBinaryLFEQs();
	}
	
	
	public List<DOMToken> getTemplateTokens() {
		return facade.getTokensOfBinaryLFEQs(); // get all the intensional template tokens
	}
	
	public List<DOMNode> getOccurrences(DOMToken templateToken) {
		return facade.getOccurrences(templateToken);
	}

	@Override
	public Fingerprint getFingerprint() {
		return facade.getFingerprint();
	}

}
