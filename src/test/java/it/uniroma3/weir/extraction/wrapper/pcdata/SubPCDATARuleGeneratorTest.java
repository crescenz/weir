package it.uniroma3.weir.extraction.wrapper.pcdata;

import static it.uniroma3.weir.fixture.VectorFixture.createExtractedVector;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.extraction.rule.SubPCDATARule;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.vector.ExtractedVector;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SubPCDATARuleGeneratorTest extends WeirTest {

	static final private String BASE_RULE_EXP = "//text()";

	static final private ExtractionRule BASE_RULE = new ExtractionRule(null,BASE_RULE_EXP);
	
	private SubPCDATARuleGenerator generator;
	
	@Before
	public void setUp() throws Exception {
		this.generator = new SubPCDATARuleGenerator(null);
	}

	@Test
	public void testRefine_noneInvariantAmongPCDATA() {
		testShot(rules(), "var0","var1","var2");
	}

	@Test
	public void testRefine_singleInvariantVariantOnTheRight() {
		testShot(rules("substring-after("+BASE_RULE_EXP+",'inv')"), "inv var0","inv var1","inv var2");
	}

	@Test
	public void testRefine_singleInvariantVariantOnTheLeft() {
		testShot(rules("substring-before("+BASE_RULE_EXP+",'inv')"), "var0 inv","var1 inv","var2 inv");
	}	
	
	@Test
	public void testRefine_leftAndRightOfInvariant () {
		testShot(rules(
				"substring-before("+BASE_RULE_EXP+",'inv')",
				"substring-after("+BASE_RULE_EXP+",'inv')"
				), 
				"var0 inv var3",
				"var1 inv var4",
				"var2 inv var5");
	}

	
	@Test
	public void testRefine_twoEmbeddingInvariants() {
		testShot(rules(
				"substring-before(substring-after("+BASE_RULE_EXP+",'inv1'),'inv2')"
				), 
				"inv1 var0 inv2",
				"inv1 var1 inv2",
				"inv1 var2 inv2");
	}

	@Test
	public void testRefine_twoEmbeddedVariants () {
		testShot(rules(
				"substring-before(substring-after("+BASE_RULE_EXP+",'inv1'),'inv2')",
				"substring-before(substring-after("+BASE_RULE_EXP+",'inv2'),'inv3')"
				), 
				"inv1 var0 inv2 var3 inv3",
				"inv1 var1 inv2 var4 inv3",
				"inv1 var2 inv2 var5 inv3");
	}	
	
	@Test
	public void testRefine_twoAndDuplicateInvariant() {
		testShot(rules(
				"substring-before(substring-after("+BASE_RULE_EXP+",'inv'),'inv')"
				), 
				"inv var0 inv",
				"inv var1 inv",
				"inv var2 inv");
	}
	
	@Test
	public void testRefine_twoAndDuplicateInvariantVariantsOnTheRight() {
		testShot(rules(
				"substring-after(substring-after("+BASE_RULE_EXP+",'inv'),'inv')"
				), 
				"inv inv var0",
				"inv inv var1",
				"inv inv var2");
	}

	@Test
	public void testRefine_twoAndDuplicateInvariantVariantsOnTheLeft() {
		testShot(rules(
				"substring-before(substring-after(substring-after("+BASE_RULE_EXP+",'invd'),'invd'),'inv')"
				), 
				"invd invd var0 inv",
				"invd invd var1 inv",
				"invd invd var2 inv");
	}

	@Test
	public void testRefine_twoAndDuplicateSeparatedInvariant() {
		testShot(rules(
				"substring-before(substring-after("+BASE_RULE_EXP+",'inv'),'invd')"
				), 
				"invd inv var0 invd",
				"invd inv var1 invd",
				"invd inv var2 invd");
	}
	
	static private List<ExtractionRule> rules(String...ruleExps) {
		final ArrayList<ExtractionRule> result = new ArrayList<>();
		for(String exp : ruleExps)
			result.add(new SubPCDATARule(BASE_RULE, exp));
		return result;
	}
	
	public void testShot(List<ExtractionRule> expected, String...values) {
		final ExtractedVector ev = createExtractedVector(BASE_RULE,values);
		final List<ExtractionRule> actual = this.generator.refine(ev);
		assertEquals(expected, actual);
	}

}
