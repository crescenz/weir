package it.uniroma3.weir.cache;

/**
 * A marker interface for classes providing a
 * {@link Fingerprint} to use with {@link CachedComputation}
 * Notice, however, that usually the correct fingerprint of
 * an object depends on the algorithm using it in input.
 * 
 */
public interface Fingerprinted {

	Fingerprint getFingerprint();
	
}
