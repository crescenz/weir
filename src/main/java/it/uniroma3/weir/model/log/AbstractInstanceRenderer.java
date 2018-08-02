package it.uniroma3.weir.model.log;

import static it.uniroma3.weir.model.log.WeirCSSclasses.PAGE_ID_CSS_CLASS;
import static it.uniroma3.weir.model.log.WeirStyles.linkToPage;
import static it.uniroma3.weir.model.log.WeirStyles.nullValue;
import it.uniroma3.hlog.render.TableRenderer;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.hiddenrelation.AbstractInstance;

public class AbstractInstanceRenderer extends TableRenderer<Webpage, AbstractInstance> {

	static final private double ID_COL     = 1;   // ...
	
	@Override
	public Class<Webpage> getRenderedElementClass() {
		return Webpage.class;
	}

	@Override
	protected void addTableColumns(Webpage first) {
		this.builder.addColumn(ID_COL);
	}

	@Override
	protected void renderHeaderRow(Webpage first) {
		headers("id");
	}

	@Override
	protected void renderDataRow(Webpage page) {
		data(PAGE_ID_CSS_CLASS, page==null ? linkToPage(page) : null, nullValue());
	}

}
