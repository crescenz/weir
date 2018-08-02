package it.uniroma3.weir.dscd.beta;

import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.dscd.DSCDIterator;
import it.uniroma3.weir.dscd.DSCDScorer;
import it.uniroma3.weir.integration.AbstractIntegration;
import it.uniroma3.weir.linking.entity.ValueEntity;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Domain;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.MappingSet;
import it.uniroma3.weir.model.hiddenrelation.AbstractRelation;
import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.value.Value;

import java.util.*;

public class BetaAlgorithm implements DSCDScorer<PageLinkage,Attribute> {

	public AbstractRelation fuse(final Experiment exp) {
		final Domain domain = exp.getDomain();
		
		/* BETA=FUSE dscd over candidate page-linkages vs all rules */
		final List<Attribute> rules = new ArrayList<>(domain.getAllAttributes());		
		final List<PageLinkage> linkages = exp.getLinkages().getAllPageLinkages();

		/* rerank rules according to their redundancy 
		 * weighted by the page-linkages similarity   */
		final DSCDIterator<PageLinkage,Attribute> iterator = 
				new DSCDIterator<>(linkages,rules,this);
		iterator.iterate();
		
		/* solves conflicts among overlapping rules */
		final BetaResolver resolver = new BetaResolver();
		resolver.resolve(iterator.getD(), iterator.getC(), linkages, rules);

		final Set<Attribute> resolvedAttributes = resolver.getResolvedAttributes();

		/* now consider all possible matches to run abstract-integration */
		MatchRepository matches = new MatchRepository();
		matches.addAllUnorderedPairs(resolvedAttributes);

		/* build the AbstractRelation that will be evaluated */
		final AbstractIntegration integrator = new AbstractIntegration();
		final MappingSet ms = integrator.integrate(matches); 
		final AbstractRelation _H_ = new AbstractRelation(exp);
		_H_.add(ms);
		return _H_;
	}
	
	@Override
	public double score(PageLinkage link, final Attribute pivot) {
		final ValueEntity e1 = (ValueEntity) link.getMin().getEntity();
		final ValueEntity e2 = (ValueEntity) link.getMax().getEntity();

		final Value vp = e1.getValue(pivot);// sicuro che pivot sia sempre sulla prima pagina ? no
		if (vp==null || vp.isNull()) return 0d;
		final Type  tp = pivot.getVector().getType();
		
		final List<Attribute> attributes = e2.getWebsite().getAttributes();
		if (attributes.isEmpty()) return 1.0d;
			
		final Attribute min = Collections.min(attributes, new Comparator<Attribute>() {
			@Override
			public int compare(Attribute a1, Attribute a2) {
				final Type t1 = Type.getCommonAncestor(tp,a1.getVector().getType());
				final Type t2 = Type.getCommonAncestor(tp,a2.getVector().getType());
				final Value v1 = e1.getValue(a1);
				final Value v2 = e2.getValue(a2);
				return Double.compare(distance(t1,vp,v1),distance(t2,vp,v2));
			}
			
		});
		final Type tmin  = Type.getCommonAncestor(tp, min.getVector().getType());
		final Value vmin = e2.getValue(min);
		return 1.0 - distance(tmin,vp,vmin);
	}

	private double distance(Type type, Value v1, Value v2) {
		if (v1.isNull() || v2.isNull()) return 1.0d;
		return type.distance(v1.getValue(), v2.getValue());	
	}

}
