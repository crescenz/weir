package it.uniroma3.weir.linking.entity;

import static it.uniroma3.weir.Formats.thousandth;
import static it.uniroma3.weir.configuration.Constants.*;
import static it.uniroma3.weir.vector.type.Type.*;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.linking.IdfRepository;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.value.Value;

import java.io.Serializable;
import java.util.*;

/**
 * 
 * By  <em>entity</em> we mean the information used to 
 * perform the alignment between two {@link Webpage}s
 * <br/>
 * Examples of possible entities are:
 * <ul>
 * <li> a <i>soft</i>-id
 * <li> a set of {@link Value}s extracted from a {@link Webpage} 
 * 	    by a set of {@link ExtractionRule}s
 * </ul>
 * 
 * @see <a href="http://dl.acm.org/citation.cfm?id=1247541">
 * <i>Query relaxation using malleable schemas</i></a>
 * <br/>
 * Xuan Zhou, Julien Gaugaz, Wolf-tilo Balke, Wolfgang Nejdl.<br/>
 * ACM SIGMOD 2007
 */
public class ValueEntity extends Entity implements Serializable {

	static final private long serialVersionUID = -7306263469330680478L;

	static final protected HypertextualLogger log = HypertextualLogger.getLogger();
	
	
	static final private Map<Type, Double> typeFactor;

	static {
		/** TODO move on file.txt property files */
		typeFactor = new HashMap<Type, Double>(){
			static final private long serialVersionUID = 1L; {
			put(STRING, 1.0);
			put(URL, 1.0);
			put(DATE, 1.0);
			put(ISBN, 1.0);
			put(PHONE, 1.0);
			put(SPACE, 0.25);
			put(MASS, 0.25);
			put(MONEY, 0.25);
			put(NUMBER, 0.1);
	    }};
	}

	static final private boolean useType   = WeirConfig.getBoolean(TYPED_ENTITIES);
	static final private double minTermSim = WeirConfig.getDouble(ENTITY_TERM_SIM_THRESHOLD);
	static final private double minTermIdf = WeirConfig.getDouble(ENTITY_TERM_IDF_THRESHOLD);

	final private List<Value> values;
		
	public ValueEntity(Webpage page) {
		super(page);
		/* collect the list of all values extracted by any extraction rule */
		this.values = initListOfExtractedValues();
	}

	private List<Value> initListOfExtractedValues() {
		final List<Attribute> attributes = this.getWebsite().getAttributes();
		if (attributes.isEmpty())
			throw new IllegalStateException("No attribute extracted from "+this.getWebsite()+" yet");
		final List<Value> values = new ArrayList<>(attributes.size());
		for (Attribute a : attributes) {
			this.values.add(this.getValue(a));
		}
		return values;
	}

	public Value getValue(Attribute a) {
		return a.getVector().get(this.getWebpage().getOverlapIndex());
	}
	
	public List<Value> getValues() {
		return this.values;
	}

	@Override
	public double similarity(Entity that) {
		return computeIdfSimilarity((ValueEntity) that);
	}
	
	/*
	 * - Da ogni pagina (di ogni sito) viene costruita un'entità, 
	 *       che ha un valore per ogni attributo pubblicato dal sito
	 *       NB: tale valore viene convertito con un tipo adatto 
	 *       	 in WEIR ogni vettore ha un tipo, ma è possibile che non tutti gli elementi siano di quel tipo 
	 *       	 ad esempio: ["a","2.0","c"] in WEIR sarebbe di tipo STRING, ma in Jaccard ogni valore  
	 *            	         ha un suo tipo (quindi il secondo elemento avrebbe NUMBER)
	 */
	private double computeIdfSimilarity(ValueEntity that) {

		double similarity = 0;
		for (final Value thisValue : this.values) {
			
			final Value thatValue = Collections.max(that.getValues(), pivotedComparator(thisValue));
			final double bestSim = valueSimilarity(thisValue,thatValue);

			if (bestSim>=minTermSim) {// CHECK for era sul linkage a livello di pagina, non di parola
				similarity += weightedSimilarity(thisValue, thatValue);
			}
		}

		log.trace("final similarity:" + thousandth.format(similarity));
		return similarity;
	}

	private Comparator<Value> pivotedComparator(final Value pivot) {
		return new Comparator<Value>() {

			@Override
			public int compare(Value v1, Value v2) {
				return Double.compare(valueSimilarity(pivot, v1),valueSimilarity(pivot, v2));
			}
			
		};
	}
	
	protected double valueSimilarity(final Value thisValue, final Value thatValue) {
		if (thisValue.isNull() || thatValue.isNull()) return 0d;
		
		final Type commonType = typeForDistance(thisValue, thatValue);
		
		return 1 - commonType.distance(thisValue, thatValue) ;
	}

	private Type typeForDistance(final Value v1, final Value v2) {
		return useType ? findMostSpecificType(v1.toString(), v2.toString()) : STRING;
	}

	protected double weightedSimilarity(final Value thisValue, final Value thatValue) {
		final Type type = typeForDistance(thisValue, thatValue);
		final double termThisIdf = getTermIdf(thisValue);
		final double termThatIdf = getTermIdf(thatValue);
		final double bestSim = valueSimilarity(thisValue,thatValue);
		log.trace(""
				+ thisValue + " vs " + thatValue + " " 
				+ ( useType ? "(" + type + ") ;" : "" ) 
				+ thousandth.format(bestSim) + ";" 
				+ thousandth.format(termThisIdf) + ";" 
				+ thousandth.format(termThatIdf) + "\n");
		double typeNormalizer = useType ? typeFactor.get(type) : 1d ;
		if (termThisIdf>=minTermIdf && termThatIdf>=minTermIdf)
			return bestSim * termThisIdf * termThatIdf * typeNormalizer;
		else return 0d;
	}

	final private double getTermIdf(final Value thisValue) {
		final IdfRepository idfThis = thisValue.getPage().getWebsite().getIdfRepository();
		final double termThisIdf = idfThis.getIdf(thisValue.toString());
		return termThisIdf;
	}

}
