package it.uniroma3.weir.model;

import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import static it.uniroma3.weir.configuration.Constants.DATASET_PATH;
import static it.uniroma3.weir.configuration.Constants.EXPERIMENTS_PATH;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.cache.Fingerprinted;
import it.uniroma3.weir.cache.Fingerprinter;
import it.uniroma3.weir.configuration.Constants;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.evaluation.GoldenReader;
import it.uniroma3.weir.integration.AttributePair;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.model.hiddenrelation.AbstractRelation;
import it.uniroma3.weir.structures.Pair2BooleanRepository;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * A class to model all the resources needed to run a complete 
 * experiment on a {@link Dataset} over a single {@link Domain} 
 * including several {@link Website}s, with the golden information 
 * needed to evaluate the results.
 */
public class Experiment implements Serializable, Fingerprinted {
	
	static final private long serialVersionUID = -7618991188797384863L;

	static final private HypertextualLogger log = HypertextualLogger.getLogger();
	
	/* dataset: swde vs weir   */
	/* domain:  book, auto ... */	
	/* site: amazon, yahoo ... */
	static final private String SITE    = "site";    

	static final private String CSV = ".csv";

	static final private String ID2URL_INDEX_NAME = "_id2name.txt";
	
	static private Experiment instance = null; /* singleton Experiment instance */
	
	static public synchronized Experiment makeExperiment(String datasetName, String domainName) {
		if (instance!=null) return instance;
		instance = new Experiment(datasetName, domainName);
		return instance;
	}

	static public synchronized Experiment getInstance() {
		if (instance!=null) return instance;
		throw new IllegalStateException("Experiment not ready yet!");
	}

	private Configuration configuration; // experiment's configuration

	private Domain domain;             /* the list of input websites   */
	
	private MappingSet goldenMappings; /* the list of golden mappings  */
	
	private AbstractRelation _H_;      /* the hidden abstract relation */

	transient
	private Pair2BooleanRepository<AttributePair, Attribute> overlapCache;
	
	/**
	 * 
	 * @param datasetName - e.g., swde/weir
	 * @param domainName  - e.g., nbaplayer
	 */
	private Experiment(String datasetName, String domainName) {
		this.domain = new Domain(domainName);
		final Dataset dataset = new Dataset(datasetName);
		dataset.addDomain(this.domain);
		this.domain.setDataset(dataset);
		this.goldenMappings = null;
		this._H_ = new AbstractRelation(this);
		this.overlapCache = new Pair2BooleanRepository<AttributePair,Attribute>();
	}

	public File getExperimentSpecificationFolder() {
		return new File(WeirConfig.getString(EXPERIMENTS_PATH));
	}
	
	public File getExperimentSpecificationFile() {
		// e.g., /
		final String dataset = this.getDataset().getName();
		final String vertical = this.getDomain().getName();
		return new File(getExperimentSpecificationFolder(), dataset+"-"+vertical + ".xml") ;
	}
	
	/**
	 * Load the configuration of the experiment to run,
	 * @throws ConfigurationException
	 */
	public void load() throws ConfigurationException {	
		final File file = resolveExperimentSpecificationFile();

		this.configuration = new XMLConfiguration(file);
		
		// Load input websites of the vertical domain associated with this experiment
		final DomainLoader loader = new DomainLoader(this);
		loader.loadWebsites(this.getWebsiteList());
	}

	private File resolveExperimentSpecificationFile() {
		final File file = this.getExperimentSpecificationFile();	

		/* load experiment specification */
		if (!file.exists()) {
			throw new RuntimeException("Experiment specification file not found: " + file.getAbsolutePath());
		}
		log.info("specification file resolved as: " + linkTo(file));
		return file;
	}
	
	public Domain getDomain() {
		return this.domain;
	}

	public Dataset getDataset() {
		return this.getDomain().getDataset();
	}

	public List<Website> getWebsites() {
		return this.getDomain().getSites();
	}
	
	public AbstractRelation getAbstractRelation() {
		return this._H_;
	}
	
	public DomainLinkage getLinkages() {
		return this._H_.getLinkages();
	}
	
	public MatchRepository getMatches() {
		return this._H_.getMatches();
	}
	
	public Pair2BooleanRepository<AttributePair, Attribute> getOverlapCache() {
		return this.overlapCache;
	}

	/* --- */
	/**
	 * @return the folder containing all available datasets
	 */
	public File getDatasetsRootFolder() {
		// e.g.,
		return new File(WeirConfig.getString(DATASET_PATH));
	}
	
	/**
	 * @param domain
	 * @return the folder containing the resources associated with a domain
	 */
	public File getDomainFolder() {
		// e.g., dataset/swde/nbaplayer
		final String datasetName = getDomain().getDataset().getName();
		final File datasetFolder = new File(getDatasetsRootFolder(), datasetName);
		final String domainName = getDomain().getName();
		final File domainFolder = new File(datasetFolder, domainName);
		return domainFolder;
	}

	/**
	 * @param sitename  - name of a site in the domain 
	 *                    covered by this experiment
	 * @return the folder containing the resources associated with the site
	 */
	public File getWebsiteFolder(String sitename) {
		// e.g., dataset/swde/nbaplayer/nbaplayer-espn/
		return new File(getDomainFolder(), sitename);
	}	
	
	/**
	 * @param sitename  - name of a site in the domain 
	 *                    covered by this experiment
	 * @return the file containing the index by id of the URL of the 
	 *         HTML pages from the site passed as a parameter.
	 */
	public File getWebsitePageIndex(String sitename) {
		// e.g., dataset/swde/nbaplayer/nbaplayer-espn/_id2name.txt
		return new File(getWebsiteFolder(sitename), ID2URL_INDEX_NAME);
	}

	public File getWebsiteGoldenCSVFile(Website website) {
		if (this.getDomain()!=website.getDomain())
			throw new IllegalArgumentException("Can query only sites in the same domain!");
		final File domainFolder = getDomainFolder();
		final File siteFolder = new File(domainFolder, website.getName());			
		final File csvFile =  new File(siteFolder,website.getName() + CSV);
		return csvFile;
	}

	public List<String> getWebsiteList() {
		return new ArrayList<String>(Arrays.asList(this.configuration.getStringArray(SITE)));
	}
	
	private void initGoldenMappings() {
		if (this.goldenMappings==null) readGoldenMappings();
	}

	/**
	 * @throws IllegalStateException if it cannot access golden information
	 */
	public void readGoldenMappings() {
		try {
			final GoldenReader reader = new GoldenReader(this);
			this.goldenMappings = reader.getGoldenMappings();
		} catch (IOException e) {
			log.error("Cannot access golden information");
			log.trace(e);
			throw new IllegalStateException(e);
		}
	}

	public MappingSet getGoldenMappingSet() {
		initGoldenMappings();
		return this.goldenMappings;
	}
	
	@Override
	public Fingerprint getFingerprint() {
		Fingerprinter printer = new Fingerprinter();
		
		printer.fingerprint(this.getDomain().getFingerprint());
		
		for(Constants c : Constants.values()) {
			printer.fingerprint(WeirConfig.getString(c));
		}
		
		return printer.getFingerprint("exp");
	}
	
	@Override
	public String toString() {
		return getDataset().getName()+" "+getDomain().getName();
	}

}
