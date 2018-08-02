package it.uniroma3.weir.model.hiddenrelation;

import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.model.WeirId;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * An abstract (i.e., [was:] <em>conceptual</em> ) attribute of the
 * {@link AbstractRelation}, that is, a set of source {@link Attribute}s.
 * <BR/>
 * 
 * Can be converted to a {@link Mapping} by invoking {@link #asMapping()}.
 * CHECK even if the linkage is not trivial?
 *
 */
public class AbstractAttribute extends WeirId {

	static final private long serialVersionUID = 3992009429892551845L;
	
	private Set<Attribute> attributes;

	public AbstractAttribute(Mapping mapping) {
		this(mapping.getAttributes());
	}
	public AbstractAttribute(Set<Attribute> sourceAttributes) {
		super(nextIdByClass(AbstractAttribute.class));
		this.attributes = new HashSet<>();
		this.attributes.addAll(sourceAttributes);
		//CHECK whether they come from different sources ???
	}
	
	public Set<Attribute> getSourceAttributes() {
		return this.attributes;
	}
	
	public Mapping asMapping() {
		return new Mapping(this.getSourceAttributes());
	}
	
}
