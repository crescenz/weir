 package it.uniroma3.weir.linking;

import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import static it.uniroma3.weir.model.log.WeirStyles.*;
import static it.uniroma3.hlog.HypertextualUtils.*;
import it.uniroma3.weir.structures.Pair;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.hlog.HypertextualUtils.Link;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.linking.linkage.DomainLinkage.AbstractWebsiteLinkageProcessor;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.Webpage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * Solve conflicts among a list of {@link PageLinkage}s, i.e.,
 * remove page-linkages that involve a page already involved
 * in a better linkage. It is based on the assumption that
 * a page from a site can be in linkage with at most another page, 
 * or, in other terms, pages from a site have been already 
 * de-duplicated, i.e., two pages from the same site cannot
 * refer to the same real-world instance.
 * <br/>
 * During the processing, it counts the <em>usage</em> of each
 * page, i.e., how many times it has been used in the linkage
 * between two sites, so to prefer the most frequently linked 
 * pages.
 */
public class LinkageConflictSolver {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	final private DomainLinkage linkages;

	final private Map<Webpage, Integer> page2usage;

	public LinkageConflictSolver(DomainLinkage rep) {
		this.linkages = rep;
		this.page2usage = new HashMap<>();
	}

	public DomainLinkage solveLinkageConflicts() {
		log.newPage("solving page-linkage conflicts");
		this.removeSuboptimalConflictingLinkages();
		this.countPagesInLinkages();
		log.endPage();
		return this.linkages;
	}

	private void removeSuboptimalConflictingLinkages() {
		/* prefer the linkage with the best similarity;      */
		/* remove any following linkages conflicting with it */
		log.trace("removing conflicting linkages");
		log.newTable();
		log.trace(header("sites pair"),header("linkages removed"));
		this.linkages.forEach(new AbstractWebsiteLinkageProcessor() {
			@Override
			public void processButSelf(WebsiteLinkage siteLinkage) {
				log.newPage();
				final Map<Webpage,PageLinkage> best1 = new HashMap<>();
				final Map<Webpage,PageLinkage> best2 = new HashMap<>();
				final Iterator<PageLinkage> it = siteLinkage.order().iterator();
				int count = 0;
				log.newPage();
				log.newTable();
				log.trace(
						header("page-linkage"),
						header("sim"),
						header("conflict"),
						header("already in"),
						header("sim")
				);
				while (it.hasNext()) {
					final PageLinkage pageLinkage = it.next();
					final Webpage page1 = pageLinkage.from(siteLinkage.getMin());
					final Webpage page2 = pageLinkage.from(siteLinkage.getMax());
					// n.b. produce side-effects on the list of page-linkages
					//      by removing the linkage from the repository
					if (best1.containsKey(page1)) {
						log(page1, pageLinkage, best1);
						siteLinkage.remove(pageLinkage);
						count++;
					} if (best2.containsKey(page2)) {
						log(page2, pageLinkage, best2);
						siteLinkage.remove(pageLinkage);
						count++;
					} else {
						best1.put(page1,pageLinkage);
						best2.put(page2,pageLinkage);
						LinkageConflictSolver.this.incPageUsage(page1);
						LinkageConflictSolver.this.incPageUsage(page2);
					}
				}
				log.endTable();
				log.endPage("removed "+count+" conflicting page-linkages");
				log.trace();
				log.trace(siteLinkage);
				final Link link = linkTo(log.endPage()).withAnchor(siteLinkage.toString());
				log.trace(link,count);
			}

			private void log(final Webpage conflict, final PageLinkage linkage, final Map<Webpage, PageLinkage> best) {
				final PageLinkage better = best.get(conflict);
				log.trace(Pair.toString(linkage),percentage(linkage.getSimilarity()),
						  conflict,
						  Pair.toString(better), percentage(better.getSimilarity()));
			}
		});
		log.endTable();
	}

	private void countPagesInLinkages() {
		this.linkages.forEach(new AbstractWebsiteLinkageProcessor() {
			@Override
			public void processButSelf(WebsiteLinkage siteLinkage) {
				for (final PageLinkage link : siteLinkage) {
					final int usage1 = LinkageConflictSolver.this.page2usage.get(link.getMin());
					final int usage2 = LinkageConflictSolver.this.page2usage.get(link.getMax());
					link.setUsage(usage1 + usage2);
				}
			}
		});

	}

	private void incPageUsage(Webpage page) {
		final Integer usage = this.page2usage.get(page);
		this.page2usage.put(page, usage == null ? 1 : usage+1 );
	}

}
