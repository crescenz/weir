package it.uniroma3.weir.linking.linkage;

import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.DomainRepository;
import it.uniroma3.weir.model.Website;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A repository of {@link WebsiteLinkage}s,  each of which is a set of 
 * {@link PageLinkage}s over every (unordered) pair of {@link Website}s
 * in a {@link Domain}.
 * 
 * This is the counterpart wrt DomainMatching class.
 * 
 * N.B.:
 *     sui linkage si lavora da coppie di siti a coppie di pagine
 *     sui matches si lavora direttamente su coppie di pagine
 */
public class DomainLinkage extends DomainRepository<WebsiteLinkage> {
             
	static final protected long serialVersionUID = 6966285610062754785L;

	static public abstract class WebsiteLinkageBuilder 
	              extends AbstractPairBuilder<WebsiteLinkage, Website>
				  implements PairBuilder<WebsiteLinkage, Website> {
		
		static final protected long serialVersionUID = 1254453860190799707L;

		@Override
		abstract public WebsiteLinkage createPair(Website s1, Website s2);
	}

	static public interface WebsiteLinkageProcessor extends PairProcessor<WebsiteLinkage, Website> {
		@Override
		public void process(WebsiteLinkage siteLinkage);
	}
	
	static public abstract class AbstractWebsiteLinkageProcessor implements WebsiteLinkageProcessor {		
		public void process(WebsiteLinkage siteLinkage) {
			final Website s1 = siteLinkage.getMin();
			final Website s2 = siteLinkage.getMax();
			if (s1.equals(s2)) return;
			else processButSelf(siteLinkage);
		}
		abstract public void processButSelf(WebsiteLinkage siteLinkage);
	}

	public DomainLinkage(Domain domain, WebsiteLinkageBuilder builder) {
		super(domain, WebsiteLinkage.class, builder);
	}
	
	public DomainLinkage top(final int k) {		
		final DomainLinkage topKrepository = 
				new DomainLinkage(getDomain(), new WebsiteLinkageBuilder() {

					static final private long serialVersionUID = 4803047118306357855L;

			@Override
			public WebsiteLinkage createPair(Website s1, Website s2) {
			    return get(s1,s2).top(k);
			}
			
		});

		return topKrepository;
	}
	
	public List<PageLinkage> getAllPageLinkages() {
		final List<PageLinkage> result = new ArrayList<>(size());
		this.forEach(new WebsiteLinkageProcessor() {			
			@Override
			public void process(WebsiteLinkage pair) {
				result.addAll(pair.getPageLinkages().getAllLinkages());
			}
		});
		return result;
	}
	
	public List<PageLinkage> getOrderedPageLinkages() {
		final List<PageLinkage> ordered = this.getAllPageLinkages();
		Collections.sort(ordered);
		return ordered;
	}
	
}
