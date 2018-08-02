package it.uniroma3.weir.linking;

import static it.uniroma3.util.CollectionsCSVUtils.map2csv;
import it.uniroma3.weir.linking.entity.Entity;
import it.uniroma3.weir.vector.value.Value;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/*
Comunque le modifiche che ricordo abbiano dato maggior benefici e che sono tuttora utilizzate sono:
- l'introduzione di una soglia per gli idf
- ri-convertire i termini degli attributi ad un tipo adatto
- non considerare per le entità, la lista dei termini ma il set
 */
public class IdfRepository implements Serializable {

	static final private long serialVersionUID = 7013331645580592925L;
	
	final private Map<String, Double> term2idf;

	public IdfRepository() {
		this.term2idf = new HashMap<>();
	}

	public void updateDfs(Entity e) {
		for (Value value : e.getValues()) {
			incTermFrequency(value.toString());
		}
	}

	private void incTermFrequency(String term) {
		final Double idf = this.term2idf.get(term);
		this.term2idf.put(term, (idf==null ? 1d : idf + 1d ) );
	}

	public void finalizeIdfs(int numberOfPages) {
		for (String term : this.term2idf.keySet()) {
			Double df = this.term2idf.get(term);
			this.term2idf.put(term, Math.log( numberOfPages / df ));
		}
	}

	public double getIdf(String term) {
		return this.term2idf.get(term);
	}

	public String toString() {
		return map2csv(this.term2idf, "\n\t", ",", "\n");
	}

}
