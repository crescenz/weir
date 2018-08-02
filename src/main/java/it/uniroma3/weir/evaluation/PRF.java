package it.uniroma3.weir.evaluation;

import static it.uniroma3.weir.Formats.percentage;

/**
 * Model the results of any experimental evaluation in which there
 * are <em>n</em> expected/golden elements and <em>m</em> 
 * actual/output <em>m</em> elements.
 * <br/>
 * The results are measured according to standard P/R/F metrics
 * and/or simple counters with the following semantics:
 * <ul><li><em>expected found:</em> 
 *  			number of golden elements found in the output (true positive)
 *     </li>
 *     <li><em>expected but not found:</em> 
 *      		number of golden elements not found in the output (false negative)
 *     </li>
 *     <li><em>found but not expected:</em> 
 *      		number of output elements not expected in the golden (false positive)
 *     </li>    
 * </ul>
 * Another (optional) counter is the number of cases evaluated to build this result
 * {@linkplain #getN()}.
 * <br/>
 * This is class is supposed to be used for evaluating different 
 * experiments with several kinds of results such as <em>linkages</em>, 
 * <em>matches</em>, <em>extraction rules</em>, <em>mappings</em>, etc., etc. 
 */
public class PRF {

	static final private double UNSET = Double.NaN;

	private double _P_ = UNSET; // Precision
	private double _R_ = UNSET; // Recall
	private double _F_ = UNSET; // F-measure

	private int tp = 0; // number of golden found in the output ; true positives
	private int fn = 0; // number of golden not found in the output ; false negatives
	private int fp = 0; // number of output not expected ; false positives
//	private int tn = 0; // number of output not found and not expected ; true negatives
						// (usually not considered)
	
	private int n = 0;  // number of cases evaluated to get this result
	                    // n = tp + fn + fp + tn 
	
	public PRF(double p, double r) {
		this(p,r,0,0,0);
	}
	
	public PRF(int tp, int fn, int fp) {
		this((double)tp / ( tp + fp ), (double)tp / ( tp + fn ), tp, fn, fp);
		this._F_ = computeF();
	}
	
	public PRF(double p, double r, int tp, int fn, int fp) {
		this(p,r,tp,fn,fp,0);
	}

	/* MGC */
	public PRF(double p, double r, int tp, int fn, int fp, int n) {
		this._P_ = p;
		this._R_ = r;
		this._F_ = computeF();
		this.tp = tp;
		this.fn = fn;
		this.fp = fp;
		this.n = n;
	}

	private double computeF() {
		if (this._P_ == 0d && this._R_ == 0d) return 0d;
		return 2 * this._P_ * this._R_ / (this._P_ + this._R_);
	}
	
	public double getPrecision() {
		return this._P_;
	}

	public double getRecall() {
		return this._R_;
	}

	public double getFMeasure() {
		return this._F_;
	}

	/**
	 * @return total number of golden elements found in the output
	 */
	public int getTruePositives() {
		return this.tp;
	}

	/**
	 * @return total number of golden elements not found in the output
	 */
	public int getFalseNegatives() {
		return this.fn;
	}

	/**
	 * @return total number of output elements not expected in the golden
	 */
	public int getFalsePositives() {
		return this.fp;
	}
	
	/**
	 * @return total number of evaluated cases to form this result
	 */
	public int getN() {
		return this.n;
	}
	
	private boolean arePRFSet() {
		return !(this._P_==UNSET && this._R_==UNSET && this._F_==UNSET);
	}

	private boolean areCountersSet() {
		return !(this.tp==0 && this.fn==0 && this.fp==0);
	}
	
	public String toStringPRF() {
		return "P=" + percentage.format(this._P_) + ";\t" +
			   "R=" + percentage.format(this._R_) + ";\t" +
			   "F=" + percentage.format(this._F_) + ";\t" ;
	}
	
	public String toStringCounters() {
		return "TP=" + this.tp + ";\t" +
			   "FN=" + this.fn + ";\t" +
			   "FP=" + this.fp ;
	}

	@Override
	public String toString() {
		return (arePRFSet() 	 ? toStringPRF() 	  : "") + 
			   (areCountersSet() ? toStringCounters() : "");
	}

}