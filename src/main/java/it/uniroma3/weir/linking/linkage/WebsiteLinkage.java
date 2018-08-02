package it.uniroma3.weir.linking.linkage;

import static it.uniroma3.weir.configuration.Constants.MAX_OVERLAPPING_SAMPLES;
import static java.util.Collections.shuffle;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.WebsitePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A set of {@link PageLinkage}s between two {@link Website}s of the same domain.
 * This is the counterpart wrt WebsiteMatching class.
 */
public class WebsiteLinkage extends WebsitePair implements Serializable, Iterable<PageLinkage> {

	static final private long serialVersionUID = -2359884265129570001L;

	static final protected HypertextualLogger log = HypertextualLogger.getLogger();
	
	final private int maxOverlap;
	
	private PageLinkageRepository linkage;

	private int originalSize; // before restricting to a subset of the linkages

	/* MGC */
	public WebsiteLinkage(Website s1, Website s2, PageLinkageRepository linkage) {
		super(s1,s2);
		this.linkage = linkage;
		this.maxOverlap = WeirConfig.getInteger(MAX_OVERLAPPING_SAMPLES);
	}

	public WebsiteLinkage(Website s1, Website s2, List<PageLinkage> linkage) {
		this(s1,s2,new PageLinkageRepository(linkage));
	}

	public WebsiteLinkage(Website s) {
		this(s, s, PageLinkageRepository.self(s.getWebpages()));
	}

/*  CHECK
 *  Not clear if it is simpler to move this logics to PageLinkageIterator level
	public WebsiteLinkage(List<Webpage> pages) {
		this(getSiteOfFirstPage(pages), pages);
	}
	
	static final private Website getSiteOfFirstPage(List<Webpage> pages) {
		return pages.get(0).getWebsite();
	}
	
	public WebsiteLinkage(Website s, List<Webpage> pages) {
		this(s, s, PageLinkageRepository.self(pages));
	}
*/
	
	public int size() {
		return this.linkage.size();
	}

	public int getOriginalSize() {
		return this.originalSize;
	}

	/**
	 * @return a {@link PageLinkageRepository}, a set of
	 *         {@linkplain PageLinkage}s involving two {@link Website}s
	 */
	public PageLinkageRepository getPageLinkages() {
		return this.linkage;
	}

	public void remove(PageLinkage pageLinkage) {
		this.linkage.remove(pageLinkage);
	}

	public WebsiteLinkage top(int k) {
		return new WebsiteLinkage(getMin(), getMax(), getPageLinkages().top(k));
	}

	/**
	 * order {@linkplain PageLinkage}s by similarity
	 * @return the page-linkages ordered by similarity
	 */
	public List<PageLinkage> order() {
		return this.getPageLinkages().order();
	}
	
	public int cutOffLinkages() {
		this.originalSize = this.linkage.size();
		if (this.linkage.size()>this.maxOverlap) {
			this.linkage = this.selectMaxOverlapRandomly();
		}
		return this.linkage.size();
	}
	
	static final private Random random = new Random(33);
	private PageLinkageRepository selectMaxOverlapRandomly() {
		/* order by similarity, so that we have a sequence of chunks 
		 * of page-linkages with decreasing similarity, each chunk
		 * is composed of linkages with the same similarity within it.
		 */
		final List<PageLinkage> linkage = this.getPageLinkages().order();

		/* find a chunk of linkages to cut, that composed of the linkages
		 * with the worst sim needed to cover all the required samples
		 */
		final int chunkToCutFirstIndex = this.maxOverlap-1;
		final double scoreOfCut = getSim(linkage, chunkToCutFirstIndex);
		int index = chunkToCutFirstIndex;
		
		/* move forward  to the  end of  the cut */
		while (index<this.linkage.size() && getSim(linkage, index)==scoreOfCut)
		{ index++; }
		final int end = index;
		/* move backward to the beginning of the cut */		
		index = chunkToCutFirstIndex;
		while (index>=0 && getSim(linkage, index)==scoreOfCut) 
		{ index--; };
		final int start = Math.max(0, index);		

		/* cut and randomly shuffle the linkages in the cut */
		final List<PageLinkage> cut = new ArrayList<>(linkage.subList(start, end));		// n.b. sublists are not serializable
		shuffle(cut,random);
		
		// first, add the top-linkages (better than those in the cut)
		final List<PageLinkage> selected = new ArrayList<>(linkage.subList(0, start));
		// then, reach the required number of samples by filling 
		// with other linkages randomly taken from the cut chunk
		selected.addAll(cut.subList(0, this.maxOverlap-selected.size()));
		return new PageLinkageRepository(selected);
	}

	final private double getSim(List<PageLinkage> linkage, int index) {
		return linkage.get(index).getSimilarity();
	}
	
	public boolean isEmpty() {
		return getPageLinkages().isEmpty();
	}
	
	@Override
	public Iterator<PageLinkage> iterator() {
		return getPageLinkages().order().iterator();
	}
	
}
