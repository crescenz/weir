package it.uniroma3.weir.vector;

import static it.uniroma3.weir.configuration.Constants.ONES2ONE;
import static it.uniroma3.weir.vector.value.Value.nonNullsTypedDistance;
import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.*;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.linking.linkage.PageLinkageIterator;
import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.value.Value;
/** 
 * This enums keeps all the behavior needed to compute a several 
 * kinds of distance functions over a {@link Vector} of typed values
 */
// http://numerics.mathdotnet.com/Distance.html
public enum DistanceFunction {
	
	MEAN() { // mean over distance values (MAE)
		
		@Override
		public double accumulate(double d) { return d;       }
		
		@Override
		public double distanceNonNulls(Value min, Value max) {
			return nonNullsTypedDistance(type, min, max);
		}

	},
	MSE() {  // mean over squared distance values
		@Override
		public double accumulate(double d) { return d * d;   }
		
		@Override
		public double distanceNonNulls(Value min, Value max) {
			return nonNullsTypedDistance(type, min, max);
		}

	},
//	HAMMING(), // mean number of equal values
//	COSINE(),     /* ONLY FOR NUMBERS */
	EUCLIDEAN() { /* ONLY FOR NUMBERS */
		/* The Normalized Euclidean Distance is */
		/* invariant to translation and scaling */
		/* https://en.wikipedia.org/wiki/Mahalanobis_distance
		 * Mahalanobis distance is preserved under full-rank 
		 * linear transformations of the space spanned by the data */
		
		private NumericVector min;
		private NumericVector max;

		@Override
		protected double accumulate(double d) {	return d*d;     }

		@Override
		protected double distanceNonNulls(Value min, Value max) {
			return distanceNonNulls(min.getNumericValue(), max.getNumericValue());
		}
		
		protected double distanceNonNulls(double vMin, double vMax) {
			final double avgMin = this.min.getAvg(), stdMin = this.min.getStd();
			final double avgMax = this.max.getAvg(), stdMax = this.max.getStd();
			final double _min_ = normalize(vMin, avgMin, stdMin);
			final double _max_ = normalize(vMax, avgMax, stdMax);
			return _max_ - _min_ ;
		}
		
		final private double normalize(double d, double avg, double std) {
			if (std==0) return NaN; // n.b.: constant vectors are turned into 0-vectors...
			/* this yields to unit-length vectors but then the distance would 
			 * not be invariant to scaling and translation from the average
			 */
/*			return ( d - avg ) / (  m  ) ;      */
			/* this does not yields unit-length vectors but the distance
			 * is invariant to scaling and translation from the average
			 */
			return ( d - avg ) / ( std ) ; // this does not lead to unit-length vectors
		}
		
		public void initFor(PageLinkageIterator it) {
			super.initFor(it);
			this.min = (NumericVector)it.getMinVector();
			this.max = (NumericVector)it.getMaxVector();
		}
		
		public double distance() {
			final double magMin = this.min.getNormMod();
			final double magMax = this.max.getNormMod();
			// distance of two all-nulls vectors = + inf
			return this.sumOfWeights==0 ? POSITIVE_INFINITY : 
				   sqrt ( this.acc / this.sumOfWeights ) / ( magMin * magMax );
		}
		
	}, 
	MINKOWSKI() { /* ONLY FOR NUMBERS */
		@Override
		protected double accumulate(double d) {	return d; }
		
		@Override
		protected double distanceNonNulls(Value min, Value max) {
			return distanceNonNulls(min.getNumericValue(), max.getNumericValue());
		}

		static final private double p = 2;
		protected double distanceNonNulls(double min, double max) {
			return pow ( pow(min,p) + pow(max,p), 1/p );
		}

	},
	CANBERRA() {  /* ONLY FOR NUMBERS */ //looks like the legacy one!
		@Override
		protected double accumulate(double d) {	return d; }

		@Override
		protected double distanceNonNulls(Value min, Value max) {
			return distanceNonNulls(min.getNumericValue(), max.getNumericValue());
		}
		
		protected double distanceNonNulls(double min, double max) {
			if (min==0d && max==0d) return 0d;
			if (min==0d || max==0d) return 1d;
			return abs(max-min) / ( abs(max)+abs(min) ); // 0<d<1
		}
	
	};

	static private int onesThreshold = WeirConfig.getInteger(ONES2ONE);
	
	protected Type type;           /* type of the vector */

	protected double acc;          /* Weighted sums accumulator */
	
	protected double sumOfWeights; /* sum of weights of the value pairs processed */
		
	public void init(Type type) {
		this.type = type;
		this.acc = 0d;
		this.sumOfWeights = 0d;
	}
	
	public void initFor(PageLinkageIterator it) {
		this.acc = 0d;
		this.sumOfWeights = 0d;		
	}
	
	/**
	 * The weighted distance between the two vectors by taking 
	 * into account all the linkages and their weight as returned 
	 * by a {@link PageLinkageIterator} object
	 * @param it - 
	 * @return the weighted distance
	 */
	public double distance(PageLinkageIterator it) {
		this.initFor(it);
		int counter_of_1s = 0;
		while (it.hasNext()) {
			it.next(); // cross next Pair of values with their weight
						
			final Value min = it.getMin(), max = it.getMax();

			if (min==null || max==null) continue;
			// ignore this pair of null-values			
			if (min.isNull() && max.isNull()) continue; // CHECK why not 0d?

			final double weight = it.getWeight();
			
			this.sumOfWeights += weight; // accumulate weights
			
			final double d = this.distance(min, max);
			
			if (d>=1) ++counter_of_1s;
			
			if (counter_of_1s==onesThreshold) 
				return POSITIVE_INFINITY;
			
			this.acc += weight * this.accumulate(d);
		}
		return this.distance();
	}
	
	abstract protected double accumulate(double d);

	public double distance(Value min, Value max) {
		if (min.isNull() || max.isNull())
			/* compute over a pair with one null value */
			return distanceOneNull(min, max);		
		else 
			/* compute over a  pair of non null values */
			return distanceNonNulls(min, max);
	}

	/**
	 * Pairwise distance between two values which are both not nulls
	 * @param min
	 * @param max
	 */
	protected double distanceNonNulls(Value min, Value max) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Pairwise distance between two values one of which is null.
	 * Default behavior is to use max value distance, i.e., 1.0
	 * @param min
	 * @param max
	 */
	protected double distanceOneNull(Value min, Value max) {
		return NULL_VS_NON_NULL_PAIWISE_DISTANCE; 
	}

	/**
	 * Wrap-up.
	 * @return the final distance value.
	 */
	public double distance() {
		// distance of two all-nulls vectors = + inf
		return ( this.sumOfWeights==0  ? POSITIVE_INFINITY : this.acc / sumOfWeights );
	}

	static public double NULL_VS_NON_NULL_PAIWISE_DISTANCE = 1d; // max value
	
}