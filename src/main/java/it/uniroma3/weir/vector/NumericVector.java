package it.uniroma3.weir.vector;

import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.value.Value;

import java.io.Serializable;
import static java.lang.Double.NaN;
import static java.lang.Math.sqrt;
/**
 * A specialized {@link Vector} hosting only numeric values as elements.
 * <BR/>
 * The distance between two vectors is computed by normalizing them:
 * first the vectors are translated of their average value to depart 
 * from the origin, and second their scaled of the standard deviation.
 * 
 * @see DistanceFunction
 */
public class NumericVector extends Vector implements Serializable {
	
	static final private long serialVersionUID = -6196171004532402669L;
	
	/* store a few aggregate measures */
	private double sum;
	private double avg;
	private double std;
	
	private double mod;          /* the magnitude of the vector as it is     */
	
	private double normMod;      /* magnitude of the vector once normalized  */
	
	private double sumOfSquares;
	
	private double transSquares; /* sum of squares once the vector has been normalized */
	
	private int nonNullCounter;
	
	public NumericVector(ExtractedVector original, Type type, String...values) { 
		super(original,type,values);
		this.avg = computeAverage();
		this.computeAggregates(this.avg);
	}

	private NumericVector(NumericVector that) {
		super(that);
		this.avg = that.avg;
		this.std = that.std;
		this.mod = that.mod;
		this.normMod = that.normMod;
		this.sumOfSquares = that.sumOfSquares;
		this.transSquares = that.transSquares;
		this.nonNullCounter = that.nonNullCounter;
	}

	@Override
	public NumericVector copy() {
		// N.B. without labels
		return new NumericVector(this);
	}
	
	private double computeAverage() {
		this.sum = 0;
		int n = 0;
		for(Value element : this.getElements()) {
			if (element.isNull()) continue;
			n++;
			this.sum += element.getNumericValue();
		}
		this.nonNullCounter = n;
		return ( n>0 ? this.sum / n : NaN);
	}
	
	private void computeAggregates(double average) {
		this.sumOfSquares = 0;
		for(Value element : this.getElements()) {
			if (element.isNull()) continue;
			final double d = element.getNumericValue();
			this.sumOfSquares += d * d;
			this.transSquares += (d-average)*(d-average);
		}
		final int n = this.countNonNulls();
		/* standard deviation */
		this.std   = ( n>0 ? sqrt(this.transSquares / n) : NaN);
		
		/* magnitude / modulo */
		this.mod = ( n>0 ? sqrt(this.sumOfSquares) : NaN);
		
		/* normalized magnitude */
		this.normMod = ( n>0 ? sqrt(this.transSquares) / this.std  : NaN );
	}

	public double getAvg()     { return this.avg; }
	
	public double getStd()     { return this.std; }

	public double getModulo()  { return this.mod; }
	
	public double getNormMod() { return this.normMod; }

	@Override
	public int countNonNulls() { return this.nonNullCounter; }

}
