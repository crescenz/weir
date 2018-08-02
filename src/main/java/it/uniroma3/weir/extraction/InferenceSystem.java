package it.uniroma3.weir.extraction;

import static it.uniroma3.hlog.HypertextualUtils.popup;
import static it.uniroma3.weir.configuration.Constants.EXTRACTION_RULES_CLASSES;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.token.dom.DOMToken;
import it.uniroma3.weir.cache.CachedComputation;
import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.extraction.rule.ExtractionRuleClass;
import it.uniroma3.weir.extraction.wrapper.template.TemplateFinder;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
/**
 * 
 * Infer the extraction rules for a website.
 *
 */
public class InferenceSystem extends CachedComputation<Website, Set<ExtractionRule>>{

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	final private List<ExtractionRuleClass> ruleClasses;
	
	final private ExtractionSampler sampler;
	
	final private TemplateFinder finder;
	
	public InferenceSystem() {
		this.ruleClasses = enabledRuleClasses();
		this.sampler = new ExtractionSampler();
		this.finder  = new TemplateFinder();
	}

	static private List<ExtractionRuleClass> enabledRuleClasses() {
		return WeirConfig.getEnumList(ExtractionRuleClass.class, EXTRACTION_RULES_CLASSES);
	}

	public Set<ExtractionRule> inferRules(Website site) {
		return cachedComputation(site);
	}

	@Override
	public Set<ExtractionRule> uncachedComputation(Website site) {
		log.newPage("extraction rules inference");
		
		/* select 'training' sample pages */
		final List<Webpage> samples = this.sampler.sample(site);
		log.trace(popup(samples.size()+" sample pages to infer rules",samples));
		site.loadPages(samples); // make sure these pages have been loaded
		
		/* Template Analysis: 
		 * as side-effects it marks template and variant nodes */
		log.newPage("looking for template tokens");
		this.finder.findTemplateTokens(samples);
		final List<DOMToken> tokens = this.finder.getTemplateTokens();
		log.page("\nTemplate tokens found", tokens);
		log.endPage();
		
		/* Rules Generation NOW based on template analysis */
		final Set<ExtractionRule> result = new LinkedHashSet<>();
		for (ExtractionRuleClass erc : this.ruleClasses) {
			log.newPage("extraction rules class - " + erc);
			final Set<ExtractionRule> rules = erc.infer(site, samples);
			result.addAll(rules);
			log.endPage();
			log.trace("generated " + rules.size() + " rules");
		}
		log.endPage();

		log.trace("generated " + result.size() + " rules");
		log.page("list of all rules generated", result);
		log.trace("\n");
		return result;
	}

	@Override
	public Fingerprint fingerprint(Website site) {
		fingerprint(site.getFingerprint());
		for(ExtractionRuleClass erc : this.ruleClasses)
			fingerprint(erc.getFingerprint());
		fingerprint(this.sampler);
		return this.getFingerprint("rule");
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + this.ruleClasses;
	}

}
