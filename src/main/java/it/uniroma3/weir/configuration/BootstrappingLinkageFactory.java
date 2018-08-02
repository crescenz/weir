package it.uniroma3.weir.configuration;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.cache.CachedComputation;
import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.linking.DomainLinker;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.model.Domain;
import static it.uniroma3.weir.configuration.Constants.*;
import static it.uniroma3.weir.configuration.WeirConfig.getString;
/**
 * Create a bootstrapping {@link DomainMatching} by using
 * the {@link DomainLinker} class specified in the configuration.
 * 
 * @see WeirConfig
 * @see Constants
 */
public class BootstrappingLinkageFactory extends CachedComputation<Domain, DomainLinkage> {

	static final private  HypertextualLogger log = HypertextualLogger.getLogger();

	public DomainLinkage link(Domain domain) {
		return cachedComputation(domain);
	}

	@Override
	public DomainLinkage uncachedComputation(Domain domain) {
		String linkerClazzname = getString(LINKING_STRATEGY);
		log.trace(" linking strategy class: "+linkerClazzname);
		try {
			final DomainLinker domainLinker = (DomainLinker) Class.forName(linkerClazzname).newInstance();
			final DomainLinkage linkages = domainLinker.link(domain);
			return linkages;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			log.error("Cannot instantiate the linking strategy class: "+linkerClazzname+"\n");
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Fingerprint fingerprint(Domain input) {
		fingerprint(input.getFingerprint());
		fingerprint(getString(LINKING_STRATEGY));
		fingerprint(getString(LINKING_TOP_K));
		fingerprint(getString(LINKING_PARAMETERS));
		fingerprint(getString(MIN_OVERLAPPING_SAMPLES));
		fingerprint(getString(MAX_OVERLAPPING_SAMPLES));
		fingerprint(getString(TYPED_ENTITIES));
		fingerprint(getString(ENTITY_FACTORY));
		fingerprint(getString(ENTITY_SIM_THRESHOLD));
		fingerprint(getString(ENTITY_TERM_SIM_THRESHOLD));
		fingerprint(getString(ENTITY_TERM_IDF_THRESHOLD));
		return getFingerprint("link");
	}
		
}
