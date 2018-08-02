package it.uniroma3.weir.evaluation.linking;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.linking.linkage.DomainLinkage.WebsiteLinkageProcessor;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.Website;

/**
 * Compute simple statistics (average and standard deviation) over the 
 * sizes of the linkages between all the possible unordered pairs of 
 * {@link Website}s in a {@link Domain}.
 */
public class DomainLinkageStatistics {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();
	
	private double average; // avg linkage size
	private double std;  	// standard deviation
	private int counter;    // total size
	
	public DomainLinkageStatistics(Domain domain) {
		this.counter = 0;
		this.average = 0;
		this.std = 0;
		this.computeStatistics(Experiment.getInstance().getLinkages());
	}
		
	private void computeStatistics(DomainLinkage repository) {
		// sum it up...
		repository.forEach(new WebsiteLinkageProcessor() {
			@Override
			public void process(WebsiteLinkage wl) {
				DomainLinkageStatistics.this.average += wl.size();
				DomainLinkageStatistics.this.counter++;
			}
			
		});

		this.average /= this.counter; // ...and then the average
		
		// ...and then the standard deviation
		repository.forEach(new WebsiteLinkageProcessor() {

			@Override
			public void process(WebsiteLinkage wl) {
				std += Math.pow(wl.size() - average, 2);
			}
			
		});

		this.std = Math.sqrt(std / counter);
	}
	
	
	public void logStatistics() {
		log.trace("average linkage size: " + this.average);		
		log.trace("linkage size standard deviation: " + std);
	}

}
