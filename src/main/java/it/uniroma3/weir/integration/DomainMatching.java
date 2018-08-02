package it.uniroma3.weir.integration;

import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.DomainRepository;
import it.uniroma3.weir.model.Website;

import java.util.ArrayList;
import java.util.List;

/**
 * A repository of {@link WebsiteMatching},  each of which is a set of 
 * {@link Match}es over every (unordered) pair of {@link Website}s
 * in a {@link Domain}.
 * 
 * This is the counterpart wrt {@link DomainLinkage} class.
 *  
 * N.B.: Altrimenti
 *     sui linkage si lavorava da coppie di siti a coppie di pagine
 *     sui matches si lavorava direttamente su coppie di pagine
 */
public class DomainMatching extends DomainRepository<WebsiteMatching> {
             
	static final private long serialVersionUID = 6966285610062754785L;

	static public abstract class WebsiteMatchBuilder 
	              extends AbstractPairBuilder<WebsiteMatching, Website>
				  implements PairBuilder<WebsiteMatching, Website> {
		
		static final private long serialVersionUID = 8387964350843733873L;

		@Override
		abstract public WebsiteMatching createPair(Website s1, Website s2);
	}

	static public interface WebsiteMatchProcessor extends PairProcessor<WebsiteMatching, Website> {
		@Override
		abstract public void process(WebsiteMatching siteMatch);
	}

	public DomainMatching(Domain domain, WebsiteMatchBuilder builder) {
		super(domain, WebsiteMatching.class, builder);
	}
	
//	public DomainMatching(List<Website> sites) {
//		this(sites, null);
//	}
	
//	public WebsiteMatching getMatch(Website s1, Website s2) {
//		return (WebsiteMatching)get(s1,s2);
//	}
	
	public DomainMatching top(final int k) {		
		final DomainMatching topKrepository = 
				new DomainMatching(getDomain(), new WebsiteMatchBuilder() {

					private static final long serialVersionUID = -2547435373261464964L;

			@Override
			public WebsiteMatching createPair(Website s1, Website s2) {
			    return get(s1,s2).top(k);
			}
			
		});

		return topKrepository;
	}
	
	public List<Match> getAllPageMatches() {
		final List<Match> result = new ArrayList<>(size());
		this.forEach(new WebsiteMatchProcessor() {			
			@Override
			public void process(WebsiteMatching pair) {
				result.addAll(pair.getMatching().getAllMatches());
			}
		});
		return result;
	}

}
