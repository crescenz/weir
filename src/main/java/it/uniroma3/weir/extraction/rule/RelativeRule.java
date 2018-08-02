package it.uniroma3.weir.extraction.rule;


public class RelativeRule extends ExtractionRule {

	static final private long serialVersionUID = -2105917974847824039L;

	public RelativeRule(String xpath) {
		super(ExtractionRuleClass.RELATIVE, xpath);
	}

	// qui sotto le uniche ipotetiche motivazioni per tenere vive
	// le due classi PositionalRule e RelativeRule? 
	// ora l'applicazione a livello di stringhe di SubPCDATARule è motivata...
//	private int distance;
//	public int foundAtDistance() {
//		return this.distance;
//	}
//
//	public void setFoundAtDistance(int distance) {
//		this.distance = distance;
//	}

}
