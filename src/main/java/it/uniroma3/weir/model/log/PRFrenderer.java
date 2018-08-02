package it.uniroma3.weir.model.log;

import static it.uniroma3.hlog.HypertextualUtils.percentage;
import static it.uniroma3.weir.model.log.WeirCSSclasses.RESULT_CSS_CLASS;
import it.uniroma3.hlog.render.ObjectRenderer;
import it.uniroma3.hlog.render.TableBuilder;
import it.uniroma3.weir.evaluation.PRF;

public class PRFrenderer implements ObjectRenderer<PRF> {

	static final public int BASE_WIDTH = 64;

	static final private double PRF_RELATIVE_WIDTH = 1;

	static final private double TFPN_RELATIVE_WIDTH = 0.6;

	
	static final TableBuilder builder = new TableBuilder(BASE_WIDTH);
	{{
		builder.setFixedLayout();
		builder.addNColumns(3, PRF_RELATIVE_WIDTH);
		builder.addNColumns(3, TFPN_RELATIVE_WIDTH);
	}}
	
	@Override
	public String toHTMLstring(PRF prf) {
		builder.table();
		builder.tr();
		builder.th("P");
		builder.th("R");
		builder.th("F");
		builder.th("tp");
		builder.th("fn");
		builder.th("fp");
		builder._tr();
		builder.tr();
		builder.td(RESULT_CSS_CLASS, percentage(prf.getPrecision()));
		builder.td(RESULT_CSS_CLASS, percentage(prf.getRecall()));
		builder.td(RESULT_CSS_CLASS, percentage(prf.getFMeasure()));
		builder.td(RESULT_CSS_CLASS, prf.getTruePositives());
		builder.td(RESULT_CSS_CLASS, prf.getFalseNegatives());
		builder.td(RESULT_CSS_CLASS, prf.getFalsePositives());
		builder._tr();
		builder._table();		
		return builder.toString();
	}

	@Override
	public Class<PRF> getRenderedObjectClass() {
		return PRF.class;
	}
	
}
