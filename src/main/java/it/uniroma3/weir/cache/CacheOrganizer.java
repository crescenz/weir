package it.uniroma3.weir.cache;

import static java.io.File.separator;

import java.io.File;

import it.uniroma3.weir.configuration.WeirConfig;

/**
 * 
 * Here is all the logic to organize cache file names
 *
 */
public class CacheOrganizer {

	/* e.g., ./weir-cache/swde/nbaplayer/linkage_swde-nbaplayer_01-02-03-04-05 */
	private static final String WEIR_CACHE =   "weir-cache" + separator +
			WeirConfig.getCurrentExperiment().getDataset().getName() + separator + 
			WeirConfig.getCurrentExperiment().getDomain().getName();
	
	
	public File getCacheDir() {
		final File dir = new File(WEIR_CACHE);
		if (!dir.exists() && !dir.mkdirs()) {
		    throw new IllegalStateException("Cannot create cache directory: " + dir);
		}
		return dir;
	}
	
	public File getCacheDatafile(Fingerprint fingerprint) {		
		final File file = new File(getCacheDir(), getCacheDatafilename(fingerprint));
		File parent = file.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
			throw new IllegalStateException(
					"Cannot create the parent directory "+parent+" of "+
					"cache file: " + file
					);
		}
		return file;
	}

	/**
	 * Here is all the logic to choose cache file names
	 * @param fingerprint
	 * @return
	 */
	private String getCacheDatafilename(Fingerprint fingerprint) {
		//TODO introduce singleton over "current" Experiment
		final String friendlyPrefix = fingerprint.getCachePrefix();
		final String dataset = WeirConfig.getCurrentExperiment().getDataset().getName();
		final String domain = WeirConfig.getCurrentExperiment().getDomain().getName();
		return friendlyPrefix+ "_" + dataset + "-" +domain + "_" + fingerprint;
	}
	
}
