package it.uniroma3.weir.dscd;

import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.linking.linkage.PageLinkageRepository;
import static it.uniroma3.weir.linking.linkage.PageLinkageRepository.*;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;

import java.util.List;
import java.util.Random;

public enum DSCDLinkages {

	/**
	 * All the page-linkages ({@link PageLinkage}s)
	 */
	ALL {
		@Override
		public PageLinkageRepository createLinkages(Website w1, Website w2) {
			return cartesianProduct(w1.getWebpages(), w2.getWebpages());
		}
	},
	
	/**
	 * Only the correct {@link PageLinkage}s
	 * (according to the pages' soft-ids).
	 */
	GOLDEN {
		@Override
		public PageLinkageRepository createLinkages(Website w1, Website w2) {
			return golden(w1.getWebpages(), w2.getWebpages());
		}

	},
	
	/**
	 * The {@link GOLDEN} {@link PageLinkage}s
	 * together with random pairs to reach a target
	 * error rate in the page-linkages.
	 */
	FIXED_ERROR_RATE {
		
		private float error_rate = 0.3f; // = WeirConfig.getDouble(Constants. ...);
		
		private Random random = new Random();
		
		@Override
		public PageLinkageRepository createLinkages(Website w1, Website w2) {
			final List<Webpage> all1 = w1.getWebpages();
			final List<Webpage> all2 = w2.getWebpages();
			final PageLinkageRepository result = golden(all1, all2);
			int golden_size = result.size();
			if (all1.isEmpty() || all2.isEmpty() || result.isEmpty())
				throw new IllegalStateException("Cannot compute a fixed error-rate set of page linkages");
			/* number of random pairs needed to reach the target error rate */
			int needed = Math.round(( 1 + error_rate ) * golden_size); 
			addRandomLinkages(result,all1,all2,needed);
			return result;
		}

		private void addRandomLinkages(PageLinkageRepository linkages,
				List<Webpage> all1, List<Webpage> all2, int needed) {
			final int toAdd = needed-linkages.size();
			for(int i=0; i<toAdd; i++)
				linkages.add(randomPageLinkage(all1,all2));
		}

		private PageLinkage randomPageLinkage(List<Webpage> all1, List<Webpage> all2) {
			final int i = this.random.nextInt(all1.size());
			final int j = this.random.nextInt(all2.size());
			return new PageLinkage(all1.get(i),all2.get(j));
		}
		
	};
	
	// VC: altri modi per scegliere i candidati "convertendo" l'output
	//     dei precedenti algoritmi di linking?
	// nb: esisteva it.uniroma3.weir.dscd.linkages.PageLinkages2EntityPairs
	
	abstract public PageLinkageRepository createLinkages(Website w1, Website w2);

}
