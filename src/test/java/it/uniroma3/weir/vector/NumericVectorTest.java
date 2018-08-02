package it.uniroma3.weir.vector;


import static it.uniroma3.weir.fixture.Asserts.assertDoubleEquals;
import static it.uniroma3.weir.fixture.VectorFixture.createNumericVector;
import it.uniroma3.weir.fixture.WeirTest;

import org.junit.Before;
import org.junit.Test;
import static java.lang.Math.sqrt;

public class NumericVectorTest extends WeirTest {
	
	private NumericVector unity;
	
	@Before
	public void setUp() {
		this.unity = createNumericVector(1,1,1);		
	}
	
	@Test
	public void testAverage() {
		assertDoubleEquals(1, unity.getAvg());
	}

	@Test
	public void testStandardDeviation() {
		assertDoubleEquals(0, unity.getStd());
	}

	@Test
	public void testMagnitudeUnity3D() {
		assertDoubleEquals(sqrt(3), unity.getModulo());
	}

	@Test
	public void testMagnitude() {
		assertDoubleEquals(5, createNumericVector(3,4).getModulo());
	}
	
	
	@Test
	public void testNormalizedMagnitude() {
		assertDoubleEquals(sqrt(2), createNumericVector(-2,2).getNormMod());
	}

	
}
