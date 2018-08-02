package it.uniroma3.weir.vector;

import static java.lang.Math.*;
import static java.util.Objects.requireNonNull;
import it.uniroma3.weir.linking.linkage.PageLinkageIterator;
import it.uniroma3.weir.vector.type.DimensionalType;
import it.uniroma3.weir.vector.type.NumberType;
import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.unitmeasure.UnitMeasure;
import it.uniroma3.weir.vector.unitmeasure.UnitMeasureGroup;
import static java.lang.Double.POSITIVE_INFINITY;

public class DistanceSupport {

	/**
	 * The weighted distance between the two vectors by taking 
	 * into account all the linkages and their weight as returned 
	 * by a {@link PairValueIterator} object
	 * @param it - 
	 * @return the weighted distance
	 */
	static public double distance(PageLinkageIterator it) {
		final Vector min = it.getMinVector();
		final Vector max = it.getMaxVector();
		final Type type = Type.getCommonAncestor(min.getType(), max.getType());
		requireNonNull(type,"The types hierarchy should be single rooted");
		
		if (isNumeric(type)) return numericDistance(type, it);
		else return type.createDistanceFunction().distance(it);
	}

	static private double numericDistance(Type commonType, PageLinkageIterator it) {
		final NumericVector min = (NumericVector)it.getMinVector();
		final NumericVector max = (NumericVector)it.getMaxVector();

		if (isDimensional(commonType)) {
			// dimensional values (with unit-measures) are involved
			if (!min.getType().isSubtypeOf(max.getType()) && 
				!max.getType().isSubtypeOf(min.getType())) { // e.g., mass vs money
				return POSITIVE_INFINITY;
			}
			if (min.getType().equals(max.getType())) {       // e.g., mass vs mass
				// Deal with different unit-measures for the same dimensional type?...
				// ...NO! the normalization should have already forced 
				//        both vectors to adopt the same reference unit
				return commonType.createDistanceFunction().distance(it)*canberraDistance(min.getNormMod(), max.getNormMod());
			}
		}
		return commonType.createDistanceFunction().distance(it);
//		else if (isDimensional(min.getType()) || isDimensional(max.getType())) {
//			//QUESTO DOVREBBE ESSERE IRRILEVANTE visto che la distanza numerica non dipende dal cambio scala
//			// one is dimensioned but the other is not e.g., mass vs numbers	
//			final DimensionalType dimType = (DimensionalType)
//					            ( isDimensional(min.getType()) ? min.getType() : max.getType() );
//			final double dimMag = isDimensional(min.getType()) ? min.getAvg() : max.getAvg() ;
//			final double undMag = isDimensional(max.getType()) ? min.getAvg() : max.getAvg() ; // was Magnitude...
//
//			// Try to guess the unit-of-measure of the undimensioned 
//			// vector among those of the dimensioned one by using 
//			// the ratio associated with the min distance
//			final UnitMeasureGroup measureGroup = dimType.getUnitMeasureGroup();
//			return minCanberraDistance(measureGroup,dimMag,undMag);
//		}// else both undimensioned... what to do with magnitudes???
//		return 0d;
	}
	
	static private boolean isNumeric(Type type) {
		return type instanceof NumberType;
	}

	static private boolean isDimensional(Type type) {
		return type instanceof DimensionalType;
	}

	static private double canberraDistance(double min, double max) {
		// N.B.: this is a Canberra distance between the vector magnitudes 
		if (min==0d && max==0d) return 0d;
		if (min==0d || max==0d) return 1d;
		return abs(max-min) / ( abs(max)+abs(min) ); // 0<d<1
	}

	@SuppressWarnings("unused")
	static private double minCanberraDistance(UnitMeasureGroup umg, double dimMag, double undMag) {
		double minDistance = 1d;
		for(UnitMeasure unit : umg.getUnitMeasures()) {
			final double d = canberraDistance(dimMag, undMag*unit.getRatio());
			if (d<minDistance) minDistance = d;
		}
		return minDistance;
	}	
	
}
