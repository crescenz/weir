package it.uniroma3.weir.model.log;

import static it.uniroma3.hlog.HypertextualUtils.tooltip;
import static it.uniroma3.weir.model.log.WeirCSSclasses.TEMPLATE_TOKEN_CSS_CLASS;
import static it.uniroma3.weir.model.log.WeirCSSclasses.TEMPLATE_TOKEN_ROLE_CSS_CLASS;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import it.uniroma3.hlog.render.IterableRenderer;
import it.uniroma3.hlog.render.TableBuilder;
import it.uniroma3.hlog.render.TableRenderer;
import it.uniroma3.token.dom.DOMToken;
import it.uniroma3.token.dom.Token;
import it.uniroma3.weir.extraction.rule.RelativeRule;

/**
 * This {@link IterableRenderer} renders collections of {@link DOMToken}s, 
 * i.e.the template tokens used to infer {@link RelativeRule}s.
 */
public class TemplateTokenListRenderer
	   extends TableRenderer<DOMToken, Iterable<DOMToken>>
	   implements IterableRenderer<DOMToken, Iterable<DOMToken>> {

	static final private int BASE_COLUMN_WIDTH = 64; // the base width of one table column in px

	static final private double DEPTH_COL = 0.4;  // Depth ...
	static final private double TOKEN_COL = 8;    // Token ...
	static final private double ANNOS_COL = 0.25; // ... XPath
	
	@Override
	public Class<Token> getRenderedElementClass() {
		return Token.class; // to be used with all Token -> DOMToken hierarchy
	}
	
	@Override
	public TableBuilder createTableBuilder() {
		this.builder = new TableBuilder(BASE_COLUMN_WIDTH).setFixedLayout(); 
		return this.builder;
	}

	@Override
	protected void addTableColumns(DOMToken first) {
		this.builder.addColumn(DEPTH_COL);
		this.builder.addColumn(ANNOS_COL);
		this.builder.addColumn(TOKEN_COL);
	}

	@Override
	public void renderHeaderRow(DOMToken first) {
		header("Depth");
		header("");
		header("Token");
	}

	@Override
	public void renderDataRow(DOMToken token) {
		templateTokenDepth(token);
		templateTokenAnnoCell(token);
		templateTokenCell(token);
	}

	private void templateTokenDepth(DOMToken token) {
		data(Integer.toString(token.depth()));
	}
	
	private void templateTokenCell(DOMToken token) {
    	final StringBuilder result = new StringBuilder();
    	if (token.isText()) result.append(token.getValue());
    	else if (token.isTag()) result.append("<" + token.getTag() + ">");  
		data(TEMPLATE_TOKEN_CSS_CLASS, escapeHtml4(result.toString()));
	}

	private void templateTokenAnnoCell(DOMToken token) {
		final String annos = token.getAllAnnotations().toString();
		data(TEMPLATE_TOKEN_ROLE_CSS_CLASS, tooltipOver(annos));
	}

	final private String tooltipOver(String annos) {
		 return ( annos!=null ? tooltip("&otimes;", escapeHtml4(annos)) : "" );
	}

}

