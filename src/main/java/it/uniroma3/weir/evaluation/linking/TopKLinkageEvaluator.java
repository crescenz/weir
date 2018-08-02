package it.uniroma3.weir.evaluation.linking;

import it.uniroma3.weir.evaluation.PRF;
import it.uniroma3.weir.evaluation.PRFBuilder;
import it.uniroma3.weir.linking.linkage.*;
import it.uniroma3.weir.linking.linkage.DomainLinkage.WebsiteLinkageProcessor;
import it.uniroma3.weir.vector.type.Type;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/*
 * Conclusioni su ciò che rimane della gerarchia di classi che ha radice in
 * DSCDLinkageEvaluator, per la valutazione dei linkage prodotti, ovvero:
 * 
 * - DSCDLinkageEvaluator
 * - FMeasureGraphEvaluator
 * - TopKLinkageEvaluator
 * 
 * Tutte ambiscono a valutare i linkage a livello di pagina, però:
 * 
 * - DSCDLinkageEvaluator & FMeasureGraphEvaluator lavorano con la matrice D 
 *                          TopKLinkageEvaluator invece no
 * 
 * - DSCDLinkageEvaluator fa il giro delle coppie di siti, le altre due no
 * 

 *                          
 * - TopKLinkageEvaluator   serve a graficare F del linkage (in realtà F=P=R) ed anche
 * 							N, ovvero il numero di coppie non ordinate di siti, in funzione
 * 							dell'"overlap", ovvero il numero minimo di istanze comuni.
 * 
 * Il motivo per cui P=R=F è che non sono esplicitamente costruiti i Linkage, 
 * ovvero l'insieme di tutti i PageLingake associati alla stessa istanza, come
 * invece avviene per i Mapping (insieme di tutti i Match associati allo stesso
 * attributo). Al momento ci si limita a contare la percentuale di PageLinkage 
 * corretti (e poi, arbitrariamente(?), si fissa R=P e quindi P=R=F).
 * 
 */
/**
 * Output an excel spreadsheet file reporting an evaluation of top-k linkages.
 * @see {@link it.uniroma3.weir.evaluation.RepositoryEvaluator<P, T>}
 * 
 *   Serve a graficare F del linkage (in realtà F=P=R) ed anche N,
 *   ovvero il numero di coppie non ordinate di siti, in funzione
 *   dell'"overlap", ovvero il numero minimo di istanze comuni.
 * 
 */
// VC: TODO separare la logica di esportazione dei risultati in .xls
public class TopKLinkageEvaluator implements WebsiteLinkageProcessor {

	private List<PRFBuilder> result;

	public TopKLinkageEvaluator() {
		this.result = new ArrayList<>();
	}
	
	public List<PRF> evaluate(DomainLinkage domainLinkage) {
		domainLinkage.forEach(this);
		return computeAverageResults();
	}
	
	@Override
	public void process(WebsiteLinkage siteLinkage) {
		evaluate(siteLinkage.getPageLinkages());
	}
	
	// VC: come veniva chiamato? per ogni coppia di siti?
	//     a giudicare dal fatto che N nei grafici di Andrea
	//     parte da 10 * 9 / 2 = 45 direi proprio di si
	private void evaluate(PageLinkageRepository repository) {
		final List<PageLinkage> ordered = repository.order();
		int correctCounter = 0;
		for(int k=0; k<ordered.size(); k++) {
			final PageLinkage link = ordered.get(k);

			if (isCorrect(link)) {
				correctCounter++;
			}

			updateResult(k, correctCounter);
		}
	}

	private boolean isCorrect(PageLinkage p) {
		final String id1 = p.getMin().getId();
		final String id2 = p.getMax().getId();
		return ( Type.STRING.distance(id1,id2)==0 );
	}

	private void updateResult(int k, double correct) {
		this.expandResultCapacityTo(k);
		final double precision = correct / (k + 1);
		final double recall = precision; // ??
		this.result.get(k).addPR(precision, recall);
	}
	
	private void expandResultCapacityTo(int k) {
		if (this.result.size()<k+1) {
			this.result.add(new PRFBuilder());
		}
	}
	
	private List<PRF> computeAverageResults() {
		final List<PRF> results = new LinkedList<>();
		for (int i=0; i<this.result.size(); i++) {
			final PRFBuilder builder = this.result.get(i);
			results.add(builder.getResult());
		}
		return results;
	}

}
