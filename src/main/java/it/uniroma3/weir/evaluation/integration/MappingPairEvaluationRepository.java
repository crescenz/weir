package it.uniroma3.weir.evaluation.integration;

import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import static it.uniroma3.hlog.HypertextualUtils.percentage;
import static it.uniroma3.weir.model.log.WeirStyles.header;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.hlog.Logpage;
import it.uniroma3.weir.evaluation.PRF;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.structures.MapPairRepository;
import it.uniroma3.weir.structures.PairRepository;

import java.util.Collection;
import java.util.Collections;

public class MappingPairEvaluationRepository
	   extends MapPairRepository<MappingPair, Mapping> 
       implements PairRepository<MappingPair, Mapping> {

	static final private long serialVersionUID = -1641476671902776842L;
	
	static final private HypertextualLogger log = HypertextualLogger.getLogger();
	
	static final private class MappingPairEvaluationBuilder 
	                     extends AbstractPairBuilder<MappingPair, Mapping>
	                     implements PairBuilder<MappingPair, Mapping> {

		static final private long serialVersionUID = -5021321227971464565L;

		final private MatchEvaluator matcher;
		
		public MappingPairEvaluationBuilder(MatchEvaluator matcher) {
			this.matcher = matcher;
		}
		
		@Override
		public MappingPair createPair(Mapping a, Mapping b) {
			log.newPage();
			final MappingEvaluator eval = new MappingEvaluator(matcher);
			final PRF result = eval.evaluate(a, b);
			log(log.endPage(), a, b, result);
			if (result.getFMeasure()>0d) /* consider only F>0 pairs */
				return new MappingPair(a, b, result);
			return null;
		}

		private void log(Logpage page, Mapping m1, Mapping m2, PRF prf) {
			log.trace(
					linkTo(page).withAnchor("evaluation"),
					m1.toString(), m2.toString(), 
					percentage(prf.getPrecision()),
					percentage(prf.getRecall()),
					percentage(prf.getFMeasure()),
					prf.getTruePositives(),
					prf.getFalseNegatives(),
					prf.getFalsePositives()
			);
		}
	}
		
	public MappingPairEvaluationRepository(MatchEvaluator mode) {
		super(new MappingPairEvaluationBuilder(mode));
	}
	
	@Override
	public MapPairRepository<MappingPair, Mapping> 
	       addCartesianProduct(Collection<Mapping> goldenSet, Collection<Mapping> outputSet) {
		log.trace("comparing output vs golden mappings");
		/* build a repository of all golden vs output mapping pairs */
		log.newPage();
		log.newTable();
		logHeader();
		super.addCartesianProduct(goldenSet, outputSet);
		log.endTable();
		log.endPage(this.size() + " mappings with F&gt;0");
		return this;
	}

	/**
	 * @return the best mapping pair according to 
	 *         {@linkplain MappingPair#compareTo(MappingPair)}
	 */
	public MappingPair getBestMappingPairByF() {
		return Collections.min(this.getAll());
	}

	private void logHeader() {
		log.trace(
				header("details"),
				header("golden m."),
				header("output m."),
				header("P"),  header("R"),  header("F"),
				header("tp"), header("fn"), header("fp")
		);
	}
	
}
