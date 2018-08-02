package it.uniroma3.weir.linking;

import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import static it.uniroma3.weir.configuration.Constants.MAX_OVERLAPPING_SAMPLES;
import static it.uniroma3.weir.configuration.Constants.MIN_OVERLAPPING_SAMPLES;
import static it.uniroma3.weir.model.log.WeirStyles.header;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.hlog.Logpage;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.integration.DomainMatching;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.linking.linkage.DomainLinkage.WebsiteLinkageProcessor;
import it.uniroma3.weir.linking.linkage.DomainLinkage.AbstractWebsiteLinkageProcessor;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;


/**
 * Given a {@link DomainMatching}, this class is in charge of selecting
 * the best {@link PageLinkage}s to find a satisfying extensional overlap within
 * the {@link Domain}'s sites, i.e., a not too big set of {@link Webpage}s for
 * each {@link Website} such that every pair of websites share as many linked
 * pages as possible.
 *
 */
public class OverlapHunter extends AbstractWebsiteLinkageProcessor implements WebsiteLinkageProcessor {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();
	
	final private DomainLinkage linkages;
	
	public OverlapHunter(DomainLinkage repository) {
		this.linkages = repository;
		checkConfiguration();
	}

	private void checkConfiguration() {
		int min = WeirConfig.getInteger(MIN_OVERLAPPING_SAMPLES);
		int max = WeirConfig.getInteger(MAX_OVERLAPPING_SAMPLES);
		if (max<min)
			throw new IllegalStateException(
					" Cannot find "+min+" overlapping pages "
					+"from "+max+" input pages per source!"
				  );
	}

	public Domain findOverlap(Domain domain) {
		log.trace("page-linkages selection for "+domain);
		log.newTable();
		log.trace(header("site"), header("site"), header("linkages"));
		this.linkages.forEach(this);
		log.endTable();
		return domain;
	}
	
	@Override
	public void processButSelf(WebsiteLinkage siteLinkage) {
		/* do NOT consider any site overlapping with itself! */
		final Website w1 = siteLinkage.getMin();
		final Website w2 = siteLinkage.getMax();

		log.newPage();
		// cut-off same-site overlap???
		
		/* page-linkage samples cut-off here ! */
		final int original = siteLinkage.size();
		final int selected = siteLinkage.cutOffLinkages();
		
		if (selected<original) {
			log.trace("originally "+original+" page linkages were present in "+siteLinkage);
			log.trace("now "+selected+" are left");
		} else {
			if (original==0) log.trace("none page linkage available");
			else log.trace("only "+original+" page linkages: max overlap not exceeded");
		}
		
		for (PageLinkage pageLinkage : siteLinkage) {
			w1.addOverlapPage(pageLinkage.from(w1)); 
			w2.addOverlapPage(pageLinkage.from(w2));
		}
		if (selected>0) {
			log.trace("the selected linkages are:");
			log.trace(siteLinkage);
		}
		final Logpage detailLogpage = log.endPage();
		log.trace(w1, w2, selected>0 ? linkTo(detailLogpage).withAnchor(selected) : 0 );
	}

}
