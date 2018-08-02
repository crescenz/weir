package it.uniroma3.weir.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * E.g., swde, weir.
 * 
 * A list of {@link Domain}s (e.g., auto, book ...), 
 * each containing a list of {@link Website}s
 * over the same vertical (e.g., amazon, barnesandnoble, etc. etc.)
 * 
 */
public class Dataset extends WeirId implements Serializable {
	
	static final private long serialVersionUID = 2677141940138205485L;
	
	private List<Domain> domains;
	
	public Dataset(String name) {
		super(name);
		this.domains = new LinkedList<>();
	}
	
	public String getName() {
		return this.getId();
	}
	
	public void addDomain(Domain domain) {
		this.domains.add(domain);
		domain.setDataset(this);
	}

	public List<Domain> getDomains() {
		return this.domains;
	}

	@Override
	public String toString() {
		return getName();
	}

}
