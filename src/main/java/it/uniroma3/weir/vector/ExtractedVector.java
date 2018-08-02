package it.uniroma3.weir.vector;

import it.uniroma3.weir.cache.Fingerprinted;

import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.value.ExtractedValue;
import it.uniroma3.weir.vector.value.GoldenValue;
import it.uniroma3.weir.vector.value.Value;

import java.io.Serializable;
import java.util.Arrays;
/**
 * 
 * A vector of {@link ExtractedValue}s as extracted from
 * a collection of {@link Webpage}s by an {@link ExtractionRule}
 * {@see ExtractionRule#applyTo(java.util.List<Webpage>)}
 */
public class ExtractedVector extends Vector 
                             implements Iterable<Value>, Serializable, Fingerprinted {
	
	static final private long serialVersionUID = 2389846799310849683L;

	static final private Normalizer normalizer = new Normalizer();

	/**
	 * Use {@link ExtractedVector} to mimic vectors of {@link GoldenValue}
	 * @param elements the golden values
	 * @return the golden vector
	 */
	static public Vector makeGoldenVector(final Website site, GoldenValue[] elements) {
		return new ExtractedVector(elements) {
			
			static final private long serialVersionUID = -2731280582231521086L;

			@Override
			public Website getWebsite() {
				return site;
			}
		}.normalize();
	}
	
	private ExtractionRule rule; // null only for golden vectors read from .csv files

	private ExtractedVector(ExtractedValue[] elements) {
		this(elements, null);
	}
	
	public ExtractedVector(ExtractedValue[] elements, ExtractionRule rule) {
		super(Type.rootType()); // ExtractedVector are STRINGs
		this.elements = elements;
		this.rule = rule;
		this.initIndexPage2Value(this.elements);
	}
	
	/**
	 * @return this vector of values
	 */
	@Override
	public ExtractedVector getOriginatingVector() {
		return this;
	}
	
	@Override
	public ExtractionRule getExtractionRule() {
		return this.rule;
	}

	@Override
	public Website getWebsite() {
		return this.getExtractionRule().getWebsite();
	}
	
	public Vector cast(Type type) {
		Vector result = tryCast(type);
		if (result==null) 
			throw new IllegalArgumentException(this + " cannot be casted to "+type);
		return result;
	}

	public Vector tryCast(Type type) {
		if (!isInstanceof(type)) return null;		
		return new Vector(this, type);
	}
	
	/**
	 * A normalized vector is a vector obtained from a {@link ExtractedVector} 
	 * by:
	 * - removing common prefix and/or suffix from all its values
	 * - removing "illegal chars" from the head&tail of the strings
	 *   (@see {@link Normalizer#isLegalChar})
	 * - casting to the most specific {@link Type} to which all of its
	 *   values are conforming
	 * @return a normalized version of this vector by cleaning texts,
	 *         removing prefixes, and suffixes, and by casting to
	 *         the most specific {@link Type}
	 */
	public Vector normalize() {
		return normalizer.normalize(this);
	}
	
	public ExtractedValue[] getElements() {
		return (ExtractedValue[]) super.getElements();
	}

	public ExtractedValue get(int index) {
		return (ExtractedValue) super.get(index);
	}
	
	/* was: */
//	public double getDistance(ExtractedVector v2) {
//		Type commonType = Type.getCommonAncestor(this.type, v2.getType());
//		if (commonType != null) {
// ...
//		}
//		return 1.0;
//	}

	// VC: Perché viene ancora chiamata pure non tenendo conto del linkage !?
	// perché si dava per scontato che fossero riarrangiati di modo che ci fosse sempre il linkage
	// Viene chiamata da LocalConsistencyEnforcer, ma lì si confrontano solo vettori dello stesso sito
	// Viene chiamata da OracleIntegrator, ma lì si confrontano solo con un vettore golden ed il linkage
	// non dovrebbe essere un problema
	// Stesso discorso per DomainAnalyzer

	@Override
	public String toString() {
		return "["+this.size()+"]"+"\t"+Arrays.toString(getElements());
	}

}
