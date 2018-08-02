package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.ecgm.Constants.LFEQ_SIZE_THRESHOLD;
import static it.uniroma3.ecgm.Constants.LFEQ_SUPP_THRESHOLD;
import static it.uniroma3.token.Constants.SEPARATOR_CHARS;
import static it.uniroma3.util.CollectionsUtils.setOf;
import static it.uniroma3.weir.configuration.Constants.MAX_PIVOT_DISTANCE;
import static it.uniroma3.weir.configuration.Constants.MIN_PIVOT_LENGTH;
import static it.uniroma3.weir.extraction.wrapper.Asserts.assertAtLeastInferredAndRefinedRule;
import static it.uniroma3.weir.extraction.wrapper.Asserts.assertAtLeastTheseRelativeRules;
import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures._HTML_TABLE_;
import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures.pivotXPath;
import static it.uniroma3.weir.fixture.WebpageFixture.webpages;
import static it.uniroma3.weir.fixture.WebsiteFixture.createEmptySite;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;

import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
/**
 * This tests are meant to document the expressivity of the generated rules.
 */
public class InferenceTest extends WeirTest {

	
	static private Set<String> ruleSet(String... xpaths) {
		return setOf(xpaths);
	}
	
	@BeforeClass
	public static void setUp() {
		WeirConfig.getInstance().setProperty(MAX_PIVOT_DISTANCE,  "2");
		WeirConfig.getInstance().setProperty(MIN_PIVOT_LENGTH,    "0");
		WeirConfig.getInstance().setProperty(LFEQ_SIZE_THRESHOLD, "2");
		WeirConfig.getInstance().setProperty(LFEQ_SUPP_THRESHOLD, "0.5");
		WeirConfig.getInstance().setProperty(SEPARATOR_CHARS, "");
	}

	@Test
	public void testInfer_table_2rows() {
		assertAtLeastTheseRelativeRules(
				ruleSet(
						pivotXPath("TD","inv0")+"/following-sibling::node()[1]/text()[1]/self::text()",
						pivotXPath("TD","inv1")+"/following-sibling::node()[1]/text()[1]/self::text()"
				),
				pages(_HTML_TABLE_(
						"<TR><TD>inv0</TD><TD>vara</TD></TR>"+
						"<TR><TD>inv1</TD><TD>varb</TD></TR>"
					 ),
					  _HTML_TABLE_(
					    "<TR><TD>inv0</TD><TD>varc</TD></TR>"+
					    "<TR><TD>inv1</TD><TD>vard</TD></TR>"
					 ))
		);
	}	

	@Test
	public void testInferAndRefine_basic_table() {
		assertAtLeastInferredAndRefinedRule(
				pages(_HTML_TABLE_("<TR><TD>inv0</TD><TD>vara</TD></TR>"),
					  _HTML_TABLE_("<TR><TD>inv0</TD><TD>varb</TD></TR>")),
				ruleSet(pivotXPath("TD","inv0")+"/following-sibling::node()[1]/text()[1]/self::text()"),
				ruleSet(), /* none can be refined; */
				"vara","varb"
		);
	}
	@Test
	public void testInferAndRefine_basic_after() {
		assertAtLeastInferredAndRefinedRule(
				pages("<HTML><BODY>pivot a</BODY></HTML>",
					  "<HTML><BODY>pivot b</BODY></HTML>"),
				ruleSet(pivotXPath("BODY")+"/text()"),
				ruleSet("substring-after("+pivotXPath("BODY")+"/text(),'pivot')"),
				"a","b"
		);
		
	}
	
	@Test
	public void testInferAndRefine_basic_before() {
		assertAtLeastInferredAndRefinedRule(
				pages("<HTML><BODY>a pivot</BODY></HTML>",
				 	  "<HTML><BODY>b pivot</BODY></HTML>"),
				ruleSet(pivotXPath("BODY")+"/text()"),
				ruleSet("substring-before("+pivotXPath("BODY")+"/text(),'pivot')"),
				"a","b"
		);
	}
	
	@Test
	public void testInferAndRefine_basic_embedded_pivot() {
		assertAtLeastInferredAndRefinedRule(
				pages("<HTML><BODY>a pivot c</BODY></HTML>",
				 	  "<HTML><BODY>b pivot d</BODY></HTML>"),
				ruleSet(pivotXPath("BODY")+"/text()"),
				ruleSet(
						"substring-before("+pivotXPath("BODY")+"/text(),'pivot')",
						"substring-after("+pivotXPath("BODY")+"/text(),'pivot')"
						),
				"a","b"
		);
	}
	
	@Test
	public void testInferAndRefine_invariantPortionOfAnchor() {
		assertAtLeastInferredAndRefinedRule(
				pages("<HTML><BODY><A href=\".\">Boston Celtics Tickets</A></BODY></HTML>",
				 	  "<HTML><BODY><A href=\".\">Philadelphia 76ers Tickets</A></BODY></HTML>"),
				ruleSet(pivotXPath("A","Tickets")+"/text()"),
				ruleSet(
					  "substring-before("+pivotXPath("A","Tickets")+"/text(),'Tickets')"
				),
				"Boston Celtics",
				"Philadelphia 76ers"
		);		
	}
	
	static public List<Webpage> pages(String... contents) {
		final List<Webpage> result = webpages(contents);
		final Website site = createEmptySite();
		for(Webpage page : result) {
			site.addPage(page);
		}
		return result;
	}
	

}
