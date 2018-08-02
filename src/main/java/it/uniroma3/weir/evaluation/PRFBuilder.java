package it.uniroma3.weir.evaluation;

import java.util.NoSuchElementException;

/**
 * A facility class to build a {@link PRF} by aggregating a sequence of 
 * partial results.
 * 
 * @see {@link PRF}
 * @see {@link RepositoryEvaluator}
 */
/* TODO 
 * remove methods not relying on a given repository */
/* i.e., these methods can be moved towards Repositories hosting the
 * data to perform the needed aggregated evaluations 
 */
public class PRFBuilder {

	private double sumOfP;   // accumulator of a sum of precision values
	private double sumOfR;   // accumulator of a sum of recall    values
	
	private int sumOfTP = 0; // total number of golden found in the output
	private int sumOfFN = 0; // total number of golden not found in the output
	private int sumOfFP = 0; // total number of output not expected
	
	private int length  = 0; // the length of the sequence of values
							 // that has been sum-up/averaged so far
	
	public PRFBuilder() {
		this.reset();
	}
	
	public void add(PRF er) {
		add(er.getPrecision(), 
			er.getRecall(),
			er.getTruePositives(), 
			er.getFalseNegatives(), 
			er.getFalsePositives());
	}
		
	public void addPR(double p, double r) {
		this.sumOfP   += p;
		this.sumOfR   += r;		
		this.length++; // we're averaging over several P/R
	}
	
	public void addCounters(int tp, int fn, int fp) {
		this.sumOfTP += tp  ;
		this.sumOfFN += fn ;
		this.sumOfFP += fp ;
		// we're accumulating (not averaging) over several values
	}

	public void add(double p, double r, int tp, int fn, int fp) {
		this.addPR(p, r);
		this.addCounters(tp, fn, fp);
	}
	
	public void reset() {
		this.sumOfP  = 0d;
		this.sumOfR  = 0d;
		this.sumOfTP = 0;
		this.sumOfFN = 0;
		this.sumOfFP = 0;
		this.length  = 0;
	}
	
	/**
	 * @return the length of the sequence of results that has been accumulated so far
	 */
	protected int getLength() {
		return length;
	}

	public PRF getResult() {
		if (this.length==0)
			return new PRF(0,0);
		return this.getResultOver(this.length);
	}
	
	public PRF getResultOver(int size) {
		if (size==0) 
			throw new NoSuchElementException("Cannot average over an empty sequence");
			
		final double avg_p = ( this.sumOfP / size );
		final double avg_r = ( this.sumOfR / size );
		
		return new PRF(avg_p, avg_r, sumOfTP, sumOfFN, sumOfFP, size);		
	}
		
}