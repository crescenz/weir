package it.uniroma3.weir.extraction.wrapper.pcdata;

public class Invariant {

	private String token;

	// true iff this invariant is adjacent to variants on the left
	private boolean left; 
	// true iff this invariant is adjacent to variants on the right
	private boolean right;
	// >0 iff this invariant occur more than once per pcdata value
	private int index;
	
	public Invariant(String token) {
		this.token  = token;
		this.left = false;
		this.right = false;
		this.index = 0;
	}

	public String getToken() {
		return this.token;
	}

	public boolean isAdjacentToVariantsOnTheLeft()  { return this.left;  }
	public boolean isAdjacentToVariantsOnTheRight() { return this.right; }

	public void setAdjacentToVariantsOnTheLeft()  {	this.left = true;  }
	public void setAdjacentToVariantsOnTheRight() {	this.right = true; }
	
	/**
	 * The index of this invariant relative to itself: 
	 * first occurrence has index 0, second has index 1 ... .
	 * 
	 * N.B. for generating subpcdata expressions we also need
	 *      the index relative to another invariant: see 
	 *      {@link InvariantFinder#getDuplicateIndexRelativeTo(Invariant, Invariant)}
	 */
	public int getSelfIndex() { return this.index;   }
	public void incrementSelfIndex() { this.index++; }
	
	@Override
	public int hashCode() {
		return this.index + this.token.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o==null || !(o instanceof Invariant)) return false;
		
		final Invariant that = (Invariant)o;
		return this.getSelfIndex()==that.getSelfIndex() && this.getToken().equals(that.getToken());
	}
	
	@Override
	public String toString() {
		return 
				( isAdjacentToVariantsOnTheLeft()  ? "<" : " " )+
				  getToken() +
				( getSelfIndex()>0 ? getSelfIndex() : "") +
				( isAdjacentToVariantsOnTheRight() ? ">" : " " );
	}

}