package it.uniroma3.weir.linking.entity;

import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.WeirId;
import it.uniroma3.weir.vector.value.Value;

import java.io.Serializable;
import java.util.List;
/**
 * 
 * By  <em>entity</em> we mean the information used to 
 * perform record-linkage between two {@link Webpage}s
 * <br/>
 * Examples of possible entities are:
 * <ul>
 * <li> a <i>soft</i>-id
 * <li> a set of {@link Value}s extracted from a {@link Webpage} 
 * 	    by the available {@link ExtractionRule}s
 * </ul>
 * 
 * The similarity between two entities is computed by the
 * method {@linkplain #similarity(Entity)}.<br/>
 * 
 * This terminology is also used in:
 * @see <a href="http://dl.acm.org/citation.cfm?id=1247541">
 * <i>Query relaxation using malleable schemas</i></a>
 * <br/>
 * Xuan Zhou, Julien Gaugaz, Wolf-tilo Balke, Wolfgang Nejdl.<br/>
 * ACM SIGMOD 2007
 */
/**
 * - Da ogni pagina (di ogni sito) viene costruita un'entità, che possiede 
 *   un valore per ogni attributo pubblicato dal sito
 *   NB: tale valore viene convertito con un tipo adatto in WEIR ogni vettore 
 *       ha un tipo, ma è possibile che non tutti gli elementi siano di quel 
 *       tipo ad es: ["a","2.0","c"]: in WEIR tale vettore avrebbe tipo STRING,
 *       ma in Jaccard ogni valore conserva il suo tipo (quindi il secondo 
 *       elemento sarebbe un NUMBER)
 *       
 * - La similarità sim tra due entità <e1,e2> è calcolata considerando, 
 *   per ogni valore t1 di e1, i passi seguenti:
 *   + si cerca il più vicino valore t2 di e2 (dove t1 e t2 devono condividere
 *     lo stesso tipo)
 *   + si prendono gli idf dei due valori solo nel caso in cui entrambi gli idf
 *     sono maggiori di una certa soglia (ovvero 4) allora
 *     sim += sim(t1,t2) * idf_t1 * idf_t2 * norm
 *     
 *   Utilizzare una soglia per gli idf permette di considerare solo termini
 *   poco ricorrenti e quindi caratterizzanti.
 *   
 *   norm invece è una costante che dipende dal tipo di t1 e t2, utilizzata per 
 *   pesare i termini sull base del loro tipo. norm assume i seguenti valori:
 *   - per STRING, DATE, ISBN, PHONE e URL è uguale a 1.0
 *   - per SPACE, MASS e MONEY è uguale a 0.25
 *   - per NUMBER è uguale a 0.1
 *
 */
/*
 * Andrea Patrizio:
 * Le modifiche tuttora utilizzate che abbiano dato maggior benefici sono:
 * + l'introduzione di una soglia per gli idf
 * + ri-convertire i termini degli attributi ad un tipo adatto
 * + non considerare per le entità, la lista dei termini ma il set (bag)
 */
public abstract class Entity 
				extends WeirId 
				implements Serializable {

	static final private long serialVersionUID = 3081838806249424139L;
	
	private Webpage page;

	protected Entity(Webpage page) {
		super(page.getId());
		this.page = page;
		page.setEntity(this);
	}

	public Website getWebsite() {
		return this.getWebpage().getWebsite();
	}

	public Webpage getWebpage() {
		return this.page;
	}

	/* this is here just to support idf computation for any type of entity */
	abstract public List<Value> getValues();
	
	abstract public double similarity(Entity that);
	
	@Override
	public int hashCode() {
		return this.getClass().hashCode()+this.getWebpage().hashCode();
	}
	@Override
	public boolean equals(Object o) {
		if (o == null || this.getClass()!=o.getClass()) return false;
		
		Entity that = (Entity)o;
		return this.getWebpage().equals(that.getWebpage());
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName()+":" + this.page.getId();
	}

}
