package it.uniroma3.weir.model.log;

import static it.uniroma3.hlog.HypertextualUtils.lazyPopup;
import static it.uniroma3.weir.model.log.WeirCSSclasses.*;
import it.uniroma3.hlog.render.IterableRenderer;
import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.linking.linkage.PageLinkageIterator;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.vector.value.Value;

import java.util.LinkedList;
import java.util.Stack;
/**
 * A renderer that pretty print an HTML table presenting
 * a list of {@link Match}es
 */
public class MatchListRenderer
			 extends MatchRendererSupport<Match,Iterable<Match>>
             implements IterableRenderer<Match, Iterable<Match>> {	
	
	@Override
	public Class<Match> getRenderedElementClass() {
		return Match.class;
	}
	
	@Override
	protected void renderHeaderRow(Match first) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected void addTableColumns(Match first) {
		this.builder.addNColumns(getNvalues(), VALUE_COL_WIDTH);
		addCentralColumns(first);
		this.builder.addNColumns(getNvalues(), VALUE_COL_WIDTH);
	}

	@Override
	public void renderDataRow(final Match match) {
		/* specialized iterator over pair of pages */
		final PageLinkageIterator  pairIt = match.iterator();
		final Stack<Value> s1 = new Stack<>();
		final LinkedList<Value> s2 = new LinkedList<>();
		int counter = 0;
		
		while (pairIt.hasNext() && counter++<getNvalues()) {
			pairIt.next();
			s1.push(pairIt.getMin());
			s2.add(pairIt.getMax());
		}
		
		/* counter == number of overlapping values... */
		
		/* values on the  left */
		for(int i=0; i<getNvalues() && !s1.isEmpty(); i++)
			this.dataValue(s1.pop());

		addCentralData(match);
		
		/* values on the right */
		for(int i=0; i<getNvalues() && !s2.isEmpty(); i++)
			this.dataValue(s2.pop());
	}

	protected void addCentralColumns(Match first) {
		this.builder.addColumn(ID_COL_WIDTH);
		this.builder.addColumn(DIST_COL_WIDTH);
		this.builder.addColumn(ID_COL_WIDTH);
	}

	protected void addCentralData(final Match match) {
		final Attribute a1 = match.getMin();
		final Attribute a2 = match.getMax();
		data(LEFT_ID_CSS_CLASS,  a1.toString());
		data(DISTANCE_CSS_CLASS, lazyPopup(_10000(match.distance()),match));
		data(RIGHT_ID_CSS_CLASS, a2.toString());
	}

}
