package it.uniroma3.weir.cache;

import it.uniroma3.hlog.HypertextualLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Objects;
/**
 * An utility class to produce (by using {@linkplain Fingerprinter#getFingerprint()})
 * and compose (by using {@linkplain Fingerprinter#fingerprint(Fingerprint)})
 * {@link Fingerprint}s, i.e., small code uniquely associated with possibly complex
 * objects. Useful to detect even small changes in different versions of the same
 * complex objects without pairwise comparing their inner details.
 *
 */
public class Fingerprinter {

	static protected HypertextualLogger log = HypertextualLogger.getLogger();

	static final private String FINGERPRINT_ALGORITHM = "MD5";

	transient private MessageDigest md;

	public Fingerprinter() {
		try {
			this.md = MessageDigest.getInstance(FINGERPRINT_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Message Digest algorithm for fingerprinting not available.",e);
		}
	}

	private MessageDigest getDigest() {
		return this.md;
	}

	//	public void resetFingerprint() {
	//		this.md.reset();
	//	}

	public Fingerprinter fingerprint(Object... objects) {
		for(Object object : objects)
			getDigest().update(Objects.toString(object).getBytes());
		return this;
	}

	public Fingerprinter fingerprint(Fingerprint fingerprint) {
		getDigest().update(fingerprint.getBytes());
		return this;
	}

//	public void fingerprint(String... input) {
//		for(String string : input)
//			getDigest().update(string.getBytes());
//	}

	public Fingerprinter fingerprint(Iterator<String> it) {
		while (it.hasNext())
			this.fingerprint(it.next());
		return this;
	}

	public Fingerprinter fingerprint(Iterable<?> iterable) {
		for(Object element  : iterable)
			this.fingerprint(element);
		return this;
	}
	
	public Fingerprint getFingerprint() {
		return getFingerprint("unknown");
	}

	public Fingerprint getFingerprint(final String cachefilePrefix) {
		final byte[] result = this.getDigest().digest();
		return new Fingerprint() {

			@Override
			public String getCachePrefix() {
				return cachefilePrefix; // user friendly prefix for naming cache files
			}

			@Override
			public byte[] getBytes() {
				return result;
			}

			@Override
			public String toString() {
				final StringBuilder result = new StringBuilder();
				for(byte b : getBytes())
					result.append(String.format("%02X", b));
				return result.toString();
			}

		};
	}

}
