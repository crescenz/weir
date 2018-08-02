package it.uniroma3.weir.model.log;

import java.util.Set;

import it.uniroma3.hlog.render.CombinedTableRenderer;
import it.uniroma3.hlog.render.IterableRenderer;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.hiddenrelation.AbstractInstance;
import it.uniroma3.weir.vector.Vector;

public class AttributeListRenderer
             extends CombinedTableRenderer<Attribute, Iterable<Attribute>,
                                           Vector, Iterable<Vector>>
             implements IterableRenderer<Attribute, Iterable<Attribute>> {

	static final private VectorListRenderer vlr = 
			new VectorListRenderer().enableTypeColumn().enableRuleColumn();
	
	public AttributeListRenderer() {
		super(new AttributeIdListRenderer(), vlr);
	}
	
	public AttributeListRenderer(Set<AbstractInstance> ai) {
		super(new AttributeIdListRenderer(), avlr(ai));		
	}

	static private VectorListRenderer avlr(Set<AbstractInstance> ai) {
		return new AlignedVectorListRenderer(ai).enableTypeColumn().enableLinkedValues();
	}
	

	@Override
	public Vector getSubElement(Attribute attribute) {
		return ( attribute==null ? null : attribute.getVector() );
	}

	@Override
	public Class<Attribute> getRenderedElementClass() {
		return Attribute.class;
	}
	
}
