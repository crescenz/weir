package it.uniroma3.weir.linking.linkage;

import static it.uniroma3.weir.fixture.WebsiteLinkageFixture.createWebsiteLinkage;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.configuration.Constants;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.fixture.WebsiteLinkageFixture;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.WebsitePair;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.value.ExtractedValue;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

public class WebsiteLinkageTest {

	@BeforeClass
	public static void setUp() {
		WeirConfig.getInstance().setProperty(Constants.MAX_OVERLAPPING_SAMPLES, "40");
	}
	
	private static Vector[] getFirstValuesByPerfectLinkage(Attribute a1, Attribute a2, int numberOfLinkage) {
		Vector v1 = new ExtractedVector(getFirstNElements(a1.getVector(), numberOfLinkage), null);
		Vector v2 = new ExtractedVector(getFirstNElements(a2.getVector(), numberOfLinkage), null);
		return new Vector[] { v1, v2 };
	}

	private static ExtractedValue[] getFirstNElements(Vector v,	int n) {
		return (ExtractedValue[])Arrays.copyOfRange(v.getElements(), 0, n);
	}

	private static Vector[] getValuesByLingake(Attribute a1, Attribute a2, int... sequence) {
		final String[] v2byIndex = new String[sequence.length];
		
		for (int i=0; i<sequence.length; i++) {
			int index = sequence[i];
			v2byIndex[i] = a2.getVector().get(index).toString();
		}
		
		Vector v1 = a1.getVector();
		Vector v2 = new Vector(null, a2.getVector().getType(), v2byIndex);
		return new Vector[] { v1, v2 };
	}

	@Test
	public void testGetSize_with_empty_linkage() {
		WebsiteLinkage wl = createWebsiteLinkage(0);
		assertEquals(0, wl.size());
	}

	@Test
	public void testGetSize_with_not_empty_linkage() {
		WebsiteLinkage wl = createWebsiteLinkage(2);
		assertEquals(2, wl.size());
	}

	@Test
	public void testGetSize_size_greater_than_samples_threshold() {
		WebsiteLinkage wl = createWebsiteLinkage(10);
		assertEquals(WebsiteLinkageFixture.MAX_SAMPLES, wl.size());
	}

	@Test
	public void testGetOriginalSize_with_empty_linkage() {
		WebsiteLinkage wl = createWebsiteLinkage(0);
		assertEquals(0, wl.getOriginalSize());
	}

	@Test
	public void testGetOriginalSize_with_not_empty_linkage() {
		WebsiteLinkage wl = createWebsiteLinkage(2);
		assertEquals(2, wl.getOriginalSize());
	}

	@Test
	public void testGetOriginalSize_size_greater_than_samples_threshold() {
		WebsiteLinkage wl = createWebsiteLinkage(10);
		assertEquals(10, wl.getOriginalSize());
	}

	@Test
	public void testGetValuesByLinkage_with_empty_linkage() {
		WebsiteLinkage wl = createWebsiteLinkage(0);
		Attribute a1 = wl.getMin().getAttributes().get(0);
		Attribute a2 = wl.getMax().getAttributes().get(0);
		assertArrayEquals(getFirstValuesByPerfectLinkage(a1, a2, 0), createWebsiteLinkage(a1, a2, 0));
	}

	@Test
	public void testGetValuesByLinkage_with_perfect_linkage() {
		WebsitePair pair = createWebsiteLinkage(2);
		Attribute a1 = pair.getMin().getAttributes().get(0);
		Attribute a2 = pair.getMax().getAttributes().get(0);
		assertArrayEquals(getFirstValuesByPerfectLinkage(a1, a2, 2), createWebsiteLinkage(a1, a2, 2));
	}

	@Test
	public void testGetValuesByLinkage_perfect_linkage_and_size_greater_than_samples_threshold() {
		WebsitePair pair = createWebsiteLinkage(10);
		Attribute a1 = pair.getMin().getAttributes().get(0);
		Attribute a2 = pair.getMax().getAttributes().get(0);
		assertArrayEquals(getFirstValuesByPerfectLinkage(a1, a2, WebsiteLinkageFixture.MAX_SAMPLES), createWebsiteLinkage(a1, a2, 10));
	}

	@Test
	public void testGetValuesByLinkage_linkage_with_two_links() {
		int [][] links = { {0, 1}, {1, 0} };
		WebsitePair pair = createWebsiteLinkage(links);
		Attribute a1 = pair.getMin().getAttributes().get(0);
		Attribute a2 = pair.getMax().getAttributes().get(0);
		assertArrayEquals(getValuesByLingake(a1, a2, 1, 0), createWebsiteLinkage(a1, a2, links));
	}

	@Test
	public void testGetValuesByLinkage_linkage_with_three_links() {
		int [][] links = { {0, 2}, {1, 0}, {2, 1} };
		WebsitePair pair = createWebsiteLinkage(links);
		Attribute a1 = pair.getMin().getAttributes().get(0);
		Attribute a2 = pair.getMax().getAttributes().get(0);
		assertArrayEquals(getValuesByLingake(a1, a2, 2, 0, 1), createWebsiteLinkage(a1, a2, links));
	}

	@Test
	public void testGetValuesByLinkage_linkage_with_number_of_links_greater_than_samples_threshold() {
		int [][] links = { {0, 4}, {1, 0}, {2, 3}, {3, 1}, {4, 2} };
		WebsitePair pair = createWebsiteLinkage(links);
		Attribute a1 = pair.getMin().getAttributes().get(0);
		Attribute a2 = pair.getMax().getAttributes().get(0);
		assertArrayEquals(getValuesByLingake(a1, a2, 4, 0, 3), createWebsiteLinkage(a1, a2, links));
	}
	
}
