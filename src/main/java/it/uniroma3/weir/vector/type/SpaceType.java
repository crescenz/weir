package it.uniroma3.weir.vector.type;

import it.uniroma3.weir.vector.unitmeasure.UnitMeasureGroup;

/**
 * 
 * A type that represents a space measure
 *
 */
public class SpaceType extends DimensionalType {

	static final private long serialVersionUID = 6404794840811945510L;

	public SpaceType() {
		super(UnitMeasureGroup.SPACE);
	}

	@Override
	public Type getParent() {
		return Type.DIMENSIONAL;
	}
	
}
