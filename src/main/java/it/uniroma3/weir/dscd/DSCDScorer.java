package it.uniroma3.weir.dscd;

public interface DSCDScorer<R,C> {
	abstract public double score(R row, C col);		
}