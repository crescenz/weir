package it.uniroma3.weir.extraction.wrapper;


import static it.uniroma3.ecgm.Constants.LFEQ_SIZE_THRESHOLD;
import static it.uniroma3.ecgm.Constants.LFEQ_SUPP_THRESHOLD;
import static it.uniroma3.token.Constants.SEPARATOR_CHARS;
import static it.uniroma3.util.CollectionsUtils.setOf;
import static it.uniroma3.weir.configuration.Constants.MAX_PIVOT_DISTANCE;
import static it.uniroma3.weir.extraction.wrapper.Asserts.assertRelativeRules;
import static it.uniroma3.weir.extraction.wrapper.Asserts.assertRelativeXPathsAtRange;
import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures.*;
import static it.uniroma3.weir.fixture.WebpageFixture.webpages;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.model.Webpage;

import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;


public class RelativeXPathBuilderTest extends WeirTest {

	static private Set<String> ruleSet(String... xpaths) {
		return setOf(xpaths);
	}

	@BeforeClass
	static public void setUp() {
		WeirConfig.getInstance().setProperty(MAX_PIVOT_DISTANCE,  "4");
		WeirConfig.getInstance().setProperty(LFEQ_SIZE_THRESHOLD, "2");		
		WeirConfig.getInstance().setProperty(LFEQ_SUPP_THRESHOLD, "0.5");		
		WeirConfig.getInstance().setProperty(SEPARATOR_CHARS, "");		
	}

	@Test
	public void testNowCanPivotXPathsOnIntraPCDATApivot() {
		assertRelativeRules(ruleSet(
					pivotXPath("P","pivot")+"/text()"
				), 	
				webpages(
						INTRA_PCDATA_PIVOT("t1a "," t2a"), 
						INTRA_PCDATA_PIVOT("t1b "," t2b"))
				);
	}

	@Test
	public void testGenerateXPaths_up0or1() {
		final List<Webpage> w = webpages(TEXT_UP("p1"), TEXT_UP("p2"));
		assertRelativeXPathsAtRange(0, ruleSet(), w); 
		assertRelativeXPathsAtRange(1, ruleSet(xPath("B","/preceding-sibling::node()[1]")), w);
	}

	@Test
	public void testGenerateXPaths_up2() {
		/* check it get to every value */
		assertRelativeXPathsAtRange(2, 
				ruleSet(xPath("B","/preceding-sibling::node()[1]"), 
						xPath("B","/../text()[1]")), 
						webpages(TEXT_UP("p1"), TEXT_UP("p2")));
	}

	@Test
	public void testGenerateXPaths_up5() {
		/* check it get to every value */
		assertRelativeXPathsAtRange(5, 
				ruleSet(xPath("B","/preceding-sibling::node()[1]"),
						xPath("B","/following-sibling::node()[1]/../text()[1]"), 
						xPath("B","/../text()[1]")
						), 
						webpages(TEXT_UP("p1"), TEXT_UP("p2")));
	}


	@Test
	public void testGenerateXPaths_down0_1() {
		final List<Webpage> w = webpages(TEXT_DOWN("c1"), TEXT_DOWN("c2"));
		assertRelativeXPathsAtRange(0, ruleSet(), w);
		assertRelativeXPathsAtRange(1, ruleSet(), w);
	}

	@Test
	public void testGenerateXPaths_down2() {
		assertRelativeXPathsAtRange(2, 
				ruleSet(xPath("BODY","/text()[1]")), 
				webpages(TEXT_DOWN("c1"), TEXT_DOWN("c2")));
	}

	@Test
	public void testGenerateXPaths_down5() {
		/* check it get to every value */
		assertRelativeXPathsAtRange(5, 
				ruleSet(xPath("BODY","/child::*[1]/text()[1]")), 
				webpages(TEXT_DOWN("c1"), TEXT_DOWN("c2")));
	}


	@Test
	public void testGenerateXPaths_dx0or1() {
		final List<Webpage> w = webpages(TEXT_DX("t1"), TEXT_DX("t2"));
		assertRelativeXPathsAtRange(0, ruleSet(), w);
		assertRelativeXPathsAtRange(1, ruleSet(), w);
	}

	@Test
	public void testGenerateXPaths_dx2() {
		assertRelativeXPathsAtRange(2, 
				ruleSet(xPath("P","/following-sibling::node()[1]/text()[1]")), 
				webpages(TEXT_DX("t1"), TEXT_DX("t2")));
	}

	@Test
	public void testGenerateXPaths_dx5() {
		/* check it get to every value */
		assertRelativeXPathsAtRange(5, 
				ruleSet(xPath("P","/following-sibling::node()[1]/text()[1]"), // DX, DW
						xPath("P","/../child::*[2]/text()[1]")),              // UP, DW, DW
						webpages(TEXT_DX("t1"), TEXT_DX("t2")));
	}


	@Test
	public void testGenerateXPaths_sx0or1() {
		final List<Webpage> w = webpages(TEXT_SX("t1"), TEXT_SX("t2"));
		assertRelativeXPathsAtRange(0, ruleSet(), w);
		assertRelativeXPathsAtRange(1, ruleSet(), w);

	}

	@Test
	public void testGenerateXPaths_sx2() {
		assertRelativeXPathsAtRange(2, 
				ruleSet(xPath("P","/preceding-sibling::node()[1]/text()[1]")), 
				webpages(TEXT_SX("t1"), TEXT_SX("t2")));
	}

	@Test
	public void testGenerateXPaths_sx5() {
		assertRelativeXPathsAtRange(5, 
				ruleSet(xPath("P","/../child::*[1]/text()[1]"),
						xPath("P","/preceding-sibling::node()[1]/text()[1]")), 
						webpages(TEXT_SX("t1"), TEXT_SX("t2")));
	}


	private final List<Webpage> surrounded = webpages(
			SURROUNDED_PIVOT("t0a","t1a","t2a","t3a"),
			SURROUNDED_PIVOT("t0b","t1b","t2b","t3b")
			);

	@Test
	public void testGenerateXPaths_allDir0or1() {
		assertRelativeXPathsAtRange(0, ruleSet(), surrounded);
		assertRelativeXPathsAtRange(1, ruleSet(), surrounded);
	}

	@Test
	public void testGenerateXPaths_allDir3() {
		assertRelativeXPathsAtRange(3, 
				ruleSet(xPath("P","/../text()[1]"), 
						xPath("P","/child::*[1]/text()[1]"), 
						xPath("P","/preceding-sibling::node()[1]/text()[1]")), 
						surrounded);
	}

	@Test
	public void testGenerateXPaths_allDir4() {
		assertRelativeXPathsAtRange(4, 
				ruleSet(xPath("P","/../text()[1]"), 
						xPath("P","/../../text()[1]"), 
						xPath("P","/child::*[1]/text()[1]"), 
						xPath("P","/following-sibling::node()[1]/text()[1]"), 
						xPath("P","/../child::*[3]/text()[1]")
						), 
						surrounded);
	}

	@Test
	public void testGenerateXPaths_allDir5() {
		assertRelativeXPathsAtRange(5, 
				ruleSet(xPath("P","/../text()[1]"),
						xPath("P","/../../text()[1]"),
						xPath("P","/child::*[1]/text()[1]"),
						xPath("P","/following-sibling::node()[1]/text()[1]"), 
						xPath("P","/../child::*[3]/text()[1]"),
						xPath("P","/preceding-sibling::node()[1]/../child::*[3]/text()[1]")), 
						surrounded);
	}


	@Test
	public void testGenerateXPaths_linearPath0() {
		assertRelativeXPathsAtRange(0, ruleSet(), 
				webpages(LONG_PATHS("t1a", "t2a"), 
						LONG_PATHS("t2a", "t2b")));
	}

	@Test
	public void testGenerateXPaths_linearPath2() {
		assertRelativeXPathsAtRange(2, 
				ruleSet(xPath("O","/../../../preceding-sibling::node()[1]/child::*[1]/child::*[1]/child::*[1]/text()[1]")), 
				webpages(LONG_PATHS("t1a", "t2a"), LONG_PATHS("t2a", "t2b")));
	}

	@Test
	public void testGenerateXPaths_linearPath4() {
		assertRelativeXPathsAtRange(4, 
				ruleSet(xPath("O","/../../../preceding-sibling::node()[1]/child::*[1]/child::*[1]/child::*[1]/text()[1]"), 
						xPath("O","/../../../following-sibling::node()[1]/child::*[2]/child::*[2]/text()[1]")), 
						webpages(LONG_PATHS("t1a", "t2a"), LONG_PATHS("t2a", "t2b")));
	}

	/* the following tests have been added after the generalization to sub PCDATA extraction */
	@Test
	public void testGenerateRelativeXPathsSubPCDATA_variantLeftOfInvariant() {
		assertRelativeRules(
				ruleSet(
						pivotXPath("P","pivot")+"/text()"
				),
				webpages(
						INTRA_PCDATA_PIVOT("leftVariantA ",""), 
						INTRA_PCDATA_PIVOT("leftVariantB ",""))
				);
	}

	@Test
	public void testGenerateRelativeXPathsSubPCDATA_variantRightOfInvariant() {
		assertRelativeRules(
				ruleSet(
						pivotXPath("P","pivot")+"/text()"
				),
				webpages(
						INTRA_PCDATA_PIVOT("", " rightVariantA "), 
						INTRA_PCDATA_PIVOT("", " rightVariantB "))
				);
	}

	@Test
	public void testGenerateRelativeXPathsSubPCDATA_invariantInTheMiddleOfVariants() {
		assertRelativeRules(
				ruleSet(
						pivotXPath("P","pivot")+"/text()"
				),
				webpages(
						INTRA_PCDATA_PIVOT("leftVariantA ", " rightVariantA"), 
						INTRA_PCDATA_PIVOT("leftVariantB ", " rightVariantB"))
				);
	}
	
	@Test
	public void testGenerateRelativeXPathsSubPCDATA_variantOutsidePCDATAwithInvariant() {
		assertRelativeRules(
				ruleSet(
// before intra-PCDATA 			xPath("P","/../following-sibling::node()[1]/text()[1]"),
// variants handling was:		xPath("P","/../text()[1]")
						pivotXPath("P","pivot")+"/text()"
				),
				webpages(
						_PIVOTED_("var0 pivot var2", "variantA"), 
						_PIVOTED_("var1 pivot var3", "variantB"))
				);
	}

	/**/
	static private String xPath(String pivotName, String p2l) {
		return pivotXPath(pivotName,"pivot")+ p2l + "/self::text()";
	}


}
