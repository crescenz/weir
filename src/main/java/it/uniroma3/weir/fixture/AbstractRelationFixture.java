package it.uniroma3.weir.fixture;


import it.uniroma3.weir.model.hiddenrelation.AbstractAttribute;
import it.uniroma3.weir.model.hiddenrelation.AbstractRelation;
import it.uniroma3.weir.model.Experiment;

public class AbstractRelationFixture {
	
	public static AbstractRelation createAbstractRelation(AbstractAttribute... abstractAttrs) {
		AbstractRelation ar = new AbstractRelation((Experiment)null);
		for (AbstractAttribute aa : abstractAttrs) {
			ar.addAbstractAttribute(aa);
		}
		return ar;
	}

}
