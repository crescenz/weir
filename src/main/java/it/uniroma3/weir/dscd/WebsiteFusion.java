package it.uniroma3.weir.dscd;

import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.integration.WebsiteMatching;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.linking.linkage.PageLinkageRepository;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.WebsitePair;
/**
 * This class hosts a set of {@link PageLinkage}s and a set of
 * {@link Match}es between two {@link Website}s of the same domain.
 * 
 * @see WebsiteLinkage
 * @see WebsiteMatching
 */
public class WebsiteFusion extends WebsitePair {

	static final private long serialVersionUID = 261693332970095457L;
	
	private WebsiteLinkage linkages;
	private WebsiteMatching matches;
	
	public WebsiteFusion(Website s1, Website s2, 
				  WebsiteLinkage links, 
				  WebsiteMatching matches) {
		super(s1,s2);
		this.linkages = links;
		this.matches = matches;		
	}
	
	public WebsiteFusion(Website s1, Website s2,
				PageLinkageRepository links,
				MatchRepository matches) {
		this(s1,s2,new WebsiteLinkage(s1,s2,links), new WebsiteMatching(s1,s2,matches));
	}
	
	public WebsiteLinkage getLinkages() {
		return this.linkages;
	}
	
	public WebsiteMatching getMatching() {
		return this.matches;
	}

}
