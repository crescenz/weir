package it.uniroma3.weir.fixture;

import static it.uniroma3.weir.fixture.AttributeFixture.createAttribute;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.hiddenrelation.AbstractAttribute;

import java.util.Arrays;
import java.util.HashSet;

public class AbstractAttributeFixture {
	
	public static AbstractAttribute createAbstractAttribute(Attribute... attrs) {
		return new AbstractAttribute(new HashSet<>(Arrays.asList(attrs)));
	}

	public static AbstractAttribute createSingletonAbstractAttribute() {
		return createAbstractAttribute(createAttribute("a"));
	}

	public static AbstractAttribute createSingletonAbstractAttributeWithDifferentData() {
		return createAbstractAttribute(createAttribute("c"));
	}

	public static AbstractAttribute createDoubletonAbstractAttribute() {
		return createAbstractAttribute(createAttribute("a"), createAttribute("b"));
	}	
	
}
