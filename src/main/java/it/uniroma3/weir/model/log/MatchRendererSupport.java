package it.uniroma3.weir.model.log;


import static it.uniroma3.hlog.HypertextualUtils.format;
import static it.uniroma3.weir.Formats.thousandth;
import static it.uniroma3.weir.Formats.ten_thousandth;
import it.uniroma3.hlog.render.IterableRenderer;
import it.uniroma3.hlog.render.Renderer;
import it.uniroma3.hlog.render.TableBuilder;
import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Webpage;
/**
 * Base class for renderers that pretty print HTML tables to present
 * the aligned values of a {@link Match}, i.e., two {@link Attribute}s
 * from a collection of {@link Webpage}s aligned by means of a set of
 * {@link PageLinkage}.
 */
abstract class MatchRendererSupport<E, IE extends Iterable<E>> 
         extends TableOfValuesRenderer<E, IE> 
	     implements IterableRenderer<E, IE>, Renderer<IE> {
		
	static final protected int BASE_COLUMN_WIDTH = 64;  // the base width of one table column in px

	static final protected int N_VALUES = 3;            // Max Number of values each attribute
		
	// mutiply the base-width for... 
	static final protected double DIST_COL_WIDTH   = 1.3;  // ...their distance
	static final protected double VALUE_COL_WIDTH  = 1.2;  // ...first three linked values
	static final protected double ID_COL_WIDTH     = 1.2;  // ...their ID
	static final protected double SEP_COL_WIDTH    = 0.3;  // ...their ID
	static final protected double SIM_COL_WIDTH    = 0.6;  // ...similarity as weight	
	
	final private int nValues;
	
	protected MatchRendererSupport() {
		this(N_VALUES);
	}	

	protected MatchRendererSupport(int n) {
		this.nValues = n;		
	}
	
	/**
	 * @return the max number of values displayed for each attribute
	 */
	public int getNvalues() {
		return this.nValues;
	}
	
	@Override
	public TableBuilder createTableBuilder() {
		this.builder = new TableBuilder()
				.setFixedLayout()
				.setBaseWidth(BASE_COLUMN_WIDTH);
		this.skipHeaderRow();
		return this.builder;
	}
	
	protected String _1000(final double d) {
		return format(thousandth, d);
	}
	
	protected String _10000(final double d) {
		return format(ten_thousandth, d);
	}

}
