package it.uniroma3.weir.extraction;

import static it.uniroma3.weir.configuration.Constants.*;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.cache.CachedComputation;
import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.extraction.wrapper.pcdata.PCDATASplitter;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.ExtractedVector;

import java.util.List;
import java.util.Set;

/**
 * Infer a set of {@link ExtractionRule}s.
 * <BR/>
 * Extract all {@link ExtractedVector}s of data from a {@link Website}.
 * <BR/>
 * Filter the extracted {@link ExtractedVector}s.
 */
public class Extractor extends CachedComputation<Website, List<ExtractedVector>> {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();
	
	public Extractor() {
	}
		
	public List<ExtractedVector> extractData(Website site) {	
		return cachedComputation(site);
	}

	@Override
	public List<ExtractedVector> uncachedComputation(Website website) {
		
		/* load */
		log.trace("\nLoading "+website+" source code pages...");
		website.loadPages();  // load HTML / DOM representation to apply rules
				
		/* infer */
		final InferenceSystem generator = new InferenceSystem();
		final Set<ExtractionRule> rules = generator.inferRules(website);

		/* extract */
		final ParallelExtractor pdp = new ParallelExtractor(website);
		final List<ExtractedVector> extracted = pdp.parallelExtraction(rules);
		
		/* sub-PCDATA refinement */
		final PCDATASplitter splitter = new PCDATASplitter(website, extracted);
		final List<ExtractedVector> refined  = splitter.split();

		/* release */
		log.trace("...releasing "+website+" pages.");				
		website.releasePages(); // get rid of heavy DOM representations
		return refined;
//was:	return extracted;
	}
	
	@Override
	public Fingerprint fingerprint(Website site) {
		fingerprint(site.getFingerprint());
		
		//TODO group properties into extraction properties, integration properties and so on...?
		fingerprint(WeirConfig.getString(MAX_EXTRACTION_SAMPLES));
		fingerprint(WeirConfig.getString(EXTRACTION_RULES_CLASSES));
		/*fingerprint(WeirConfig.getString(ECGM_CONFIG));*/ /* !?! */
		fingerprint(WeirConfig.getString(MAX_PIVOT_DISTANCE));
		fingerprint(WeirConfig.getString(MAX_PERCENTAGE_EQUALS));
		fingerprint(WeirConfig.getString(MAX_PERCENTAGE_NULLS));
		fingerprint(WeirConfig.getString(MAX_VALUE_LENGTH));
		fingerprint(WeirConfig.getString(EXTRACTION_RULES_FILTERS));
		fingerprint(WeirConfig.getString(DATATYPES));
		return getFingerprint("data");
	}

}