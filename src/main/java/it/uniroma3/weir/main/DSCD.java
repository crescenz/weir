package it.uniroma3.weir.main;

import it.uniroma3.weir.dscd.DSCDAlgorithm;
import it.uniroma3.weir.dscd.DomainFusion;
import it.uniroma3.weir.evaluation.linking.DomainLinkageStatistics;
import it.uniroma3.weir.extraction.Extraction;
import it.uniroma3.weir.linking.entity.Entity;
import it.uniroma3.weir.linking.entity.ValueEntity;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.hiddenrelation.AbstractRelation;
/**
 * An implementation of the <em>DSCDAlgorithm</em> algorithm.
 * 
 * @see <a href="http://dl.acm.org/citation.cfm?id=1247541">
 * <i>Query relaxation using malleable schemas</i></a>
 * <br/>
 * Xuan Zhou, Julien Gaugaz, Wolf-tilo Balke, Wolfgang Nejdl.<br/>
 * ACM SIGMOD 2007.
 * <br/>
 * 
 * <em>Section 3.1</em>
 */
public class DSCD extends FusionTemplate {
	
	public static void main(String[] args) {
		new DSCD().run(args);
	}

	@Override
	protected AbstractRelation process(Experiment exp) {
		final Domain domain = exp.getDomain();
		
		// log simple statistics over input candidate linkages
		new DomainLinkageStatistics(domain).logStatistics();

		/** generate extraction rules and get values 
		 *  (they're used by the entities below)  */
		new Extraction(exp).extract();
		log.trace();
		
		//final DomainMatching linkages = createInitialLinkages(exp);
		//questo finira' dentro it.uniroma3.weir.dscd.linkages.PageLinkages2EntityPairs?
		// NO: è un modo per scegliere i candidati.

		/** create entities, i.e., information for linking pages */
		createLinkingEntities(domain);

		/* DSCD acts as WebsiteFusionBuilder      */
		final DSCDAlgorithm dscd = new DSCDAlgorithm();
		/* a DomainRepository to save the results */
		final DomainFusion fusion = new DomainFusion(domain, dscd);
		
		final AbstractRelation _H_ = new AbstractRelation(exp);
		_H_.add(fusion);
		return _H_;// TODO it DomainFusion -> DomainLinkage | MappingSet
	}

	@Override
	protected void evaluate(AbstractRelation _H_) {
		/* to evaluate output matches  */
//		final DSCDMatchesEvaluator mEval = new DSCDMatchesEvaluator();
		/* to evaluate output linkages */
//		final DSCDLinkageEvaluator lEval = new DSCDLinkageEvaluator();
	}

	private void createLinkingEntities(Domain domain) {
		for(Website site : domain)
			createLinkingEntities(site);
	}

	private void createLinkingEntities(Website site) {
		if (!site.getEntities().isEmpty()) return;

		for (Webpage p : site.getOverlappingPages()) {
			Entity e = new ValueEntity(p);
			p.setEntity(e);
		}
	}

}
