package it.uniroma3.weir.model.log;


import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import static it.uniroma3.hlog.HypertextualUtils.styled;
import static it.uniroma3.weir.configuration.Constants.GOOD_MATCH_THRESHOLD;
import static it.uniroma3.weir.configuration.Constants.PERFECT_MATCH_THRESHOLD;
import static it.uniroma3.weir.model.log.WeirCSSclasses.*;
import it.uniroma3.hlog.render.ObjectRenderer;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Webpage;

import java.util.Objects;


/**
 * Centralize project-wide methods and constants
 * to facilitate the {@link ObjectRenderer}s to
 * adopt a uniform aspect.
 */
public class WeirStyles {

	static final public String header(Object o) {
		return styled(TABLE_HEADER_CSS_CLASS, o.toString()).toString();
	}
	
	static final public String leftId(final Webpage p) {
		return p.getId()+"<sub>"+p.getWebsite().getIndex()+"</sub>";
	}

	static final public String rightId(final Webpage p) {
		return "<sub>"+p.getWebsite().getIndex()+"</sub>"+p.getId();
	}
	
	static final public String nullValue() {
		return nullStyled("null");
	}

	static final public String nullStyled(Object o) {
		return "<span class=\"nullValue\">"+o.toString()+"</span>";
	}
	
	static final public String noOverlap() {
		return nullStyled("no overlap");
	}
	
	static final public String linkToPage(Webpage page) {
		return ( page!=null ? linkTo(page.getURI()).withAnchor(page.getWeirId()).toString() : nullValue() );
	}
	
	static final public String getCSSclass(Attribute a) {
		return getCSSclass(a, null);
	}

	static final public String getCSSclass(Attribute a, String cssClass) {
		String result = ATTRIBUTE_ID_CLASS + Objects.toString(cssClass, "");
		
		if (a.isTarget()) {

			final double d = a.getGoldenMatching().distance();

			if (d<=WeirConfig.getDouble(PERFECT_MATCH_THRESHOLD))
				result += " "+TARGET_ATTRIBUTE_ID_CLASS ;
			else if (d<=WeirConfig.getDouble(GOOD_MATCH_THRESHOLD))		
				result += " "+GOOD_ATTRIBUTE_ID_CLASS ;
		}
		
		return result.trim();
	}

}
