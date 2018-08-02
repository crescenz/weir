package it.uniroma3.weir.extraction.wrapper;


import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures.*;
import static it.uniroma3.weir.fixture.WebpageFixture.webpages;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.extraction.rule.PositionalRule;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.model.Webpage;

import java.util.List;
import java.util.Set;

import org.junit.Test;

public class PositionalGeneratorTest extends WeirTest {

	static private Set<ExtractionRule> ruleSet(String... xpaths) {
		return PositionalRule.ruleSet(null, xpaths);
	}
	
	@Test
	public void testInferRules_no_text() {
		assertPositionalRules(ruleSet(), webpages(_HTML_TREE_("<HR/>")));
	}

	@Test
	public void testInferRules_whitespaces() {
		assertPositionalRules(ruleSet(), webpages(_HTML_TREE_("<P>   </P>")));
	}

	@Test
	public void testInferRules_textById() {
		assertPositionalRules(
				ruleSet("//P[@id='001']/text()[1]"), 
				webpages(_HTML_TREE_("<P id='001'>text</P>")));
	}

	@Test
	public void testInferRules_textAndWhitespaces() {
		assertPositionalRules(
				ruleSet("//DIV[@id='002']/text()[1]"), 
				webpages(TEXT_AND_WHITESPACES));
	}

	@Test
	public void testInferRules_siblingTextsById() {
		assertPositionalRules(
				ruleSet("//P[@id='001']/text()[1]", 
						"//P[@id='001']/text()[2]"), 
						webpages(_HTML_TREE_("" +
								"<P id='001'>" +
								/**/"text_1" +
								/**/"<BR/>" +
								/**/"text_2" +
								"</P>")));
	}

	@Test
	public void testInferPositionalRulesOverMultiWordPCDATA() {
		assertPositionalRules(
				ruleSet("//P[@id='001']/text()[1]"), 
				webpages(_HTML_TREE_("" +
						"<P id='001'>" +
						/**/"multi word text" +
						"</P>")));
	}

	@Test
	public void testInferRules_byId() {
		assertPositionalRules(
				ruleSet("//P[@id='001']/text()[1]", 
						"//P[@id='002']/text()[1]"), 
						webpages(_HTML_TREE_("" +
								"<P id='001'>" +
								/**/"text_1" +
								"</P>" +
								"<P id='002'>" +
								/**/"text_2" +
								"</P>")));
	}

	@Test
	public void testInferRules_distinctPaths() {
		assertPositionalRules(
				ruleSet("//DIV[@id='001']/text()[1]", 
						"//SPAN[@id='002']/text()[1]"), 
						webpages(TEXTS_OF_DISTINCT_PATHS));
	}

	@Test
	public void testInferRules_aNewRuleOnNewPage() {
		assertPositionalRules(
				ruleSet("//SPAN[@id='001']/text()[1]",
						"//SPAN[@id='002']/text()[1]"
						), 
				webpages(
						_HTML_TREE_("<SPAN id='001'>text1</SPAN>"),
						_HTML_TREE_("<SPAN id='001'>text1</SPAN><SPAN id='002'>text2</SPAN>")						
						));
	}
	
	static private void assertPositionalRules(
			Set<ExtractionRule> expected, 
			List<Webpage> pages) {
		final PositionalGenerator generator = new PositionalGenerator();
		assertEquals(
				"Wrong set of positional extraction rules",
				expected, 
				generator.inferRules(pages));
	}

}
