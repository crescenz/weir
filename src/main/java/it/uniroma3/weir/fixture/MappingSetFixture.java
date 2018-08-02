package it.uniroma3.weir.fixture;

import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.model.MappingSet;
import it.uniroma3.weir.model.Website;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingSetFixture {
	
	public static MappingSet createMappingSet(boolean onlyRedundantGolden, Website... wss) {
		Map<String, List<Attribute>> tag2attrs = new HashMap<String, List<Attribute>>();
		for (Website ws : wss) {
			for (Attribute a : ws.getAttributes()) {
				List<Attribute> attrs = tag2attrs.get("tag");
				if (attrs == null) {
					attrs = new ArrayList<Attribute>();
					tag2attrs.put("tag", attrs);
				}
				attrs.add(a);
			}
		}
		
		MappingSet mappSet = new MappingSet();
		for (String tag : tag2attrs.keySet()) {
			Mapping m = new Mapping();
			List<Attribute> attrs = tag2attrs.get(tag);
			
			if (onlyRedundantGolden && attrs.size() == 1)
				continue;
			
			for (Attribute a : tag2attrs.get(tag)) {
				m.add(a);
			}
			mappSet.addMapping(m);
		}
		return mappSet;
	}
	
	public static MappingSet createMappingSet(Mapping... mappings) {
		MappingSet mapSet = new MappingSet();
		for (Mapping m : mappings) {
			mapSet.addMapping(m);
		}
		return mapSet;
	}
	
}
