package it.uniroma3.weir.model.hiddenrelation;

import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.WeirId;

import java.util.*;


/**
 * 
 * An abstract instance of the {@link AbstractRelation}, i.e.,
 * a set of {@link Webpage}s referring to the same conceptual
 * instance.
 * <BR/>
 * This is the extensional counterpart of an AbstractAttribute.
 *
 */
public class AbstractInstance extends WeirId implements Iterable<Webpage> {

	static final private long serialVersionUID = -7325745800528228290L;

	static final public Comparator<AbstractInstance> COMPARATOR_BY_SIZE = new Comparator<AbstractInstance>() {
		@Override
		public int compare(AbstractInstance i1, AbstractInstance i2) {
			int cmp = i2.size()-i1.size(); // bigger is better
			if (cmp==0) {
				final Webpage f1 = i1.firstNonNullPage();
				final Webpage f2 = i2.firstNonNullPage();
				cmp = f1.compareTo(f2);
			}
			if (cmp==0) cmp = i1.compareTo(i2);
			return cmp;
		}
	};
	
	private Webpage[] site2page; // site index -> page

	public AbstractInstance(Domain domain, Webpage...pages) {
		this(domain, Arrays.asList(pages));
	}
	
	public AbstractInstance(Domain domain, Iterable<Webpage> pages) {
		super(nextIdByClass(AbstractInstance.class));
		if (domain==null)
			throw new IllegalArgumentException("Domain cannot be null");
		this.site2page = new Webpage[domain.size()];
		for(Webpage page : pages) {
			checkDomain(domain, page);
			add(page);
		}
	}

	private void checkDomain(Domain domain, Webpage page) {
		if (!page.getWebsite().getDomain().equals(domain))
			throw new IllegalArgumentException(page + " is out of domain "+domain);
	}

	public Collection<Webpage> getWebpages() {
		return Arrays.asList(this.site2page);
	}

	public boolean isConflicting(Webpage page) {
		Objects.requireNonNull(page, "null page");
		final int siteIndex = page.getWebsite().getIndex();
		final Webpage thereis = this.site2page[siteIndex];
		return thereis!=null && !thereis.equals(page);		
	}
	
	public boolean contains(Webpage page) {
		Objects.requireNonNull(page, "null page");
		final int siteIndex = page.getWebsite().getIndex();
		final Webpage thereis = this.site2page[siteIndex];
		return thereis!=null && thereis.equals(page);		
	}
	
	public void add(Webpage page) {
		Objects.requireNonNull(page, "Cannot add a null page!");
		final int siteIndex = page.getWebsite().getIndex();
		final Webpage was = this.site2page[siteIndex];
		if (was==null || was==page) {
			this.site2page[siteIndex] = page;
		} else {
			final String msg = 	
					  " Cannot create an abstract instance"
					+ " from conflicting pages: " + page + " and " +was 
					+ " are from the same site";
			throw new IllegalArgumentException(msg);
		}
	}

	public Webpage from(Website site) {
		Objects.requireNonNull(site,"A null site has been specified");
		return this.site2page[site.getIndex()];
	}

	public Webpage firstNonNullPage() {
		for(Webpage p : this)
			if (p!=null) return p;
		throw new IllegalStateException(
			 "An "+this.getClass().getSimpleName()
			+" object cannot contain only nulls pages");
	}
	
	public int size() {
		int result = 0;
		for(Webpage page : this.site2page)
			if (page!=null) result++;
		return result;
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + Arrays.toString(this.site2page);
	}

	@Override
	public Iterator<Webpage> iterator() {
		return Arrays.asList(this.site2page).iterator();
	}

}
