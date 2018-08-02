package it.uniroma3.weir.integration;

import static it.uniroma3.weir.configuration.Constants.WORK_ON_OVERLAP;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.OracleIntegrator;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.evaluation.DomainAnalyzer;
import it.uniroma3.weir.extraction.Extraction;
import it.uniroma3.weir.integration.lc.LooseLocalConsistencySolver;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.MappingSet;
import it.uniroma3.weir.model.hiddenrelation.AbstractRelation;
/**
 * <b>W</b>eb <b>E</b>xtraction and <b>I</b>ntegration of <b>R</b>edundant data.
 * <p>
 * Main algorithm that includes at least these phases:
 * <ul>
 * <li> extraction
 * <li> weak-rule removal
 * <li> abstract integration
 * <li> [loose] local consistency resolution
 * </ul>
 * </p>
 * 
 * <HR/>
 * @see <a href="http://www.vldb.org/pvldb/vol6/p805-bronzi.pdf">
 * <i>Extraction and integration of partially overlapping web sources</i></a>.
 * <br/>
 * Mirko Bronzi, Valter Crescenzi, Paolo Merialdo, Paolo Papotti.<br/>
 * Proceedings of the VLDB Endowment.<br/>
 * Volume 6 Issue 10, August 2013 (pages 805-816).<br/>
 * 
 */
public class Weir {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	public AbstractRelation analyze(Experiment experiment) {
		final Domain domain = experiment.getDomain();
		final boolean workOn = WeirConfig.getBoolean(WORK_ON_OVERLAP);
		log.trace("working on "+ ( !workOn ? "all" : "overlap" ) + " pages");
		
		/* extract normalized and labeled data from input websites */
		log.newPage("extraction from "+domain);
		/* n.b. quick extraction only from the overlapping? pages; 
		 *      the extraction should be expanded over all the input
		 *      pages for a proper final evaluation.
		 */
		final Extraction extraction = new Extraction(experiment);	
		extraction.extract();
		log.endPage();
		
		log.trace("total number of attributes found: " + extraction.getTotalExtractedAttribute());
		
		log.newPage("choosing target attributes");
		
		// A basic integrator based on golden information.
		//
		// It sets an upper-bound to the integration results obtainable by any
		// integration algorithm taking as input the attributes as extracted
		// produced by the previous extraction step.
		final OracleIntegrator oracle = new OracleIntegrator(experiment);
		oracle.integrateJustExtracted();
		log.endPage();
		oracle.logSummary();
		/* use the golden data to analyze the domain wrt the assumptions */
		final DomainAnalyzer meea = new DomainAnalyzer(experiment);
		meea.logReport();
		/*                              */
		/* build the pyramid of matches */
		log.newPage("integration over "+domain);
		final PyramidBuilder egypto = new PyramidBuilder();

		final MatchRepository matches = egypto.buildPyramid(experiment);
		
		/* remove weak rules */
		final WeakRuleRemoval wrremover = new WeakRuleRemoval(matches);
		wrremover.removeWeakRules();
		
		/* integrate results */
		final AbstractIntegration integrator = new AbstractIntegration();
		final MappingSet integrated = integrator.integrate(matches);
		log.endPage();		
		
		/* solve loose local consistency assumption violation */
		log.newPage("solving l.c.a. violations");
		final LooseLocalConsistencySolver solver = new LooseLocalConsistencySolver(matches);
		final MappingSet output = solver.solveConflicts(integrated);
		log.endPage();
		
		log.newPage("resulting abstract relation");
		final AbstractRelation _H_ = experiment.getAbstractRelation();
		_H_.add(output);                       // create abstract attributes
//		log.trace("output linkages:");
		_H_.add(experiment.getLinkages());     // create abstract instances
		log.trace("abstract instances:");
		log.trace(_H_.getAbstractInstances()); // n.b. side effects on mappings' rendering
		log.trace();
		log.trace("abstract mappings:"); // log aligned mappings with aligned attributes
		log.trace(_H_.getAbstractAsMappings());
		log.endPage();
		
		return _H_;
	}
	
}
