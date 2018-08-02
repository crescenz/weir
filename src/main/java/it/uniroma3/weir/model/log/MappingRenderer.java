package it.uniroma3.weir.model.log;

import it.uniroma3.hlog.render.ObjectRenderer;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.model.hiddenrelation.AbstractInstance;
import it.uniroma3.weir.model.hiddenrelation.AbstractRelation;

import java.util.Set;

public class MappingRenderer implements ObjectRenderer<Mapping> {

	static final private String MAPPING_HTML_TEMPLATE(boolean isGolden, String id, String atts) {
		final String goldenCls = ( isGolden ? " class=\"goldenMapping\"" : "" ) ; 
		return "<TABLE"+goldenCls+">"
				+ "<TR><TD class=\"mappingId\"><SPAN>"+id+"</SPAN></TD>"
				+     "<TD>"+atts+"</TD></TR>"
			 + "</TABLE>";
	}
	
	// an AttributeListRenderer to be used when pages are not aligned
	static final private AttributeListRenderer ualr = new AttributeListRenderer();

	@Override
	public Class<Mapping> getRenderedObjectClass() {
		return Mapping.class;
	}
	
	@Override
	public String toHTMLstring(Mapping m) {
		/* render *aligned* vectors */
		AttributeListRenderer alr = null; 
		if (areAlignedPages(m)) {
			/* domain linkage, i.e., abstract relation are available  */
			/* print by using aligned pages, i.e., abstract instances */
			alr = new AttributeListRenderer(getAbstractInstances(m));
		} else {
			/* print without aligning pages */
			alr = ualr;
		}
		return MAPPING_HTML_TEMPLATE(m.isGolden(), header(m), alr.toHTMLstring(m));
	}
	
	private boolean areAlignedPages(Mapping m) {
		// pages can be aligned iff abstract instances are already available
		final AbstractRelation _H_ = Experiment.getInstance().getAbstractRelation();
		return _H_!=null && !_H_.getAbstractInstances().isEmpty();
	}
	
	private Set<AbstractInstance> getAbstractInstances(Mapping m) {
		final AbstractRelation _H_ = Experiment.getInstance().getAbstractRelation();
		return _H_.getAbstractInstances();
	}
	
	private String header(Mapping m) {
		final String mapId = "Mapping " + m.getId();
		return ( m.isComplete() ? mapId : "<i>"+mapId+"</i>" );
	}

}
