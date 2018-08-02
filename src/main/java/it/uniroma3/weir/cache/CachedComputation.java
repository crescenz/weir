package it.uniroma3.weir.cache;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.configuration.Constants;
import it.uniroma3.weir.configuration.WeirConfig;

//import java.io.Serializable; // N.B. List is not Serializable, even if ArrayList is

/** 
 * Large-grain Memoizer for heavy computations: given an input, 
 * it computes a corresponding output, but it retrieves the result 
 * from a cache if already available, or store it into the cache
 * if it was not.
 * 
 * It is based on {@link Cache} and the clients need to specify how to
 * <i>fingerprinting</i> the input by implementing the method 
 * {@linkplain CachedComputation#fingerprint(Object)}
 * 
 * For using this cache, let your class performing the heavy computation
 * within a method <code>IN heavyMethod(OUT)</code>
 * extend this class and provide its implementation in the 
 * {@linkplain OUT CachedComputation#uncachedComputation(IN)} method. Then
 * let your original method invoke 
 * {@linkplain OUT CachedComputation#cachedComputation(IN)}. 
 * For example, the client class will look like this:
 * 
 * <pre>public class ClientOfCacheClass extends CachedComputation<Integer, String> {
    ...
   	public String heavyMethod(Integer in) {
		return cachedComputation(in);
	}
	@Override
	public String uncachedComputation(Integer in) {
	   ...copy original heavyMethod here...
	}
	...
}</pre>
 * 
 * Persistence is achieved by means of java serialization.
 * 
 * With Java 8 lambdas this stuff could be improved.
 *
 * @param <IN> type of input
 * @param <OUT> type of output
 */
public abstract class CachedComputation<IN, OUT> extends Fingerprinter {

	static final protected HypertextualLogger log = HypertextualLogger.getLogger();
	
	static final private String SWITCH_PROPERTY_SUFFIX = ".cache";
	
	transient final private Cache<OUT> cache; /* a cache cannot cache itself ! */
		
	protected CachedComputation() {
		this.cache = new Cache<OUT>();
		// to keep distinct classes from sharing cache files ...
//		fingerprint(this.getClass().getName()); // ... uncomment this line
	}
	
	/**
	 * This method must be invoked to look up 
	 * in the cache a previously computed result.
	 * In case of <em>cache miss</um>,
	 * {@linkplain CachedComputation#uncachedComputation(IN)}
	 * will be invoked to compute the result.
	 * It cannot be overridden.
	 */
	final public OUT cachedComputation(IN input) {
		if (!isCacheEnabled()) return uncachedComputation(input);
		log.newPage();
		final String msg = "computations of "+this.getClass()+" are cached";
		log.trace(msg);
		final Fingerprint f = fingerprint(input);
		OUT output = this.cache.loadSerializedData(f);
		if (output==null) {
			/* cache miss */
			log.endPage(msg+": cache miss");		
			cacheMissed(input, output);
			output = uncachedComputation(input);
			cache.storeSerializedData(f, output);
		} else {
			/* cache hit */
			log.endPage(msg+": cache hit");
			cacheHit(input,output);
		}
		return output;
	}

	private boolean isCacheEnabled() {
		return 	isGlobalCachingEnabled() &&	isThisComputationCachingEnabled();
	}

	private boolean isGlobalCachingEnabled() {
		return WeirConfig.getBoolean(Constants.CACHING_ENABLED);
	}

	private boolean isThisComputationCachingEnabled() {
		return WeirConfig.getConfiguration().getBoolean(getCacheSwitchPropertyName(), false);
	}

	private String getCacheSwitchPropertyName() {
		// e.g., it.uniroma3.weir.extraction.Extractor.cache
		return this.getClass().getName()+SWITCH_PROPERTY_SUFFIX;
	}

	protected void cacheHit(IN input, OUT cachedResult) { 
	}
	
	protected void cacheMissed(IN input, OUT newResult) { 
	}
	
	/**
	 * This method must be overridden to specify the
	 * how to compute the {@linkplain Fingerprint}
	 * associated with the input data
	 */
	public abstract Fingerprint fingerprint(IN input);

	/**
	 * This method must be overridden to specify how
	 * to compute new result
	 */
	public abstract OUT uncachedComputation(IN input);
		
}
