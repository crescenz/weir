package it.uniroma3.weir.linking.linkage;


import static it.uniroma3.weir.configuration.Constants.ENTITY_SIM_THRESHOLD;
import static it.uniroma3.weir.vector.type.Type.STRING;
import static java.util.Collections.sort;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.WebsitePair;
import it.uniroma3.weir.structures.MapPairRepository;
import it.uniroma3.weir.structures.PairRepositories;
import it.uniroma3.weir.structures.PairRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
/**
 * VC:
 * N.B. è la controparte "naturale" di MatchRepository che non include un
 * 		primo livello di indicizzazione basato su {@link WebsitePair}s
 */
public class PageLinkageRepository
	   extends MapPairRepository<PageLinkage, Webpage> 
	   implements PairRepository<PageLinkage, Webpage> {

	static final private long serialVersionUID = 609955104922679986L;

	public PageLinkageRepository() {
		this(new Builder());
	}

	private PageLinkageRepository(Builder builder) {
		super(builder);
	}
	
	public PageLinkageRepository(Collection<PageLinkage> linkage) {
		this();
		this.addAll(linkage);
	}

	static public PageLinkageRepository cartesianProduct(
			List<Webpage> all1, 
			List<Webpage> all2) {
		final PageLinkageRepository result = new PageLinkageRepository();
		result.addCartesianProduct(all1, all2);
		return result;
	}
	
	static public PageLinkageRepository self(List<Webpage> all) {
		final PageLinkageRepository result = new PageLinkageRepository();
		for(Webpage page : all)
			result.add(new PageLinkage(page, page));
		return result;
	}
	
	/*
	 * Use pages' soft-id to establish the golden page-linkages
	 */
	static public PageLinkageRepository golden(
			  					List<Webpage> all1, 
			  					List<Webpage> all2) {
		PageLinkageRepository result = new PageLinkageRepository(new GoldenBuilder());
		result.addCartesianProduct(all1, all2);
		return result;
	}
	
	static public PageLinkageRepository allOverSimilarityThreshold(double th,
																   List<Webpage> all1, 
																   List<Webpage> all2) {
		PageLinkageRepository result = new PageLinkageRepository(new ThresholdBuilder(th));
		result.addCartesianProduct(all1, all2);
		return result;
	}
	
	
	static public PageLinkageRepository topK(int k, 
											 List<Webpage> all1, 
											 List<Webpage> all2) {
		PageLinkageRepository result = new PageLinkageRepository();
		PairRepositories.addTopKCartesianProduct(k, result,
				result.getPairBuilder(),
				new Weighter(),
				all1, all2);
		return result;
	}

	static public class Builder 
				  extends AbstractPairBuilder<PageLinkage, Webpage>
				  implements PairBuilder<PageLinkage, Webpage> {

		static final private long serialVersionUID = 3351913587434462500L;

		@Override
		public PageLinkage createPair(Webpage a, Webpage b) {
			return new PageLinkage(a,b);
		}

	}
	
	static public class ThresholdBuilder extends Builder 
						implements Serializable {

		static final private long serialVersionUID = 6895434123663627268L;
		
		final private double minSim;
		
		public ThresholdBuilder() {
			this(WeirConfig.getDouble(ENTITY_SIM_THRESHOLD));
		}
		public ThresholdBuilder(double threshold) {
			this.minSim = threshold;
		}
		@Override
		public PageLinkage createPair(Webpage a, Webpage b) {
			final double sim = a.getEntity().similarity(b.getEntity());
			if (sim>this.minSim)
				return new PageLinkage(a,b,sim);
			else return null;
		}

	}

	static public class GoldenBuilder extends Builder {

		static final private long serialVersionUID = 3839227278791217035L;
		
		/* TODO: double-check soft-ids uniqueness */
		@Override
		public PageLinkage createPair(Webpage a, Webpage b) {
			if (isAcorrectLinkage(a, b))
				return new PageLinkage(a, b);
			else return null;
		}
		/*
		 * Use pages' soft-id to establish the golden page-linkages
		 */
		private boolean isAcorrectLinkage(Webpage a, Webpage b) {
			final String aid = a.getId();
			final String bid = b.getId();
			return  ( STRING.distance(aid, bid) == 0d );
		}
	}
	
	static public class Weighter 
				  extends AbstractPairWeighter<PageLinkage, Webpage>
				  implements PairWeighter<PageLinkage, Webpage> {

		static final private long serialVersionUID = 1191973173227834858L;
		@Override
		public double weight(PageLinkage p) {
			return p.getSimilarity();
		}
		@Override
		public double weight(Webpage a, Webpage b) {
			return a.getEntity().similarity(b.getEntity());
		}

	}
	
	public PageLinkageRepository top(int k) {
		final int n = this.size();
		if (k<n) return this;
		return new PageLinkageRepository(order().subList(0, Math.min(k,n)));
	}
	
	public List<PageLinkage> order() {
		final List<PageLinkage> all = getAllLinkages();
		sort(all);
		return all;
	}	

	public LinkedList<PageLinkage> getAllLinkages() {
		final LinkedList<PageLinkage> allLinkages = new LinkedList<>();
		allLinkages.addAll(this.getAll());
		return allLinkages;
	}

}
