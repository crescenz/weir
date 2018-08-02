package it.uniroma3.weir.cache;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.configuration.WeirConfig;
import static it.uniroma3.weir.configuration.Constants.CACHING_ENABLED;
import static it.uniroma3.hlog.HypertextualUtils.linkTo;

import java.io.*;
/**
 * A persistent cache based on java object serialization to save
 * the data of partial heavy computations such as extracted data
 * and linkage information over a whole domain composed of several
 * websites, each with thousands of webpages.
 * 
 * The cached data are <i>fingerprinted</i>, i.e., they are uniquely
 * associated with a {@link Fingerprint} that is required to recover
 * and to store the cached data.
 * 
 * {@link Fingerprint} are produced by means of {@link Fingerprinter}.
 * 
 * If {@link Fingerprint} depends on input, they can be used to 
 * automatically detect changes on the input parameters that
 * need a complete new computation of the results.
 * 
 * @param <T> the type of {@link Serializable} cache data
 */
public class Cache<T> {

	static private final HypertextualLogger log = HypertextualLogger.getLogger();
		
	private CacheOrganizer organizer;
	
	public Cache() {
		this.organizer = new CacheOrganizer();
	}
	
	@SuppressWarnings("unchecked")
	public T loadSerializedData(Fingerprint fingerprint) {
		if (!WeirConfig.getBoolean(CACHING_ENABLED)) {
			log.trace("caching disabled");
			return null;
		}
		try {
			log.trace("caching enabled");
			final File toLoad = this.organizer.getCacheDatafile(fingerprint);
			log.trace("the fingerprint of the cached data is: "+fingerprint);
			log.trace("loading cached data from " + linkTo(toLoad));
			if (!toLoad.exists()) {
				log.trace("cache file not found");
				return null;
			}
			final FileInputStream fis = new FileInputStream(toLoad);
			final ObjectInputStream in = new ObjectInputStream(fis);
			final Object cachedData = in.readObject();
			fis.close();
			log.trace("cached data loaded");
			return (T)cachedData;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public void storeSerializedData(Fingerprint fingerprint, T data) {
		try {
			final File toSave = this.organizer.getCacheDatafile(fingerprint);
//			log.trace("saving cache data into " + linkTo(toSave));

			final FileOutputStream fos = new FileOutputStream(toSave);
			final ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(data);
			out.close();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
