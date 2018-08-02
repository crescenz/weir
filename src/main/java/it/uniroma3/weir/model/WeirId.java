package it.uniroma3.weir.model;


import it.uniroma3.id.IdGenerator;
import it.uniroma3.id.Identifiable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Centralize coupling with {@link Identifiable}.
 *
 */
public class WeirId implements Identifiable, Comparable<Identifiable>, Serializable {

	static final private long serialVersionUID = 5906549171892484171L;

	private String id;
	
	static final public String nextIdByClass(Class<?> cls) {
		Objects.requireNonNull(cls);
		return Integer.toString(IdGenerator.nextIdByClass(cls));
	}
	
	protected WeirId(String id) {
		this.id = id;
	}

	public String getId() { return this.id; }
	
	/**
	 * Override this method if this id mark an object with
	 * a website provenance (e.g., rule, vector, attribute...)
	 * @return a pretty-print HTML id making apparent the
	 *         website of provenance of this object, if any.
	 */
	public String getWeirId() { 
		return getInitial()+"<sub>"+getId()+"</sub>"; 
	}
	
	protected char getInitial() {
		return getLowerCaseLettersFromClassName();
	}

	protected char getLowerCaseLettersFromClassName() {
		return this.getClass().getSimpleName().toLowerCase().charAt(0);
	}

	@Override
	public int hashCode() { return this.getClass().hashCode()+Objects.hashCode(this.getId()); }

	@Override
	public boolean equals(Object o) {
		if (o == null || this.getClass()!=o.getClass()) return false;

		final Identifiable that = (Identifiable)o;
		return this.getId().equals(that.getId());
	}

	public void resetCounter() {
		IdGenerator.resetCounterOfClass(this);
	}
	
	@Override
	public String toString() {
		return getWeirId();
	}
	
	@Override
	public int compareTo(Identifiable that) {
		return this.getId().compareTo(that.getId());
	}

}
