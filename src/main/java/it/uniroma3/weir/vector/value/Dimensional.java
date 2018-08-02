package it.uniroma3.weir.vector.value;

import java.io.Serializable;

/**
 * A dimensional {@link Number} i.e., a number associated with an unit-measure
 */
public class Dimensional extends Number implements Serializable {
	
	static final private long serialVersionUID = -3974518091205124729L;
	
	private String marker; /* unit measure marker */
	
	public Dimensional(double number, String marker) {
		super(number);
		this.marker = marker;
	}

	public String getMarker() {
		return this.marker;
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && this.marker.equals(((Dimensional)o).getMarker());
	}

	@Override
	public String toString() {
		return super.toString() + " " + this.marker;
	}

}
