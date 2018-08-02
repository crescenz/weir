package it.uniroma3.weir.integration;

import static it.uniroma3.hlog.HypertextualUtils.format;
import static it.uniroma3.weir.Formats.thousandth;
import static it.uniroma3.weir.configuration.Constants.MIN_OVERLAPPING_SAMPLES;
import static java.lang.Double.POSITIVE_INFINITY;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.structures.Pair;
import it.uniroma3.weir.vector.value.Value;

import java.io.Serializable;
// * VC: this could be another WeightedPair; CHECK
public class Match extends AttributePair 
			       implements Comparable<Match>, Iterable<Pair<Value>>, Serializable {

	static final private long serialVersionUID = 7249565290440373278L;
		
	static final private int minLinkageSize = WeirConfig.getInteger(MIN_OVERLAPPING_SAMPLES);

	private double distance;

	public Match(Attribute a1, Attribute a2, double d) {
		super(a1, a2);
		// TODO Used only by MinimumExtractionEvaluator: remove and merge the two constructors?
		this.distance = d;
	}

	public Match(AttributePair pair, double d) {
		this(pair.getMin(), pair.getMax(), d);
	}
	
	public Match(Attribute a1, Attribute a2) {
		this(a1, a2, -1);
		this.distance = this.initDistance();
	}
	
	@Override
	public double distance() {
		return this.distance;
	}
	
	private double initDistance() {
		final WebsiteLinkage linkage = getLinkage();
		
		/* not enough overlap ? */
		return ( linkage.size()<minLinkageSize ? POSITIVE_INFINITY : super.distance() );
	}
	
	@Override
	public int compareTo(Match that) {
		return Double.compare(this.distance(), that.distance());
	}
	
	@Override
	public String toString() {
		return 
		  "(" + this.getMin() + "," + this.getMax() + ")&nbsp;"+
		   "d="+format(thousandth,this.distance()) ;
	}

}
