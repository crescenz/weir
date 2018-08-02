package it.uniroma3.weir.extraction.wrapper;


import static it.uniroma3.ecgm.Constants.LFEQ_SIZE_THRESHOLD;
import static it.uniroma3.ecgm.Constants.LFEQ_SUPP_THRESHOLD;
import static it.uniroma3.util.CollectionsUtils.setOf;
import static it.uniroma3.weir.configuration.Constants.MAX_PIVOT_DISTANCE;
import static it.uniroma3.weir.extraction.wrapper.Asserts.*;
import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures.*;
import static it.uniroma3.weir.fixture.WebpageFixture.*;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.model.Webpage;

import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

public class RelativeGeneratorTest extends WeirTest {
	
	static private Set<String> ruleSet(String... xpaths) {
		return setOf(xpaths);
	}
	
	@BeforeClass
	static public void setUp() {
		WeirConfig.getInstance().setProperty(LFEQ_SIZE_THRESHOLD, "2");		
		WeirConfig.getInstance().setProperty(LFEQ_SUPP_THRESHOLD, "0.2");		
		WeirConfig.getInstance().setProperty(MAX_PIVOT_DISTANCE, "4");
	}
	
	
	/* --- Tests on particular text formats --- --- */

	private static final String BASIC_XPATH = pivotXPath("P")+"/../text()[1]/self::text()";
	/* e.g.: //P[contains(text(),'pivot')]" */
	
	@Test
	public void testInferRules_basic() {
		assertARelativeXPath(BASIC_XPATH, pivotedPagesWithText("text"));
	}
	
	@Test
	public void testInferRules_whitespaces() {
		assertARelativeXPath(pivotXPath("BODY","te")+"/text()", pivotedPagesWithText("te xt"));
	}

	@Test
	public void testInferRules_minus_sign() {
		assertARelativeXPath(BASIC_XPATH, pivotedPagesWithText("te-xt"));
	}

	@Test
	public void testInferRules_underscore() {
		assertARelativeXPath(BASIC_XPATH, pivotedPagesWithText("te_xt"));
	}

	@Test
	public void testInferRules_slash() {
		assertARelativeXPath(BASIC_XPATH, pivotedPagesWithText("te/xt"));
	}

	@Test
	public void testInferRules_backslash() {
		assertARelativeXPath(BASIC_XPATH, pivotedPagesWithText("te\\xt"));
	}

	/* --- Tests on particular pivot formats --- --- */

	@Test
	public void testInferRules_basicPivot() {
		assertARelativeXPath(BASIC_XPATH, pivotedPagesWithPivot("pivot"));
	}
	
	@Test
	public void testInferRules_whitespacedPivot() {
		assertARelativeXPath(pivotXPath("P","pi")+
							 "/../text()[1]/self::text()", 
							 pivotedPagesWithPivot("pi vot"));
	}

	@Test
	public void testInferRules_minusPivot() {
		assertARelativeXPath(pivotXPath("P","pi-vot")+
				    		 "/../text()[1]/self::text()",
							 pivotedPagesWithPivot("pi-vot"));
	}

	@Test
	public void testInferRules_underscorePivot() {
		assertARelativeXPath(pivotXPath("P","pi_vot")+
							 "/../text()[1]/self::text()", 
							 pivotedPagesWithPivot("pi_vot"));
	}

	@Test
	public void testInferRules_slashPivot() {
		assertARelativeXPath(pivotXPath("P","pi/vot")+
				             "/../text()[1]/self::text()",
				             pivotedPagesWithPivot("pi/vot"));
	}

	@Test
	public void testInferRules_backslashPivot() {
		assertARelativeXPath(pivotXPath("P","pi\\vot")+
							 "/../text()[1]/self::text()", 
							 pivotedPagesWithPivot("pi\\vot"));
	}

	/* --- Tests with trees having only the pivot or only the data --- */
	
	@Test
	public void testInferRules_noPivot() {
		assertRelativeRules(ruleSet(), webpages(_HTML_TREE_("a"), _HTML_TREE_("b")));
	}
	
	@Test
	public void testInferRules_justPivot() {
		final String emptyPage = _HTML_TREE_("");
		assertRelativeRules(ruleSet(), webpages(emptyPage, emptyPage));
	}

	@Test
	public void testInferRules_Up() {
		assertARelativeXPath(
			    pivotXPath("P")+"/../text()[1]/self::text()",
				webpages(_HTML_TREE_("pa<P>pivot</P>"), 
						 _HTML_TREE_("pb<P>pivot</P>")));
	}

	@Test
	public void testInferRules_UpUp() {
		assertARelativeXPath(
			    pivotXPath("B")+"/../../text()[1]/self::text()",
				webpages(_HTML_TREE_("pa<P><B>pivot</B></P>"), 
						 _HTML_TREE_("pb<P><B>pivot</B></P>")));
	}

	@Test
	public void testInferRules_Up_2values() {
		final List<Webpage> w = webpages(
				UP_2_VALUES("p1a","p1a"), 
				UP_2_VALUES("p1b","p1b"));
		assertARelativeXPath(pivotXPath("P")+"/../text()[1]/self::text()", w);
		assertARelativeXPath(pivotXPath("P")+"/../text()[2]/self::text()", w);
	}

	@Test
	public void testInferRules_Down() {
		final List<Webpage> w = webpages(
				_HTML_TREE_("<P id='p'><B>" + "ca" +"</B></P>" ), 
				_HTML_TREE_("<P id='p'><B>" + "cb" +"</B></P>" ));
		assertARelativeXPath("//P[@id='p']/child::*[1]/text()[1]/self::text()", w);
	}

	@Test
	public void testInferRules_Down_2values() {
		final List<Webpage> w = webpages(
				DOWN_2_VALUES("c1a","c2a"), 
				DOWN_2_VALUES("c1b","c2b"));

		assertARelativeXPath(pivotXPath("BODY")+"/child::*[1]/text()[1]/self::text()", w);
		assertARelativeXPath(pivotXPath("BODY")+"/child::*[2]/text()[1]/self::text()", w);
	}

	@Test
	public void testInferRules_siblingsDown() {
		final List<Webpage> w = webpages(
				SIBLING_DOWN("c1a","c2a"), 
				SIBLING_DOWN("c1b","c2b"));
		assertARelativeXPath(pivotXPath("BODY")+"/child::*[1]/text()[1]/self::text()",w);
		assertARelativeXPath(pivotXPath("BODY")+"/child::*[1]/text()[2]/self::text()",w);		
	}

	/* --- Tests with trees (having pivot and data) on direction dx (following-sibling::*) --- */
	
	@Test
	public void testInferRules_dx() {
		final List<Webpage> w = webpages(TEXT_DX("sa"), TEXT_DX("sb"));
		assertARelativeXPath(pivotXPath("P")+"/following-sibling::node()[1]/text()[1]/self::text()",w);
		assertARelativeXPath(pivotXPath("P")+"/../child::*[2]/text()[1]/self::text()",w);
	}
	
	@Test
	public void testInferRules_multiDx() {
		final List<Webpage> w = webpages(
				MULTI_DX("s1a","s1b"), 
				MULTI_DX("s2a","s2b"));
		assertARelativeXPath(pivotXPath("P")+"/following-sibling::node()[1]/text()[1]/self::text()",w);
		assertARelativeXPath(pivotXPath("P")+"/following-sibling::node()[1]/following-sibling::node()[1]/text()[1]/self::text()",w);
	}
	
	@Test
	public void testInferRules_siblingsDx() {
		final List<Webpage> w = webpages(
				MULTI_SIBLING_DX("s1a","s2a"),
			    MULTI_SIBLING_DX("s1b","s2b"));
		assertARelativeXPath(pivotXPath("P")+"/following-sibling::node()[1]/text()[1]/self::text()",w);
		assertARelativeXPath(pivotXPath("P")+"/following-sibling::node()[1]/text()[2]/self::text()",w);
	}

	/* --- Tests with trees (having pivot and data) on direction sx (preceding-sibling::*) --- */

	@Test
	public void testInferRules_sx() {
		final List<Webpage> w = webpages(TEXT_SX("sa"), TEXT_SX("sb"));
		assertARelativeXPath(pivotXPath("P")+"/preceding-sibling::node()[1]/text()[1]/self::text()",w);
		assertARelativeXPath(pivotXPath("P")+"/../child::*[1]/text()[1]/self::text()",w);
	}

	@Test
	public void testInferRules_multiSx() {
		final List<Webpage> w = webpages(
				MULTI_SX("s1a","s1b"), 
				MULTI_SX("s2a","s2b"));
		assertARelativeXPath(pivotXPath("P")+"/preceding-sibling::node()[1]/text()[1]/self::text()",w);
		assertARelativeXPath(pivotXPath("P")+"/preceding-sibling::node()[1]/preceding-sibling::node()[1]/text()[1]/self::text()",w);
	}
	
	@Test
	public void testInferRules_siblingsSx() {
		final List<Webpage> w = webpages(
				MULTI_SIBLING_SX("s1a","s1b"), 
				MULTI_SIBLING_SX("s2a","s2b"));
		assertARelativeXPath(pivotXPath("P")+"/preceding-sibling::node()[1]/text()[1]/self::text()",w);
		assertARelativeXPath(pivotXPath("P")+"/preceding-sibling::node()[1]/text()[2]/self::text()",w);
	}


	/* --- Tests with trees having two pivots --- */

	@Test
	public void testInferRules_2pivots() {
		final List<Webpage> w = webpages(DOUBLE_PIVOT("ta"), DOUBLE_PIVOT("tb"));
		assertARelativeXPath(pivotXPath("BODY","pivot_1")+"/child::*[1]/text()[1]/self::text()",w);
		assertARelativeXPath(pivotXPath("P","pivot_2")+"/preceding-sibling::node()[1]/text()[1]/self::text()",w);
	}


	@Test
	public void testInferRules_2values2pivots() {
		final List<Webpage> w = webpages(
			DOUBLE_PIVOT_DOUBLE_TEXT("t1a","t2a"), 
			DOUBLE_PIVOT_DOUBLE_TEXT("t1b","t2b"));
		assertARelativeXPath(pivotXPath("BODY","pivot_1")+"/child::*[1]/text()[1]/self::text()", w);
		assertARelativeXPath(pivotXPath("BODY","pivot_1")+"/child::*[2]/child::*[1]/text()[1]/self::text()", w);
		assertARelativeXPath(pivotXPath("P","pivot_2")+"/child::*[1]/text()[1]/self::text()", w);
		assertARelativeXPath(pivotXPath("P","pivot_2")+"/preceding-sibling::node()[1]/text()[1]/self::text()", w);
	}

	@Test
	public void testInferRules_multi2pivots() {
		assertAtLeastTheseRelativeRules(ruleSet(
				pivotXPath("BODY","pivot_1")+"/child::*[1]/text()[1]/self::text()", 
				pivotXPath("BODY","pivot_1")+"/child::*[1]/text()[2]/self::text()", 
				pivotXPath("BODY","pivot_1")+"/child::*[2]/child::*[1]/text()[1]/self::text()", 
				pivotXPath("BODY","pivot_1")+"/child::*[2]/child::*[1]/text()[2]/self::text()", 
				pivotXPath("P","pivot_2")+"/preceding-sibling::node()[1]/text()[1]/self::text()", 
				pivotXPath("P","pivot_2")+"/preceding-sibling::node()[1]/text()[2]/self::text()", 
				pivotXPath("P","pivot_2")+"/child::*[1]/text()[1]/self::text()", 
				pivotXPath("P","pivot_2")+"/child::*[1]/text()[2]/self::text()"), 
				webpages(
					_DOUBLE_PIVOT_MULTI_("t1a","t2a","t3a","t4a"), 
					_DOUBLE_PIVOT_MULTI_("t1b","t2b","t3b","t4b"))
				);
	}

	/* --- Tests with trees (having pivot and data) on all directions --- */

	@Test
	public void testInferRules_allDirections() {
		assertAtLeastTheseRelativeRules(
				ruleSet(pivotXPath("P")+"/child::*[1]/text()[1]/self::text()", 
						pivotXPath("P")+"/following-sibling::node()[1]/text()[1]/self::text()", 
						pivotXPath("P")+"/preceding-sibling::node()[1]/text()[1]/self::text()"), 
				webpages(SURROUNDED_PIVOT("pa","sa","ca","sa"), 
						 SURROUNDED_PIVOT("pb","sb","cb","sb")));
	}
	
}
