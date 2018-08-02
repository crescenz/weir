package it.uniroma3.weir.fixture;

import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.evaluation.PRF;

public class Asserts {

	static final public double EXACT_EQUALS_TOLERANCE = 0;
	
	static final private double DOUBLE_TOLERANCE = 0.001;

	static final public void assertEvaluationResult(PRF expected, PRF actual) {
		
		assertExactEquals(expected.getPrecision(), actual.getPrecision());
		assertExactEquals(expected.getRecall(), actual.getRecall());
		assertExactEquals(expected.getFMeasure(), actual.getFMeasure());
		
		assertEquals(expected.getTruePositives(), actual.getTruePositives());
		assertEquals(expected.getFalseNegatives(), actual.getFalseNegatives());
		assertEquals(expected.getFalsePositives(), actual.getFalsePositives());
		
	}

	static final private void assertExactEquals(double expected, double value) {
		assertEquals(expected, value, EXACT_EQUALS_TOLERANCE);
	}

	static final public void assertDoubleEquals(double expected, double actual) {
		assertEquals(expected, actual, DOUBLE_TOLERANCE);
	}
}
