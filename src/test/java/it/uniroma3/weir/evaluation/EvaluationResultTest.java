package it.uniroma3.weir.evaluation;

import static it.uniroma3.weir.fixture.Asserts.*;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class EvaluationResultTest {
	
	private PRF result;
	
	private PRFBuilder builder;
	
	@Before
	public void setUp() {
		this.builder = new PRFBuilder();
	}

	@Test
	public void testFMeasure_ZeroPrecision() {
		assertDoubleEquals(0d, new PRF(0d, 1d, 0, 0, 0).getFMeasure());
	}

	@Test
	public void testFMeasure_ZeroRecall() {
		assertDoubleEquals(0d, new PRF(1d, 0d, 0, 0, 0).getFMeasure());
	}

	@Test
	public void testFMeasure_when_precision_and_recall_are_greater_than_zero() {
		assertDoubleEquals(0.66, new PRF(0.5, 1.0, 0, 0, 0).getFMeasure());
	}

	@Test
	public void testGetTotalExpectedFound_after_creation() {
		assertEquals(0, result.getTruePositives());
	}

	@Test
	public void testGetTotalExpectedNotFound_after_creation() {
		assertEquals(0, result.getFalseNegatives());
	}

	@Test
	public void testGetTotalFoundNotExpected_after_creation() {
		assertEquals(0, result.getFalsePositives());
	}

	@Test
	public void testUpdateResult() {
		builder.add(new PRF(1.0, 1.0, 2, 3, 4));
		assertEvaluationResult(new PRF(1, 1, 2, 3, 4), builder.getResultOver(1));
	}

	@Test
	public void testUpdateResult_more_updates() {
		builder.add(new PRF(1.0, 1.0, 2, 3, 4));
		builder.add(new PRF(0.5, 0.5, 2, 1, 0));
		assertEvaluationResult(new PRF(1.5, 1.5, 4, 4, 4), builder.getResultOver(1));
	}

	@Test
	public void testFinalizeResult() {
		builder.add(new PRF(1.0, 1.0, 2, 3, 4));
		builder.add(new PRF(0.5, 0.5, 2, 1, 0));		
		assertEvaluationResult(new PRF(0.75, 0.75, 4, 4, 4), builder.getResultOver(2));
	}

}
