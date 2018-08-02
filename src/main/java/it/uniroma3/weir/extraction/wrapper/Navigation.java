package it.uniroma3.weir.extraction.wrapper;


import static java.util.Objects.requireNonNull;

import java.util.*;

import org.w3c.dom.Node;

public class Navigation implements Iterable<Step> {


	static public Navigation navigation(Step...steps) {
		return new Navigation(steps);
	}
	
	private LinkedList<Step> steps;
	
	public Navigation(Navigation toCopy) {
		this.steps = new LinkedList<>(toCopy.steps);
	}

	public Navigation(Step...steps) {
		this.steps = new LinkedList<>(Arrays.asList(steps));
	}
	
	public Navigation() {
		this.steps = new LinkedList<>();
//		this.steps.add(NO);
	}	
	
	public Navigation append(Step step) {
		final Navigation result = new Navigation(this);
		result.steps.addLast(step);
		return result;
	}

	@Override
	public Iterator<Step> iterator() {
		return this.steps.iterator();
	}	
	
	public Navigation stripFirstIfNotEmpty() {
		final Navigation result = new Navigation(this);
		if (!result.steps.isEmpty())
			result.steps.removeFirst();
		return result;
	}

	public Step getLast() {
		return this.steps.getLast();
	}

	private final Object NODE_ALREADY_SEEN_MARKER = new Object();
	
	public boolean hasKnot(Node from, Step last) {
		requireNonNull(from, "Need a starting node to look for knots");
		final Map<Node, Object> seen = new IdentityHashMap<>();
		seen.put(from, NODE_ALREADY_SEEN_MARKER);
		Node current = from;
		for(Step step : this) {
			current = step.to(current);
			if (seen.containsKey(current)) return true;
			seen.put(current, NODE_ALREADY_SEEN_MARKER);
		}
		return seen.containsKey(last.to(current));
	}

	public boolean isEmpty() {
		return this.steps.isEmpty();
	}

	@Override
	public int hashCode() {
		return this.steps.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o==null || !(o instanceof Navigation)) return false;
		
		final Navigation that = (Navigation)o;
		return this.steps.equals(that.steps);
	}
	
	@Override
	public String toString() {
		return this.steps.toString();
	}
	
}
