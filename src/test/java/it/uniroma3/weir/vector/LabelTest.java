package it.uniroma3.weir.vector;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

public class LabelTest {
	
	@Test
	public void testCompareTo_label_with_same_distance() {
		assertEquals(-1, createLabel("l1", 1).compareTo(createLabel("l2", 1)));
	}

	@Test
	public void testCompareTo_label_with_different_distance() {
		assertEquals(-1, createLabel("l1", 1).compareTo(createLabel("l2", 100)));
	}

	@Test
	public void testCompareTo_label_with_more_distances() {
		assertEquals(-1, createLabel("l1", 1, 1, 1).compareTo(createLabel("l2", 1, 2, 3)));
	}

	@Test
	public void testMergeCounters_one_label() {
		assertEquals(createLabels("l1", 1, 2), Label.merge(createLabels("l1", 1), createLabels("l1", 2)));
	}
	
	@Test
	public void testMergeCounters_two_labels() {
		assertEquals(createLabels("l1", 1, 2, "l2", 3, 4), Label.merge(createLabels("l1", 1, "l2", 3), createLabels("l1", 2, "l2", 4)));
	}
	
	static public Label createLabel(String label, int... distances) {
		return new Label(label,distances);
	}

	static public Set<Label> createLabels(Object... attrs) {
		final Set<Label> result = new LinkedHashSet<>();
		for (int i=0; i<attrs.length; i++) {
			final Label label = new Label(attrs[i].toString());
			while (i+1<attrs.length && attrs[i+1] instanceof Integer) {
				label.addDistance((Integer) attrs[i+1]);
				i++;
			}
			result.add(label);
		}
		return result;
	}

}
