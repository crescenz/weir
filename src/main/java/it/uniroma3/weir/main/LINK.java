package it.uniroma3.weir.main;

import static it.uniroma3.hlog.HypertextualLogger.getLogger;
import it.uniroma3.weir.configuration.BootstrappingLinkageFactory;
import it.uniroma3.weir.evaluation.linking.TopKLinkageEvaluator;
import it.uniroma3.weir.evaluation.linking.TopKXLSExporter;
import it.uniroma3.weir.linking.LinkageConflictSolver;
import it.uniroma3.weir.linking.OverlapHunter;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Experiment;

import java.io.IOException;

import jxl.JXLException;

/**
 * Execute an {@link Experiment} to create and evaluate the linkages produced
 * over an input {@link Domain}.
 */
public class LINK extends ExperimentMainTemplate {

	public static void main(String[] args) throws IOException {
		log = getLogger(LINK.class);
		new LINK().run(args);
	}
	
	@Override
	protected void execute(Experiment exp) throws IOException {
		/* compute initial linkages */
		log.newPage("linking");
		final Domain domain = exp.getDomain();
		final BootstrappingLinkageFactory factory = new BootstrappingLinkageFactory();
		final DomainLinkage linkages = factory.link(domain);		

		/* solve conflicts between page-linkages */
		final LinkageConflictSolver solver = new LinkageConflictSolver(linkages);				
		solver.solveLinkageConflicts();

		/* use linkages to find best overlapping pages between websites */				
		log.newPage("search overlap");
		final OverlapHunter overlapHunter = new OverlapHunter(linkages); 				
		overlapHunter.findOverlap(domain);
		log.endPage();
		
		/* log summary table by means of 
		 * it.uniroma3.weir.linking.linkage.DomainLinkageSummaryRenderer */
		log.trace("linkage summary");
		log.trace(domain);
		
		/* create AbstractInstances from aligned pages */
		exp.getAbstractRelation().add(linkages);
		
		/* evaluate resulting linkages */
		final TopKLinkageEvaluator evaluator = new TopKLinkageEvaluator();
		
		/* export results into an .xls */
		try {
			new TopKXLSExporter().export(evaluator.evaluate(linkages));
		} catch (JXLException jxle) {
			log.trace(jxle);
			jxle.printStackTrace();
		}
		log.endPage();
	}

}