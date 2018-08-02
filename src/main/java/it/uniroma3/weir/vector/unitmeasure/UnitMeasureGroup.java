package it.uniroma3.weir.vector.unitmeasure;

import static it.uniroma3.weir.vector.unitmeasure.UnitMeasure.*;
/**
 * 
 * Group unit-measures {@link UnitMeasure}s by the corresponding {@link Type}.
 *
 */
public enum UnitMeasureGroup {

	/**
	 *  By convention, the first unit-measure in each group is used as reference
	 */
	MASS(	KILOGRAM,
			POUND,
			STONE,
			OUNCES),
			
	SPACE(	METER,
			CENTIMETER,
			FOOT,
			INCHES),
			
	MONEY(  DOLLAR,
			EURO,  
			STERLING);

	private UnitMeasure[] units;

	private UnitMeasureGroup(UnitMeasure...unitMeasures) {
		this.units = unitMeasures;
	}

	public UnitMeasure[] getUnitMeasures() {
		return this.units;
	}
	
	public UnitMeasure findUnitMarker(String candidate) {
		for (UnitMeasure unit : this.units) {
			for (String marker : unit.getUnitMarkers()) {
				if (marker.equalsIgnoreCase(candidate)) {
					return unit;
				}
			}
		}
		return null;
	}
	
	/**
	 *  By convention, the first unit-measure in each group is used as reference
	 */
	public UnitMeasure getReferenceUnit() {
		return this.units[0];
	}

}
