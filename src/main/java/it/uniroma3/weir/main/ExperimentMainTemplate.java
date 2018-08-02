package it.uniroma3.weir.main;

import static it.uniroma3.hlog.HypertextualLogManager.loadConfiguration;
import static it.uniroma3.hlog.HypertextualLogger.getLogger;
import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.model.Experiment;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.varia.NullAppender;

/**
 * Base class to write applications with a main() method 
 * executing one experiment at a time. The experiments 
 * (dataset and domain) are specified by command-line parameters.
 * E.g.: {@link WEIR}, {@link LINK}, {@link FUSE}.
 * <br/>
 * Subclasses must override {@link #execute(Experiment)}.
 *
 */
public abstract class ExperimentMainTemplate {

	static final protected String WEIR_CONFIG_FILENAME 		= "./weir-config.properties";

	static final protected String WEIR_TEST_CONFIG_FILENAME = "./weir-experiment-test-config.properties";

	static final protected String WEIR_HLOG_PROPERTIES 		= "/weir-hlog.properties";

	static protected HypertextualLogger log;

	static {
		org.apache.log4j.BasicConfigurator.configure(new NullAppender());
		/* otherwise one of the old LFEQ comparators that implements
		 * a partial-but-not-total ordering would rise exceptions 
		 * when executed by new java versions. */
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		System.setProperty("java.util.logging.manager", "it.uniroma3.hlog.HypertextualLogManager");
		loadConfiguration(WEIR_HLOG_PROPERTIES); // now automatically loaded by hlog
	}

	/* dataset: swde vs weir   */
	/* domain:  book, auto ... */	
	/* site: amazon, yahoo ... */
	static protected List<Experiment> listOfExperimentsToExecute(String[] args) {
		String[][] experimentNames = null;
		final int numberOfDomains = (args.length - (testModeEnabled(args) ? 1 : 0 ) ) / 2;
		if (numberOfDomains==0) {
			log.trace("none domain specified: executing default experiment");
			experimentNames = getDefaultExperimentNames();
		} else  {
			experimentNames = getExperimentNames(args);
			log.trace(numberOfDomains + " domain name(s) specified");
		}
		final List<Experiment> result = new LinkedList<>();
		for(String[] experiment : experimentNames) {
			String dataset = experiment[0];
			String domain  = experiment[1];
			result.add(Experiment.makeExperiment(dataset,domain)); // FIXME later
		}
		if (result.size()>1) 
			log.trace("list of experiments specified "+result);
		return result;				
	}

	static private String[][] getExperimentNames(String[] args) {
		int firstExpIndex = ( testModeEnabled(args) ? 1 : 0 );
		/* two args per exp: dataset / domain name */
		final int nexps = (args.length-firstExpIndex) / 2; 
		String[][] result = new String[nexps][2];
		for(int expIndex=0; expIndex<nexps; expIndex++) {
			final int index = expIndex / 2 + firstExpIndex;
			String[] expname = { args[index], args[index+1] };
			result[expIndex] = expname;
		}
		return result;
	}

	static private String[][] getDefaultExperimentNames() {
		return new String[][] {
				/*{<dataset>,<domain>},*/				
				//									    {"test","synth"},
				//				{"swde","auto"},
				//				{"swde","book"},
				//				{"swde","camera"},
				//				{"swde","job"},
				//				{"swde","movie"},
				{"swde","nbaplayer"},
				//				{"swde","restaurant"},
				//				{"swde","university"},
				//				{"weir","book"},
				//				{"weir","finance"},
				//				{"weir","soccer"},
				//				{"weir","videogame"}
		};
	}

	protected long startTime, endTime;

	protected void run(String[] args) {
		log = getLogger(this.getClass());
		
		log.info("command-line args received: "+Arrays.toString(args));

		/* load WEIR system-wide configuration */	
		final String configFilename = (testModeEnabled(args) ?
				WEIR_TEST_CONFIG_FILENAME :	WEIR_CONFIG_FILENAME );

		if (testModeEnabled(args))
			log.trace("working in test mode: using weir test configuration");

		URL configURL = ClassLoader.getSystemResource(configFilename);	
		try {
			WeirConfig.load(configURL);
		} catch (ConfigurationException cfgEx) {
			throw new RuntimeException("Cannot read WEIR configuration: "+configFilename,cfgEx);
		}
		log.info("system config file resolved as: " + linkTo(configURL));
		for(Experiment exp : listOfExperimentsToExecute(args)) {
			try {
				log.trace();
				/* set as current the experiment specification */
				WeirConfig.getInstance().setCurrentExperiment(exp);
				
				/* load input websites */
				log.newPage("loading experiment "+exp);
				exp.load();
				log.endPage();

				log.trace("number of input websites: " + exp.getWebsites().size());
				
				/* read golden information */
				exp.readGoldenMappings();

				/*   execute a generic experiment  which may involve  */
				/*   ( extraction | integration | linking )+  phases  */
				this.startTime = System.currentTimeMillis();
				execute(exp);
				this.endTime = System.currentTimeMillis();			

				log.trace("experiment time: " + (this.endTime - this.startTime) / 1000 + " secs");
				log.trace();
				pressAnyKeyToContinue();				
			}
			catch (Exception e) {
				e.printStackTrace();
				log.error("Failed! "+e);
				log.throwing(e);
			}			
			log.trace("<hr/>");
		}
	}

	private void pressAnyKeyToContinue() throws IOException {
		System.out.println("Press any key to continue...");
		try {
			log.trace("\n");
			System.in.read();
			System.out.println("key pressed!");
			/* n.b. this will trigger Object Server's shutdown hook */
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	static private boolean testModeEnabled(String[] args) {
		return args.length>0 && args[0].toLowerCase().equals("-test");
	}

	abstract protected void execute(Experiment experiment) throws Exception ;

}
