package it.uniroma3.weir.model.log;

import static it.uniroma3.weir.model.log.TableOfValuesRenderer.MAX_NUMBER_VALUES;
import static it.uniroma3.weir.model.log.WeirCSSclasses.*;
import static it.uniroma3.weir.model.log.WeirStyles.header;
import static it.uniroma3.weir.model.log.WeirStyles.linkToPage;
import it.uniroma3.hlog.render.IterableRenderer;
import it.uniroma3.hlog.render.TableBuilder;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.hiddenrelation.AbstractInstance;

import java.util.*;

public class SetOfAbstractInstanceRenderer implements IterableRenderer<AbstractInstance, Set<AbstractInstance>> {

	static final private double SITE_ID_COL = 2.2d;
	static final private double VALUE_COLS  = 1.16d;

	@SuppressWarnings("unchecked")
	@Override
	public Class<Set<AbstractInstance>> getRenderedObjectClass() {
		return (Class<Set<AbstractInstance>>) Set.class.asSubclass(Set.class);
	}

	@Override
	public Class<AbstractInstance> getRenderedElementClass() {
		return AbstractInstance.class;
	}

	@Override
	public String toHTMLstring(Set<AbstractInstance> instances) {
		if (instances.isEmpty()) return "none";
		
		final SortedSet<AbstractInstance> ordered = new TreeSet<>(AbstractInstance.COMPARATOR_BY_SIZE);
		ordered.addAll(instances);
		final List<Website> sites = Experiment.getInstance().getDomain().getSites();
		final TableBuilder builder = new TableBuilder().setFixedLayout();		
		builder.table();
		builder.addColumn(SITE_ID_COL);
		builder.addNColumns(MAX_NUMBER_VALUES, VALUE_COLS);
		for(Website site : sites) {
			builder.tr();
			builder.th(header(site));
			int counter = 0;
			for(AbstractInstance ai : ordered) {
				final Webpage page = ai.from(site);
				builder.td(VALUE_CSS_CLASS,linkToPage(page));
				counter++;
				if (counter>MAX_NUMBER_VALUES) break;
			}
			if (counter!=instances.size())
				builder.td("&hellip;");
			builder._tr();
		}
		builder._table();
		return builder.toString();
	}

}
