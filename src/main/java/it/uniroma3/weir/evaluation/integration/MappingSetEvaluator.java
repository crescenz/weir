package it.uniroma3.weir.evaluation.integration;

import static it.uniroma3.weir.configuration.Constants.REMOVE_MATCHING_ATTRIBUTE;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.evaluation.PRF;
import it.uniroma3.weir.evaluation.PRFBuilder;
import it.uniroma3.weir.evaluation.RepositoryEvaluator;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.model.hiddenrelation.AbstractRelation;
import it.uniroma3.weir.structures.PairRepository.AbstractPairWeighter;
import it.uniroma3.weir.structures.PairRepository.PairWeighter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Evaluate an {@link AbstractRelation} by looking for the best correspondences
 * between the set of <em>output</em> {@link Mapping}s and the set of 
 * <em>golden</em> {@link Mapping}s.
 */
public class MappingSetEvaluator extends RepositoryEvaluator<MappingPair, Mapping>{

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	static final private PairWeighter<MappingPair, Mapping> weighter =
			new AbstractPairWeighter<MappingPair,Mapping>() {

		static final private long serialVersionUID = 6775936065615985786L;

		@Override
		public double weight(MappingPair pair) {
			return pair.getScore().getFMeasure();
		}
	};

	
	// true iff an output attribute is "consumed" once matched,
	// i.e., it can be associated only once to a golden mapping
	final private boolean consume;

	private Set<Mapping> goldenSet;
	private Set<Mapping> outputSet;

	/**
	 * A repository of all mapping pairs involving
	 * one golden mapping and one output mapping;
	 * then select the best pairs.
	 */
	public MappingSetEvaluator(MatchEvaluator mode) {
		super(new MappingPairEvaluationRepository(mode),weighter);
		this.consume = WeirConfig.getBoolean(REMOVE_MATCHING_ATTRIBUTE);
		log.trace("matches evaluator used: " + mode);
		log.trace("consume best matching mapping during evaluation: "+ this.consume);
	}

	public MappingPairEvaluationRepository getRepository() {
		return (MappingPairEvaluationRepository)super.getRepository();
	}

	public PRF evaluate(Set<Mapping> goldenSet, Set<Mapping> outputSet) {
		this.goldenSet = goldenSet;
		this.outputSet = outputSet;
		this.getRepository().addCartesianProduct(this.goldenSet,this.outputSet);
		
		/**
		 * First build a repository of all mapping pairs selected
		 * for a fine-grained attribute-pairwise evaluation
		 */
		final List<MappingPair> selected = new ArrayList<>();
		while (!this.repository.isEmpty()) {
			final MappingPair best = this.getRepository().getBestMappingPairByF();// use topK()
			final Mapping golden = best.retain(goldenSet);
			final Mapping output = best.getMate(golden);

			log.trace("golden mapping " + golden.getLabel());
			log.trace(golden);
			log.trace("best matching mapping is:");
			log.trace(output);
			log.trace("with: " + best.getScore());
			this.repository.removeAll(golden); /* move on to other goldens */
			selected.add(best);
			if (this.consume) {
				/* check whether an output mapping is consumed, i.e., once
				 * associated with a golden mapping it cannot be reused    */
				this.repository.removeAll(output);					
			}
			log.trace();
			log.trace("<hr/>");
			log.trace();
		}
		/* use this repository to host selected mappings */
		this.repository.addAll(selected);
		log.trace(this.repository.size() + " mappings have been selected");
		
		return getMappingLevelEvaluation();
	}

	/**
	 * @return {@link PRF} obtained by averaging at "mapping level"
	 */
	public PRF getMappingLevelEvaluation() {
		return super.evaluate(this.goldenSet, this.outputSet); 
	}
	/**
	 * @return {@link PRF} obtained by averaging at "attribute level"
	 */
	public PRF getAttributeLevelEvaluation() {
		final PRFBuilder prfBuilder = new PRFBuilder();
		for(MappingPair best : this.getRepository().getAll())
			prfBuilder.add(best.getScore());
		return prfBuilder.getResult();
	}
	
}