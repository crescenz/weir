package it.uniroma3.weir.evaluation;

import static it.uniroma3.weir.fixture.AbstractAttributeFixture.*;
import static it.uniroma3.weir.configuration.Constants.REMOVE_MATCHING_ATTRIBUTE;
import static it.uniroma3.weir.fixture.AbstractRelationFixture.createAbstractRelation;
import static it.uniroma3.weir.fixture.MappingFixture.*;
import static it.uniroma3.weir.fixture.MappingSetFixture.createMappingSet;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.evaluation.integration.MappingSetEvaluator;
import it.uniroma3.weir.fixture.Asserts;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.MappingSet;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.hiddenrelation.AbstractRelation;

import org.junit.BeforeClass;
import org.junit.Test;

import static it.uniroma3.weir.evaluation.integration.MatchEvaluator.EQUALITY;
public class MappingSetTest {

	static private Domain domain;

	@BeforeClass
	public static void setUp() {
		domain = new Domain("test");
		domain.addSite(new Website("w1"));
		domain.addSite(new Website("w2"));
		//was:	List<Website> wss = new ArrayList<Website>();
		//		wss.add(new Website("w1"));
		//		wss.add(new Website("w2"));
		//		
		//		Experiment.setWebsites(wss);
	}
	
	private void assertEqualsDonotConsumeMatching(PRF expected, AbstractRelation H, MappingSet golden) {
		assertEvaluation(false,expected,H,golden);
	}

//	private void assertEquals(PRF expected, AbstractRelation H, MappingSet golden) {
//		assertEvaluation(true,expected,H,golden);
//	}

	private void assertEvaluation(boolean removeMatching, PRF expected, AbstractRelation H, MappingSet golden) {
		WeirConfig.getInstance().setProperty(REMOVE_MATCHING_ATTRIBUTE, Boolean.toString(removeMatching));
		MappingSetEvaluator seeker = new MappingSetEvaluator(EQUALITY);
		PRF result = seeker.evaluate(
				H.getExperiment().getGoldenMappingSet().getMappings(),
				H.getAbstractAsMappings());
		Asserts.assertEvaluationResult(expected, result);
	}
	
	@Test
	public void testAnalyze_singleton_vs_singleton() {
		assertEqualsDonotConsumeMatching(new PRF(1.0, 1.0, 1, 0, 0), createAbstractRelation(createSingletonAbstractAttribute()), createMappingSet(createSingletonMapping()));
	}

	@Test
	public void testAnalyze_doubleton_vs_doubleton() {
		assertEqualsDonotConsumeMatching(new PRF(1.0, 1.0, 2, 0, 0), createAbstractRelation(createDoubletonAbstractAttribute()), createMappingSet(createDoubletonMapping()));
	}

	@Test
	public void testAnalyze_singleton_vs_doubleton() {
		assertEqualsDonotConsumeMatching(new PRF(1.0, 0.5, 1, 1, 0), createAbstractRelation(createSingletonAbstractAttribute()), createMappingSet(createDoubletonMapping()));
	}

	@Test
	public void testAnalyze_doubleton_vs_singleton() {
		assertEqualsDonotConsumeMatching(new PRF(0.5, 1.0, 1, 0, 1), createAbstractRelation(createDoubletonAbstractAttribute()), createMappingSet(createSingletonMapping()));
	}

	@Test
	public void testAnalyze_wrong_rule() {
		assertEqualsDonotConsumeMatching(new PRF(0.0, 0.0, 0, 1, 1), createAbstractRelation(createSingletonAbstractAttributeWithDifferentData()), createMappingSet(createSingletonMapping()));
	}

	@Test
	public void testAnalyze_two_vs_two_AllMatching() {
		assertEqualsDonotConsumeMatching(new PRF(1.0, 1.0, 2, 0, 0), createAbstractRelation(createSingletonAbstractAttribute(), createSingletonAbstractAttributeWithDifferentData()), createMappingSet(createSingletonMapping(), createSingletonMappingWithDifferentData()));
	}

	@Test
	public void testAnalyze_two_vs_two_OneMatchingOneDoesnot() {
		assertEqualsDonotConsumeMatching(new PRF(0.5, 0.5, 1, 1, 2), createAbstractRelation(createSingletonAbstractAttribute(), createDoubletonAbstractAttribute()), createMappingSet(createSingletonMapping(), createSingletonMappingWithDifferentData()));
	}

	@Test
	public void testAnalyze_one_vs_two_GoldenMatching() {
		assertEqualsDonotConsumeMatching(new PRF(1.0, 1.0, 2, 0, 0), createAbstractRelation(createSingletonAbstractAttribute()), createMappingSet(createSingletonMapping(), createSingletonMapping()));
	}

	/* --- Tests with removing matching abstract attribute */

	@Test
	public void testEvaluateByRemovingMatching_singleton_vs_singleton() {
		assertEqualsDonotConsumeMatching(new PRF(1.0, 1.0, 1, 0, 0), createAbstractRelation(createSingletonAbstractAttribute()), createMappingSet(createSingletonMapping()));
	}

	@Test
	public void testEvaluateByRemovingMatching_doubleton_vs_doubleton() {
		assertEqualsDonotConsumeMatching(new PRF(1.0, 1.0, 2, 0, 0), createAbstractRelation(createDoubletonAbstractAttribute()), createMappingSet(createDoubletonMapping()));
	}

	@Test
	public void testEvaluateByRemovingMatching_singleton_vs_doubleton() {
		assertEqualsDonotConsumeMatching(new PRF(1.0, 0.5, 1, 1, 0), createAbstractRelation(createSingletonAbstractAttribute()), createMappingSet(createDoubletonMapping()));
	}

	@Test
	public void testEvaluateByRemovingMatching_doubleton_vs_singleton() {
		assertEqualsDonotConsumeMatching(new PRF(0.5, 1.0, 1, 0, 1), createAbstractRelation(createDoubletonAbstractAttribute()), createMappingSet(createSingletonMapping()));
	}

	@Test
	public void testEvaluateByRemovingMatching_wrong_rule() {
		assertEqualsDonotConsumeMatching(new PRF(0.0, 0.0, 0, 1, 1), createAbstractRelation(createSingletonAbstractAttributeWithDifferentData()), createMappingSet(createSingletonMapping()));
	}

	@Test
	public void testEvaluateByRemovingMatching_two_vs_two_AllMatching() {
		assertEqualsDonotConsumeMatching(new PRF(1.0, 1.0, 2, 0, 0), createAbstractRelation(createSingletonAbstractAttribute(), createSingletonAbstractAttributeWithDifferentData()), createMappingSet(createSingletonMapping(), createSingletonMappingWithDifferentData()));
	}

	@Test
	public void testEvaluateByRemovingMatching_two_vs_two_OneMatchingOneDoesnot() {
		assertEqualsDonotConsumeMatching(new PRF(0.5, 0.5, 1, 1, 2), createAbstractRelation(createSingletonAbstractAttribute(), createDoubletonAbstractAttribute()), createMappingSet(createSingletonMapping(), createSingletonMappingWithDifferentData()));
	}

	@Test
	public void testEvaluateByRemovingMatching_one_vs_two_GoldenMatching() {
		assertEqualsDonotConsumeMatching(new PRF(0.5, 0.5, 1, 1, 0), createAbstractRelation(createSingletonAbstractAttribute()), createMappingSet(createSingletonMapping(), createSingletonMapping()));
	}
	
}
