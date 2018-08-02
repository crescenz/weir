package it.uniroma3.weir.linking;

import static it.uniroma3.weir.configuration.Constants.*;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.cache.CachedComputation;
import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.linking.entity.Entity;
import it.uniroma3.weir.linking.entity.EntityFactory;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.linking.linkage.DomainLinkage.WebsiteLinkageBuilder;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
/**
 *
 * Create a {@link DomainMatching} over an input {@link Domain}.
 * This class performs two main steps:
 * <ol>
 * 	<li> attach an {@link Entity} to each {@link Webpage} by using the
 *       appropriate {@link EntityFactory}
 *  <li> create a resulting {@link DomainMatching} by using a
 *  	 {@link WebsiteLinkageBuilder} (as specified by a subclass)
 *  	 selecting the linkages over every pair of {@link Website}s
 * </ol>
 */
public class DomainLinker extends CachedComputation<Domain, DomainLinkage> {

	static final protected HypertextualLogger log = HypertextualLogger.getLogger();

	private DomainLinkage repository;
	
	/* max number of candidate linkages to consider */
	final private int topK;
	
	/* the linking strategy parameters */
	final private String linkingParameters;

	/* the type of entities used */
	final private String entityFactory;
	
	public DomainLinker() {
		this.topK = WeirConfig.getInteger(LINKING_TOP_K);
		this.entityFactory 	   = WeirConfig.getString(ENTITY_FACTORY);
		this.linkingParameters = WeirConfig.getString(LINKING_PARAMETERS);
	}

	public DomainLinkage getRepository() {
		return this.repository;
	}

	public int getLinkingTopK() {
		return this.topK;
	}
	
	public String getLinkingParameters() {
		return this.linkingParameters;
	}

	public String getEntityFactory() {
		return this.entityFactory;
	}
	
	public DomainLinkage link(Domain domain) {
		return this.cachedComputation(domain);
	}

	@Override
	public DomainLinkage uncachedComputation(final Domain domain) {
		log.newPage("linking " + domain + " with " + this.getClass());
		
		/*  create entities over domain's pages */
		this.createEntities(domain); // n.b. by entity we mean information for page-alignment
		
		/* select the candidate record linkages */
		this.repository = new DomainLinkage(domain, websiteLinkageBuilder());	
		
		/* n.b. possible linkage conflicts are not solved here */
		log.endPage();
		return this.repository;
	}

	protected WebsiteLinkageBuilder websiteLinkageBuilder() {
		return new CandidateLinkageFilter();
	}

	protected void createEntities(Domain domain) {
		final EntityFactory factory = EntityFactory.valueOf(getEntityFactory());
		factory.createEntities(domain);
	}
	
	@Override
	public Fingerprint fingerprint(Domain domain) {
		this.fingerprint(this.getClass().toString());
		this.fingerprint(domain.getFingerprint());
		this.fingerprint(getLinkingTopK());
		this.fingerprint(getLinkingParameters());
		this.fingerprint(getEntityFactory());
		return this.getFingerprint("link");
	}

}