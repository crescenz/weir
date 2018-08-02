package it.uniroma3.weir.main;

import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.hiddenrelation.AbstractRelation;

/**
 * Base class to write applications with a main() method to
 * execute fusion experiments specified by command-line parameters.
 * (Fusion includes record-linkage, extraction, integration
 * see {@link AbstractRelation}.
 *  <br/>
 * These applications must include an evaluation step.
 * <br/>
 * Subclasses must override {@link #process(Experiment)} and
 * {@link #evaluate(AbstractRelation)} methods.
 * 
 * @see {@link ExperimentMainTemplate}.
 */
// VC: allo stato, valutare se rifonderla dentro ExperimentMainTemplate
//     Al momento serve solo a "forzare" il passaggio per una AbstractRelation
//     su cui poi eseguire una generica valutazione
public abstract class FusionTemplate extends ExperimentMainTemplate {
	
	@Override
	protected void execute(Experiment exp) {
		/* perform the experiment */
		final AbstractRelation _H_ = process(exp);
				
		/*  evaluate  the results */
		evaluate(_H_);
	}
		
	abstract protected AbstractRelation process(Experiment exp);
	
	abstract protected void evaluate(AbstractRelation _H_);
		
}
