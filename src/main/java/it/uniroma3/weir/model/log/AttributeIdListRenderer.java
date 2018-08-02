package it.uniroma3.weir.model.log;


import it.uniroma3.hlog.render.IterableRenderer;
import it.uniroma3.hlog.render.TableBuilder;
import it.uniroma3.hlog.render.TableRenderer;
import it.uniroma3.weir.model.Attribute;
import static it.uniroma3.weir.model.log.WeirCSSclasses.*;

public class AttributeIdListRenderer
             extends TableRenderer<Attribute, Iterable<Attribute>>
             implements IterableRenderer<Attribute, Iterable<Attribute>> {

	static final private double ID_COL_WIDTH = 0.80;  // id-column width = 
											          // times the base-width
	@Override
	public TableBuilder createTableBuilder() {
		this.builder = new TableBuilder();
		return this.builder;
	}

	@Override
	protected void addTableColumns(Attribute attribute) {
		this.builder.addColumn(ID_COL_WIDTH);
	}

	@Override
	public void renderHeaderRow(Attribute first) {
		header("A<sub>id</sub><sup>site</sup>");
	}

	@Override
	public void renderDataRow(Attribute attribute) {
		data(getAttributeCSSclass(attribute), attribute.toString());
	}

	private String getAttributeCSSclass(Attribute a) {
		return ( a.isTarget() ? TARGET_ATTRIBUTE_ID_CLASS : ATTRIBUTE_ID_CLASS ) ;
	}
	

	@Override
	public Class<Attribute> getRenderedElementClass() {
		return Attribute.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<Iterable<Attribute>> getRenderedObjectClass() {
		return (Class<Iterable<Attribute>>) Iterable.class.asSubclass(Iterable.class);
	}

}
