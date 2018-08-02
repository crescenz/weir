package it.uniroma3.weir.vector.format;

import it.uniroma3.weir.vector.value.Number;
import static it.uniroma3.weir.vector.format.Regexps.*;
import static it.uniroma3.weir.vector.format.FormatRule.*;
import static it.uniroma3.weir.vector.unitmeasure.UnitMeasure.FOOT;
import static it.uniroma3.weir.vector.unitmeasure.UnitMeasure.INCHES;
/**
 * A {@link FormatRule} with the semantics to interpret the
 * extracted string occurrence as a {@link Number}.
 * See  method {@link #getNumber()}.
 *
 */
public enum NumberDecoders implements TypeDecoder {
	
	NOT_AVAILABLE("N(\\.?)A(\\.?)|unch") {
		@Override
		public Number decodeNumber(String value) {
			return new Number(0.0);
		}
	},
	EXPONENTIAL(NUMBER_REGEXP + EXP_SUFFIX_REGEXP) {
		@Override
		public Number decodeNumber(String value) {
			if (value.length()<=10) {
				final String[] numbers = value.split("[eE]");
				double base = Double.parseDouble(numbers[0]);
				int exp = Integer.parseInt(numbers[1]);
				return new Number(base * Math.pow(10, exp));
			}
			return null;
		}
	},
	HUNDREDS(HUNDREDS_REGEXP) {
		@Override
		public Number decodeNumber(String value) {
			double number = Double.parseDouble(stripCommas(value));
			return new Number(number);
		}
	},
	NUMBER(NUMBER_REGEXP) {
		@Override
		protected Number decodeNumber(String value) {
			/* remove localization ., signs, if any */
			if (value.contains(",") && value.contains(".")) {
				value = stripCommas(value);
			}
			else if (value.contains(",") && !value.contains(".")) {
				// there are , signs without any . signs
				if (value.indexOf(',')==value.lastIndexOf(',')) {
					// just one occurrence of , means .
					value = value.replaceAll(",", ".");
				}
				else {
					// multiple occurrences of ',' ...
					// ... means it is just a digit sep.
					value = stripCommas(value);
				}
			}
			return new Number(Double.parseDouble(value));
		}
	},
	MILLION(MILLION_REGEXP) {
		@Override
		protected Number decodeNumber(String value) {
			double number = Double.parseDouble(numberRule.extract(value));
			return new Number(number * Math.pow(10, 6));
		}
	},
	FOOT_INCHES(FOOT_INCHES_REGEXP) {// 6-2 (implicit foot-inches)
		@Override
		protected Number decodeNumber(String value) {
			final String[] numbers = value.split("-");
			final int foot  = Integer.parseInt(numbers[0]);
			final int inches = Integer.parseInt(numbers[1]);
			return new Number(FOOT.getRatio()*foot+INCHES.getRatio()*inches);
		}
	};
	
	static final private String stripCommas(String value) {
		return value.replace(",", "");
	}
	
	protected FormatRule rule;
	
	private NumberDecoders(String regex) {
		this.rule = new FormatRule("^"+regex+"$");
	}
	
	/**
	 * 
	 * @return the {@link Number} in this format or null
	 *         if this format does not apply
	 */
	@Override
	public Number decode(String value) {
		final String extracted = this.rule.extract(value);
		if (extracted==null) return null;
		else return decodeNumber(extracted);
	}

	abstract protected Number decodeNumber(String value);
	
}
