package it.uniroma3.weir.model.log;


import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import static it.uniroma3.hlog.HypertextualUtils.truncateEscapeAndTooltip;
import static it.uniroma3.weir.model.log.WeirCSSclasses.VALUE_CSS_CLASS;
import static it.uniroma3.weir.model.log.WeirStyles.nullValue;

import java.net.URI;
import java.util.Objects;

import it.uniroma3.hlog.HypertextualUtils.Link;
import it.uniroma3.hlog.render.IterableRenderer;
import it.uniroma3.hlog.render.Renderer;
import it.uniroma3.hlog.render.TableBuilder;
import it.uniroma3.hlog.render.TableRenderer;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.value.ExtractedValue;
import it.uniroma3.weir.vector.value.Value;

/**
 * Base class for renderers that pretty print HTML tables 
 * of {@link Value}s, their corresponding {@link ExtractedValue}s.
 * 
 * This classes provide specialized {@link #dataValue(...)}s methods.
 * 
 */
abstract class TableOfValuesRenderer<E, IE extends Iterable<E>> 
         extends TableRenderer<E, IE> 
	     implements IterableRenderer<E, IE>, Renderer<IE> {

	static final protected int MAX_VALUE_LENGTH  = 16;  // Max characters per value
	
	static final protected int MAX_NUMBER_VALUES = 12;  // Max values per row

	@Override
	abstract public TableBuilder createTableBuilder();
	
	/**
	 * We are assuming all rows of values have the same length
	 * and the values withing each row are properly aligned by
	 * means of this method
	 * 
	 * @param an iterable over the values of each row
	 * @return 
	 */
	protected Iterable<Value> enumerateColumnValues(Iterable<Value> iterable) {
		return iterable;
	}

	protected void dataValue(Value v) {
		this.dataValue(VALUE_CSS_CLASS, v);
	}

	protected void dataValue(String styOrcls, Value v) {
		this.data(styOrcls, valueTooltip(v), nullValue());
	}

	protected void dataValue(String styOrcls, Value value, Vector vector) {
		this.dataValue(styOrcls, value, vector.getExtractedValue(value));
	}
	
	protected void dataValue(String styOrcls, Value v, ExtractedValue ev) {
		this.data(styOrcls, valueTooltip(v, ev), nullValue());
	}
	
	protected void linkedDataValue(Value v, ExtractedValue ev) {
		Link link = null;
		if (v!=null) {
			/* the values are presented as link to aligned pages */
			final URI uri = v.getPage().getURI();
			link = linkTo(uri).withAnchor(valueTooltip(v,  ev));
		}	
		this.data(VALUE_CSS_CLASS, link, nullValue());
	}

	static final protected String valueTooltip(Value v) {
		return valueTooltip(Objects.toString(v, null), Objects.toString(v, null));
	}
	static final protected String valueTooltip(Value v, ExtractedValue ev) {
		return valueTooltip(Objects.toString(v, null), Objects.toString(ev, null));
	}
	static final protected String valueTooltip(String value, String extracted) {
		/* render the value as extracted from the page, and
		 * show the normalized value when the mouse is hover it
		 */
		return truncateEscapeAndTooltip(extracted, MAX_VALUE_LENGTH, value);
	}
	
}
