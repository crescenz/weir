package it.uniroma3.weir.vector;

import java.io.Serializable;
import java.util.*;

/**
 *
 * A vector's label with the heuristics distances used to find it.
 * Check:
 * Mirko Bronzi, Valter Crescenzi, Paolo Merialdo, Paolo Papotti:
 * Extraction and Integration of Partially Overlapping Web Sources.
 * PVLDB 6(10): 805-816 (2013)
 *
 * Section 5.3: "Labeling"
 */
public class Label implements Comparable<Label>, Serializable {

	static final private long serialVersionUID = 111527081861471054L;

	final private String label;

	final private List<Integer> distances;

	public Label(String label, int... distances) {
		this.label = label;
		this.distances = new ArrayList<>();
		for(final int d: distances)
			this.addDistance(d);
	}

	public String getLabel() {
		return this.label;
	}

	public void addDistance(int d) {
		this.distances.add(d);
	}

	public void addDistances(List<Integer> dists) {
		this.distances.addAll(dists);
	}

	public List<Integer> getDistances() {
		return this.distances;
	}

	private double sizeAvgDistanceRatio(Label label) {
		return label.getDistances().size() / (label.getAvgDistance() + 1);
	}

	private double getAvgDistance() {
		double result = 0.0;
		for (final int d : this.getDistances()) {
			result += d;
		}
		return result / this.distances.size();
	}

	private static Label merge(Label l1, Label l2) {
		if (!l1.getLabel().equals(l2.getLabel()))
			throw new IllegalArgumentException("Only same labels can be merged");
		final Label result = new Label(l1.getLabel());
		result.addDistances(l1.getDistances());
		result.addDistances(l2.getDistances());
		return result;
	}

	public static Set<Label> merge(Collection<Label> _labels1, Collection<Label> _labels2) {
		/* an inefficient but simple algorithm to merge two small sets of labels */
		final List<Label> labels1 = new ArrayList<>(_labels1);
		final List<Label> labels2 = new ArrayList<>(_labels2);
		final Set<Label> result = new HashSet<>();
		for (final Label label : labels1) {
			merge(result, label, labels2);
		}
		for (final Label label : labels2) {
			merge(result, label, labels1);
		}
		result.addAll(labels1); //never merged from the  first set
		result.addAll(labels2); //never merged from the second set
		return result;
	}

	private static void merge(Set<Label> result, Label wanted, List<Label> list) {
		/* merge 'wanted' with any equivalent label from 'list' */
		/* remove the mergee and accumulate the merged into 'result' */
		final int index = list.indexOf(wanted);
		if (index!=-1) {
			final Label other = list.get(index);       // the mergee
			list.remove(index);
			final Label merged = merge(wanted, other); // the merged
			result.add(merged);
		}
		return; /* n.b. assuming the label is in 'list' at most once */
	}

	@Override
	public int hashCode() {
		return this.getLabel().hashCode();
	}
	
	@Override
	public int compareTo(Label that) {
		// prefer labels with a bigger size to average distance ratio
		int result = Double.compare(this.sizeAvgDistanceRatio(that),this.sizeAvgDistanceRatio(this));
		// ... and then shorter label are better
		if (result==0) result = Integer.compare(this.getLabel().length(),that.getLabel().length());
		// ... and then alphabetical order to break ties
		if (result==0) result = this.getLabel().compareTo(that.getLabel());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Label))	return false;
		final Label that = (Label)o;
		return this.getLabel().equals(that.getLabel()) ;
	}

	@Override
	public String toString() {
		return this.getLabel() + ( this.getDistances().isEmpty() ? "" : this.getDistances() );
	}
}
