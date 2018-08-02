package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.util.CollectionsCSVUtils.collection2csv;
import static it.uniroma3.weir.fixture.VectorFixture.createVector;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import it.uniroma3.util.MarkUpUtils;
import it.uniroma3.weir.extraction.ParallelExtractor;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.extraction.rule.SubPCDATARule;
import it.uniroma3.weir.extraction.wrapper.pcdata.SubPCDATARuleGenerator;
import it.uniroma3.weir.extraction.wrapper.template.TemplateFinder;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.ExtractedVector;

import java.util.*;

public class Asserts {

	static Set<String> assertRelativeXPathsAtRange(
			int range, 
			Set<String> expected, 
			List<Webpage> pages) {
		final TemplateFinder hunter = new TemplateFinder();
		hunter.findTemplateTokens(pages);
		final RelativeGenerator builder = new RelativeGenerator(range, 3);
		final Set<String> actual = builder.inferRules(pages);
		return assertAtLeastTheRules(expected, actual, pages);
	}

	static Set<String> assertARelativeXPath(String expected, List<Webpage> pages) {
	    final RelativeGenerator generator = new RelativeGenerator();
		final Set<String> rules = generator.inferRules(pages);
		assertTrue( msg(expected, pages, rules),
					rules.contains(expected));
		return rules;
	}

	static Set<String> assertAtLeastTheseRelativeRules(Set<String> expected, List<Webpage> pages) {
	    final RelativeGenerator generator = new RelativeGenerator();
		final Set<String> actual = generator.inferRules(pages);
		return assertAtLeastTheRules(expected, actual, pages);
	}

	static private Set<String> assertAtLeastTheRules(
			Set<String> expected, final Set<String> actual,
			List<Webpage> pages) {
		for(String expectedRule : expected) {
			assertTrue( msg(expectedRule.toString(), pages, actual), 
						actual.contains(expectedRule));
		}
		return actual;
	}
	
	static public Set<String> assertAtLeastInferredAndRefinedRule(
								List<Webpage> pages, 
								Set<String> expectedBeforeRefining,
								Set<String> expectedAfterRefining,
								String...expectedValues) {
		final Set<String> actualBeforeRefining = assertAtLeastTheseRelativeRules(expectedBeforeRefining, pages);
		final Website site = pages.iterator().next().getWebsite();

		/* extract */
		final ParallelExtractor pdp = new ParallelExtractor(pages);
		final List<ExtractedVector> extracted = pdp.parallelExtraction(ruleSet(site,actualBeforeRefining));

		/* refine  */
		final SubPCDATARuleGenerator refiner = new SubPCDATARuleGenerator(site);
		final Set<ExtractionRule> actualAfterRefining = refiner.refine(extracted);

		assertAtLeastTheRules(expectedAfterRefining, xpathSet(actualAfterRefining), pages);
		
		/* check whether the expected extracted values have been provided ?*/
		if (expectedValues.length>0 && expectedAfterRefining.size()==1) {
			final String expectedRefinedXPath = expectedAfterRefining.iterator().next();
			final SubPCDATARule subpcdatarule = findSubPCDATARule(actualAfterRefining, expectedRefinedXPath);
			assertNotNull("Expected subpcdata-extraction rule not found", subpcdatarule);
			assertExtractedVector(pages, subpcdatarule, expectedValues);
		}
		return xpathSet(actualAfterRefining);
	}
	
	private static SubPCDATARule findSubPCDATARule(Set<ExtractionRule> rules, String wanted) {
		final Iterator<ExtractionRule> it = rules.iterator();
		while (it.hasNext()) {
			ExtractionRule rule = it.next();
			if (rule.getXPath().equals(wanted))
				return (SubPCDATARule) rule;
		}
		return null;
	}

	static void assertExtractedVector(List<Webpage> pages, ExtractionRule rule, String...expectedValues) {
		assumeTrue(pages.size()==expectedValues.length);
		/* extract */
		final ParallelExtractor pdp = new ParallelExtractor(pages);
		final List<ExtractedVector> extracted = pdp.parallelExtraction(Collections.singleton(rule));
		assertEquals(1, extracted.size());
		final ExtractedVector actual = extracted.iterator().next();
		assertEquals(createVector(pages.toArray(new Webpage[0]), expectedValues),actual.normalize());
	}

	static private String msg(String expected, List<Webpage> pages,
			final Set<String> rules) {
		return "Expected extraction rule\n "+expected+
					"\n\tnot found within\n"+collection2csv(rules,"","\n","")+
					"\ngenerated from pages\n"+sources(pages);
	}

	static public void assertRelativeRules(Set<String> expected, List<Webpage> pages) {
	    final RelativeGenerator generator = new RelativeGenerator();
		assertEquals(
					"\nWrong set of relative extraction rules"+
					" generated from pages:\n"+sources(pages),
					expected, generator.inferRules(pages));
	}

	static private String sources(List<Webpage> pages) {
		StringBuilder result = new StringBuilder();
		for(Webpage page : pages) {
			result.append(MarkUpUtils.dumpTree(page.getDocument()));
//			result.append("\n");
		}
		return result.toString();
	}

	static public Set<ExtractionRule> ruleSet(Website site, Collection<String> xpaths) {
		final Set<ExtractionRule> result = new LinkedHashSet<>();
		for(String xpath : xpaths) {
			final ExtractionRule rule = new ExtractionRule(null,xpath);
			rule.setWebsite(site);
			result.add(rule);
		}
		return result;
	}
	
	
	static public Set<String> xpathSet(Set<ExtractionRule> rules) {
		final Set<String> result = new LinkedHashSet<>();
		for(ExtractionRule rule : rules)
			result.add(rule.getXPath());
		return result;
	}
	
}
