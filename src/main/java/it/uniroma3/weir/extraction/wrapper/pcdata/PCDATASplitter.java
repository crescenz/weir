package it.uniroma3.weir.extraction.wrapper.pcdata;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.extraction.ParallelExtractor;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.model.log.VectorListRenderer;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Given a collection of {@link ExtractedVector}s obtained
 * from a {@link Website} by applying a collection of 
 * {@link ExtractionRule}s extracting values at level of 
 * entire PCDATAs, this class is devoted to detect which 
 * PCDATAs can be further decomposed into variant and 
 * invariant sub-components, and to refine each of the 
 * former rules into several rules by making use of XPath 
 * string functions to split the whole-PCDATA vectors
 * values into several sub-PCDATA values.
 * 
 */
public class PCDATASplitter {
	
	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	final private Website website;
	
	final private List<ExtractedVector> vectorsOfPCDATA;

	private Set<ExtractionRule> refined;

	public PCDATASplitter(Website website, List<ExtractedVector> extracted) {
		this.website = website;
		this.vectorsOfPCDATA = extracted;
		this.refined = new HashSet<>();
	}

	static final private VectorListRenderer vlr = 
			new VectorListRenderer().enableIdColumn().enableRuleColumn();
	
	public List<ExtractedVector> split() {
		log.newPage("refining "+website+" rules to extract intra pcdata values");
		// n.b. produce side-effects on this.original by removing 
		//      the rules that need to be refined; insert into 
		//      this.refined the refined versions of the rules.
		final SubPCDATARuleGenerator generator = new SubPCDATARuleGenerator(website);
		
		this.refined = generator.refine(this.vectorsOfPCDATA);
		log.trace(this.refined.size()+" new refined rules generated:");
		log.trace(this.refined);

		/* extract vectors of the refined rules, if any */
		if (!this.refined.isEmpty()) {
			final ParallelExtractor pdp = new ParallelExtractor(website);
			final List<ExtractedVector> extracted = pdp.parallelExtraction(this.refined);
			this.vectorsOfPCDATA.addAll(extracted);
			log.page("extracted " + extracted.size() + " new vectors",
					vlr.toHTMLstring(new ArrayList<Vector>(extracted)));
		}
		log.endPage();
		log.trace("generated "+refined.size()+" refined rules");
		return this.vectorsOfPCDATA;
	}
	
}
