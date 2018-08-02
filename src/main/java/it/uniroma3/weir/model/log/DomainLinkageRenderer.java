package it.uniroma3.weir.model.log;

import static it.uniroma3.hlog.HypertextualUtils.popup;
import static it.uniroma3.weir.model.log.WeirCSSclasses.SITE_ID_CSS_CLASS;
import static it.uniroma3.weir.model.log.WeirCSSclasses.SITE_LINKAGE_CSS_CLASS;
import it.uniroma3.hlog.render.IterableRenderer;
import it.uniroma3.hlog.render.ObjectRenderer;
import it.uniroma3.hlog.render.TableRenderer;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.Website;

public class DomainLinkageRenderer implements ObjectRenderer<DomainLinkage> {

	@Override
	public Class<DomainLinkage> getRenderedObjectClass() {
		return DomainLinkage.class;
	}

	@Override
	public String toHTMLstring(DomainLinkage dl) {
		// render as a collection of websites: one per table-row
		return new DomainLinkageSummaryRenderer().toHTMLstring(dl.getDomain());
	}

	public static class DomainLinkageSummaryRenderer
				  extends TableRenderer<Website, Iterable<Website>>
				  implements IterableRenderer<Website, Iterable<Website>> {

		static final private double OVERLAP_COL_WIDTH    = 1.2;
		static final private double SITE_ID_COLUMN_WIDTH = 2;

		@Override
		public Class<Website> getRenderedElementClass() {
			return Website.class;
		}

		@Override
		protected void addTableColumns(Website first) {
			this.builder.setFixedLayout();
			this.builder.addColumn(SITE_ID_COLUMN_WIDTH);
			this.builder.addNColumns(first.getDomain().size(), OVERLAP_COL_WIDTH);
		}

		@Override
		protected void renderHeaderRow(Website first) {
			final Domain domain = first.getDomain();
			header("");
			for(int i=0; i<domain.size(); i++)
				header(SITE_ID_CSS_CLASS,domain.getSites().get(i).toHTMLstring());
		}

		@Override
		protected void renderDataRow(Website w_i) {
			final Domain domain = w_i.getDomain();
			final DomainLinkage repo = Experiment.getInstance().getLinkages();

			/* row of other websites in linkages with this one */
			header(w_i.toHTMLstring());
			int i = repo.getIndex(w_i);
			for(int j=0; j<domain.size(); j++) {			
				if (j < i) {
					data(""); // show an upper-triangle matrix
					continue;
				}
				final Website w_j = domain.getSites().get(j);
				final WebsiteLinkage linkage = repo.get(w_i, w_j);
				if (i == j) {
					final String anchor = w_j.getWebpages().size() + " - "
									    + w_j.getOverlappingPages().size();
					data(SITE_LINKAGE_CSS_CLASS, anchor );
				}
				else {
					final String anchor = linkage.getOriginalSize() + " - " 
										+ linkage.getPageLinkages().size();
					data(SITE_LINKAGE_CSS_CLASS, ( linkage.getPageLinkages().size()>0 ? popup(anchor,linkage) : anchor ) );
				}
			}
		}
	}

}

