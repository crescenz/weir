package it.uniroma3.weir.model;

import static it.uniroma3.util.CollectionsCSVUtils.*;

import java.util.*;

/**
 * 
 * A set of {@link Mapping}s each containing a set of source {@link Attribute}s
 *
 */
public class MappingSet extends WeirId implements Iterable<Mapping> {

	static final private long serialVersionUID = 6869464396960420003L;

	private Set<Mapping> mappings; // VC: why not Set<> ??? use index to align output vs golden?
	
	private Map<Attribute, Mapping> attribute2mapping;

	public MappingSet() {
		super(nextIdByClass(MappingSet.class));
		this.mappings = new HashSet<Mapping>();
		this.attribute2mapping = new LinkedHashMap<>();
	}
	
	public MappingSet(MappingSet ms) {
		this();
		for(Mapping mapping : ms)
			this.addMapping(mapping);
	}
	
	public void addMapping(Mapping m) {
		this.mappings.add(m);
		for (Attribute a : m.getAttributes()) {
			this.attribute2mapping.put(a, m);
		}
	}

	public void addAll(Collection<Mapping> mappings) {
		for(Mapping m : mappings)
			this.addMapping(m);
	}

	public void removeMappings(MappingSet toRemove) {
		for (Mapping m : toRemove.getMappings()) {
			this.removeMapping(m);
		}		
	}

	public void removeMapping(Mapping m) {
		this.mappings.remove(m);
		for (Attribute a : m.getAttributes()) {
			this.attribute2mapping.remove(a);
		}
	}

	public Mapping getMappingByAttribute(Attribute a) {
		return this.attribute2mapping.get(a);
	}

	public MappingSet keepAllBut(Mapping but) {
		final MappingSet result = new MappingSet(this);
		result.removeMapping(but);
		return result;
	}
	
	public Set<Mapping> getMappings() {
		return this.mappings;
	}
	
	public SortedSet<Mapping> getMappingsOrderedByLabel() {
		final TreeSet<Mapping> result = new TreeSet<Mapping>(new Comparator<Mapping>() {
			@Override
			public int compare(Mapping m1, Mapping m2) {
				return m1.getLabel().compareTo(m2.getLabel());
			}			
		});
		result.addAll(this.getMappings());
		return result;
	}

	public Set<Attribute> flat() {
		final Set<Attribute> result = new HashSet<>();
		for(Mapping m : this)
			result.addAll(m.getAttributes());
		return result;
	}
	
	public int size() {
		return this.mappings.size();
	}
	
	@Override
	public Iterator<Mapping> iterator() {
		return getMappings().iterator();
	}
	
	@Override
	public char getInitial() {
		return Character.toUpperCase(getLowerCaseLettersFromClassName());
	}
		
	@Override
	public String toString() {
		return collection2csv(this.mappings);
	}

}
