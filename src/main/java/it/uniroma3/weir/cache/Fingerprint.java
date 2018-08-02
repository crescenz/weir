package it.uniroma3.weir.cache;
/**
 * A.k.a. code uniquely associated with input parameters of
 * a {@link CachedComputation} and used by a {@link Cache}
 * to retrieved results already computed and persistently saved.
 *
 * Ideally, a fingerprint is an <em>hashcode</em> without conflicts.
 * 
 * TODO log-friendly explanation of Fingerprint
 */
public interface Fingerprint {
	
	public byte[] getBytes();
	
	public String getCachePrefix();// { return "unknown"; } // friendly prefix to recognize cache data file by intent

	
	// enum over "linkage", "data", "pyramid" ...
}
