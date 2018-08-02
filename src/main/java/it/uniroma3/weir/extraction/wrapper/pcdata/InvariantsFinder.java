package it.uniroma3.weir.extraction.wrapper.pcdata;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.value.Value;
import static it.uniroma3.weir.configuration.Constants.*;
/**
 * Given an {@link ExtractedVector} extracting whole PCDATAs (i.e., full DOM 
 * textual leaves) as values, finds a list of non-optional invariant tokens 
 * occurring in <em>every</em> value (e.g., a common prefix/suffix).
 * <BR/>
 * Deal with invariants occurring several times per value but 
 * it does <em>not</em> deal with "optional" invariant structures.
 */
public class InvariantsFinder {

	private int undersamplingThreshold;

	private Pattern tokenizationPattern;
	
	private int maxInvariantLength;
	
	private Pattern invariantBlacklist;

	private List<Invariant> invariants;

	private List<Invariant> useful;	
	
	private boolean filterOnMaxLengthEnabled;

	private boolean invariantBlacklistEnabled;
	
	public InvariantsFinder() {
		this.undersamplingThreshold = WeirConfig.getInteger(PCDATA_UNDERSAMPLING_THRESHOLD);
		this.tokenizationPattern = Pattern.compile(WeirConfig.getString(PCDATA_TOKENIZATION_PATTERN));
		this.maxInvariantLength  = WeirConfig.getInteger(PCDATA_MAX_INVARIANT_LENGTH);
		this.invariantBlacklist  = Pattern.compile(WeirConfig.getString(PCDATA_INVARIANT_BLACKLIST));
		this.filterOnMaxLengthEnabled  = ( maxInvariantLength!=0 ) ;
		this.invariantBlacklistEnabled = (!this.invariantBlacklist.toString().isEmpty());
	}

	public List<Invariant> findInvariantTokens(ExtractedVector ev) {
		final InferenceEngine engine = new InferenceEngine();
		/* process all non-null values in the vector */
		for(Value value : ev) {
			if (value.isNull()) continue;

			final String pcdata = value.toString();
			final List<String> tokens = tokenize(pcdata);
			// search for invariants occurring in
			// every PCDATA and close to variants 
			engine.analyze(tokens);			
			if (engine.getInvariants().isEmpty()) // no-way!
				return Collections.emptyList();
		}

		// avoid false or useless invariants due 
		// to under-sampling or constant vector
		if (engine.getNumberOfPCDATAprocessed()>=this.undersamplingThreshold) {  			
			this.invariants = engine.getInvariants();
			this.useful = selectUsefulInvariants(this.invariants);
		}
		else {
			this.invariants = Collections.emptyList();
			this.useful = Collections.emptyList();
		}
		return this.useful;
	}

	private List<String> tokenize(String pcdata) {
		final Matcher matcher = this.tokenizationPattern.matcher(pcdata);
		final List<String> result = new ArrayList<>();

		while (matcher.find()) {
			final String token = matcher.group();
			if (!token.trim().isEmpty())
				result.add(token);
		}
		return result;
	}

	private boolean isForbiddenAsInvariant(Invariant inv) {
		final String token = inv.getToken();
		// discard candidate invariants that could produce useless segmentations
		// or dangerous over-segmentations, e.g., ',' '.', "-", "/" symbols
		return (this.filterOnMaxLengthEnabled && token.length()>this.maxInvariantLength ) || 
				this.invariantBlacklistEnabled && this.invariantBlacklist.matcher(token).matches();
	}

	public List<Invariant> getInvariants() {
		return this.invariants;
	}
	
	public List<Invariant> getUsefulInvariants() {
		return this.useful;
	}

	/**
	 * Erase useless invariant, i.e., 
	 * those which are not adjacent only to other invariant tokens.
	 * 
	 * @param invs - input list of invariants to filter
	 * @return the selected useful invariants
	 */
	private List<Invariant> selectUsefulInvariants(List<Invariant> invs) {
		final List<Invariant> result = new ArrayList<>(invs);
		computeDuplicateSelfIndices(result);
		// erase useless invariant adjacent
		// only to other invariant tokens
		eraseUselessInvariants(result);
		return result;
	}

	private void eraseUselessInvariants(List<Invariant> invs) {
		final Iterator<Invariant> it = invs.iterator();
		while (it.hasNext()) {
			final Invariant inv = it.next();
			if (!inv.isAdjacentToVariantsOnTheLeft() && 
				!inv.isAdjacentToVariantsOnTheRight() ||
				isForbiddenAsInvariant(inv))	{
				it.remove();
			}
		}
	}

	private void computeDuplicateSelfIndices(List<Invariant> invs) {
		for(int i=0; i<invs.size(); i++) {
			final Invariant inv_i = invs.get(i);
			for(int j=i+1; j<invs.size(); j++) {
				final Invariant inv_j = invs.get(j);
				if (inv_i.getToken().equals(inv_j.getToken())) {
					inv_j.incrementSelfIndex();
				}
			}
		}		
	}

	public int getDuplicateIndexRelativeTo(Invariant wanted, Invariant reference) {
		int duplicateIndex = 0;
		
		/* skip to the reference invariant */
		int i = this.getInvariants().indexOf(reference);
		if (i==-1) throw new IllegalArgumentException(
						"Cannot find within "+this.getInvariants()+" the reference "+reference
				   );
		
		/* now count how many more duplicate occurrences to target invariant */
		for(int j=i+1; j<this.getInvariants().size(); j++) {
			final Invariant inv_j = this.getInvariants().get(j);
			
			/* is it the same occurrence of the wanted duplicate */
			if (inv_j.equals(wanted))
				break;
			
			/* is it another duplicate occurrence? */
			if (inv_j.getToken().equals(wanted.getToken()) &&
				inv_j.getSelfIndex()!=wanted.getSelfIndex())
				duplicateIndex++;
		}
		return duplicateIndex;
	}

}
