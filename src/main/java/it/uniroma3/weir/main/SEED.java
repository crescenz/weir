package it.uniroma3.weir.main;

import it.uniroma3.weir.cache.CachedComputation;
import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.evaluation.PRF;
import it.uniroma3.weir.evaluation.PRFBuilder;
import it.uniroma3.weir.extraction.Extraction;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.linking.linkage.DomainLinkage.WebsiteLinkageBuilder;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
//import it.uniroma3.weir.linking.linkage.WebsitePair;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.value.Value;

import java.io.IOException;
import java.util.*;
/**
 * This is an automatic algorithm for extracting pages' soft-ids from a seed 
 * set of sample ids in order to apply a linking algorithm over them.
 * 
 * @see {@link SoftIdsLinker}
 * <HR/>
 * @see <a href="http://www.vldb.org/pvldb/vol6/p805-bronzi.pdf">
 * <i>Extraction and integration of partially overlapping web sources</i></a>.
 * Mirko Bronzi, Valter Crescenzi, Paolo Merialdo, Paolo Papotti.<br/>
 * Proceedings of the VLDB Endowment.<br/>
 * Volume 6 Issue 10, August 2013 (pages 805-816).<br/>
 * <em>Section 5.4: "Associating Pages with Soft-ids"</em>
 */
public class SEED extends CachedComputation<Experiment, DomainLinkage> {
	
	
	public static void main(String[] args) throws IOException {
		new MainSoftIds().run(args);
	}
	
	public static class MainSoftIds extends ExperimentMainTemplate {

		@Override
		protected void execute(Experiment experiment) {
			final Extraction extraction = new Extraction(experiment);
			extraction.extract();
			log.trace();
			
			final SEED softIdsCreator = new SEED();
			softIdsCreator.createIds(experiment);
		}

	}
	
	private DomainLinkage inputGoldenRepository;
	
	private Map<Website, List<AttributeValues>> site2values;

	private Map<Website, List<String>> site2ids;
	
	private Set<String> actualIds;
	
	private Experiment experiment;
	
	public SEED() {
		this.actualIds = new HashSet<String>();
		this.site2values = new HashMap<>();
		this.site2ids = new HashMap<>();
	}
	
	private HashSet<String> seeds() {
		Map<String, String[]> domain2seeds = new HashMap<String, String[]>();
//		domain2seeds.put("swde-restaurant", new String[] {"202-338-3830", "202-638-0800", "202-654-0999", "202-783-1475", "203-622-8450", "206-283-3313", "206-320-9771", "206-382-6999", "206-524-4044", "206-632-0767"});
//		domain2seeds.put("swde-book"      , new String[] {"9780060256678", "9780060525613", "9780060525651", "9780060554736", "9780061139673", "9780061994395", "9780091922290", "9780099387916", "9780136009986", "9780140053203"});
//		domain2seeds.put("swde-university", new String[] {"Albright College", "Anderson University", "Appalachian State University", "Augsburg College", "Austin College", "Belmont Abbey College", "Brenau University", "Brescia University", "Bridgewater College", "Buena Vista University"});
//		domain2seeds.put("swde-camera"    , new String[] {"06852", "227510", "227515", "26195", "3637B001", "4342B001", "8009581", "8281180", "8721664", "8770414", "8836645"});
//		domain2seeds.put("swde-auto"      , new String[] {"2010 BMW M3", "2010 BMW X5", "2010 BMW X6 M", "2010 BMW Z4", "2010 Bentley Continental GT", "2010 Bentley Continental GTC", "2010 Chevrolet Aveo", "2010 Chevrolet Express 1500", "2010 Dodge Journey	2010 Ford Fusion", "2010 GMC Savana 1500"});
//		domain2seeds.put("swde-movie"     , new String[] {"2001: A Space Odyssey", "A Life Less Ordinary", "A Little Princess", "Bad Boys", "Bad Company", "Basic Instinct", "Blade Runner", "Conan the Barbarian", "Father of the Bride", "Friday the 13th"});
//		domain2seeds.put("weir-finance"   , new String[] {"AES", "AFL", "TWX", "T", "ABT", "ANF", "ADPT", "ADBE", "AMD", "ADVS"});
//		domain2seeds.put("weir-book"      , new String[] {"0001049305", "0001049313", "0001049321", "0001049356", "0001049410", "0001049429", "0001049682", "0001049690", "0001049852", "0001049860", "0001049895", "0001050028", "0001050044", "0001050052", "0001050060", "0001050079", "0001050087", "0001050095", "0001050109", "0001050117"});
//		domain2seeds.put("weir-soccer"    , new String[] {"Abel Xavier", "Alan O'Brien", "Albert Luque", "Alex", "Andreas Ottl", "Andrew Taylor", "Andri Pyatov", "Artem Kravets", "Badr El Kaddouri", "Bastian Schweinsteiger", "Breno"});
//		domain2seeds.put("weir-videogame" , new String[] {"2006 FIFA World Cup", "Alien Hominid", "Aliens: Colonial Marines", "Avatar: The Last Airbender", "Bee Movie Game", "Bionicle Heroes", "Bliss Island", "Blitz: The League", "BloodRayne", "Bolt", "Brain Challenge", "Brave: A Warrior's Tale"});
		final String[] seeds = domain2seeds.get(WeirConfig.getCurrentExperiment().getDataset().getName()+ "-" + WeirConfig.getCurrentExperiment().getDomain().getName());
		return new HashSet<>(Arrays.asList(seeds));
	}
	
	
	private Map<Website, List<AttributeValues>> createMap(List<Website> sites) {
		Map<Website, List<AttributeValues>> site2values = new HashMap<>();		
		for (Website site : sites) {
			List<AttributeValues> list = new ArrayList<AttributeValues>();
			for (Attribute attr : site.getAttributes()) {
				list.add(new AttributeValues(attr));
			}
			site2values.put(site, list);
		}
		return site2values;
	}
	
	@Override
	public Fingerprint fingerprint(Experiment exp) {
		// TODO	fingerprint(exp);
		return getFingerprint("soft-ids");
	}

	public DomainLinkage createIds(Experiment exp) {
		return cachedComputation(exp);
	}
	@Override
	public DomainLinkage uncachedComputation(Experiment exp) {
		this.inputGoldenRepository = exp.getLinkages(); // VC: n.b. this is used as Golden Linkage Repository
		this.experiment = exp;
		List<Website> sites = exp.getWebsites();
		this.actualIds = seeds();		
		this.site2values = createMap(sites);
		log.newPage("soft ids creation");

		for (int i = 0; i < sites.size(); i++) {
			log.newPage("iteration " + i);
			
			SiteAndRule war = findSiteWithBestIntersection(sites, this.actualIds);
			sites.remove(war.getWebsite());
			this.actualIds.addAll(war.getRuleValues());
			this.site2ids.put(war.getWebsite(), war.getRuleValues());
			
			log.endPage();
			log.trace("best website is: " + war.getWebsite());
			log.trace("best attribute is: " + war.getRuleValues());
			log.trace();
		}
		log.endPage();
		this.evaluateIds();
		return this.saveLinkage();
	}

	private SiteAndRule findSiteWithBestIntersection(List<Website> sites, Set<String> ids) {
		Website best = null;
		AttributeValues bestAttr = null;
		int bestIntersection = -1;
		
		for (Website site : sites) {
			log.trace(site.toString());
			AttributeValues bestWsAttr = null;
			int bestWsIntersection = -1;
			
			for (AttributeValues attr : this.site2values.get(site)) {
				int intersection = getIntersection(ids, attr);
				
				if (intersection > bestWsIntersection) {
					bestWsIntersection = intersection;
					bestWsAttr = attr; 
				}
			}		
			
			log.trace("best intersection: " + bestWsIntersection);
			log.trace("best attribute is: " + bestWsAttr.getValues());
			
			if (bestWsIntersection > bestIntersection) {
				log.trace("OK, best so far");
				bestIntersection = bestWsIntersection;
				best = site;
				bestAttr = bestWsAttr;
			}
			
			log.trace();
		}
		
		return new SiteAndRule(best, bestAttr);
	}

	private int getIntersection(Set<String> ids, AttributeValues attr) {
		List<String> copyList = new ArrayList<String>();
		copyList.addAll(ids);
		copyList.retainAll(attr.getValues());
		return copyList.size();
	}

	public void evaluateIds() {
		log.newPage("evaluate ids");
		PRFBuilder builder = new PRFBuilder();
		
		for (Website site : this.experiment.getWebsites()) {
			log.trace(site.toString());
			List<String> ids = new ArrayList<String>();
			double intersection = 0;
			for (int i = 0; i < site.getOverlappingPages().size(); i++) {
				String pageId = site.getOverlappingPages().get(i).getId();
				String attrValue = this.site2ids.get(site).get(i);
				ids.add(pageId);
				if (attrValue != null) {
					intersection += 1 - Type.STRING.distance(pageId, attrValue);
				}
			}
			
			double precision = intersection / this.site2ids.get(site).size();
			double recall = intersection / ids.size();
			
			int total_expected_found = (int) intersection;
			int total_expected_NOT_found = (int) (ids.size() - intersection);
			int total_found_NOT_expected = (int) (this.site2ids.get(site).size() - intersection);

			PRF siteResult = new PRF(precision, recall, total_expected_found, total_expected_NOT_found, total_found_NOT_expected);
			log.trace("number of pages: " + site.getOverlappingPages().size());
			log.trace("page ids: " + ids);
			log.trace("ids found: " + this.site2ids.get(site));
			log.trace("result: <b>" + siteResult + "</b>");
			log.trace();
			builder.add(siteResult);
		}
		log.endPage();
		log.trace("result: " + builder.getResultOver(this.experiment.getWebsites().size()));
	}
	
	public DomainLinkage saveLinkage() {
		log.newPage("saving linkage");
		final Domain domain = this.experiment.getDomain();
		DomainLinkage repository = new DomainLinkage(domain, new WebsiteLinkageBuilder() {

			static final private long serialVersionUID = -7493644268980955704L;

			@Override
			public WebsiteLinkage createPair(Website w_i, Website w_j) {
				return linkBasedOnEqualsSoftIds(w_i, w_j);
			}
			
		});
		log.endPage();		
//		evaluateLinkage(repository);
		return repository;
	}
	
	private WebsiteLinkage linkBasedOnEqualsSoftIds(Website w1, Website w2) {
		log.newPage(w1 + " vs " + w2);
		List<PageLinkage> links = new ArrayList<PageLinkage>();
		
		for (int i = 0; i < w1.getOverlappingPages().size(); i++) {
			String id1 = this.site2ids.get(w1).get(i);
			
			for (int j = 0; j < w2.getOverlappingPages().size(); j++) {
				String id2 = this.site2ids.get(w2).get(j);
				if (id1 != null && id2 != null && Type.STRING.distance(id1, id2) == 0) {
					PageLinkage pl = new PageLinkage(w1.getOverlappingPages().get(i), w2.getOverlappingPages().get(j), 0.0);
					links.add(pl);
					log.trace(pl.toString());
				}
			}
			
		}
		log.endPage();
		
		return new WebsiteLinkage(w1, w2, links);
	}

	private static class SiteAndRule {
		
		private Website site;
		private AttributeValues values;
		
		public SiteAndRule(Website site, AttributeValues best) {
			this.site = site;
			this.values = best;
		}

		public Website getWebsite() {
			return this.site;
		}

		public List<String> getRuleValues() {
			return this.values.getValues();
		}

	}
	
	private static class AttributeValues {
		
		private List<String> values;
		
		public AttributeValues(Attribute attr) {
			this.values = new ArrayList<String>();
			for (Value v : attr.getVector().getElements()) {
				this.values.add(v.toString());
			}
		}
		
		public List<String> getValues() {
			return this.values;
		}
		
	}
	
}
