package it.uniroma3.weir.model;

import it.uniroma3.weir.structures.ArrayPairRepository;
import it.uniroma3.weir.structures.PairRepository;

/**
 * A specialized {@link ArrayPairRepository} to store and access
 * {@link WebsitePair}s, i.e., pairs of {@link Website}s from
 * a {@link Domain}
 */
public class DomainRepository<P extends WebsitePair>
							 extends ArrayPairRepository<P, Website> 
                             implements PairRepository<P, Website> {

	static final private long serialVersionUID = -1794558104963697324L;

	private Domain domain;
	
//		
//	public DomainRepository(Collection<Website> sites) {
//		this(sites, null);
//	}
//	
	@SuppressWarnings("unchecked")
	public DomainRepository(Domain domain, 
			   			    PairBuilder<P, Website> builder) {
		this(domain, (Class<P>) WebsitePair.class, builder);
	}	
	
	protected DomainRepository(Domain domain, 
							   Class<P> cls,
							   PairBuilder<P, Website> builder) {
		super(domain.getSites(),cls,builder);
		this.domain = domain;
	}

	public Domain getDomain() {
		return this.domain;
	}

}
