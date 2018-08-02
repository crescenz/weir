package it.uniroma3.weir.vector.value;

import static java.lang.Math.abs;

import com.wcohen.ss.UnsmoothedJS;
import com.wcohen.ss.api.StringDistance;

public enum ValueDistances {
	
	HAMMING {
		@Override
		public double distance(Object value1, Object value2) {
			return value1.equals(value2) ? 0.0 : 1.0;
		}
	},

	JSDISTANCE {
		@Override
		public double distance(Object value1, Object value2) {
			final double score = JS_DISTANCE.score(value1.toString(), value2.toString());
			return (score > 1d ? 0d : 1d - score );
		}
	},

	DATE_DISTANCE {
		@Override
		public double distance(Object value1, Object value2) {
			return dateDistance(value1, value2);
		}
	},

	NUMBER_DISTANCE {
		@Override
		public double distance(Object value1, Object value2) {
			return numberDistance(value1, value2);
		}

	},

	DIMENSIONAL_DISTANCE {
		@Override
		public double distance(Object value1, Object value2) {
			return dimensionalDistance(value1, value2);
		}

	},
	
	ISBN_DISTANCE {
		@Override
		public double distance(Object value1, Object value2) {
			return isbnDistance(value1, value2);
		}
	};

	abstract public double distance(Object value1, Object value2);

	static final private StringDistance JS_DISTANCE = new UnsmoothedJS();

//	static final public double dateDistance(Object value1, Object value2) {
//		final Date date1 = (Date)value1, date2 = (Date)value2;
//
//		return Math.max(1d,
//			   hammingDistanceOnDefined(date1.getYear(),  date2.getYear()) +
//			   hammingDistanceOnDefined(date1.getMonth(), date2.getMonth()) +
//			   hammingDistanceOnDefined(date1.getDay(),   date2.getDay())
//		) ;
//	}

	static final public double dateDistance(Object value1, Object value2) {
		final Date date1 = (Date)value1, date2 = (Date)value2;
	    final int _1970_ = 1970;
		final long d1 = date1.daysFromYear(_1970_);
		final long d2 = date2.daysFromYear(_1970_);
	    return canberraDistance(d1, d2);
	}

	
	static final public double hammingDistanceOnDefined(int v1, int v2) {
		// 0 distance if at least a value is not defined
		return (v1==-1||v2==-1) ? 0 : HAMMING.distance(v1, v2);		
	}
	
	static final public double numberDistance(Object value1, Object value2) {
		final double d1 = ((Number)value1).getValue();
		final double d2 = ((Number)value2).getValue();

		return magnitudeRatio(d1, d2);
	}

	static final public double dimensionalDistance(Object value1, Object value2) {
		final Dimensional dn1 = (Dimensional)value1;
		final Dimensional dn2 = (Dimensional)value2;

		if (!dn1.getMarker().equals(dn2.getMarker())) return 1d;

		double d1 = dn1.getValue();
		double d2 = dn2.getValue();
		return magnitudeRatio(d1, d2);
	}

	static final private double magnitudeRatio(double d1, double d2) {
		if (!(d1 > 0 && d2 > 0)) {
			double d = Math.abs(Math.min(d1, d2)) + 1;
			d1 += d;
			d2 += d;
		}

		return Math.abs((d1 - d2) / (d1 + d2));
	}	

	static final private double canberraDistance(double d1, double d2) {
		if (d1==0d && d2==0d) return 0d;
		if (d1==0d || d2==0d) return 1d;
		return abs(d2-d1) / ( abs(d2)+abs(d1) ); // 0<d<1
	}

	static final private double isbnDistance(Object v1, Object v2) {
		final String isbn1 = (String)v1;
		final String isbn2 = (String)v2;
		if ((isbn1.length()!=10 && isbn1.length()!=13) || 
			(isbn2.length()!=10 && isbn2.length()!=13)) 
			return JSDISTANCE.distance(isbn1, isbn2);
		return HAMMING.distance(isbn1, isbn2);
//		return HAMMING.distance(last10digits(isbn1), last10digits(isbn2));
	}
	
//	static final private String last10digits(String s) {
//		if (s.length()<10) return s;
//		return s.substring(s.length()-10);
//	}

}
