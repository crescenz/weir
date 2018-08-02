package it.uniroma3.weir.evaluation.integration;

import static it.uniroma3.weir.Formats.percentage_f;
import static it.uniroma3.hlog.HypertextualUtils.format;
import static it.uniroma3.weir.evaluation.integration.MatchEvaluator.DISTANCE;
import static it.uniroma3.weir.model.log.WeirStyles.header;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.evaluation.PRF;
import it.uniroma3.weir.evaluation.RepositoryEvaluator;
import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Mapping;

/**
 * Evaluate an <em>output</em> vs a <em>golden</em> {@link Mapping}.
 * <br/>
 * Comparing two mappings is a complex activity that requires: 
 * <em>(i)</em> first, to match every @link Attribute} from one mapping 
 * with every attribute from the other mapping; 
 * <em>(ii)</em> then to choose which matches should be finalized for a 
 * fine-grained (at level of values) evaluation of their similarity.
 * <br/>
 * In the setting considered, a mapping includes at most one attribute from
 * a site, so the attribute pairwise matching is trivial.
 * <br/>
 * However, for a fine-grained quantitative evaluation of the mappings
 * similarity, a single aggregated score is not enough. Several indicators are
 * produced: the number of attributes expected in the golden mapping and found
 * in the output mapping (<em>true positives</em>), those which have not been
 * found (<em>false negatives</em>), the number of attributes found in the
 * output but not expected according to the golden (<em>false positives</em>); 
 * the average precision, recall, and F-measure of the final matches.
 * <br/>
 * These produced measures  are saved into an {@link PRF} object.
 * 
 * @see {@link Mapping}, {@link MatchEvaluator}, {@link PRF}
 */
public class MappingEvaluator extends RepositoryEvaluator<Match, Attribute> {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	
	final private MatchEvaluator mode;
	
	public MappingEvaluator() {
		this(DISTANCE);
	}

	/**
	 * A repository of all matches between the 
	 * attributes of the two mappings being compared
	 */
	public MappingEvaluator(MatchEvaluator mode) {
		super(new MatchEvaluationRepository(mode), mode);
		this.mode = mode;
	}

	@Override
	public MatchEvaluationRepository getRepository() {
		return (MatchEvaluationRepository) this.repository;
	}
	
	public PRF evaluate(Mapping golden, Mapping output) {
		log.trace("golden mapping ");
		log.trace(golden);
		log.trace("output mapping ");
		log.trace(output);
		log.trace("<hr/>");
		this.repository.addCartesianProduct(golden.getAttributes(), output.getAttributes());

		// N.B. 'golden by golden' logics or holistic logics are equivalent
		//       given the per-site attributes matching logics
		final int nOfMatches = this.repository.size();
		log.trace("there are "+nOfMatches+" matches with score>0");
		if (nOfMatches>0) {
			log.trace("matching all output attributes vs golden "+golden);
			log.newTable();
			logHeader();
			for (final Attribute wanted : golden.getAttributes()) {
				final Match best = this.getRepository().findBestMatching(wanted);
				if (best!=null) {
					log(wanted, best);
				}
			}
			log.endTable();
		}
		/* now evaluate the repository with all the saved matches */
		final PRF result = super.evaluate(golden.getAttributes(), output.getAttributes());
		log.trace(result);
		return result;
	}

	private void logHeader() {
		log.trace(
				  header("golden"), 
				  header("best output"),
				  header("score"), 
				  header("match details")
		);
	}

	private void log(final Attribute wanted, final Match best) {
		log.trace(wanted.toString(),
				  best.getMate(wanted).toString(),
				  format(percentage_f, this.mode.score(best)), 
				  best);
	}

}
