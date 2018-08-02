package it.uniroma3.weir.model.log;

import static java.util.Collections.singletonList;
import it.uniroma3.hlog.render.ObjectRenderer;
import it.uniroma3.weir.vector.Vector;

public class VectorRenderer implements ObjectRenderer<Vector> {

	static final private VectorListRenderer clr = (VectorListRenderer) 
			new VectorListRenderer().enableIdColumn().enableRuleColumn().skipHeaderRow();
	
	static final public String verbose(Vector v) {
		return new VectorListRenderer().enableRuleColumn().toHTMLstring(singletonList(v));
	}
	
	private final VectorListRenderer vlr;
	
	public VectorRenderer(VectorListRenderer vlr) {
		this.vlr = vlr;
	}
	
	public VectorRenderer() {
		this(clr);
	}
	
	@Override
	public String toHTMLstring(Vector v) {
		return vlr.toHTMLstring(singletonList(v));
	}

	@Override
	public Class<Vector> getRenderedObjectClass() {
		return Vector.class;
	}

}
