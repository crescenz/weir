package it.uniroma3.weir.extraction.wrapper.pcdata;

import static it.uniroma3.hlog.HypertextualUtils.*;
import static it.uniroma3.weir.model.log.WeirCSSclasses.INVARIANT_CSS_CLASS;
import static it.uniroma3.weir.model.log.WeirCSSclasses.VALUE_CSS_CLASS;
import static it.uniroma3.weir.model.log.WeirStyles.nullValue;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.lang3.StringEscapeUtils.escapeXml;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.configuration.Constants;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.extraction.rule.SubPCDATARule;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.value.ExtractedValue;
import static it.uniroma3.weir.configuration.Constants.REMOVE_REFINED_RULES;

import java.util.*;
/**
 * Given an {@link ExtractionRule} extracting whole PCDATAs (DOM text leaves) 
 * it refines the rule by producing a set of {@link ExtractionRule}s to extract
 * each a sub-PCDATA variant value.
 */
public class SubPCDATARuleGenerator {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	static final protected int MAX_VALUE_LENGTH  = 32;
	
	static final protected int MAX_VALUES = 10;

	static final protected int MAX_INVARIANTS = 4;

	final private Website website;

	final private InvariantsFinder finder;
		
	final private boolean removeRefinedRules;
	
	public SubPCDATARuleGenerator(Website website) {
		this.finder = new InvariantsFinder();
		this.website = website;
		this.removeRefinedRules = WeirConfig.getBoolean(REMOVE_REFINED_RULES);
	}

	public Set<ExtractionRule> refine(List<ExtractedVector> pcdataVectors) {
		final Set<ExtractionRule> result = new HashSet<>();
		final Iterator<ExtractedVector> it = pcdataVectors.iterator();
		log.trace("showing max "+MAX_INVARIANTS+" invariants and max "+MAX_VALUES+" values");
		log.newTable();
		logTableHeaders();
		int counter=0;
		while (it.hasNext()) {
			final ExtractedVector ev = it.next();
			final List<ExtractionRule> subpcdataRules = this.refine(ev);
			if (!subpcdataRules.isEmpty()) {
				if (this.removeRefinedRules)
					it.remove();
				result.addAll(subpcdataRules);
				counter++;
			}
		}
		log.endTable();
		log.trace(counter+" rules have been refined to extract intra-PCDATA values.");
		return result;
	}

	private void logTableHeaders() {
		final Object[] headers = new String[MAX_INVARIANTS+1];
		Arrays.fill(headers, "<em>splitter</em>");
		headers[0] = "<em>rule</em>";
		log.trace(headers);
	}
	
	public List<ExtractionRule> refine(ExtractedVector ev) {
		final List<ExtractionRule> result;
		final List<Invariant> invs = this.finder.findInvariantTokens(ev);
		if (invs.isEmpty()) 
			result = Collections.emptyList();
		else result = refineExtractionRule(ev, invs);
		logTableEntry(ev, invs);
		return result;
	}

	// VC: servirebbe un meccanismo (basato su proxy/aspetti?) per eliminare questo metodo da qui...
	private void logTableEntry(ExtractedVector ev, final List<Invariant> invs) {
		if (invs.isEmpty()) return; // skip rules that haven't been refined

		// render at most nv values
		final int nv = Math.min(MAX_VALUES,ev.size());
		final Object[] msgs = new Object[MAX_INVARIANTS+nv+1]; // rule, invariants, and values
		
		/* rule */
		final ExtractionRule rule = ev.getExtractionRule();
		final String xpath = ( rule!=null ? rule.getXPath() : null );
		msgs[0] = (xpath!=null ? tooltip("&#10148;", escapeHtml4(xpath)) : "" );
		
		/* invariants */
		int offset = +1;
		// render exactly MAX_INVARIANTS invariants
		for(int i=0; i<MAX_INVARIANTS; i++) {
			final String content = ( i<invs.size() ? escapeHtml4(invs.get(i).toString()) : null);
			
			msgs[offset+i] = tag("span",INVARIANT_CSS_CLASS,content,"");
		}
		
		/* extracted values */
		offset += MAX_INVARIANTS;
		for(int i=0; i<nv; i++) {
			final ExtractedValue value = ev.getElements()[i];
			final String content = truncateEscapeAndTooltip(value.toString(), MAX_VALUE_LENGTH);
			msgs[offset+i] = tag("span",VALUE_CSS_CLASS,content,nullValue());
		}
		
		log.trace(msgs);
	}	
	
	private List<ExtractionRule> refineExtractionRule(ExtractedVector ev, List<Invariant> invs) {
		if (invs.isEmpty()) return Collections.emptyList();
		final ExtractionRule generating = ev.getExtractionRule();
		final List<ExtractionRule> result = new LinkedList<>();
		// simple index positions of variant content: given n invariant tokens,
		// 0 means before first one, 1 just after it, n is just after last one
		final int n = invs.size();		
		for(int position=0; position<=n; position++) {
			final ExtractionRule rule = generateRule(generating, invs, position);
			if (rule!=null)
				result.add(rule);
		}
		return result;
	}

	private ExtractionRule generateRule(ExtractionRule base, List<Invariant> invs, int pos) {
		/* pos = 0 */
		if (pos==0) {
			if (invs.get(0).isAdjacentToVariantsOnTheLeft())
				return generateBeforeRule(base, invs.get(0));
			else return null;
		}
		/* pos = n */
		final int n = invs.size();
		if (pos==n) {
			if (invs.get(n-1).isAdjacentToVariantsOnTheRight())
				return generateAfterRule(base, invs.get(n-1));
			else return null;
		}
		/* 0 < pos < n */
		if (invs.get(pos-1).isAdjacentToVariantsOnTheRight() && 
			invs.get(pos).isAdjacentToVariantsOnTheLeft()) {
			return generateBeforeAfterRule(base, invs.get(pos-1), invs.get(pos));
		}
		else return null;
	}

	private ExtractionRule generateBeforeRule(ExtractionRule base, Invariant inv) {
		int index=inv.getSelfIndex(); // deal with duplicate invariants (index>0)
		return generateBeforeRule(base, inv, index);
	}
	
	private ExtractionRule generateBeforeRule(ExtractionRule base, Invariant inv, int index) {
		if (index>0)
			base = generateAfterRule(base, inv, index-1);
		final SubPCDATARule before = 
				new SubPCDATARule(base,"substring-before("+base.getXPath()+","+literal(inv)+")");
		before.setWebsite(this.website);
		return before;
	}

	private ExtractionRule generateAfterRule(ExtractionRule base, Invariant inv) {
		int index=inv.getSelfIndex(); // deal with duplicate invariants (index>0)
		return generateAfterRule(base, inv, index);
	}

	private ExtractionRule generateAfterRule(ExtractionRule base, Invariant inv, int index) {
		SubPCDATARule after;
		do {
		  after = new SubPCDATARule(base,"substring-after("+base.getXPath()+","+literal(inv)+")");
		  after.setWebsite(this.website);
		  base = after;
		} while (--index>=0);
		return after;
	}

	private ExtractionRule generateBeforeAfterRule(ExtractionRule base, Invariant inv0, Invariant inv1) {
		final int _1wrt0_ = this.finder.getDuplicateIndexRelativeTo(inv1,inv0);
		
		return generateBeforeRule(generateAfterRule(base, inv0), inv1, _1wrt0_);
	}

	final private String literal(Invariant inv) {
		return "'" + escapeXml(inv.getToken()) + "'";
	}
	
}
