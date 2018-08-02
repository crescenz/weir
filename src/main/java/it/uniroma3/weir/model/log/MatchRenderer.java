package it.uniroma3.weir.model.log;

import static it.uniroma3.hlog.HypertextualUtils.*;
import static it.uniroma3.weir.model.log.WeirCSSclasses.*;
import static it.uniroma3.weir.model.log.WeirStyles.*;
import it.uniroma3.hlog.HypertextualUtils.Link;
import it.uniroma3.hlog.render.IterableRenderer;
import it.uniroma3.hlog.render.TableBuilder;
import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.linking.linkage.PageLinkageIterator;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.structures.Pair;
import it.uniroma3.weir.vector.DistanceFunction;
import it.uniroma3.weir.vector.NumericVector;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.type.NumberType;
import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.value.Value;
/**
 * A renderer that pretty print an HTML table presenting
 * the aligned values of a {@link Match}, 
 * i.e.,two {@link Attribute}s from {@link Webpage}s aligned 
 * by means of a list of {@link PageLinkage}s
 */
// TODO uptype to support pair-of-vector-distances rendering
public class MatchRenderer 
             extends MatchRendererSupport<Pair<Value>, Match>
             implements IterableRenderer<Pair<Value>, Match> {
	
	static final protected int BASE_COLUMN_WIDTH = 120;  // the base width of one table column in px

	// distance between two values
	static final protected double VALUES_DIST_COL_WIDTH = 0.5;  

	private Match match;
	
	private Type type;	
	
	private PageLinkageIterator pairIt;
	
	private Attribute left;
	private Attribute right;

	/* just for numeric vectors */
	private DistanceFunction distanceFunction;
	
	@Override
	public Class<Match> getRenderedObjectClass() {
		return Match.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Pair<Value>> getRenderedElementClass() {
		return (Class<? extends Pair<Value>>) Pair.class.asSubclass(Pair.class);
	}

	@Override
	public TableBuilder createTableBuilder() {
		super.createTableBuilder();		
		this.builder.setBaseWidth(BASE_COLUMN_WIDTH);
		return this.builder;
	}
	
	@Override
	protected void addTableColumns(Pair<Value> first) {
		this.builder.addColumn(VALUES_DIST_COL_WIDTH);
		/* */
		this.builder.addColumn(VALUE_COL_WIDTH);
		this.builder.addColumn(ID_COL_WIDTH);
		this.builder.addColumn(SIM_COL_WIDTH);
		this.builder.addColumn(ID_COL_WIDTH);
		this.builder.addColumn(VALUE_COL_WIDTH);
	}

	@Override
	public void renderRows(Iterable<Pair<Value>> iterable) {
		this.pairIt = init(iterable);
		/* header ... */
		this.builder.tr();
		this.renderHeaderRow(first(iterable));
		this.builder._tr();
		/* ... body   */
		
		/* no overlap at all */
		if (!this.pairIt.hasNext()) {
			this.builder.tr();
			data(noOverlap());
			this.builder._tr();
		}
		/* overlap available */
		while (this.pairIt.hasNext()) {
			this.builder.tr();
			this.renderDataRow(this.pairIt.next());
			this.builder._tr();
		}
	}

	private PageLinkageIterator init(Iterable<Pair<Value>> iterable) {
		this.match = (Match) iterable;
		final PageLinkageIterator pairIt = match.iterator();
		
		// Match's min attribute on the left  hand side
		this.left  = this.match.getMin();
		// Match's max attribute on the right hand side
		this.right = this.match.getMax();

		this.type = Type.getCommonAncestor(
				left.getVector().getType(), 
				right.getVector().getType()
		);
		this.distanceFunction = this.type.createDistanceFunction();
		this.distanceFunction.initFor(pairIt);
		return pairIt;
	}
	
	@Override
	public void renderHeaderRow(Pair<Value> pl) {
		header(DISTANCE_CSS_CLASS, "d");
		headerAttribute(getLeftIdCSSclass(left), left);
		header("page");
		header("weight");
		header("page");
		headerAttribute(getRightIdCSSclass(right), right);
	}

	private void headerAttribute(final String cssClass, final Attribute a) {
		final String tooltip = a.getVector().getType().toString();
		header(cssClass, nativeTooltip(a.toString(),tooltip));
	}

	private String getLeftIdCSSclass(Attribute left) {
		return getCSSclass(left, LEFT_ID_CSS_CLASS);
	}

	private String getRightIdCSSclass(Attribute right) {
		return getCSSclass(right, RIGHT_ID_CSS_CLASS);
	}
	
	@Override
	public void renderDataRow(Pair<Value> pair) {
		final PageLinkage pl = this.pairIt.getCurrentPageLinkage();
		final Webpage leftPage  = pl.from(this.left.getWebsite());
		final Webpage rightPage = pl.from(this.right.getWebsite());
		final Value vL = this.left.getVector().get(leftPage); 
		final Value vR = this.right.getVector().get(rightPage);
		
		final Link leftLink  = linkTo(leftPage.getURI()).withAnchor(leftId(leftPage));
		final Link rightLink = linkTo(rightPage.getURI()).withAnchor(rightId(rightPage));

		// a table row of two cells per pair of values
		dataDistance(vL, vR);
		dataValue(LEFT_VALUE_CSS_CLASS,  vL, this.left.getVector());
		data(LEFT_PAGE_CSS_CLASS,  leftLink.toString());
		data(SIMILARITY_CSS_CLASS, percentage(pl.getSimilarity()));
		data(RIGHT_PAGE_CSS_CLASS, rightLink.toString());
		dataValue(RIGHT_VALUE_CSS_CLASS, vR, this.right.getVector());
	}

	private void dataDistance(Value vL, Value vR) {
		if (vL==null || vR==null) {
			data(VALUE_DISTANCE_CSS_CLASS, null, null /*nullValue()*/);
			return;
		}
			
		final double distance = this.distanceFunction.distance(vL,vR);
		if (isNumeric(this.type)) {
			final double weight = this.pairIt.getWeight();
			final String tooltip = 
					"d = " + _1000(weight) + "*"
			           + "[" 
							+ fraction(vL, this.left.getVector()) + "-" + fraction(vR, this.right.getVector()) 
					   + "]";
			data(VALUE_DISTANCE_CSS_CLASS, nativeTooltip(_1000(distance), tooltip));
		} else {
			data(VALUE_DISTANCE_CSS_CLASS, _1000(distance));
		}
	}

	private String fraction(final Value value, final Vector vector) {
		if (value.isNull()) return null;

		final NumericVector vect = (NumericVector)vector;
		final double   v = value.getNumericValue();
		final double avg = vect.getAvg();
		final double std = vect.getStd();
		return "(" + _1000(v) + "-" + _1000(avg)+")" + "/" + _1000(std) ;
	}

	private boolean isNumeric(Type type) {
//		return type.isSubtypeOf(Type.NumberType);
		return type instanceof NumberType;
	}
	
}
