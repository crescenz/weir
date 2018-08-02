package it.uniroma3.weir;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Share project-wide useful {@link NumberDecoders}s
 */
public class Formats {

	static final public NumberFormat percentage = NumberFormat.getPercentInstance(Locale.ITALY);
	static final public NumberFormat percentage_2 = NumberFormat.getPercentInstance(Locale.ITALY);
	static final public NumberFormat percentage_f = new DecimalFormat("##0.##%");
	
	static {
		percentage.setMinimumFractionDigits(0);
		percentage.setMaximumFractionDigits(2);
		percentage_2.setMinimumFractionDigits(2);
		percentage_2.setMaximumFractionDigits(2);
	}
	
	static final public DecimalFormat billionth  = new DecimalFormat("0.#########");

	static final public DecimalFormat millionth  = new DecimalFormat("0.######");

	static final public DecimalFormat ten_thousandth = new DecimalFormat("0.#####");

	static final public DecimalFormat thousandth  = new DecimalFormat("0.###");
	
	static final public DecimalFormat tenth  = new DecimalFormat("0.0");

	static final public DecimalFormat hundredth  = new DecimalFormat("0.00");

}
