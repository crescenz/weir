package it.uniroma3.weir.evaluation;

import it.uniroma3.weir.structures.MapPairRepository;
import it.uniroma3.weir.structures.Pair;
import it.uniroma3.weir.structures.PairRepository;
import static it.uniroma3.weir.structures.PairRepository.PairWeighter;

import java.util.HashSet;
import java.util.Set;

/**
 * A facility class to evaluate the pairs within a {@link PairRepository}.
 * 
 * The pairs can be evaluated by means of a {@link PairWeighter}.
 * 
 * The result is an {@link PRF} object obtained
 * by averaging over the pairs involving 
 * a set of <em>golden</em> elements vs 
 * a set of <em>output</em> elements.
 * @see {@link #evaluate(Set golden, Set output)}
 * @see PRF
 * @see PairWeighter
 */
public class RepositoryEvaluator<P extends Pair<T>, 
								 T extends Comparable<? super T>> {
	
	protected MapPairRepository<P,T> repository;
	
	protected PairWeighter<P,T> pairEvaluator;
	
	public RepositoryEvaluator(MapPairRepository<P,T> repository, 
							   PairWeighter<P,T> pairEvaluator) {
		this.repository = repository;
		this.pairEvaluator = pairEvaluator;
	}
	
	public PairRepository<P, T> getRepository() {
		return this.repository;
	}
	
	
	/**
	 * Build an {@link PRF} object by averaging over the
	 * set of pairs involving the given set of <em>golden</em> elements
	 * and of <em>output</em> elements.
	 * @param golden
	 * @param output
	 * @return an {@link PRF} object hosting a P/R/F evaluation
	 */
	public PRF evaluate(Set<T> golden, Set<T> output) {
		final Set<P> output2golden = this.repository.getPairs(output);
		final Set<P> golden2output = this.repository.getPairs(golden);
		final double precision = avg(output2golden);
		final double recall    = avg(golden2output);

		final int tp = output2golden.size();
		final int fn = countNotFound(golden, output);
		final int fp = countNotFound(output, golden);

		return new PRF(precision, recall, tp, fn, fp);
	}

	private int countNotFound(Set<T> golden, Set<T> output) {
		final Set<T> notFound = new HashSet<>(golden);
		final Set<T> found = this.repository.getMates(output);
		notFound.removeAll(found);
		return notFound.size();
	}

	private double avg(Set<P> pairs) {
		if (pairs.isEmpty())
			return 0d;
//			throw new NoSuchElementException("Cannot average over an empty set");
		double sum = 0d;
		for(P pair : pairs)
			sum += this.pairEvaluator.weight(pair);
		return ( sum / pairs.size() );
	}
		
}