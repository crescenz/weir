package it.uniroma3.weir.linking.linkage;

import it.uniroma3.weir.linking.entity.Entity;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.WebsitePair;
import it.uniroma3.weir.structures.Pair;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;

import static it.uniroma3.weir.Formats.thousandth;
/**
 * A linkage between two {@link Webpage}s of different {@link Website}s
 */
// * VC: this could be a WeightedPair; CHECK
public class PageLinkage extends Pair<Webpage> implements Comparable<PageLinkage>, Serializable {

	static final private long serialVersionUID = -842394759592783593L;
		
	private double similarity;
	
	private int usage; // sum of usages of its pages
	
	public PageLinkage(Webpage page1, Webpage page2, double sim) {
		super(page1,page2);
		this.similarity = sim;
		this.usage = -1;
	}

	public PageLinkage(Pair<Webpage> p, double sim) {
		this(p.getMin(), p.getMax(), sim);
	}

	public PageLinkage(Webpage page1, Webpage page2) {
		this(page1.getEntity(), page2.getEntity());
	}

	public PageLinkage(Entity e1, Entity e2) {
		this(e1.getWebpage(),e2.getWebpage(),e1.similarity(e2));
	}	

	public Webpage from(Website site) {
		Objects.requireNonNull(site);
		if (getMin().getWebsite().equals(site))
			return getMin();
		if (getMax().getWebsite().equals(site))
			return getMax();
		throw new NoSuchElementException("Unknown site for this "+getClass()+": "+site);
	}
	
	public WebsitePair sites() {
		return new WebsitePair(getMin().getWebsite(), getMax().getWebsite());
	}
	
	public double getSimilarity() {
		return this.similarity;
	}

	public int getUsage() {
		return this.usage;
	}

	public void setUsage(int usage) {
		this.usage = usage;
	}

	@Override
	public int compareTo(PageLinkage that) {
		/* descending similarity order */
		int result = Double.compare(that.getSimilarity(), this.getSimilarity());
		/* decreasing usage order */
		if (result==0) result = Integer.compare(that.getUsage(), this.getUsage());
		/* force a total ordering */
		if (result==0) result = compare(this,that);
		return result;
	}
	
	@Override
	public String toString() {
		return super.toString()+ " sim=" + thousandth.format(this.getSimilarity());
				/*+ "\tusage=" + this.getUsage()*/
	}
	
}