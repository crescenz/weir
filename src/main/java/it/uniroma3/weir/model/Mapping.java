package it.uniroma3.weir.model;

import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.integration.lc.LocalConsistency;

import java.util.*;
import static it.uniroma3.weir.model.Attribute.COMPARATOR_BY_SITE;
//import static it.uniroma3.util.CollectionsCSVUtils.*;
/**
 *
 * A mapping is a set of source (was: 'physical') {@link Attribute}s
 * extracted from a set of {@linkplain Webpage}s of a {@link Website}
 * and containing semantically related attributes.
 *
 * Mappings marked as <em>complete</em> (see {@link #markAsComplete()}
 * cannot be expanded anymore.
 *
 * If the <em>local consistency assumption</em> holds, a mapping cannot
 * contains multiple attribute from the same source.
 *
 * @see {@link LocalConsistency}
 *
 */
public class Mapping extends WeirId 
                     implements Iterable<Attribute>, Cloneable {

	static final private long serialVersionUID = -9088976421187454061L;

	private Set<Attribute> attributes; // VC: why not Set !?

	private boolean complete; // marked as complete/frozen mapping

	private boolean golden;   // marked as golden mapping, i.e.,
							  // it's used to evaluate produced mappings

	public Mapping() {
		this(Collections.<Attribute>emptyList());
	}

	public Mapping(Attribute singleton) {
		this(Collections.singleton(singleton));
	}

	public Mapping(Attribute... attrs) {
		this(Arrays.asList(attrs));
	}

	public Mapping(Mapping m1, Mapping m2) {
		this();
		this.addAll(m1.getAttributes());
		this.addAll(m2.getAttributes());
	}

	//MGC
	public Mapping(Collection<Attribute> attrs) {
		super(nextIdByClass(Mapping.class));
		this.attributes = new HashSet<>();
		this.complete = false;
		this.golden = false;
		this.addAll(attrs);
	}

	public boolean isGolden() {
		return this.golden;
	}

	public void setAsGolden() {
		this.golden = true;
	}

	public void add(Attribute attribute) {
		if (!this.attributes.contains(attribute))
			this.attributes.add(attribute);
	}

	public void addAll(Collection<Attribute> attributes) {
		for(final Attribute a : attributes)
			this.add(a);
	}

	public void addAll(Mapping m) {
		this.addAll(m.getAttributes());
	}

	public boolean removeAll(Collection<Attribute> attributes) {
		return this.attributes.removeAll(attributes);
	}

	public boolean removeAll(Mapping m) {
		return this.attributes.removeAll(m.getAttributes());
	}

	public Set<Attribute> getAttributes() {
		return new HashSet<>(this.attributes);
	}

	public void markAsComplete() {
		this.complete = true;
	}

	public boolean isComplete() {
		return this.complete;
	}

	public boolean isEmpty() {
		return ( this.size()==0 );
	}

	public int size() {
		return this.getAttributes().size();
	}

	public Set<Attribute> fromSource(Website site) {
		final Set<Attribute> result = new HashSet<>();
		for (final Attribute a : this.getAttributes()) {
			if (a.getWebsite().equals(site))
				result.add(a);
		}
		return result;
	}

	public Set<Website> getSourceSites() {
		final Set<Website> sites = new HashSet<>();
		for (final Attribute a : this.getAttributes()) {
			sites.add(a.getWebsite());
		}
		return sites;
	}

	/**
	 * {@link Attribute}s coming from the same {@link Website}
	 * and in the same mapping  are conflicting. <br/>
	 * They violate the <em>Local Consistency</em> assumption.
	 *
	 * @return the conflicting attributes in this mapping.
	 *///TODO either generalize this method to make use of LocalConsistencyCondition,
	public Set<Attribute> getConflictingAttributes() {
		final Set<Website> sourcesofar = new HashSet<>();
		final Set<Attribute> conflicting = new HashSet<>();

		for (final Attribute a : this.getAttributes()) {
			final Website source = a.getWebsite();
			if (!sourcesofar.contains(source)) {
				// first time for this source
				sourcesofar.add(source);
			} else {
				// source already met
				final Set<Attribute> sameSource = this.fromSource(source);
				conflicting.addAll(sameSource);
			}
		}
		return conflicting;
	}

	/**
	 * @param candidates 
	 * @return the closest attribute among a set of candidates
	 */
	public Match findClosest(Attribute pivot) {
		return pivot.findClosest(getAttributes());
	}

	/**
	 * @return the label of first attribute, if any; null, otherwise
	 */
	public String getLabel() {
		if (this.isEmpty()) return null;
		return this.getAttributes().iterator().next().getFirstLabel();
	}

	@Override
	public Iterator<Attribute> iterator() {
		return this.getAttributesOrderedBySite().iterator();
	}
	
	public SortedSet<Attribute> getAttributesOrderedBySite() {
		final SortedSet<Attribute> result = new TreeSet<Attribute>(COMPARATOR_BY_SITE);
		result.addAll(this.getAttributes());
		return result;
	}

	@Override
	public Mapping clone() {
		try {
			// make a shallow copy
			final Mapping result = (Mapping) super.clone();
			// now make it a deep copy
			result.attributes = new HashSet<>(this.getAttributes());
			return result;
		} catch (final CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return 	( isGolden() ? getLabel() + " " : "" ) + 
				super.toString()/*+"="+collection2csv(getAttributes())*/;
	}

}
