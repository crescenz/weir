package it.uniroma3.weir.extraction.rule;

import it.uniroma3.weir.model.Website;

import java.util.HashSet;
import java.util.Set;

public class PositionalRule extends ExtractionRule {

	static final private long serialVersionUID = -7421316705644819691L;

	static public Set<ExtractionRule> ruleSet(Website site, String... xpaths) {
		Set<ExtractionRule> rules = new HashSet<>();
		for (String xpath : xpaths) {
			final PositionalRule rule = new PositionalRule(xpath);
			rule.setWebsite(site);
			rules.add(rule);
		}
		return rules;
	}
	
	public PositionalRule(String xpath) {
		super(ExtractionRuleClass.POSITIONAL,xpath);
	}


}
