package it.uniroma3.weir.integration;

import it.uniroma3.weir.linking.linkage.PageLinkageIterator;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.structures.Pair;
import it.uniroma3.weir.vector.DistanceSupport;
import it.uniroma3.weir.vector.value.Value;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;


public class AttributePair extends Pair<Attribute> 
						   implements Iterable<Pair<Value>>, Serializable {

	static final private long serialVersionUID = -2759095271433013713L;

	public AttributePair(Attribute a, Attribute b) {
		super(a, b);
	}

	public WebsiteLinkage getLinkage() {
		final Website s1 = getMin().getWebsite();
		final Website s2 = getMax().getWebsite();
		return Experiment.getInstance().getLinkages().get(s1, s2);
	}

	public PageLinkageIterator iterator() {
		return valuesIterator(this.getLinkage());
	}

	public PageLinkageIterator valuesIterator(WebsiteLinkage wl) {		
		return new PageLinkageIterator(this, wl, false);
	}

	public PageLinkageIterator valuesAsExtractedIterator() {		
		return new PageLinkageIterator(this, this.getLinkage(), true);
	}

	public Attribute from(Website site) {
		Objects.requireNonNull(site);
		if (getMin().getWebsite().equals(site))
			return getMin();
		if (getMax().getWebsite().equals(site))
			return getMax();
		throw new NoSuchElementException("Unknown site for this pair of attributes: "+site);
	}
	
	/**
	 * The distance between the two attributes by taking
	 * into account all the linkages and their weight.
	 * Use the {@link WebsiteLinkage} known at domain level.
	 * 
	 * @return the distance between the two attributes
	 */
	public double distance() {		
		return distance(this.getLinkage());
	}

	/**
	 * The distance between the two attributes by taking
	 * into account all the linkages and their weight
	 * @param wl the linkages to consider
	 * @return the distance between the two attributes
	 */
	public double distance(WebsiteLinkage wl) {		
		return DistanceSupport.distance(valuesIterator(wl));
	}

}