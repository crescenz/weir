package it.uniroma3.weir.linking.linkage;

import static it.uniroma3.weir.fixture.WebsiteFixture.createWebsite;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.configuration.Constants;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.fixture.WebsiteLinkageFixture;
import it.uniroma3.weir.linking.LegacySoftIdsLinker;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Website;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;


public class SoftIdsLinkerTest {
	
	@SafeVarargs
	private static void assertLinkage(List<Website> wss, int[]... pairs) {
		WeirConfig.getInteger(Constants.MAX_OVERLAPPING_SAMPLES);

//was:	OverlapHunter linker = new OverlapHunter(repository, 1.0);
//		linker.buildLinkages(wss);

		LegacySoftIdsLinker linker2 = new LegacySoftIdsLinker(1.0);
		Domain domain = new Domain("test");
		for(Website site : wss) domain.addSite(site);
		DomainLinkage repository = linker2.link(domain);
		Website w1 = wss.get(0);
		Website w2 = wss.get(1);
		WebsiteLinkage linkage = WebsiteLinkageFixture.createWebsiteLinkage(w1, w2, pairs);
		assertEquals(linkage, repository.get(w1, w2));
	}

	@Test
	public void testBuildLinkages_empty_linkage() {
		assertLinkage(createWebsiteList(createWebsite("p1", "p2"), createWebsite("p3", "p4")));
	}

	@Test
	public void testBuildLinkages_non_empty_linkage() {
		final int[][] uPair =  { { 0, 0 } };
		assertLinkage(createWebsiteList(createWebsite("p1", "p2"), createWebsite("p1", "p3")), uPair);
	}

	@Test
	public void testBuildLinkages_total_linkage_beetween_same_sites() {
		final int[][] uPairs =  { { 0, 0 }, { 1, 1 } };
		assertLinkage(createWebsiteList(createWebsite("p1", "p2"), createWebsite("p1", "p2")), uPairs);
	}

	@Test
	public void testBuildLinkages_total_linkage_between_different_sites() {
		final int[][] uPairs =  { { 0, 1 }, { 1, 0 } };
		assertLinkage(createWebsiteList(createWebsite("p1", "p2"), createWebsite("p2", "p1")), uPairs);
	}

	@Test
	public void testBuildLinkages_first_site_smaller_than_second() {
		final int[][] uPair =  { { 0, 1 } };
		assertLinkage(createWebsiteList(createWebsite("p2"), createWebsite("p1", "p2", "p3")), uPair);
	}

	@Test
	public void testBuildLinkages_second_site_smaller_than_first() {
		final int[][] uPair =  { { 1, 0 } };
		assertLinkage(createWebsiteList(createWebsite("p1", "p2", "p3"), createWebsite("p2")), uPair);
	}

	static private List<Website> createWebsiteList(Website... sites) {
		return Arrays.asList(sites);
	}
	
}
