package it.uniroma3.weir.extraction;


import static it.uniroma3.hlog.HypertextualUtils.*;
import static it.uniroma3.weir.Formats.percentage;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.hlog.Logpage;
import it.uniroma3.weir.extraction.filter.FilterChain;
import it.uniroma3.weir.integration.lc.LocalConsistencyEnforcer;
import it.uniroma3.weir.model.*;
import it.uniroma3.weir.model.log.VectorListRenderer;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.Label;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.VectorCaster;
import it.uniroma3.weir.vector.value.Value;

import java.util.LinkedList;
import java.util.List;

/**
 * Manages the <em>extraction</em> of <em>normalized</em> and <em>labeled</em>
 * {@linkplain ExtractedVector} of {@link Value}s from a list of 
 * {@linkplain Website}s each composed of a set of {@linkplain Webpage}s.
 * 
 */
public class Extraction {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	final private Extractor extractor;
	
	final private Experiment experiment;

	private int attributesCounter;
	
	public Extraction(Experiment exp) {
		this.extractor = new Extractor();
		this.experiment = exp;
		this.attributesCounter = 0;
	}

	public void extract() {
		final Domain domain = this.experiment.getDomain();
		final List<Website> websites = domain.getSites();
		log.trace("extraction from "+domain);
		int sitesCounter = 1;
		this.attributesCounter = 0;
		for (final Website site : websites) {
			log.newPage("processing website (" + sitesCounter + "/" + websites.size()+ ")\t"+site);
			this.process(site);
			log.endPage();

			attributesCounter += site.getAttributes().size();

			log.newPage("found " + site.getAttributes().size() + " attributes");
			log.trace("attributes extracted from: "+site);
			log.trace(site.getAttributes());
			log.endPage();
			log.trace();

			sitesCounter++;
		}
		log.trace("total number of attributes found over "+websites.size()
				 +" websites: " + this.attributesCounter);
	}
	
	public int getTotalExtractedAttribute() {
		return this.attributesCounter;
	}

	private void process(Website website) {
		log.trace("website: " + website);
		final List<Webpage> webpages = website.getWebpages();
		log.page("total   pages: " + webpages.size(),webpages);
		final List<Webpage> overlap = website.getOverlappingPages();
		log.page("overlap pages: " + overlap.size(),overlap);

		//N.B.: extraction includes rules inference
		final List<ExtractedVector> extracted = this.extraction(website);
		
		final List<ExtractedVector> filtered  = this.filtering(extracted);

		final List<ExtractedVector> labeled   = this.labeling(filtered);

		final List<Vector> normalized = this.normalization(labeled);

		// enforce local consistency constraint
		final List<Vector> consistent = this.enforceLocalConsistency(normalized);

		for (final Vector v : consistent) {
			website.addAttribute(new Attribute(v));
		}
	}

	private List<ExtractedVector> extraction(Website website) {
		log.newPage();
		final List<ExtractedVector> extracted  = this.extractor.extractData(website);
		log.endPage("extraction produced "+extracted.size()+" vectors");
		return extracted;
	}

	private List<ExtractedVector> filtering(List<ExtractedVector> vectors) {
		log.newPage();
		final int before = vectors.size();
		new FilterChain().filter(vectors);
		final int after = vectors.size();
		final double perc = (double)after/before;
		log.endPage("filtering extraction rules/vectors "
				  + "(remaining "+after+", i.e., "+format(percentage,perc)+")");
		return vectors;
	}

	private List<Vector> normalization(List<ExtractedVector> vectors) {
		log.newPage("normalizing extracted data");
		log.trace("available datatypes from the most specific to the most general:");
		log.trace(new VectorCaster().getAvailableDatatypes());

		final List<Vector> result = new LinkedList<>();
		log.newTable();
		for (final ExtractedVector extracted : vectors) {
			log.newPage();
			final Vector normalized = extracted.normalize();
			result.add(normalized);			
			log(log.endPage(), extracted, normalized);
			log.trace("<hr/>");
		}
		log.endTable();
		log.endPage();
		return result;
	}

	static final private VectorListRenderer rendererWithTypeAndId = 
			new VectorListRenderer().enableIdColumn().enableTypeColumn();
	
	private void log(final Logpage page, final ExtractedVector e, final Vector n) {
		log.trace(linkTo(page).withAnchor("normalization details"), rendererWithTypeAndId.toHTMLstring(e, n));
	}

	private List<ExtractedVector> labeling(List<ExtractedVector> vectors) {
		log.newPage();
		final Labeler labeler = new Labeler();
		labeler.label(vectors);
		final int n = 3;
		final List<Label> bestLabels = labeler.getBestLabels(n);
		log.endPage( "labeling (best "+n+" labels: "+bestLabels+")");
		return vectors;
	}

	private List<Vector> enforceLocalConsistency(List<Vector> labeled) {
		log.newPage();
		final LocalConsistencyEnforcer lcEnforcer = new LocalConsistencyEnforcer();
		final List<Vector> filtered = lcEnforcer.enforceLocalConsistency(labeled);
		final int removed = labeled.size() - filtered.size();
		log.endPage("enforcing local consistency removed " + removed +	" vectors "  
				+ "(" + format(percentage, (double)removed / labeled.size())+")");
		log.trace(popup("remaining " + filtered.size() + " vectors",filtered));
//		log.trace("remaining " + filtered.size() + " vectors",filtered);
		return filtered;
	}

}
