package it.uniroma3.weir.linking;

import static it.uniroma3.weir.Formats.thousandth;
import it.uniroma3.hlog.HypertextualLogger;
import static it.uniroma3.weir.configuration.Constants.*;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.evaluation.linking.CandidateLinkingStatistics;
import it.uniroma3.weir.linking.linkage.DomainLinkage.WebsiteLinkageBuilder;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.linking.linkage.PageLinkageRepository;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;

import java.util.*;
import static it.uniroma3.weir.linking.linkage.PageLinkageRepository.allOverSimilarityThreshold;
/**
 	Consider a list of candidate {@link PageLinkage}s ordered by similarity for 
 	a pair of {@link Website}s. 
	
	For each {@link Page} from a {@link Website}, it select at most <tt>k</tt> 
	linking candidates from the other sites.
	
	Then it selects only the {@link PageLinkage}s in which the best similarity 
	score overcome the average similarity times a given multiplier, and it is
	below the average for the other.
	
	Stop when the similarity score "vanishes", i.e., it decreases more than a given percentage
	threshold from a candidate to the next.
	
	Select at most <tt>k</tt> candidate.
	<br/>
	FIXME Note that the average is set by false linkages whose max number is O(n*k) vs max O(n) true linkages
 */
public class CandidateLinkageFilter extends WebsiteLinkageBuilder {
	
	static final private long serialVersionUID = -8608454023526656715L;

	static final private HypertextualLogger log = HypertextualLogger.getLogger();
	
	
	// min similarity threshold need to consider a linkage
	private double minSimilarity;
	
	// do not proceed with list of candidates whose first score is less than 
	// this times the average score, i.e., no evidence of correct linkage
	private double firstScoreMultip;
	
	// do not proceed with list of candidates whose second-best score is lower 
	// than the first one by less than this percentage, i.e., no evidence that
	// the score is capable of clearly distinguishing true linkage from others
	private double firstStepMinPerc;
	
	// do not proceed with candidates lower than the previous score by more 
	// than this percentage, i.e., all evidence suddenly vanished
	private double otherStepMaxPerc;

	/* max number of candidate linkages to consider */
	final private int topK;

	final private CandidateLinkingStatistics stats; // stats about candidate linkage filters

	public CandidateLinkageFilter() {		
		this.stats = new CandidateLinkingStatistics();

		this.minSimilarity = WeirConfig.getDouble(ENTITY_SIM_THRESHOLD);
		this.topK = WeirConfig.getInteger(LINKING_TOP_K);

		String[] params = WeirConfig.getList(LINKING_PARAMETERS).toArray(new String[0]);
		
		this.firstScoreMultip = Double.parseDouble(params[0]);
		this.firstStepMinPerc = Double.parseDouble(params[1]);
		this.otherStepMaxPerc = Double.parseDouble(params[2]);
	}
	
	@Override
	public WebsiteLinkage createPair(Website w_i, Website w_j) {
		if (w_i.equals(w_j)) return new WebsiteLinkage(w_i);

		log.newPage(w_i + " vs " + w_j);
				
		PageLinkageRepository linkages = allOverSimilarityThreshold(this.minSimilarity, 
				w_i.getWebpages(), w_j.getWebpages()
		);

		linkages = linkages.top(topK);
		
		if (this.topK>1)
			filterCandidates(linkages, w_i, w_j);

		log.endPage();
		
		 // n.b. no linkage cut-off was here!!!		
		return new WebsiteLinkage(w_i, w_j, new ArrayList<>(linkages.getAll()));
	}
	
	private void filterCandidates(PageLinkageRepository linkages, Website w_i, Website w_j) {
		for(Webpage p_i : w_i.getWebpages()) {
			filterCandidates(p_i, linkages);
		}
		for(Webpage p_j : w_j.getWebpages()) {
			filterCandidates(p_j, linkages);			
		}	
	}

	private void filterCandidates(Webpage pivot, PageLinkageRepository repository) {
		final List<PageLinkage> candidates = new ArrayList<>(repository.getPairs(pivot));
		if (candidates.isEmpty()) {
			log.trace("no linkage candidates for "+pivot);
		} else if (candidates.size()==1) {// just one candidate
			log.trace("just one linkage candidate for "+pivot);
		} else {
			filterCandidates(candidates, repository);
		}
	}

	private void filterCandidates(List<PageLinkage> candidates, PageLinkageRepository repository) {
		Collections.sort(candidates);
		
		double averageScore = averageSimilarity(candidates);				

		final Iterator<PageLinkage> candidatesIterator = candidates.iterator();		
		// VC was: boolean aCorrectLinkageExists = this.stats.updateStatisticsFromComplete(pivot,site2,averageScore);

		//  minimum similarity threshold for the first candidate
		double firstThreshold = averageScore * this.firstScoreMultip;
		log.trace("first min threshold: " + thousandth.format(firstThreshold));
		
		final PageLinkage firstCandidate = candidatesIterator.next();		
		final double firstScore = firstCandidate.getSimilarity();
		
		if (firstScore<firstThreshold) {
			log.trace("the first candidate ",firstCandidate," does not reach the first threshold");
			
//			if (!aCorrectLinkageExists) { // keep track of the reason ...
			
			log.trace();
			removeAllRemainingPairs(repository,candidatesIterator,firstCandidate);
			return ;
		}
		
		// maximum similarity threshold for the second candidate		
		final double secondThreshold = firstScore - firstScore * this.firstStepMinPerc;
		log.trace("second max threshold: " + secondThreshold);
		final PageLinkage secondCandidate = candidatesIterator.next();
		final double secondScore = secondCandidate.getSimilarity();
		
		if (secondScore > secondThreshold) {
			log.trace("first and second candidates ", secondCandidate," are too close each other");
			
//			if (!aCorrectLinkageExists) { // keep track of the reason ...
			removeAllRemainingPairs(repository,candidatesIterator,secondCandidate);
			return;
		}
		
		// maximum similarity threshold for each following candidate
		double prevSim = secondScore;
		while (candidatesIterator.hasNext()) {
			PageLinkage nextCandidate = candidatesIterator.next();
			final double nextSim = nextCandidate.getSimilarity();
			final double nextSimilarityThreshold = prevSim - prevSim * this.otherStepMaxPerc;
			log.trace("next min threshold: " + nextSimilarityThreshold);
			if (nextSim<=nextSimilarityThreshold)  {
				log.trace("next candidate ", nextCandidate," vanishes");
				removeAllRemainingPairs(repository,candidatesIterator,nextCandidate);
				break;
			}
			prevSim = nextSim;
		}
//		if (!aCorrectLinkageExists) { // keep track of the reason ...
	}
	
	private void removeAllRemainingPairs(
			PageLinkageRepository repository,	
			Iterator<PageLinkage> it,
			PageLinkage last) {
		repository.remove(last);
		while (it.hasNext()) repository.remove(it.next());
	}

	private double averageSimilarity(List<PageLinkage> linkages) {
		// VC: qui una PageLinkageRepository.avg() ci starebbe bene.
		double averageScore = 0;
		for (PageLinkage l : linkages) {
			averageScore += l.getSimilarity();
		}
		averageScore /= linkages.size();

		return averageScore;
	}
	
	public void printStatistics() {
		this.stats.printStatistics();
	}
	
	public CandidateLinkingStatistics getLinkageStatistics() {
		return this.stats;
	}

}
