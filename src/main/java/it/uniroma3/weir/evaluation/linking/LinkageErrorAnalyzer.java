package it.uniroma3.weir.evaluation.linking;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.evaluation.PRF;
import it.uniroma3.weir.evaluation.PRFBuilder;
import it.uniroma3.weir.linking.linkage.DomainLinkage;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.Website;

import java.text.DecimalFormat;
import java.util.List;

// compare with CandidateLinkageStatistics
public class LinkageErrorAnalyzer {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	final private PRFBuilder builder;

	private double totalLinkages;
	private int existOnlyOneRightLinkage;
	private int existOnlyOneWrongLinkage;
	private int rightLinkageExistAsFirstCandidateAndWasChosen;
	private int rightLinkageExistInCandidatesAndWasChosen;
	private int rightLinkageExistAndAnotherWasChosen;
	private int aCorrectLinkageExistButNothingHasBeenSelected;
	private int rightLinkageNotExistAndAnotherWasChosen;
	private int rightLinkageNotExistAndNothingWasChosen;
	
	private int[][] linkages;
	
	public LinkageErrorAnalyzer(int wss) {
		this.builder = new PRFBuilder();
		this.linkages = new int[wss][wss];
	}

	public void analyzeLinkages(Website w1, Website w2, List<PageLinkage> correctPairs) {
		this.linkages[w1.getIndex()][w2.getIndex()] = correctPairs.size();	
		
		if (correctPairs.size() != 0) {
			double intersection = 0;
			for (PageLinkage p : correctPairs) {
				if (sameIdentifier(p)) {
					intersection++;
				}
			}
			
			double r = intersection / correctPairs.size();
			this.builder.addPR(r, r);
			log.trace(r);
		}
		else {			
			this.builder.addPR(1d, 1d);
			log.trace("1d");
		}		
	}

	public void checkExistAcorrectLinkage(List<PageLinkage> entityVector) {
		this.totalLinkages++;

		for (PageLinkage p : entityVector) {
			if (sameIdentifier(p)) {
				this.aCorrectLinkageExistButNothingHasBeenSelected++;
				return;
			}
		}
		
		this.rightLinkageNotExistAndNothingWasChosen++;
	}

	public void checkLinkages(List<PageLinkage> entityVector, PageLinkage best) {
		this.totalLinkages++;

		boolean rightLinkageExist = false;
		int i;
		for (i = 0; i < entityVector.size(); i++) {
			PageLinkage p = entityVector.get(i);
			if (sameIdentifier(p)) {
				rightLinkageExist = true;
				break;
			}
		}
		
		if (rightLinkageExist) {
			if (sameIdentifier(best)) {
				if (i == 0) {
					this.rightLinkageExistAsFirstCandidateAndWasChosen++;
				}
				else {
					this.rightLinkageExistInCandidatesAndWasChosen++;
				}
			}
			else {
				this.rightLinkageExistAndAnotherWasChosen++;
			}
		}
		else {
			this.rightLinkageNotExistAndAnotherWasChosen++;
		}
	}

	public void checkIsACorrectLinkage(PageLinkage p) {
		this.totalLinkages++;

		if (sameIdentifier(p)) {
			this.existOnlyOneRightLinkage++;
		}
		else {
			this.existOnlyOneWrongLinkage++;
		}
	}

	private boolean sameIdentifier(PageLinkage p) {
		return p.getMin().getId().equals(p.getMax().getId());
	}

	public void finalizeAndPrintResult(List<Website> wss, DomainLinkage rep) {
		PRF result = this.builder.getResult();
		StringBuilder sb = new StringBuilder("\n");
		
		sb.append("\n");
		sb.append("\n");
		sb.append("RESULT: " + result);
		sb.append("\n");
		sb.append("\n");
		DecimalFormat df = new DecimalFormat("##.##");
		
		sb.append("totalLinkages:\t" + (int)this.totalLinkages + "\n");
		sb.append("existOnlyOneRightLinkage:\t" + this.existOnlyOneRightLinkage + "\t" + df.format(this.existOnlyOneRightLinkage * 100 / this.totalLinkages) + " %\n");
		sb.append("existOnlyOneWrongLinkage:\t" + this.existOnlyOneWrongLinkage + "\t" + df.format(this.existOnlyOneWrongLinkage * 100 / this.totalLinkages) + " %\n");
		sb.append("rightLinkageExistAsFirstCandidateAndWasChosen:\t" + this.rightLinkageExistAsFirstCandidateAndWasChosen + "\t" + df.format(this.rightLinkageExistAsFirstCandidateAndWasChosen * 100 / this.totalLinkages) + " %\n");
		sb.append("rightLinkageExistInCandidatesAndWasChosen:\t" + this.rightLinkageExistInCandidatesAndWasChosen + "\t" + df.format(this.rightLinkageExistInCandidatesAndWasChosen * 100 / this.totalLinkages) + " %\n");
		sb.append("rightLinkageExistAndAnotherWasChosen:\t" + this.rightLinkageExistAndAnotherWasChosen + "\t" + df.format(this.rightLinkageExistAndAnotherWasChosen * 100 / this.totalLinkages) + " %\n");
		sb.append("aCorrectLinkageExistButNothingHasBeenSelected:\t" + this.aCorrectLinkageExistButNothingHasBeenSelected + "\t" + df.format(this.aCorrectLinkageExistButNothingHasBeenSelected * 100 / this.totalLinkages) + " %\n");
		sb.append("rightLinkageNotExistAndAnotherWasChosen:\t" + this.rightLinkageNotExistAndAnotherWasChosen + "\t" + df.format(this.rightLinkageNotExistAndAnotherWasChosen * 100 / this.totalLinkages) + " %\n");
		sb.append("rightLinkageNotExistAndNothingWasChosen:\t" + this.rightLinkageNotExistAndNothingWasChosen + "\t" + df.format(this.rightLinkageNotExistAndNothingWasChosen * 100 / this.totalLinkages) + " %\n");
		sb.append("\n");
		sb.append("\n");
		
		log.trace(sb.toString());
		System.out.println(sb.toString());
		
		sb = new StringBuilder();
		sb.append("\n\n");
		for (Website ws : wss) {
			sb.append("\t" + ws);
		}
		sb.append("\n");
		
		for (int i = 0; i < this.linkages.length; i++) {
			sb.append(wss.get(i));
			for (int j = 0; j < this.linkages[0].length; j++) {
				if (j > i) {
					WebsiteLinkage wl = rep.get(wss.get(i), wss.get(j));
					sb.append("\t" + this.linkages[i][j] + " / " + wl.size());
				}
				else if (j == i) {
					sb.append("\tX");
				}
				else {
					sb.append("\t");
				}
			}
			sb.append("\n");
		}
		
		System.out.println(sb.toString());
	}

}
