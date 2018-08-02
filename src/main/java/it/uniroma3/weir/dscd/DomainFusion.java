package it.uniroma3.weir.dscd;

import it.uniroma3.weir.integration.DomainMatching;
import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.DomainRepository;
import it.uniroma3.weir.model.Website;

/**
 * A repository of {@link WebsiteFusion}s,  each of which hosts a set of
 * {@link PageLinkage}s and a set of {@link Match}es over every 
 * (unordered) pair of {@link Website}s in a {@link Domain}.
 * 
 * @see DomainLinkage
 * @see DomainMatching
 * @see WebsiteFusion
 */
public class DomainFusion extends DomainRepository<WebsiteFusion> {
             
	static final private long serialVersionUID = 6966285610062754785L;

	static public abstract class WebsiteFusionBuilder 
	              extends AbstractPairBuilder<WebsiteFusion, Website>
				  implements PairBuilder<WebsiteFusion, Website> {

		static final private long serialVersionUID = -8183162425180135035L;

		@Override
		abstract public WebsiteFusion createPair(Website s1, Website s2);
	}

	static public interface WebsiteFusionProcessor extends PairProcessor<WebsiteFusion, Website> {
		@Override
		abstract public void process(WebsiteFusion siteFusion);
	}

	public DomainFusion(Domain domain, WebsiteFusionBuilder builder) {
		super(domain, WebsiteFusion.class, builder);
	}
	
	public WebsiteFusion getFusion(Website s1, Website s2) {
		return get(s1,s2);
	}
	
}
