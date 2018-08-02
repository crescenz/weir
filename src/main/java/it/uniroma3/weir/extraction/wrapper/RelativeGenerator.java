package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.hlog.HypertextualLogger.getLogger;
import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import static it.uniroma3.hlog.render.Renderers.iterable2objectRenderer;
import static it.uniroma3.weir.configuration.Constants.MAX_PIVOT_DISTANCE;
import static it.uniroma3.weir.configuration.Constants.MIN_PIVOT_OCCURRENCES;
import static it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates.*;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.hlog.HypertextualUtils.Link;
import it.uniroma3.hlog.render.ObjectRenderer;
import it.uniroma3.token.dom.DOMToken;
import it.uniroma3.token.dom.node.DOMNode;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.extraction.rule.ExtractionRuleClass.RuleGenerator;
import it.uniroma3.weir.extraction.wrapper.template.TemplateFinder;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.log.TemplateTokenListRenderer;

import java.util.*;

import org.w3c.dom.Node;
/**
 * Generates relative XPath expressions pivoted
 * on a set of invariant template token nodes 
 */
public class RelativeGenerator implements RuleGenerator {

	static final private HypertextualLogger log = getLogger();

	final private TemplateFinder finder;
	
	final private int range;

	final private RelativeXPathBuilder factory;

	private int maxOccsWithoutNewRules = 0;

	private List<DOMToken> tokens;
	
	public RelativeGenerator() {
		this(WeirConfig.getInteger(MAX_PIVOT_DISTANCE), 
			 WeirConfig.getInteger(MIN_PIVOT_OCCURRENCES));
	}

	public RelativeGenerator(int range, int maxUnproductiveOccs) {
		this.finder  = new TemplateFinder();
		this.range   = range;
		this.maxOccsWithoutNewRules = maxUnproductiveOccs;
		this.factory = new RelativeXPathBuilder();
	}

	public Set<String> inferRules(List<Webpage> samples) {
		this.finder.findTemplateTokens(samples);
		this.tokens = this.finder.getTemplateTokens();
		final Set<String> rules = new LinkedHashSet<>();
		log.trace("Evaluating each of suitable "+tokens.size()+" template tokens as pivot");
		log.newTable();
		log.trace("i","pivot","XPaths generated");
		for (int i=0; i<tokens.size(); i++) {
			rules.addAll(generatePivotedRules(i));
		}
		log.endTable();
		return rules;
	}

	private Set<String> generatePivotedRules(final int index) {
		final DOMToken pivot = this.tokens.get(index);
		if (!isSuitablePivotToken(pivot)) {
			return Collections.emptySet();
		}

		int occCounter = 0;
		log.newPage();
		final Set<String> rules = new LinkedHashSet<>();
		// working on extensional occurrences of this pivot
		final List<DOMNode> pivotOccurrences = this.finder.getOccurrences(pivot);
		log.trace("@processing "+pivotOccurrences.size()+" occurrences of pivot ");
		log.trace(pivot);
		log.trace("Looking for paths leading to text values from each pivot occurrence:");
		int occsWithoutDiscoveringRules = 0;
		for(Node occurrence : pivotOccurrences) {
			// bounded number of XPath steps available: initially the full range
			final TreeExplorer explorer = new TreeExplorer(this.range);
			log.newPage();
			final Set<Navigation> found = explorer.explore(occurrence);
			log.endPage(occCounter
					+") Rules: accumulated so far: "+rules.size()
					+"; Found on this occ.: "+found.size());
			int discoveredRulesCounter = 0;
			for (Navigation navigation : found) {
				final String xpath = this.factory.makeXPath(occurrence,navigation);
				if (rules.add(xpath)) {
					log.trace("New rule detected: "+xpath);
					discoveredRulesCounter++;
				}
			}
			if (discoveredRulesCounter==0) {
				occsWithoutDiscoveringRules++;
				if (occsWithoutDiscoveringRules==this.maxOccsWithoutNewRules) {
					log.trace("didn't find anything new for "+occsWithoutDiscoveringRules+" consecutive occurrences");
					log.trace("giving up with this pivot");
					break;
				}
			}
			else occsWithoutDiscoveringRules=0;
			occCounter++;
		}
		log.trace();
		log.trace("XPaths generated from this pivot: ", rules.isEmpty() ? " none. " : rules);
		log(index, pivot, rules.size());
		return rules;
	}
	
	static final private TemplateTokenListRenderer tokenlistrenderer = new TemplateTokenListRenderer();
	
	static final private ObjectRenderer<DOMToken> temptokenrenderer = iterable2objectRenderer(tokenlistrenderer); {{
		tokenlistrenderer.skipHeaderRow();
	}};
	
	private void log(final int index, final DOMToken pivot, int size) {
		final Link link = linkTo(log.endPage()).withAnchor(Integer.toString(size));
		final String pivotRendering = temptokenrenderer.toHTMLstring(pivot);
		log.trace(index+"/"+this.tokens.size(), pivotRendering, link);
	}

}
