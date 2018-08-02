package it.uniroma3.weir.lc;

import static it.uniroma3.weir.fixture.VectorFixture.createVector;
import static it.uniroma3.weir.fixture.VectorFixture.createVectorList;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.configuration.Constants;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.integration.lc.LocalConsistencyEnforcer;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.type.Type;

import java.util.List;

import org.junit.Test;

public class LCEnforcerTest {

	private static void assertExpectedVectors(List<Vector> expectedVectors, List<Vector> extractedVectors) {

		WeirConfig.getInstance().setProperty(Constants.LOCAL_CONSISTENCY_THRESHOLD, "1.0");
		LocalConsistencyEnforcer lce = new LocalConsistencyEnforcer();
		assertEquals(expectedVectors, lce.enforceLocalConsistency(extractedVectors));
	}

	@Test
	public void testCluster_one_vector_generates_one_vector() {
		assertExpectedVectors(createVectorList(createVector("a")), createVectorList(createVector("a")));
	}

	@Test
	public void testCluster_two_equal_vectors_generates_one_vector() {
		assertExpectedVectors(createVectorList(createVector("a")), createVectorList(createVector("a"), createVector("a")));
	}

	@Test
	public void testCluster_two_different_vectors_generates_two_vectors() {
		assertExpectedVectors(createVectorList(createVector("a"), createVector("b")), createVectorList(createVector("a"), createVector("b")));
	}

	@Test
	public void testCluster_type_of_vectors_influence_the_clusters() {
		assertExpectedVectors(createVectorList(createVector("a"), createVector(Type.NUMBER, "1.0")), createVectorList(createVector("a"), createVector(Type.NUMBER, "1.0")));
	}

	@Test
	public void testCluster_the_best_vector_is_that_with_less_null() {
		assertExpectedVectors(createVectorList(createVector("a", "a")), createVectorList(createVector("a", null), createVector("a", "a")));
	}
	
	@Test
	public void testCluster_vectors_are_grouped_by_type_and_similarity_and_the_best_is_choosen() {
		assertExpectedVectors(createVectorList(createVector("a"), createVector(Type.NUMBER, "1.0"), createVector("b")), createVectorList(createVector("a"), createVector(Type.NUMBER, "1.0"), createVector("b"), createVector(Type.NUMBER, "1.0")));
	}
	
}
