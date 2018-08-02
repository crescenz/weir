package it.uniroma3.weir.model.log;

import static it.uniroma3.weir.model.log.WeirCSSclasses.*;
import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import static it.uniroma3.weir.Formats.*;
import it.uniroma3.hlog.render.Renderer;
import it.uniroma3.hlog.render.TableRenderer;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import static it.uniroma3.weir.model.log.WeirStyles.*;

public class WebsiteLinkageRenderer 
	         extends TableRenderer<PageLinkage, WebsiteLinkage>
	         implements Renderer<WebsiteLinkage> {

	static final private double ID_COL     = 1;   // ...
	static final private double INDEX_COL  = 0.8; // ...
	static final private double SIM_COL    = 1;   // ...
	static final private double USAGE_COL  = 0.5; // ...

	private Website left;
	private Website right;
	
	@Override
	protected void addTableColumns(PageLinkage first) {
		this.builder.addColumn(INDEX_COL,ID_COL,ID_COL,INDEX_COL,USAGE_COL,SIM_COL);
	}

	@Override
	protected void renderHeaderRow(PageLinkage first) {
		headers("Index","ID","sim","ID","Index");
		header("headerUsage", "#Use");
		this.left = first.sites().getMin();
		this.right = first.sites().getMax();
	}

	@Override
	protected void renderDataRow(PageLinkage pl) {
		final Webpage l = pl.from(this.left);
		final Webpage r = pl.from(this.right);
		data(INDEX_CSS_CLASS,Integer.toString(l.getOverlapIndex()));
		data(LEFT_ID_CSS_CLASS, linkTo(l.getURI()).withAnchor(leftId(l)).toString());
		data(SIMILARITY_CSS_CLASS, percentage.format(pl.getSimilarity()));
		data(RIGHT_ID_CSS_CLASS, linkTo(r.getURI()).withAnchor(rightId(r)).toString());
		data(INDEX_CSS_CLASS,Integer.toString(r.getOverlapIndex()));
		data(LINKAGE_USAGE_CSS_CLASS,Integer.toString(pl.getUsage()));
	}

	@Override
	public Class<PageLinkage> getRenderedElementClass() {
		return PageLinkage.class;
	}
	
	@Override
	public Class<WebsiteLinkage> getRenderedObjectClass() {
		return WebsiteLinkage.class;
	}
	
}
