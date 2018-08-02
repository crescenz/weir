package it.uniroma3.weir.linking.entity;

import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.linking.IdfRepository;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;

import java.util.List;

public enum EntityFactory {

	VALUE_ENTITY {
		@Override
		protected Entity createEntity(Webpage page) {
			return new ValueEntity(page);
		}
	},
	
	SOFTID_ENTITY {
		@Override
		protected Entity createEntity(Webpage page) {
			return new SoftIdEntity(page);
		}		
	};
	
	static final protected HypertextualLogger log = HypertextualLogger.getLogger();

	public void createEntities(final Domain domain) {
		final List<Website> sites = domain.getSites();
		log.newPage("creating entities over vertical domain "+domain);
		for (Website site : sites) {
			createEntities(site);
		}
		log.endPage();
	}

	protected void createEntities(final Website site) {
		log.newPage("creating entities over site "+site);
		log.trace(site.getWebpages().size() + " pages to process");
		
		final IdfRepository idfs = site.getIdfRepository();
		
		for (final Webpage page : site.getWebpages()) {	// VC: n.b. it was based on OverlappingPages
			log.trace("processing "+linkTo(page.getURI()).withAnchor(page.getId()));
			Entity e = createEntity(page);
			page.setEntity(e);
			idfs.updateDfs(e);
		}
		log.trace("entities found:");
		log.trace(site.getEntities());
		idfs.finalizeIdfs(site.getWebpages().size());
		log.trace(idfs); // ??
		log.endPage();
	}

	abstract protected Entity createEntity(final Webpage page);

}
