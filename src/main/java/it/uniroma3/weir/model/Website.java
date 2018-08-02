package it.uniroma3.weir.model;

import static it.uniroma3.weir.configuration.Constants.WORK_ON_OVERLAP;
import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.cache.Fingerprinted;
import it.uniroma3.weir.cache.Fingerprinter;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.linking.IdfRepository;
import it.uniroma3.weir.linking.entity.Entity;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.*;

/**
 * 
 * A website containing a list of {@linkplain Webpage}s
 * of a given vertical {@linkplan Domain} as part of an
 * input {@linkplain Dataset}.
 *
 */
public class Website extends WeirId implements Serializable, Fingerprinted {

	static final private long serialVersionUID = -8085665096612727628L;

	private Domain domain;

	private IdfRepository idfs;

	private List<Webpage> pages;    // all available pages from this website

	// this is a view collecting all the overlapping pages, i.e., 
	// those overlapping at least with another site from the domain
	private List<Webpage> overlap;
	
	private Set<Webpage> loaded;
	
	// A better approach would be not to materialize the overlap, 
	// but let PageLinkage play its role between any pair of websites. 
	// by saving the indices over all the input pages. 
	// That would require a significant code refactoring
	
	private List<Attribute> attributes;

	/* This is the index of this page over the collection of all 
	 * the sites from its domain. Transient because it depends on
	 * how many other sites have been included in the domain.
	 */
	transient private int index;
	
	public Website(String name) {
		super(name);
		this.overlap = null;
		this.pages = null;
		this.loaded = null;
		this.attributes = new ArrayList<>();
		this.idfs = new IdfRepository();
		this.index = -1;
	}
	/* let the website recompute its index after deserialization */
	private Object readResolve() throws ObjectStreamException {
		this.index = -1; // -1 means: recompute it!
		return this;
	}

	public String getName() {
		return this.getId();
	}

	public Domain getDomain() {
		return this.domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	/**
	 * Add a page to this website
	 * @param page
	 */
	public void addPage(Webpage page) {
		if (this.pages==null)
			this.pages = new ArrayList<>();
		page.setWebsite(this, this.pages.size());
		this.pages.add(page);
	}

	/**
	 * Add a page to the overlapping portion of this site 
	 * provided that it is already in its support
	 * @param the 'overlapping' page
	 */
	public void addOverlapPage(Webpage page) {
		checkIsFromSameSite(page);
		if (this.overlap==null)
			this.overlap = new ArrayList<>();
		if (!this.overlap.contains(page)) {
			page.setOverlapIndex(this.overlap.size());
			this.overlap.add(page);
		}
	}

	private void checkIsFromSameSite(Webpage page) {
		if (page.getWebsite()!=this)
			throw new IllegalArgumentException(page+" is from another site ("+page.getWebsite()+")");
	}

	/**
	 * 
	 * @return all the pages in this website that overlap at
	 *         least with another website
	 */
	public List<Webpage> getOverlappingPages() {
		return this.overlap;
	}

	/**
	 * @return 	 the index of this sites over the collection of all 
	 * 			 the sites from its domain
	 */
	public int getIndex() {
		if (this.index!=-1) return this.index;
		this.index = getDomain().getSites().indexOf(this);
		return this.index;
	}
	
	/**
	 * 
	 * @return all the pages in this website
	 */
	public List<Webpage> getWebpages() {		
		return this.pages;
	}

	public List<Webpage> getWorkingPages() {
		return ( WeirConfig.getBoolean(WORK_ON_OVERLAP) ? this.getOverlappingPages() : this.getWebpages() );
	}
	
	public void clear() {
		if (this.pages!=null)
			this.pages.clear();
		if (this.overlap!=null)
			this.overlap.clear();
	}

	/**
	 * Shrink this website to retain only the overlapping
	 * pages and getting rid of all non-overlapping pages
	 * 
	 * @return the number of pages after the shrinking
	 */
	public int shrinkToOverlap() {
		Objects.requireNonNull(this.overlap, "Overlapping pages not set yet");
		final List<Webpage> overlap = new ArrayList<>(this.getOverlappingPages());
		this.clear();
		for(Webpage page : overlap) {
			this.addPage(page);
			this.addOverlapPage(page);
		}
		return this.getOverlappingPages().size();
	}
	
	public Webpage findPageByName(final String name) {
		for (Webpage page : this.getWebpages()) {
			if (name.equals(page.getName())) {
				return page;
			}
		}
		return null;
	}
	
	public void loadPages() {
		loadPages(this.getWorkingPages());
	}

	public void loadPages(Collection<Webpage> pages) {
		if (this.loaded==null)
			this.loaded = new LinkedHashSet<Webpage>();
//		try {
//			final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			for (final Webpage p : pages) {
//				pool.submit(new Runnable() {
//					@Override
//					public void run() {	
						p.loadDocument();
						this.loaded.add(p);
//					}
//				});
			}
//			pool.awaitTermination(10, TimeUnit.SECONDS);
//			pool.shutdown();
//		} catch (InterruptedException e) {
//			throw new IllegalStateException(e);
//		}
	}
	
	public void releasePages() {
		if (this.loaded==null) return;
		
		for (Webpage p : this.loaded) {
			p.releaseDocument();
		}
	}

	public void normalizeDOMdocuments() {
		// this is required to deal with the extraction logics of the rules
		// that forms the output Value by means of a first DOM node they extract
		for(Webpage page : this.getWorkingPages())
			page.getDocument().normalize();
		//FIXME però poi non si riesce più a capire chi sia invariante 
		// perché le annotazioni/UserData saltano...fare spikes!
	}

	
	public void addAttribute(Attribute attribute) {
		attribute.setWebsite(this);
		this.attributes.add(attribute);
	}

	public List<Attribute> getAttributes() {
		return this.attributes;
	}

	public List<Entity> getEntities() {
		return new AbstractList<Entity>() {

			@Override
			public Entity get(int index) {
				return getOverlapping().get(index).getEntity();
			}

			@Override
			public int size() {
				return getOverlapping().size();
			}

			private List<Webpage> getOverlapping() {
				final List<Webpage> overlap = getOverlappingPages();
				return ( overlap!=null ? overlap : Collections.<Webpage>emptyList() );
			}

		};
	}

	public IdfRepository getIdfRepository() {
		return this.idfs;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.getName())+Objects.hashCode(this.getDomain());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o==null || this.getClass()!=o.getClass()) return false;
		
		final Website that = (Website)o;
		return this.getName().equals(that.getName()) &&
			   this.getDomain().equals(that.getDomain());
	}

	@Override
	public String toString() {
		return toHTMLstring();
//		return this.getName()+ "-" + this.getDomain();
	}

	public String toHTMLstring() {
		return getShortDomainFreeName() + "<sub>" + this.getIndex() + "</sub>";
	}

	/* get rid of the domain name in the site name, if possible */
	final private String getShortDomainFreeName() {
		final String siteName = this.getName();
		final String domainName = getDomain().getName();
		final int index = siteName.indexOf(domainName);
		return (index!=-1 ? siteName.substring(index+domainName.length()+1) : siteName);
	}

	@Override
	public Fingerprint getFingerprint() {
		final Fingerprinter printer = new Fingerprinter();
		printer.fingerprint(this.getName());

		for (Webpage page : this.getWebpages()) {
			printer.fingerprint(page.getId());
		}
		return printer.getFingerprint("site");
	}
	
}
