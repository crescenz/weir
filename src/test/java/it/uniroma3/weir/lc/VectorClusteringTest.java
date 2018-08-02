package it.uniroma3.weir.lc;


import static it.uniroma3.weir.configuration.Constants.LOCAL_CONSISTENCY_THRESHOLD;
import static it.uniroma3.weir.fixture.VectorFixture.*;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.integration.lc.LocalConsistencyEnforcer;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.type.Type;

import java.util.List;

import org.junit.Test;

public class VectorClusteringTest extends WeirTest {

	private static void assertExpectedClusters(List<List<Vector>> expectedClusters, List<Vector> vectors) {
		WeirConfig.getInstance().setProperty(LOCAL_CONSISTENCY_THRESHOLD, "1.0");

		LocalConsistencyEnforcer lce = new LocalConsistencyEnforcer();
		lce.enforceLocalConsistency(vectors);
		assertEquals(expectedClusters, lce.enforceLocalConsistency(vectors));
	}

	@Test
	public void testCluster_one_vector_generates_one_cluster() {
		assertExpectedClusters(
				createVectorClusters(
						createVectorList(createVector("a"))), 
				createVectorList(createVector("a")));
	}

	@Test
	public void testCluster_two_equal_vectors_generates_one_cluster() {
		assertExpectedClusters(
				createVectorClusters(
						createVectorList(createVector("a"), createVector("a"))), 
				createVectorList(createVector("a"), createVector("a")));
	}

	@Test
	public void testCluster_two_different_vectors_generates_two_cluster() {
		assertExpectedClusters(
				createVectorClusters(
						createVectorList(createVector("a")), 
						createVectorList(createVector("b"))), 
				createVectorList(createVector("a"), createVector("b")));
	}

	@Test
	public void testCluster_clusters_contains_only_similar_vector() {
		assertExpectedClusters(
				createVectorClusters(
						createVectorList(createVector("a"), createVector("a")), 
						createVectorList(createVector("b"))), 
				createVectorList(createVector("a"), createVector("b"), createVector("a")));
	}

	@Test
	public void testCluster_type_of_vectors_influence_the_clusters() {
		assertExpectedClusters(
				createVectorClusters(
						createVectorList(createVector("a")), 
						createVectorList(createVector(Type.NUMBER, "1.0"))), 
				createVectorList(createVector("a"), createVector(Type.NUMBER, "1.0")));
	}

	@Test
	public void testCluster_vectors_are_grouped_by_type_and_similarity() {
		assertExpectedClusters(
				createVectorClusters(
						createVectorList(createVector("a")), 
						createVectorList(createVector(Type.NUMBER, "1.0"), createVector(Type.NUMBER, "1.0")), 
						createVectorList(createVector("b"))), 
				createVectorList(createVector("a"), createVector(Type.NUMBER, "1.0"), createVector("b"), createVector(Type.NUMBER, "1.0")));
	}
	
}
