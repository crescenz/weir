package it.uniroma3.weir.extraction.wrapper.template;

import static it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates.isText;

import java.util.List;

import it.uniroma3.dom.visitor.DOMVisitor;
import it.uniroma3.dom.visitor.DOMVisitorListenerAdapter;
import it.uniroma3.token.dom.DOMToken;
import it.uniroma3.weir.model.Webpage;

import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class TemplateMarker {

	static final public String INVARIANT_MARKER = "inv_marker";	
	static final public String VARIANT_MARKER   = "var_marker";

	static final private String YES_IT_IS_A_TEMPLATE_NODE = "yes";

	private TemplateFinder finder;
	
	public TemplateMarker(TemplateFinder finder) {
		this.finder = finder;
	}
	
	public void markTokens(List<Webpage> pages) {
		/* annotate invariant vs variant nodes (by using DOM user-data) */
		
		// Annotate all texts as variants... 
		markAllTextsAsVariants(pages);
		// ...but the invariants texts.
		markTemplateInvariants();
	}

	private void markAllTextsAsVariants(List<Webpage> pages) {
		final DOMVisitor visitor = new DOMVisitor(new DOMVisitorListenerAdapter() {
			@Override
			public void text(Text text) {
				markVariantNode(text);
			}

		});
		for(Webpage page : pages)
			visitor.visit(page.getDocument());
	}

	private void markTemplateInvariants() {
		// mark every extensional occurrence of any template token
		for(DOMToken tt : this.finder.getIntensionalTemplateTokens())
			markTemplateToken(tt.getDOMNode());
		// mark every intensional occurrence of any template token
		for(DOMToken tt : this.finder.getExtensionalTemplateTokens())
			markTemplateToken(tt.getDOMNode());
	}

	private void markVariantNode(Text text) {
		final String marker = getVarMarkerValue(text);
		text.setUserData( VARIANT_MARKER, marker, null );
	}
	
	private void markTemplateToken(Node node) {
		final String marker = getInvMarkerValue(node);
		// delete the variant marker if already present, and...
		node.setUserData(  VARIANT_MARKER,   null, null );
		node.setUserData(INVARIANT_MARKER, marker, null ); // ...replace it
	}

	private String getVarMarkerValue(Text text) {
		final String trim = text.getNodeValue().trim();
		return trim.isEmpty() ? null : trim;
	}

	private String getInvMarkerValue(Node node) {
		return ( isText(node) ? node.getNodeValue().trim() : YES_IT_IS_A_TEMPLATE_NODE ) ;
	}

	// this works for extensional nodes
	static public boolean isVariantNode(Node occurrence) {
		return ( occurrence.getUserData(VARIANT_MARKER)!=null ) ;
	}

	// this works for extensional nodes
	static public boolean isTemplateNode(Node occurrence) {
		return ( occurrence.getUserData(INVARIANT_MARKER)!=null ) ;
	}
	
}
