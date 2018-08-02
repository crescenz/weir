package it.uniroma3.weir.fixture;

import static it.uniroma3.weir.fixture.VectorFixture.createVector;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.type.Type;

public class AttributeFixture {

	public static Attribute createAttribute(String... elements) {
		return createAttribute(createVector(elements));
	}

	public static Attribute createAttribute(Type type, String... elements) {
		return createAttribute(createVector(type, elements));
	}

	private static Attribute createAttribute(Vector vector) {
		Attribute attr = new Attribute(vector);
//		attr.setWebsiteId(0);
		return attr;
	}
	
	public static Attribute createRandomAttribute(int numberOfValues) {
		return new Attribute(VectorFixture.createRandomStringVector(numberOfValues));
	}

}
