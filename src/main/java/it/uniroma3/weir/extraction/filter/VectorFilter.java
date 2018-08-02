package it.uniroma3.weir.extraction.filter;

import it.uniroma3.weir.vector.Vector;

public interface VectorFilter {

	public enum FilterType {
		NUMBER_OF_INVARIANT(new NumberOfInvariantsFilter()),
		NUMBER_OF_NULLS(new NumberOfNullsFilter()),
		MAX_LENGTH(new LengthOfValuesFilter());
		
		private FilterType(VectorFilter f) {
			this.filter = f;
		}
		
		private VectorFilter filter;
		
		public VectorFilter getFilter() {
			return this.filter;
		}
	}
	
	/**
	 * 
	 * @param vector
	 * @return true iff the vector should be selected and used
	 *         false iff the vector should be discarded
	 */
	public boolean filter(Vector vector);
	
}
