package it.uniroma3.weir.extraction.wrapper.pcdata;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class InferenceEngine {
	
	// keep a list of ordered invariant, and
	// deal with duplicate invariant tokens e.g.  var1/ var2 / var3
	private int counter = 0;
	
	final private List<Invariant> invs = new LinkedList<>();

	public void analyze(List<String> tokens) {
		if (this.counter++ == 0) {
			// this first PCDATA set the initial structure
			invs.addAll(makeInitialInvariants(tokens));
		} else {				
			markInvariants(tokens);
		}
		this.counter++;
	}

	/* This method look for a shared structure 
	 * - var0 inv0 var1 inv1 var2 ... inv_n var_n -
	 * among all tokenized PCDATA values,
	 * where each var_i can also be the empty string;
	 * then save into Invariant objects which inv_i has 
	 * variant token on its left hand or on its right hand.
	 */
	private void markInvariants(List<String> tokens) {
		retainAllInvariantsIn(tokens); 
		if (this.invs.isEmpty()) return; // no invariant left

		// find invariant tokens and whether they're adjacent to variants
		final Iterator<String> tokIt = tokens.iterator();
		final Iterator<Invariant> invIt = this.invs.iterator();

		// true iff from the last invariant and so far, 
		// at least a variant token has been met
		boolean seenAvariant = false; 
		Invariant lastInv = null;
		while (invIt.hasNext() || tokIt.hasNext()) { //consume all
			Invariant inv = null;
			if (invIt.hasNext())
				inv = invIt.next();

			/* search this invariant within tokens */
			while (tokIt.hasNext()) {
				final String token = tokIt.next();

				if (inv!=null && inv.getToken().equals(token)) {
					if (seenAvariant) {
						if (inv!=null) inv.setAdjacentToVariantsOnTheLeft();
						if (lastInv!=null)
							lastInv.setAdjacentToVariantsOnTheRight();
					}
					seenAvariant = false;
					lastInv = inv;
					break; /* move to next invariant */
				} else {
					// if it is not a invariant it must be a variant
					if (lastInv!=null)
						lastInv.setAdjacentToVariantsOnTheRight();
					seenAvariant = true;
				}
			}
		}
	}

	private void retainAllInvariantsIn(List<String> tokens) {
		if (tokens.isEmpty()) return;
		// n.b. ignore order! otherwise a much more complex analysis 
		//      (aka LFEQ) would be needed anyway
		final Iterator<Invariant> it = this.invs.iterator();
		while (it.hasNext()) {
			final Invariant inv = it.next();
			if (!tokens.contains(inv.getToken()))
				it.remove();
		}				
	}

	private List<Invariant> makeInitialInvariants(List<String> tokens) {
		final List<Invariant> result = new LinkedList<>();
		for(String token : tokens)
			result.add(new Invariant(token));
		return result;
	}

	public List<Invariant> getInvariants() {
		return this.invs;
	}

	public int getNumberOfPCDATAprocessed() {
		return this.counter;
	}

}