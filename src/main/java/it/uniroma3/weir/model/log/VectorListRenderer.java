package it.uniroma3.weir.model.log;

import static it.uniroma3.weir.model.log.WeirStyles.nullValue;
import static it.uniroma3.hlog.HypertextualUtils.*;
import static it.uniroma3.weir.model.log.WeirCSSclasses.*;
import it.uniroma3.hlog.HypertextualUtils;
import it.uniroma3.hlog.render.IterableRenderer;
import it.uniroma3.hlog.render.TableBuilder;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.Label;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.value.Value;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * This {@link IterableRenderer} to render {@link Vector}s
 * of the same size that it assumes already properly aligned 
 * by printing all {@link Value}s as returned by {@link Vector#iterator()}.
 */

public class VectorListRenderer
	   extends TableOfValuesRenderer<Vector, Iterable<Vector>>
	   implements IterableRenderer<Vector, Iterable<Vector>> {
	
	static final protected int MAX_VALUE_LENGTH  = 10;

	static final protected int BASE_COLUMN_WIDTH = 64; // the base width of one table column in px

	static final protected double ID_COL      = 0.7;  // ids...
	static final protected double TYPE_COL    = 1.3;  // ... type ...
	static final protected double LABEL_COL   = 1.4;  // ... labels ...
	static final protected double VALUES_COLS = 1.2;  // ... values ...
	static final protected double RULE_COL    = 0.3;  // ... extraction rule ...
	
	static final protected int MAX_LABELS  	    =  3;
	static final protected int MAX_LABEL_LENGTH = 16;
	
	protected boolean idColumnEnabled    = false;
	
	protected boolean labelColumnEnabled = false;
	
	protected boolean ruleColumnEnabled = false;
	
	protected boolean typeColumnEnabled = false;
	
	protected boolean linkedValueEnabled = false;
	
	protected boolean pushNonNullEnabled = false;

	// list of pages over which the vector's values should be rendered
	// null means shown all values
	protected List<Webpage> pages; 
	
	public VectorListRenderer(List<Webpage> pages) {
		this();
		this.pages = pages;		
	}
	
	public VectorListRenderer() {
		this.pages = null;
		this.idColumnEnabled    = false;
		this.labelColumnEnabled = false;
		this.ruleColumnEnabled  = false;
		this.typeColumnEnabled  = false;
		this.linkedValueEnabled = false;
		this.pushNonNullEnabled = true;
	}

	public VectorListRenderer enableIdColumn() {
		this.idColumnEnabled = true;
		return this;
	}

	public VectorListRenderer enableLabelColumn() {
		this.labelColumnEnabled = true;
		return this;
	}
	
	public VectorListRenderer enableRuleColumn() {
		this.ruleColumnEnabled = true;
		return this;
	}
	
	public VectorListRenderer enableTypeColumn() {
		this.typeColumnEnabled = true;
		return this;
	}

	public VectorListRenderer enablePushNonnull() {
		this.pushNonNullEnabled = true;
		return this;
	}
	
	final public VectorListRenderer enableLinkedValues() {
		this.linkedValueEnabled = true;
		return this;
	}
	
	final public boolean isIdColumnEnabled() {
		return this.idColumnEnabled;
	}
	
	final public boolean isLabelColumnEnabled() {
		return this.labelColumnEnabled;
	}
	
	final public boolean isRuleColumnEnabled() {
		return this.ruleColumnEnabled;
	}
	
	final public boolean isTypeColumnEnabled() {
		return this.typeColumnEnabled;
	}
	
	final public boolean isLinkedValueEnabled() {
		return this.linkedValueEnabled;
	}
	
	final public boolean isNonnullPushingEnabled() {
		return this.pushNonNullEnabled;
	}

	@Override
	public Class<Vector> getRenderedElementClass() {
		return Vector.class;
	}
	
	@Override
	public TableBuilder createTableBuilder() {
		this.builder = new TableBuilder(BASE_COLUMN_WIDTH).setFixedLayout(); 
		return this.builder;
	}

	@Override
	protected void addTableColumns(Vector extracted) {
		final int n = numberOfColumns(extracted);
		if (isIdColumnEnabled())    this.builder.addColumn(ID_COL);
		if (isRuleColumnEnabled())	this.builder.addColumn(RULE_COL);
		if (isLabelColumnEnabled())	this.builder.addColumn(LABEL_COL);
		if (isTypeColumnEnabled())  this.builder.addColumn(TYPE_COL);
		this.builder.addNColumns(n,VALUES_COLS);  // a column for each value 
	}

	@Override
	public void renderHeaderRow(Vector first) {
		if (isIdColumnEnabled())    header("id");
		if (isRuleColumnEnabled())  header("R.");
		if (isLabelColumnEnabled()) header(LABEL_HEADER_CSS_CLASS, "Labels");
		if (isTypeColumnEnabled())  header("Type");
		int counter = 0;
		for (Value v : enumerateColumnValues(first)) {
			// e.g., nullValue() for aligned abstract instances
			header(PAGE_ID_HEADER_CSS_CLASS, v!=null ? linkToPageOf(v) : nullValue() );
			counter++;
		}
		if (counter!=first.size())
			data("&hellip;");
	}

	@Override
	protected Iterable<Value> enumerateColumnValues(Iterable<Value> iterable) {
		final Vector vector = (Vector)iterable;
		Iterable<Value> result = valuesIndexedByPages(vector);
		if (result==null)
			/* no page to index the vector (e.g., it's a golden vector) */
			result = atMostN(Arrays.asList(vector.getElements()), MAX_NUMBER_VALUES);
		return result;
	}

	protected String linkToPageOf(Value v) {
		final String id = v.getPage().getId();
		final URI uri = v.getPage().getURI();
		final String anchor = truncateEscapeAndTooltip(id, MAX_VALUE_LENGTH);
		return linkTo(uri).withAnchor(anchor).toString();
	}
	
	private Iterable<Value> valuesIndexedByPages(final Vector vector) {
		final List<Webpage> pages = selectPagesToShow(vector);
		if (pages==null) return null;
		final List<Value> values = new ArrayList<>(pages.size());
		for(Webpage page : pages) {
			values.add(vector.get(page));
		}
		return values;
	}

	/**
	 * This methods implements the policy to choose the pages to show.
	 * @param vector
	 * @return
	 */
	private List<Webpage> selectPagesToShow(final Vector vector) {
		/* use the specified pages, if any */
		List<Webpage> result = null;
		if (this.pages!=null) result = this.pages;
		else {
			final Website website = vector.getWebsite();
			/* otherwise, use the overlapping pages, if already set */
			if (website.getOverlappingPages()!=null)
				result = website.getOverlappingPages();
			else if (isNonnullPushingEnabled())
				result = pagesOfValues(vector); // TODO finalize logics
			/* otherwise, use all the available pages, if loaded */
			else if (website.getWebpages()!=null)
				result = website.getWebpages();
			/* otherwise get pages from values */
			else return null;
		}
		return atMostN(result, MAX_NUMBER_VALUES);
	}

	private List<Webpage> pagesOfValues(Vector vector) {
		final List<Webpage> result = new ArrayList<>();
		final List<Webpage> otherNulls = new ArrayList<>();
		for(Value v : vector.getElements()) {
			if (!v.isNull()) result.add(v.getPage());
			else otherNulls.add(v.getPage());
		}
		result.addAll(otherNulls);
		return result;
	}

	static protected <T> List<T> atMostN(List<T> result, int n) {
		result = ( result.size() > n ? result.subList(0, n) : result );
		return result;
	}

	protected int numberOfColumns(Vector extracted) {
		return extracted==null ? 0 : selectPagesToShow(extracted).size();
	}
	
	@Override
	public void renderDataRow(Vector vector) {
		if (vector==null) return;
		if (isIdColumnEnabled())    idCell(vector.getWeirId());
		if (isRuleColumnEnabled())  ruleCell(vector.getExtractionRule());
		if (isLabelColumnEnabled()) labelsCell(vector.getLabels());
		if (isTypeColumnEnabled())  typeCell(vector.getType());

		int counter = 0;

		for (Value value : enumerateColumnValues(vector)) {
			valueCell(vector, value);
			counter++;
		}
		
		if (counter!=vector.size())
			data("&hellip;");
	}
	
	protected void idCell(String weirid) {
		data(ID_CSS_CLASS, weirid);
	}

	protected void valueCell(Vector vector, Value value) {
		if (isLinkedValueEnabled())
			linkedValueCell(vector,value);
		else 
			unlinkedValueCell(vector,value);
	}

	protected void linkedValueCell(Vector vector, Value value) {
		if (value!=null)
			linkedDataValue(value,vector.getExtractedValue(value));
		else dataValue(value);
	}

	protected void unlinkedValueCell(Vector vector, Value value) {
		dataValue(value);		
	}

	protected void typeCell(Type type) {
		data(TYPE_CSS_CLASS, type.toString());
	}
	protected void ruleCell(ExtractionRule rule) {
		final String xpath = ( rule!=null ? rule.getXPath() : null );
		data( xpath!=null ? HypertextualUtils.tooltip("&#10148;", xpath) : "" );
	}
	protected void labelsCell(Set<Label> labels) {
		final String label = firstN(labels,1);
		data(LABEL_CSS_CLASS, ( labels.size()<=1 ? label : HypertextualUtils.tooltip(label,firstLabels(labels)) ) );
	}

	protected String firstLabels(Set<Label> labels) {
		return firstN(labels, MAX_LABELS, MAX_LABEL_LENGTH);
	}

}
