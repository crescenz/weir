package it.uniroma3.weir.extraction;

import static it.uniroma3.weir.configuration.Constants.MIN_EXTRACTION_SAMPLES;
import static it.uniroma3.weir.configuration.Constants.MAX_EXTRACTION_SAMPLES;

import java.util.*;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.cache.Fingerprinted;
import it.uniroma3.weir.cache.Fingerprinter;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;

/**
 * Given a {@link Website}, this class selects from its {@link Webpage}s
 * the sample set used to infer the {@link ExtractionRule}.
 */
public class ExtractionSampler implements Fingerprinted {
	
	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	final private int minSamples;
	
	final private int maxSamples;
	
	private Random random;

	public ExtractionSampler() {
		this.minSamples = WeirConfig.getInteger(MIN_EXTRACTION_SAMPLES);
		this.maxSamples = WeirConfig.getInteger(MAX_EXTRACTION_SAMPLES);
		this.random = null;
	}
	
//	public List<Webpage> sample(Website site) {
//		final List<Webpage> pages = site.getOverlappingPages();
//		log.trace("max samples to infer rules:\t" + this.maxSamples);
//		return pages.subList(0, Math.min(pages.size(), this.maxSamples));
//	}

	/* Choose the training samples randomly but preserve the reproducibility */
	public List<Webpage> sample(Website site) {
		final int size = site.getWebpages().size();
		if (size<this.minSamples) {
			log.trace(size+" input pages are not enough to infer rules");
			return Collections.emptyList();
		}
			
		this.random = new Random(site.getName().hashCode());
		log.trace("max samples to infer rules:\t" + this.maxSamples);
		final List<Webpage> overlapPages = site.getOverlappingPages();
		final List<Webpage> websitePages = site.getWebpages();
		return selectRandomly(overlapPages, websitePages);
	}

	private List<Webpage> selectRandomly(List<Webpage> overlap, List<Webpage> pages) {
		final List<Webpage> result = new LinkedList<>();
		if (overlap==null) overlap = Collections.emptyList();
		final List<Webpage> input = new ArrayList<>(overlap);
		/* try to cover all max-samples with input pages, prefer overlap 
		 * pages but if they're not enough fill up with other input pages
		 */
		if (input.size()<this.maxSamples) {
			final List<Webpage> other = new LinkedList<>(pages);
			other.removeAll(input);
			input.addAll(other);
		}
		while (result.size()<this.maxSamples && !input.isEmpty()) {
			final int index = Math.abs(this.random.nextInt() % input.size());
			final Webpage page = input.remove(index);
			result.add(page);
		}
		return result;
	}
	
	@Override
	public Fingerprint getFingerprint() {
		return new Fingerprinter().
				fingerprint(this.minSamples).
				fingerprint(this.maxSamples).getFingerprint();
	}

}
