package it.uniroma3.weir.vector;

import static it.uniroma3.weir.fixture.Asserts.assertDoubleEquals;
import static it.uniroma3.weir.fixture.VectorFixture.createExtractedVector;
import static it.uniroma3.weir.fixture.VectorFixture.createNumericVector;
import static java.lang.Double.NaN;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.linking.linkage.PageLinkageIterator;

import org.junit.Before;
import org.junit.Test;


public class DistanceFunctionTest extends WeirTest {

	private DistanceFunction euclidean;
	
	@Before
	public void setUp() throws Exception {
		this.euclidean = DistanceFunction.EUCLIDEAN;
		this.euclidean.initFor(null);
	}

	@Test
	public void testStandardizeEucleudianDistance_length1() {

		assertDistance(1d, /* 1 is the max normalized distance */
				vector(0, 1), 
				vector(1, 0)
		);
	}
	
	@Test
	public void testStandardizeEucleudianDistance_length2() {

		assertDistance(1d, /* 1 is the max normalized distance */
				vector(0, 2), 
				vector(2, 0)
		);
	}

	@Test
	public void testStandardizeEucleudianDistance_length3d() {

		assertDistance(2d/3d,
				vector( 1,  0,  1), 
				vector( 0,  1,  0)
		);
	}

	@Test
	public void testStandardizeEucleudianDistance_length4d() {

		assertDistance(0.5d,
				vector(0, 1, 0, 1), 
				vector(1, 0, 1, 0)
		);
	}
	
	@Test
	public void testStandardizeEucleudianDistance_constantVectors() {
		/* once constant vectors are translated back to the origin, they de-
		 * generated into the null-vector and their distance is not defined 
		 */
		assertDistance(NaN,
				vector(1), 
				vector(1)
		);
		assertDistance(NaN,
				vector(1,1,1), 
				vector(1,1,1)
		);
	}
	
	
	@Test
	public void testDistanceScaleInvariant() {
		assertDistance(0,
				vector( 1,   2,   3   ), 
				vector( 1*2, 2*2, 3*2 )
		);		
	}

	@Test
	public void testDistanceScaleAndTranslationInvariant() {
		final NumericVector celsius    = vector( 1,        2,        3        );
		final NumericVector fahrenheit = vector( 1*1.8+32, 2*1.8+32, 3*1.8+32 );
		assertDistance(0,
				celsius, 
				fahrenheit
		);		
	}

	@Test
	public void testSpaceMeterVsFoot() {
		final NumericVector height_in_meters = (NumericVector) 
				createNormalizedVector("6\'8\"","6\'8\"","6\'5\"");
		final NumericVector height_in_foots = (NumericVector) 
				createNormalizedVector("2.032m","2.032m","1.903m");
		assertDistance(0, height_in_meters, height_in_foots);
	}
	
	@Test
	public void testSpaceFootVsMeterAdimensioned() {
		final NumericVector height_adimens = (NumericVector) 
				createNormalizedVector("6\'8\"","6\'8\"","6\'5\"");
		final NumericVector height_in_foots = (NumericVector) 
				createNormalizedVector("2.032","2.032","1.903");
		assertDistance(0, height_adimens, height_in_foots);
	}

	@Test
	public void testSpaceFootVsCentimeterAdimensioned() {
		final NumericVector height_adimens = (NumericVector) 
				createNormalizedVector("6\'8\"","6\'8\"","6\'5\"");
		final NumericVector height_in_foots = (NumericVector) 
				createNormalizedVector("203.2","203.2","190.3");
		assertDistance(0, height_adimens, height_in_foots);
	}

	private Vector createNormalizedVector(String...values) {
		return createExtractedVector(values).normalize();
	}
	

	private void assertDistance(final double expected, final NumericVector v1, final NumericVector v2) {
		assertDoubleEquals(expected, distance(v1,v2));
	}

	private double distance(NumericVector v1, NumericVector v2) {
		return this.euclidean.distance(PageLinkageIterator.pairwiseIterator(v1, v2));
	}

	static final private NumericVector vector(Object...values) {
		return createNumericVector(values);
	}
	
}
