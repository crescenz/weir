package it.uniroma3.weir.fixture;

import static it.uniroma3.weir.fixture.AttributeFixture.createAttribute;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.vector.type.Type;

public class MappingFixture {
	
	public static Mapping createMapping(Attribute... attrs) {
		Mapping mapping = new Mapping();
		for (Attribute a : attrs) {
			mapping.add(a);
		}
		return mapping;
	}

	public static Mapping createEmptyMapping() {
		return new Mapping();
	}
	
	public static Mapping createSingletonMapping() {
		return createMapping(createAttribute("a"));
	}

	public static Mapping createSingletonMappingWithDifferentData() {
		return createMapping(createAttribute("c"));
	}

	public static Mapping createSingletonMappingWithDifferentDataType() {
		return createMapping(createAttribute(Type.NUMBER, "1.0"));
	}

	public static Mapping createSingletonMappingWithDifferentWebsite() {
		Attribute attr = createAttribute("a");
//was:		attr.setWebsiteId(1); //default is 0
		attr.setWebsite(WebsiteFixture.createWebsite("site")); 
		return createMapping(attr);
	}

	public static Mapping createDoubletonMapping() {
		return createMapping(createAttribute("a"), createAttribute("b"));
	}	

}
