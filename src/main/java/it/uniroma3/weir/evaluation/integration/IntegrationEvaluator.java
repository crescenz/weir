package it.uniroma3.weir.evaluation.integration;

import static it.uniroma3.weir.configuration.Constants.MATCH_EVALUATOR;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.hlog.Logpage;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.evaluation.PRF;
import it.uniroma3.weir.evaluation.linking.TopKLinkageEvaluator;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.model.hiddenrelation.AbstractRelation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static it.uniroma3.weir.model.log.WeirStyles.*;
import static it.uniroma3.hlog.HypertextualUtils.*;
/**
 * 
 * Evaluate <em>output</em> {@link Mapping}s by matching them with 
 * <em>golden</em> {@link Mapping}s (i.e., perfect mappings manually
 * crafted to work as a reference just for evaluation purposes).
 * 
 * This class evaluates the output integration (schema matches) and hence
 * it indirectly evaluates the produced extraction rules, as well.
 * It does not cover the evaluation of the linkages (instance linkages).
 * 
 * @see {@link TopKLinkageEvaluator} for how the output linkages are evaluated.
 */
public class IntegrationEvaluator {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	private List<MatchEvaluator> modes;

	private List<PRF> results;

	public IntegrationEvaluator() {
		this(getConfiguredMatchEvaluators());
	}
	
	static private MatchEvaluator[] getConfiguredMatchEvaluators() {
		List<MatchEvaluator> result = new LinkedList<>();
		for(String mode : WeirConfig.getList(MATCH_EVALUATOR))
			//FIXME move to Enum the static factory method for lists...
			result.add(MatchEvaluator.valueOf(mode.toUpperCase()));
		return result.toArray(new MatchEvaluator[0]);
	}

	public IntegrationEvaluator(MatchEvaluator... modes) {
		this.modes = Arrays.asList(modes);
		this.results = new LinkedList<>();
	}
	
	public List<MatchEvaluator> getMatchEvaluators() {
		return this.modes;
	}	

	public void evaluate(AbstractRelation _H_) {
		final Experiment experiment = _H_.getExperiment();
		final Set<Mapping> golden = experiment.getGoldenMappingSet().getMappings();
		final Set<Mapping> output = _H_.getAbstractAsMappings();
		log.newTable();
		for(MatchEvaluator mode : getMatchEvaluators()) {
			Logpage page;
			page = log.newPage();
			final MappingSetEvaluator eval = new MappingSetEvaluator(mode);
			final PRF result = eval.evaluate(golden, output);
			page = log.endPage();
			
			this.results.add(result);

			// Evaluate at mappings' level
			log.trace(header("level"),header(linkTo(page).withAnchor("scoring with "+mode)));
			log.trace("mapping",eval.getMappingLevelEvaluation());
			
			// Evaluate at attributes' level
			log.trace("attribute",eval.getAttributeLevelEvaluation());
		}
		log.endTable();
	}

	public List<PRF> getResults() {
		return this.results;
	}

}