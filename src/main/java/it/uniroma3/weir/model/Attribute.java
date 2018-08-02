package it.uniroma3.weir.model;

import static it.uniroma3.hlog.HypertextualUtils.styled;
import static it.uniroma3.weir.model.log.WeirStyles.getCSSclass;
//import it.uniroma3.hlog.render.Renderable;
import it.uniroma3.weir.MatchRepository.MatchWeighter;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.integration.AttributePair;
import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.linking.linkage.PageLinkageIterator;
import it.uniroma3.weir.structures.Pair2BooleanRepository;
import it.uniroma3.weir.structures.PairRepositories;
import it.uniroma3.weir.vector.Label;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.value.ExtractedValue;
import it.uniroma3.weir.vector.value.Value;

import java.io.Serializable;
import java.util.*;


/**
 * A <em>source</em> (was: <em>physical</em>) attribute from a {@link Website}.
 * It contains a {@link Vector} of {@link Value}s as extracted from
 * the {@link Webpage}s of the site.
 */
public class Attribute extends WeirId implements Serializable/*, Renderable */ {

	static final private long serialVersionUID = -8690221131140807617L;
	
	static final public Comparator<Attribute> COMPARATOR_BY_SITE = new Comparator<Attribute>() {
		@Override
		public int compare(Attribute a1, Attribute a2) {
			return a1.getWebsite().compareTo(a2.getWebsite());
		}
	};

	private Website website;

	private Vector vector;

	private Match goldenMatch;

	private boolean correct; // marked as correct
	
	public Attribute(Vector vector) {
		super(nextIdByClass(Attribute.class));
		this.vector = vector;
		this.goldenMatch = null;
		this.correct = false;
	}

	public Vector getVector() {
		return this.vector;
	}

	public void setWebsite(Website website) {
		this.website = website;
	}

	public Website getWebsite() {
		return this.website;
	}
	
	public Value get(Webpage page) {
		return this.getVector().get(page);
	}

	/**
	 * Set whether this is the best attribute matching with a golden attribute,
	 * i.e., it is the target output attribute at the lowest distance from the 
	 * golden of a certain {@link Website} 
	 * @param golden - the golden attribute this attribute matches with
	 */
	public void setAsTarget(Match best) {
		final Attribute golden = best.getMate(this);
		if (!this.sameWebsiteAs(golden))
			throw new IllegalArgumentException("target and golden must be from the same site!");
		this.goldenMatch = best;
	}

	public boolean isTarget() {
		return this.goldenMatch!=null;
	}

	public void markAsCorrect() {
		this.correct = true;
	}
	
	public boolean isCorrect() {
		return this.correct;
	}
	
	/**
	 * @return the {@link Match} with the golden {@link Attribute}
	 * 		   with this attribute if any, null otherwise
	 */
	public Match getGoldenMatching() {
		return this.goldenMatch;
	}
	
	public boolean sameWebsiteAs(Attribute that) {
		return this.getWebsite().equals(that.getWebsite());
	}

	/**
	 * Returns the distance to another attribute taking into account
	 * the linkage between the two involved websites, i.e., the two
	 * websites this attribute and the other come from.
	 * @param that the other attribute to compute the distance to
	 * @return
	 */
	public double distance(Attribute that) {
		final AttributePair pair = new AttributePair(this, that);
		return pair.distance();
	}


	/**
	 * @param candidates 
	 * @return the closest attribute among a set of candidates, 
	 *         or <i>null</i> if the set of candidates is empty.
	 */
	public Match findClosest(Collection<Attribute> candidates) {
		if (candidates.isEmpty()) return null;
		final Attribute pivot = this;
		final Attribute min = Collections.min(candidates, 
				PairRepositories.pivotedComparator(pivot, new MatchWeighter())
		);
		return new Match(pivot, min);
	}
	
	/** 
	 * @ return an id that follows PVLDB paper convention 
	 * */
	public String getID() {
		return this.getId() + "_" + this.getWebsite().getIndex();
	}

	public ExtractionRule getExtractionRule() {
		return getVector().getOriginatingVector().getExtractionRule();
	}

	public Set<Label> getLabels() {
		return this.getVector().getLabels();
	}

	public String getFirstLabel() {
		if (this.getLabels().isEmpty()) return null;
		return this.getLabels().iterator().next().getLabel();
	}
	
	public int size() {
		return getVector().size();
	}


	public boolean overlap(Attribute that) {
		// it does not overlap with itself, by definition
		if (this.equals(that)) return false;
		
		// attributes from different sites cannot overlap
		if (!this.sameWebsiteAs(that)) return false;

		final Pair2BooleanRepository<AttributePair, Attribute> cache = 
				Experiment.getInstance().getOverlapCache();
		
		Boolean result = cache.get(this, that);
		if (result!=null) return result; /* cache hit */
		
		/* cache miss :( */
		final AttributePair pair = new AttributePair(this, that);		
		
		/* iterate over values as originally extracted from the pages */
		final PageLinkageIterator itExt = pair.valuesAsExtractedIterator();
		/* iterate over values as derived from the extracted strings  */
		final PageLinkageIterator itVal = pair.iterator();
		
		while (itExt.hasNext()) {
			// cross next pair of linked ExtractedValue s
			itExt.next();
			itVal.next();
			final Value value1 = itVal.getMin();
			final Value value2 = itVal.getMax();
			final ExtractedValue extValue1 = (ExtractedValue) itExt.getMin();
			final ExtractedValue extValue2 = (ExtractedValue) itExt.getMax();

			if (extValue1==null || extValue2==null) continue;
			if (extValue1.isNull() || extValue2.isNull()) continue;

			final String mark1 = extValue1.getOccurrenceMark();
			final String mark2 = extValue2.getOccurrenceMark();

			if (mark1==null || mark2==null) continue;
			/* same PCDATA and same values extracted from it ? */
			if (mark1.equals(mark2) && value1.equals(value2)) {
				return cache.cache(pair, true);
			}
		}
		
		return cache.cache(pair, false);
	}

	@Override
	public String getWeirId() {
		final int siteId = this.getWebsite().getIndex();
		return super.getWeirId()+"<sup>" + siteId + "</sup>";
	}
	
	//@Override
	public String toHTMLstring() {
		final StringBuilder result = new StringBuilder(40);
//		if (this.isCorrect()) result.append("<sup>&odot;</sup>");
		if (this.isTarget()) result.append("<sup>&oast;</sup>");
		result.append(this.getWeirId());
		return styled(getCSSclass(this), result.toString()).toString();
	}

	@Override
	public String toString() {
		return this.toHTMLstring();
	}
		
}
