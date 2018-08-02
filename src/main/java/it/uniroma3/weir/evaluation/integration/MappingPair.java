package it.uniroma3.weir.evaluation.integration;

import it.uniroma3.weir.evaluation.PRF;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.structures.Pair;

public class MappingPair extends Pair<Mapping> implements Comparable<MappingPair> {

	static final private long serialVersionUID = -7001725701211070081L;

	final private PRF score;

	public MappingPair(Mapping a, Mapping b, PRF score) {
		super(a, b);
		this.score = score;
	}

	public PRF getScore() {
		return this.score;
	}

	@Override
	public int compareTo(MappingPair that) {
		/* descending order according to F-measure */
		final double fThis = this.getScore().getFMeasure();
		final double fThat = that.getScore().getFMeasure();
		if (fThis!=fThat)
			return -Double.compare(fThis, fThat);
		
		/* then, descending order wrt the number of true positives */
		final int thisTP = this.getScore().getTruePositives();
		final int thatTP = that.getScore().getTruePositives();
		if (thisTP!=thatTP)
			return -Integer.compare(thisTP,thatTP);
		
		/* then, ascending order wrt the number of false negatives */
		final int thisFN = this.getScore().getFalseNegatives();
		final int thatFN = that.getScore().getFalseNegatives();
		if (thisFN!=thatFN)
			return +Integer.compare(thisFN,thatFN);
		
		/* finally, descending order wrt the number of false positives */
		final int thisFP = this.getScore().getFalsePositives();
		final int thatFP = that.getScore().getFalsePositives();
		return -Integer.compare(thisFP,thatFP);		
	}
	
}
