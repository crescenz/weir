package it.uniroma3.weir.extraction.rule;

import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.cache.Fingerprinted;
import it.uniroma3.weir.cache.Fingerprinter;
import it.uniroma3.weir.extraction.wrapper.PositionalGenerator;
import it.uniroma3.weir.extraction.wrapper.RelativeGenerator;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public enum ExtractionRuleClass implements Serializable, Fingerprinted {
	
	POSITIONAL(new PositionalGenerator()) {
		@Override
		public ExtractionRule rule(String xpath) {
			return new PositionalRule(xpath);
		}

	},
	RELATIVE(new RelativeGenerator()) {		
		@Override
		public ExtractionRule rule(String xpath) {
			return new RelativeRule(xpath);
		}
	};
	
	static public interface RuleGenerator {
		public Set<String> inferRules(List<Webpage> samples);
	}
	
	private RuleGenerator generator;
	
	private ExtractionRuleClass(RuleGenerator generator) {
		this.generator = generator;
	}
	
	public Set<ExtractionRule> ruleSet(Website site, Collection<String> xpaths) {
		final Set<ExtractionRule> result = new LinkedHashSet<>();
		for(String xpath : xpaths) {
			final ExtractionRule extractionRule = rule(xpath);
			extractionRule.setWebsite(site);
			result.add(extractionRule);
		}
		return result;
	}
	
	public Set<String> inferRules(List<Webpage> samples) {
		return this.generator.inferRules(samples);		
	}
	
	protected RuleGenerator getRuleGenerator() {
		return this.generator;
	}
	
	public Set<ExtractionRule> infer(Website site, List<Webpage> samples) {
		final Set<String> rules = inferRules(samples);
		return ruleSet(site,rules);
	}
	
	protected abstract ExtractionRule rule(String xpath) ;
	
	@Override
	public Fingerprint getFingerprint() {
		final Fingerprinter fingerprinter = new Fingerprinter();
		fingerprinter.fingerprint(this.getClass());
		return fingerprinter.getFingerprint();
	}
}