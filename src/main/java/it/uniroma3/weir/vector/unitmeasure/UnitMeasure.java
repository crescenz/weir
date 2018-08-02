package it.uniroma3.weir.vector.unitmeasure;

import it.uniroma3.weir.vector.type.DimensionalType;

import java.io.Serializable;

/**
 *
 * The unit-measures used for {@link DimensionalType}s
 * and grouped into {@link UnitMeasureGroup}s
 *
 */
public enum UnitMeasure implements Serializable {

	/* MASS  */
	KILOGRAM("kilogram", 1.0, "kg"),   /* kiloggram as reference */
	POUND("pound", 0.45359237, "lbs", "lb", "pound", "pounds"),
	OUNCES("ounce", 0.0283495, "ounce", "ounces"),
	STONE("stone", 6.35029318, "st"),

	/* SPACE */
	METER("meter", 1.0, "m"),          /* meter as reference     */
	CENTIMETER("centimeter", 0.01, "cm"),
	INCHES("inches", 0.0254, "in", "\""),
	FOOT("foot", 0.3048, "ft", "'"),

	/* MONEY */
	EURO("euro", 1.15, "€", "EUR"),
	DOLLAR("dollar", 1.0, "$", "USD"), /* dollar as reference    */
	STERLING("sterling", 1.55, "£", "GBP");

	final private String name;         // name of this unit of measure
	final private String[] markers;    // commons unit markers e.g., $ (USD)
	final private double ratio;        // ratio to reference unit-measure 

	private UnitMeasure(String name, double ratio2ref, String... markers) {
		this.name = name;
		this.markers = markers;
		this.ratio = ratio2ref;
	}

	public String getName() {
		return this.name;
	}

	public double getRatio() {
		return this.ratio;
	}

	public String[] getUnitMarkers() {
		return this.markers;
	}

}
