package it.uniroma3.weir.dscd.beta;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.evaluation.linking.TopKLinkageEvaluator;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.WebsitePair;
import it.uniroma3.weir.structures.Pair;
import it.uniroma3.weir.vector.ExtractedVector;

import java.util.*;

import org.jblas.DoubleMatrix;

public class BetaResolver {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();
	
	private Map<Attribute, Double> attribute2Cvalue;
	private Set<Attribute> resolvedAttributes;
	
	private int totalOverlappings;
	private int zeros;
	
	public BetaResolver() {
		this.attribute2Cvalue = new HashMap<>();
	}
	
	public void resolve(DoubleMatrix D, DoubleMatrix C, List<PageLinkage> links, List<Attribute> attributes) {
		
		resolveEntities(D, links);
		
		for (int i=0; i<attributes.size(); i++) {
			Attribute attr = attributes.get(i);
			Double value = C.get(i);
			this.attribute2Cvalue.put(attr, value);
		}
		
		this.resolvedAttributes = resolveAttributes(attributes);
		System.out.println("totalOverlappings:\t" + this.totalOverlappings);
		System.out.println("zeros:\t" + this.zeros);
	}
	
	private void resolveEntities(DoubleMatrix D, List<PageLinkage> links) {
		Map<WebsitePair, List<PageLinkage>> wss2linkage = new HashMap<>();
		
		for (int i=0; i<D.length; i++) {
			PageLinkage p = links.get(i);
			WebsitePair wss = new WebsitePair(p.getMin().getWebsite(), p.getMax().getWebsite());
			List<PageLinkage> linkage = wss2linkage.get(wss);
			
			if (linkage == null) {
				linkage = new ArrayList<>();
				wss2linkage.put(wss, linkage);
			}
			
			double sim = D.get(i);
			linkage.add(new PageLinkage(p, sim));
		}
		
		final TopKLinkageEvaluator verifier = new TopKLinkageEvaluator();
		
		for (Pair<Website> wss : wss2linkage.keySet()) {
			List<PageLinkage> linkage = wss2linkage.get(wss);
			Collections.sort(linkage);
			//verifier.evaluate(new PageLinkageRepository(linkage));
		}

		//verifier.computeAverageResults(); //...
	}
	
	private Set<Attribute> resolveAttributes(List<Attribute> attributes) {
		log.newPage("resolve attributes");
		Set<Attribute> correct = new HashSet<>(attributes);
		
		for (Attribute a : attributes) {
			if (correct.contains(a)) {
				
				List<Attribute> overlapped = getOverlappingAttributes(a);
				Attribute best = findBest(overlapped);
				
				if (best!=null) {
					overlapped.remove(best);
					correct.removeAll(overlapped);
				}
			}
		}
		
		log.endPage();
		return correct;
	}

	private List<Attribute> getOverlappingAttributes(Attribute pivot) {
		log.trace("overlapping attributes:");
		final List<Attribute> overlapping = new ArrayList<>();
		
		for (Attribute a : pivot.getWebsite().getAttributes()) {
			if (haveOverlappingRules(pivot, a)) {
				overlapping.add(a);
			}			
		}	

		return overlapping;
	}
	
	private boolean haveOverlappingRules(Attribute a1, Attribute a2) {
		final ExtractedVector vector1 = a1.getVector().getOriginatingVector();
		final ExtractedVector vector2 = a2.getVector().getOriginatingVector();
		
		for (int i=0; i<vector1.size(); i++) {
			final String mark1 = vector1.get(i).getOccurrenceMark();
			final String mark2 = vector2.get(i).getOccurrenceMark();

			if (Objects.equals(mark1, mark2)) return true;
		}
		
		return false;
	}
	
	private Attribute findBest(List<Attribute> overlapped) {
		final LinkedList<Attribute> attrs = new LinkedList<>(overlapped);
		
		Collections.sort(attrs, new Comparator<Attribute>() {
			@Override
			public int compare(Attribute a1, Attribute a2) {
				double w1 = attribute2Cvalue.get(a1);
				double w2 = attribute2Cvalue.get(a2);
				return Double.compare(w1,w2);
			}
		});

		Collections.sort(attrs);
		final Attribute best = attrs.getFirst();
		double bestWeight = this.attribute2Cvalue.get(best);
		log.trace("best: " + best.getID()+ " "+bestWeight);
		log.trace();
		logAttributes(attrs);
		
		this.totalOverlappings++;
		if (0<=bestWeight && bestWeight<=0.001) {
			this.zeros++;
			return null;
		}
		
		return best;
	}

	private void logAttributes(final LinkedList<Attribute> attrs) {
		for (Attribute a : attrs) {
			log.trace(a + " "+ this.attribute2Cvalue.get(a));
		}
		log.trace();
	}

	public Set<Attribute> getResolvedAttributes() {
		return this.resolvedAttributes;
	}
	
}
