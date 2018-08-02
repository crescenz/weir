package it.uniroma3.weir.vector.type;

import it.uniroma3.weir.vector.unitmeasure.UnitMeasureGroup;

/**
 * 
 * A {@link Type} that represents numbers used to measure mass
 *
 */
public class MassType extends DimensionalType {

	static final private long serialVersionUID = 27420713290470762L;

	public MassType() {
		super(UnitMeasureGroup.MASS);
	}

	@Override
	public Type getParent() {
		return Type.DIMENSIONAL;
	}
		
}
