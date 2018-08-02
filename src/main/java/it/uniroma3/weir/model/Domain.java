package it.uniroma3.weir.model;

import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.cache.Fingerprinted;
import it.uniroma3.weir.cache.Fingerprinter;

import java.io.Serializable;
import java.util.*;

/**
 * e.g., auto, book, nbaplayers, jobs, finance, etc. etc.
 *
 * A list of {@link Website}s in the same vertical domain
 * (and belonging to the same {@link Dataset}
 */
public class Domain extends WeirId 
                    implements Serializable, Fingerprinted, Iterable<Website> {

	static final private long serialVersionUID = -8078531720257505938L;

	private Dataset dataset;

	final private List<Website> sites;

	public Domain(String name) {
		super(name);
		this.sites = new LinkedList<>();
	}
		
	public String getName() {
		return this.getId();
	}
	
	public void addSite(Website site) {
		checkIsFromSameDomain(site);
		site.setDomain(this);
		this.sites.add(site);
	}
	
	private void checkIsFromSameDomain(Website site) {
		final Domain domain = site.getDomain();
		if (domain!=null && domain!=this)
			throw new IllegalArgumentException(site+" is from another domain ("+domain+")");
	}

	public List<Website> getSites() {
		return this.sites;
	}

	public Dataset getDataset() {
		return this.dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public int size() {
		return this.getSites().size();
	}

	@Override
	public Iterator<Website> iterator() {
		return getSites().iterator();
	}
	
	public Set<Attribute> getAllAttributes() {
		final Set<Attribute> all = new LinkedHashSet<>();
		for (Website site : getSites()) {
			all.addAll(site.getAttributes());
		}
		return all;
	}
	
	public void shrinkToOverlap() {
		for(Website site : this)
			site.shrinkToOverlap();
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(this.getName())+Objects.hashCode(this.getDataset());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o==null || o.getClass()!=getClass()) return false;
		
		final Domain that = (Domain)o;
		return this.getName().equals(that.getName()) &&
			   this.getDataset().equals(that.getDataset());
	}
	
	@Override
	public Fingerprint getFingerprint() {
		Fingerprinter printer = new Fingerprinter();		
		printer.fingerprint(this.getName());
		printer.fingerprint(this.getDataset().getName());
		for(Website site : getSites())
			printer.fingerprint(site.getFingerprint());
		return printer.getFingerprint("domain");
	}

	@Override
	public String toString() {
		return this.getDataset()+"-"+getName();
	}
	
}
