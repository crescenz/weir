package it.uniroma3.weir.evaluation;

import static it.uniroma3.weir.fixture.MappingFixture.*;
import it.uniroma3.weir.evaluation.integration.MappingEvaluator;
import it.uniroma3.weir.fixture.Asserts;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.model.Website;

import org.junit.BeforeClass;
import org.junit.Test;

public class MappingEvaluatorTest {

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

	private void testAnalyze(PRF expectedResult, Mapping outputMapping, Mapping goldenMapping) {
		final MappingEvaluator evaluator = new MappingEvaluator();
		final PRF result = evaluator.evaluate(goldenMapping, outputMapping);
		Asserts.assertEvaluationResult(expectedResult, result);
	}

	@Test
	public void test_empty_mapping_vs_empty_mapping() {
		testAnalyze(new PRF(1.0, 1.0, 0, 0, 0), createEmptyMapping(), createEmptyMapping());
	}

	@Test
	public void test_empty_mapping_vs_singleton_mapping() {
		testAnalyze(new PRF(1.0, 0.0, 0, 1, 0), createEmptyMapping(), createSingletonMapping());
	}

	@Test
	public void test_singleton_mapping_vs_empty_mapping() {
		testAnalyze(new PRF(0.0, 1.0, 0, 0, 1), createSingletonMapping(), createEmptyMapping());
	}

	@Test
	public void test_singleton_mapping_vs_singleton_mapping() {
		testAnalyze(new PRF(1.0, 1.0, 1, 0, 0), createSingletonMapping(), createSingletonMapping());
	}

	@Test
	public void test_singleton_mapping_vs_singleton_mapping_with_different_data_type() {
		testAnalyze(new PRF(0.0, 0.0, 0, 1, 1), createSingletonMapping(), createSingletonMappingWithDifferentDataType());
	}

	@Test
	public void test_singleton_mapping_vs_singleton_mapping_with_different_data() {
		testAnalyze(new PRF(0.0, 0.0, 0, 1, 1), createSingletonMapping(), createSingletonMappingWithDifferentData());
	}

	@Test
	public void test_singleton_mapping_vs_singleton_mapping_with_different_website() {
		testAnalyze(new PRF(0.0, 0.0, 0, 1, 1), createSingletonMapping(), createSingletonMappingWithDifferentWebsite());
	}

	@Test
	public void test_singleton_mapping_vs_doubleton_mapping() {
		testAnalyze(new PRF(1.0, 0.5, 1, 1, 0), createSingletonMapping(), createDoubletonMapping());
	}

	@Test
	public void test_doubleton_mapping_vs_singleton_mapping() {
		testAnalyze(new PRF(0.5, 1.0, 1, 0, 1), createDoubletonMapping(), createSingletonMapping());
	}

}
