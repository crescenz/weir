package it.uniroma3.weir.integration;

import static java.util.Collections.sort;
import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.WebsitePair;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
/**
 * A set of {@link Match}es between two {@link Website}s of the same domain.
 * This is the counterpart wrt WebsiteLinkage class.
 */
public class WebsiteMatching extends WebsitePair implements Serializable, Iterable<Match> {

	static final private long serialVersionUID = -1313568007429503747L;
		
	private MatchRepository match;

	public WebsiteMatching(Website w1, Website w2, MatchRepository matching) {
		super(w1,w2);
		this.match = matching;
	}	
	
	public WebsiteMatching(Website w1, Website w2, List<Match> matching) {
		this(w1,w2,new MatchRepository(matching));
	}

	public int size() {
		return this.match.size();
	}

	/**
	 * @return a {@link MatchRepository}, a set of
	 *         {@linkplain Match}es involving two {@link Website}s
	 */
	public MatchRepository getMatching() {
		return this.match;
	}
	
	/**
	 * Order {@linkplain Match}es by similarity
	 * @return the match ordered by similarity
	 */
	public List<Match> order() {
		final List<Match> all = getMatching().getAllMatches();
		sort(all);
		return all;
	}
	
	public WebsiteMatching top(int k) {
		return new WebsiteMatching(getMin(), getMax(), getMatching().top(k));
	}

	@Override
	public Iterator<Match> iterator() {
		return getMatching().getAllMatches().iterator();
	}
	
}
