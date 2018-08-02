package it.uniroma3.weir.model.log;

import it.uniroma3.ecgm.LFEQ;
import it.uniroma3.hlog.render.ObjectRenderer;

import java.util.Arrays;

// This stuff should be moved into ECGM module
public class LFEQRenderer implements ObjectRenderer<LFEQ> {

	
	static final private TemplateTokenListRenderer ttlr = new TemplateTokenListRenderer();

	@Override
	public Class<LFEQ> getRenderedObjectClass() {
		return LFEQ.class;
	}
	
	@Override
	public String toHTMLstring(LFEQ lfeq) {
		return LFEQ_HTML_TEMPLATE(lfeq);
	}
	
	static final private String LFEQ_HTML_TEMPLATE(final LFEQ lfeq) {
		return "<TABLE>"
				+ "<TR><TD><SPAN>LFEQ"+lfeq.getId()+"</SPAN></TD><TD>"+lfeq.getOccurrencesVector()+"</TD></TR>"
				+ "<TR><TD/><TD>"+ttlr.toHTMLstring(Arrays.asList(lfeq.getTokens()))+"</TD></TR></TABLE>";
	}
	

}
