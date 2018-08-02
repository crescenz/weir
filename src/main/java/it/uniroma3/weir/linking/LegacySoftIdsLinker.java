package it.uniroma3.weir.linking;

import it.uniroma3.weir.configuration.Constants;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.linking.linkage.DomainLinkage.WebsiteLinkageBuilder;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.wcohen.ss.UnsmoothedJS;
import com.wcohen.ss.api.StringDistance;

/**
 * Populate a given input {@link DomainMatching} by means of
 * a simple linkage algorithm based on string distance 
 * (see {@link it.uniroma3.weir.linking.SoftIdsLinker.JS_DISTANCE})
 * over pages' soft-ids.
 */
// VC: Superato dalla generalizzazione dei due vecchi Linker.
//     Tenuto solo a conferma.
public class LegacySoftIdsLinker extends DomainLinker {

	static final private StringDistance JS_DISTANCE = new UnsmoothedJS();

	private double minSimThreshold; // min similarity to evaluate the linkage of two soft-ids 

	public LegacySoftIdsLinker() {
		this(WeirConfig.getDouble(Constants.LINKING_PARAMETERS));
	}

	public LegacySoftIdsLinker(double th) {
		this.minSimThreshold = th;
	}
	
	protected WebsiteLinkageBuilder websiteLinkageBuilder() {
		return new WebsiteLinkageBuilder() {

			static final private long serialVersionUID = 984272917377231345L;

			@Override
			public WebsiteLinkage createPair(final Website s1, final Website s2) {
				return link(s1,s2);
			}
		};
	}

	public WebsiteLinkage link(Website site1, Website site2) {
		log.newPage("linking "+site1+" vs "+site2);

		final List<PageLinkage> pageLinkages = new ArrayList<>();
		log.trace("looking for the best linkage of each page");

		for (Webpage page1 : site1.getWebpages()) {
			log.newTable();
			PageLinkage best = getBestPageLinkageFor(page1, site2);

			if (best==null) {
				log.trace("<em>none found</em>");
			} else {
				pageLinkages.add(best);				
				log.trace("found",best.getMate(page1));
			}
			log.endTable();
		}
		log.endPage();
		return new WebsiteLinkage(site1, site2, pageLinkages);
	}

	private PageLinkage getBestPageLinkageFor(final Webpage pivot, Website site2) {
		if (site2.getWebpages().isEmpty()) return null;

		// it selects the best candidate linkages for the given pivot page from site 1
		final Webpage best = Collections.max(site2.getWebpages(), new Comparator<Webpage>() {
			@Override//Move it into Repositories
			public int compare(Webpage p1, Webpage p2) {
				return Double.compare(getScore(pivot, p1),getScore(pivot, p2));
			}// CHECK MatchEvaluator.pivotedComparator

		});
		final double bestScore = getScore(pivot, best);

		return (bestScore>=this.minSimThreshold ? new PageLinkage(pivot, best, bestScore) : null);
	}
	//TODO Removed once Moved into SoftIdEntity
	private double getScore(Webpage page1, Webpage page2) {
		final String id1 = page1.getId();// VC: Introduce IdEntity extends ValueEntity (DONE)
		final String id2 = page2.getId();// VC: Introduce EntityScorer extends PairScorer
		return Math.min(JS_DISTANCE.score(id1, id2), 1.0d);
	}

}
