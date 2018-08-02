package it.uniroma3.weir;

import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.structures.MapPairRepository;

import java.io.Serializable;
import java.util.*;

/**
 * Build the pyramid of {@link Match}es between a set of {@link Attribute}s.
  * VC: Rename into DomainMatchRepository?
  *     sui matches si lavora direttamente su coppie di attributi (di vari siti)
 */
public class MatchRepository extends MapPairRepository<Match, Attribute>
							 implements Serializable {

	static final private long serialVersionUID = 2464872423035063431L;
	
	static public class MatchBuilder extends AbstractPairBuilder<Match, Attribute>
									 implements PairBuilder<Match, Attribute> {

		static final private long serialVersionUID = -7450068650977451286L;

		private boolean finiteness; // true iff keep only match at finite distance
		
		public MatchBuilder() {
			this(false);
		}
		private MatchBuilder(boolean onlyFinite) {
			this.finiteness = onlyFinite;
		}
		
		public Match createPair(Attribute a_i, Attribute a_j) {
			final Match match = new Match(a_i,a_j);
			if (!this.finiteness) return match;
			if (0<=match.distance() && match.distance()<=1)
				return match;
			else return null;
		}
	}
	
	static public class MatchWeighter extends AbstractPairWeighter<Match,Attribute> {

		static final private long serialVersionUID = 3733107753758734397L;

		@Override
		public double weight(Match m) {
			return m.distance();
		}
		
		@Override
		public double weight(Attribute a, Attribute b) {
			return new Match(a,b).distance();
		}
		
	}

	static public MatchBuilder finiteMatchBuilder() {
		return new MatchBuilder(true);
	}

	static public MatchRepository finiteMatchRepository() {
		return new MatchRepository(new MatchBuilder(true));
	}
	
	public MatchRepository() {
		this(new MatchBuilder());		
	}

	public MatchRepository(MatchBuilder builder) {
		super(builder);
	}

	public MatchRepository(Collection<Match> matches) {
		this();
		this.addAll(matches);
	}

	public MatchRepository(MatchRepository matches) {
		this(matches.getAllMatches());
	}
	
	//TODO remove this and who's using it
	public double getDistance(Attribute a, Attribute b) {
		final Match match = get(a, b);
		return ( match==null ? 1d : match.distance() );
	}
	
	public MatchRepository top(int k) {
		final int n = this.size();
		if (k<n) return this;
		return new MatchRepository(order().subList(0, Math.min(k,n)));
	}

	public Match min() {
		return Collections.min(getAllMatches());
	}

	public Match max() {
		return Collections.max(getAllMatches());
	}

	public LinkedList<Match> order() {
		final LinkedList<Match> similarities = getAllMatches();
		Collections.sort(similarities);
		return similarities;
	}

	public LinkedList<Match> getAllMatches() {
		final LinkedList<Match> allMatches = new LinkedList<>();
		allMatches.addAll(super.getAll());
		return allMatches;
	}

	public Set<Match> getMatches(Mapping m) {
		final Set<Match> matches = new HashSet<>();
		for(Attribute a : m.getAttributes())
			matches.addAll(this.getPairs(a));
		return matches;
	}
//
//	static public double avg(Set<Match> matches) {
//		if (matches.isEmpty())
//			throw new NoSuchElementException("Cannot average over an empty set");
//		double sum = 0d;
//		for(Match match : matches)
//			sum += match.distance();
//		return ( sum / matches.size());
//	}

}
