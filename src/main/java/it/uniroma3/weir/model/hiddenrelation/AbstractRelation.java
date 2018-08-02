package it.uniroma3.weir.model.hiddenrelation;

import static it.uniroma3.util.CollectionsCSVUtils.collection2csv;
import static it.uniroma3.hlog.HypertextualUtils.objectPopup;
import static it.uniroma3.hlog.HypertextualUtils.popup;
import static it.uniroma3.weir.model.log.WeirStyles.*;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.dscd.DomainFusion;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.*;

import java.util.*;

/**
 *
 * The Abstract Relation is the final data structure produced by
 * solving a triple extraction-linking-integration problem.
 * 
 * @see <a href="http://www.vldb.org/pvldb/vol6/p805-bronzi.pdf">
 * <i>Extraction and integration of partially overlapping web sources</i></a>.
 * <br/>
 * Mirko Bronzi, Valter Crescenzi, Paolo Merialdo, Paolo Papotti.<br/>
 * Proceedings of the VLDB Endowment VLDB.<br/>
 * Volume 6 Issue 10, August 2013 (pages 805-816).<br/>
 * <em>Section 2.</em>
 *
 */
public class AbstractRelation {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	/* accumulate linkage/match info to form an integrated view of the sources */
	
	private DomainLinkage linkages;    /* linkages between pages */

	private MatchRepository matches;   /* matches between attributes */
	
	/**
	 * set of aligned source {@link Webpage}s
	 */
	final private Set<AbstractInstance> instances;

	/**
	 * set of matched source {@link Attribute}s 
	 * (within the same mapping)
	 */
	final private Set<AbstractAttribute> attributes;

	/**
	 *  Indexing maps to support query of {@link AbstractAttribute}s
	 *  by source {@link Attribute}, and query of 
	 *  {@link AbstractInstance}s by {@link Webpage}
	 */
	// source attribute -> abstract attribute
	final private Map<Attribute, AbstractAttribute> source2attribute;
	
	//             page -> abstract instance
	final private Map<Webpage, AbstractInstance> page2instance;

	/**
	 *  The experiment producing this hidden abstract relation
	 */
	final private Experiment experiment;

	public AbstractRelation(Experiment exp) {
		this.experiment = exp;
		this.attributes = new LinkedHashSet<>();
		this.instances  = new LinkedHashSet<>();
		this.page2instance    = new HashMap<>();
		this.source2attribute = new HashMap<>();
	}

	public Experiment getExperiment() {
		return this.experiment;
	}
	
	public DomainLinkage getLinkages() {
		return this.linkages;
	}
	
	public MatchRepository getMatches() {
		return this.matches;
	}
	
	public void setMatches(MatchRepository matches) {
		this.matches = matches;
	}

	public void setLinkages(DomainLinkage linkages) {
		this.linkages = linkages;
	}
	
	public void add(MappingSet mappingSet) {
		// create abstract attributes
		log.newPage();
		log.newTable();
		int red = 0;
		int non = 0;
		for(Mapping m : mappingSet) {
			if (m.size()>=2) {
				log.trace("redundant", m.toString());
				this.addAbstractAttribute(new AbstractAttribute(m));
				red++;
			} else {
				log.trace("not redundant", objectPopup(m.toString(),m));
				non++;
			}
		}
		log.endTable();
		log.endPage("found "+red+" redundant mappings and "+non+" singleton");
	}

	public void add(DomainLinkage linkage) {
		this.setLinkages(linkage);
		/* create abstract instances over domain linkages */
		final Domain domain = this.experiment.getDomain();
		final List<PageLinkage> ordered = linkage.getOrderedPageLinkages();
		log.newPage("removing 3+pages conflicting linkages");
		log.newTable();
		log.trace(header("page-linkage"),header("sim"),header("ai"));
		for(PageLinkage pl : ordered) {
			AbstractInstance ai = this.page2instance.get(pl.getMin());
			if (ai==null) ai = this.page2instance.get(pl.getMax());
			
			if (ai==null) {
				ai = new AbstractInstance(domain, pl.getMin(), pl.getMax());
				this.instances.add(ai);
			} else {
				if (!ai.isConflicting(pl.getMin()) && !ai.isConflicting(pl.getMax())) {
					ai.add(pl.getMin());
					ai.add(pl.getMax());
				} else remove(linkage, ai, pl);
			}
			this.page2instance.put(pl.getMin(), ai);
			this.page2instance.put(pl.getMax(), ai);
		}
		log.endTable();
		log.endPage();
	}
	
	//TODO Introduce AbstractLinker
	private void remove(DomainLinkage linkage, AbstractInstance ai, PageLinkage pl) {
		final Website siteMin = pl.getMin().getWebsite();
		final Website siteMax = pl.getMax().getWebsite();
		final WebsiteLinkage siteLinkage = linkage.get(siteMin, siteMax);
		siteLinkage.remove(pl);
		log.trace(pl,pl.getSimilarity(), popup("&bigotimes;",ai) ) ;
	}

	public void add(DomainFusion fusion) {
		// create abstract attributes and instances
		// TODO
	}

	public Set<AbstractInstance> getAbstractInstances() {
		return this.instances;
	}

	public Set<AbstractAttribute> getAbstractAttributes() {
		return this.attributes;
	}

	public Set<Mapping> getAbstractAsMappings() {
		final Set<Mapping> result = new HashSet<Mapping>();
		for(AbstractAttribute aa : getAbstractAttributes())
			result.add(aa.asMapping());
		return result;
	}

	public void addAbstractAttribute(AbstractAttribute aa) {
		this.attributes.add(aa);
		for (final Attribute attr : aa.getSourceAttributes()) {
			this.source2attribute.put(attr, aa);
		}
	}

	public AbstractAttribute getAbstractBySource(Attribute attribute) {
		return this.source2attribute.get(attribute);
	}

	public AbstractInstance getInstanceByPage(Webpage page) {
		return this.page2instance.get(page);
	}	

	@Override
	public String toString() {
		return collection2csv(this.getAbstractAttributes(),"","\n","");
	}

}
