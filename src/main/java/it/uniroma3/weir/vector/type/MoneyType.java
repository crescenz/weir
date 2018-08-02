package it.uniroma3.weir.vector.type;

import it.uniroma3.weir.vector.unitmeasure.UnitMeasureGroup;

/**
 * 
 * A numeric {@link Type} associated with a currency unit-measure
 *
 */
public class MoneyType extends DimensionalType {

	static final private long serialVersionUID = -1974988256961316992L;

	public MoneyType() {
		super(UnitMeasureGroup.MONEY);
	}
	
	@Override
	public Type getParent() {
		return Type.DIMENSIONAL;
	}

}
