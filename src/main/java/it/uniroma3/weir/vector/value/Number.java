package it.uniroma3.weir.vector.value;

import static it.uniroma3.weir.Formats.thousandth;
import it.uniroma3.weir.vector.type.DimensionalType;
import it.uniroma3.weir.vector.type.NumberType;

import java.io.Serializable;

/**
 * This class does not add anything wrt to {@link Double} but it is need
 * to let {@link DimensionalType#tryCast(String)} method return a subtype
 * (i.e., {@link Dimensional} of what {@link NumberType#tryCast(String)} 
 * returns. 
 */
public class Number implements Comparable<Number>, Serializable {
	
	static final private long serialVersionUID = 6682331611071247916L;

	private Double value;
	
	public Number(Double number) {
		this.value = number;
	}
	
	public Double getValue() {
		return value;
	}

	@Override
	public int compareTo(Number that) {
		return Double.compare(this.getValue(), that.getValue());
	}

	@Override
	public boolean equals(Object o) {
		if (o==null || (o.getClass()!=this.getClass())) return false;
		final Number that = (Number)o;
		return this.value.equals(that.value);
	}

	@Override
	public String toString() {
		return thousandth.format(this.value);
	}

}
