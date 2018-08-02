package it.uniroma3.weir.linking.linkage;

import it.uniroma3.weir.integration.AttributePair;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.structures.Pair;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.value.Value;

import java.util.Iterator;

/** 
 * A specialized {@link PairIterator&lt;Value&gt;} implementation that returns
 * weighted pair of {@link Value}s from two {@link Attribute}s according to a
 * list of weighted {@link PageLinkage}s stored into a {@link WebsiteLinkage} 
 * object provided at construction time.
 * <BR/>
 * It stores the current pair during iterator, i.e., the last pair of values 
 * returned by {@linkplain PairValueIterator#next()}.
 * 
 * <BR/>
 * These values are also retrievable by means of these getters methods:
 * {@linkplain PageLinkageIterator#getMinValue()}, 
 * {@linkplain PageLinkageIterator#getMaxValue()}, and the corresponding
 * pair-weight is returned by {@linkplain PageLinkageIterator#getWeight()}.
 * <BR/>
 * Notice that which value is <em>min</em> and which one is <em>max</em> is
 * set by the input {@link AttributePair} rather than by their ordering
 * in the {@link Pair&lt;Value&gt;} object built.
 * 
 */
public class PageLinkageIterator extends PairIterator<Value> {

	//For legacy code and DSCD coupling FIXME
	static final public PageLinkageIterator pairwiseIterator(Vector v1, Vector v2) {
		return new PageLinkageIterator(v1, v2, getLinkage(v1, v2));
	}
	
	static final private WebsiteLinkage getLinkage(Vector v1, Vector v2) {
		final Website s1 = v1.getWebsite();
		final Website s2 = v2.getWebsite();
		return Experiment.getInstance().getLinkages().get(s1, s2);
	}
	
	final private WebsiteLinkage siteLinkage;
		
	final private Website minSite;
	final private Website maxSite;

	final private Iterator<PageLinkage> pageLinkageIterator;

	final private Vector minVector;
	final private Vector maxVector;
	
	// current stuff
	private PageLinkage currentPageLinkage;

	private Value minValue;
	private Value maxValue;
	
	private double weight;
	
	/**
	 * MGC
	 * @param pair - the pair of {@link Attribute}s whose values are iterated
	 * @param sl - the {@link WebsiteLinkage} linking their {@link Webpage}s
	 * @param origVsNorm - true iff iterates over original 
	 *                     (i.e., string values as extracted)
	 *				       or normalized values (i.e., values 
	 *				       as produced by the normalization)
	 */
	public PageLinkageIterator(AttributePair pair, WebsiteLinkage sl, boolean origVsNorm) {
		this(getVector(pair.getMin(), origVsNorm), getVector(pair.getMax(), origVsNorm), sl);
	}

	public PageLinkageIterator(AttributePair pair, WebsiteLinkage sl) {
		this(pair, sl, false);
	}
	
	public PageLinkageIterator(Vector min, Vector max, WebsiteLinkage sl) {
		this.minVector = min;
		this.maxVector = max;
		this.minSite = this.minVector.getWebsite();
		this.maxSite = this.maxVector.getWebsite();
		this.siteLinkage = sl;

		this.pageLinkageIterator = sl.getPageLinkages().getAllLinkages().iterator();
		
		this.currentPageLinkage = null;
		this.minValue = null;
		this.maxValue = null;
		this.weight = 1d;
	}
	
	/**
	 * @param a - an {@link Attribute}
	 * @param originalOrNormalized true iff it selects the vector underlying an
	 *        {@link Attribute} or its the vector of original extracted values
	 */
	static final private Vector getVector(Attribute a, boolean originalOrNormalized) {
		return ( originalOrNormalized ? a.getVector().getOriginatingVector() : a.getVector() );
	}
	
	@Override
	public boolean hasNext() { return this.pageLinkageIterator.hasNext(); }

	@Override
	public Pair<Value> next() {
		// N.B.: min Vector is that from the min Website: same as for max Vector...
		this.currentPageLinkage = pageLinkageIterator.next();
		final Webpage minPage = this.currentPageLinkage.from(this.minSite);
		final Webpage maxPage = this.currentPageLinkage.from(this.maxSite);
		this.weight = this.currentPageLinkage.getSimilarity();
		this.minValue = this.minVector.get(minPage);
		this.maxValue = this.maxVector.get(maxPage);
		/* N.B. min and max as values from min and max as vectors, 
		 * once inserted into a pair of values could be reordered! */
		if (this.minValue==null || this.maxValue==null) return null;
		return new Pair<>(this.minValue, this.maxValue);
	}
	
	/**
	 * @return the last value from the min {@link Attribute}
	 */
	@Override
	public Value getMin() {	return this.minValue; }

	/**
	 * @return the last value from the max {@link Attribute}
	 */
	@Override
	public Value getMax() { return this.maxValue; }

	/**
	 * @return the {@link Vector} associated with the min {@link Attribute}
	 */
	public Vector getMinVector() { return this.minVector; }

	/**
	 * @return the {@link Vector} associated with the max {@link Attribute}
	 */
	public Vector getMaxVector() { return this.maxVector; }

	public PageLinkage getCurrentPageLinkage() {
		return this.currentPageLinkage;
	}
	
	public double getWeight() { 
		return this.weight;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName()+" over "+this.siteLinkage;
	}

}
