package it.uniroma3.weir.main;

import static it.uniroma3.util.CollectionsCSVUtils.collection2csv;
import it.uniroma3.weir.configuration.BootstrappingLinkageFactory;
import it.uniroma3.weir.dscd.beta.BetaAlgorithm;
import it.uniroma3.weir.evaluation.integration.IntegrationEvaluator;
import it.uniroma3.weir.extraction.Extraction;
import it.uniroma3.weir.linking.LinkageConflictSolver;
import it.uniroma3.weir.linking.OverlapHunter;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.hiddenrelation.AbstractRelation;
/*
 * - per eseguire betaWeir devo eseguire il main in MainBetaOnJaccardLinkage, vero?

	Sì. Purtroppo l'esecuzione è un pò articolata: (VC: Confermo :(((
	- il dominio di riferimento può essere selezionato tramite il metodo _domains_
	- il linkage su cui far partire l'algoritmo invece è dato dalla concatenazione di linkageSuffix e _TOPK
 * linkageSuffix è il suffisso che viene posto dopo il nome del lingake e rappresenta i valori delle euristiche con cui si è costruito quel linkage
 * _TOPK invece indica quale versione dei topk linkage utilizzare

I vari valori di linkageSuffix che vede commentati nella classe fanno riferimento a diversi domini, e sono ordinati
 secondo l'ordine definito in _domains_: il primo è per swd-job, il secondo per swde-auto, ecc...

Esempio pratico:
lancio MainBetaOnJaccardLinkage sul dominio "swde-auto", con _TOPK="_top20" e linkageSuffix="_1.5_0.075_0.05_1.0"
- il linkage che viene utilizzato è Temp\jaccard_linkages\swde-auto_1.5_0.075_0.05_1.0_top20
- i dati di estrazione ed integrazione vengono salvati/caricati nella cartella Temp\weir-cache_swde-auto

 */
/**
 * FUSE == BETA
 */
public class FUSE extends FusionTemplate {

	public static void main(String[] args) {
		new FUSE().run(args);
	}

	@Override
	protected AbstractRelation process(Experiment exp) { 
		/* initial extraction */
		// VC: dovrebbe servire per creare le ValueEntity che servono per Templated Jaccard
		final Extraction extraction = new Extraction(exp);
		extraction.extract();

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
		
		/* create AbstractInstances from page-linkages */
		exp.getAbstractRelation().setLinkages(linkages);
		
		final BetaAlgorithm alg = new BetaAlgorithm();
		return alg.fuse(exp);
	}

	@Override
	protected void evaluate(AbstractRelation _H_) {
		final IntegrationEvaluator evaluator = new IntegrationEvaluator();
		evaluator.evaluate(_H_);
		System.out.println(collection2csv(evaluator.getResults(),"\n\t","\n\t","\n"));
	}
	
}
