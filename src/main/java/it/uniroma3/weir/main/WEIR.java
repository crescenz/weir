package it.uniroma3.weir.main;

import static it.uniroma3.util.CollectionsCSVUtils.collection2csv;
import static it.uniroma3.weir.configuration.Constants.SHRINK_TO_OVERLAP;
import it.uniroma3.weir.configuration.BootstrappingLinkageFactory;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.evaluation.integration.IntegrationEvaluator;
import it.uniroma3.weir.integration.Weir;
import it.uniroma3.weir.linking.LinkageConflictSolver;
import it.uniroma3.weir.linking.OverlapHunter;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.hiddenrelation.AbstractRelation;

import java.io.IOException;

public class WEIR extends FusionTemplate {

	public static void main(String[] args) throws IOException {
		new WEIR().run(args);
	}

	@Override
	protected AbstractRelation process(Experiment exp) {
		final boolean shrink = WeirConfig.getBoolean(SHRINK_TO_OVERLAP);
		
		DomainLinkage linkages = link(exp);
		final Domain domain = exp.getDomain();
		if (shrink) {
			log.newPage("shrinking "+domain+" sources to their overlap");
			/* shrink the input domain to the connected 
			 * component composed of its overlapping pages */
			domain.shrinkToOverlap();
			/* repeat linking again */
			linkages = link(exp);
			log.endPage();
		}
		/* finalize page-alignments into an AbstractInstance  */
		exp.getAbstractRelation().add(linkages);

		/* log a domain linkage summary table */
		log.trace("linkage summary");
		log.trace(exp.getLinkages());
		final Weir weir = new Weir();

		final AbstractRelation _H_ = weir.analyze(exp);

		return _H_;
	}

	private DomainLinkage link(Experiment exp) {
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
		log.endPage();
		return linkages;
	}
	
	@Override
	protected void evaluate(AbstractRelation _H_) {
		log.warn("n.b. evaluation restricted to <u>redundant</u> pages");
		final IntegrationEvaluator ieval = new IntegrationEvaluator();
		ieval.evaluate(_H_);
		System.out.println(collection2csv(ieval.getResults(),"\n\t","\n\t","\n"));
	}

}
