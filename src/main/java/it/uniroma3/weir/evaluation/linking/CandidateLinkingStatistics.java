package it.uniroma3.weir.evaluation.linking;

import it.uniroma3.weir.linking.entity.Entity;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//
//import jxl.write.Label;
//import jxl.write.Number;
//import jxl.write.WritableSheet;
//import jxl.write.WriteException;
//import jxl.write.biff.RowsExceededException;

/*
 * TODO: estirpare la logica di creazione del foglio di calcolo,
 * accentrare la gestione delle statistiche
 */
/**
 * Collects statistics during the candidate linkages creation step.
 *
 */
public class CandidateLinkingStatistics {

	static final public int CANDIDATE_LIMIT = 50;
	
	private int totalLinkages;
		
	/* stats for studying similarities trends over top linkages */
	private int existingLinkagesCounter;     // da fondere con linkagesYes? in realtà questo si limita ai candidates
	private int nonExistingLinkagesCounter;  // da fondere con linkagesNo? in realtà questo si limita ai candidates
			
	// VC: li usa come array!
	private List<Double> similaritiesForExistingLinkages;
	private List<Double> similaritiesForNonExistingLinkages;
	
	private List<Integer> correctLinkagesAmongCandidateSimilarities; // conta per ogni posizione il numero di candidati 
	                                                                 // (ordinati per similarità) che si trovano lì
	
	private double averageSimilarityForExistingLinkages;
	private double averageSimilarityForNonExistingLinkages;
	
	/* stats for counting true/false positive/negative s */
	
	// VC: TODO (finire) estirpati da LinkageCreator e raccolti qui:
	private int linkagesYes;              // rename -> truePositive !?
	private int linkagesNo;               // rename -> trueNegative !?
	
	private int linkagesYesFalsePositive; // rename -> falsePositive
	private int linkagesYesFalseNegative; // rename -> falseNegative
	
	/* stats for reasons of false negatives */
	
	private int falseNegativeForScoreMultiplier;
	private int falseNegativeForFirstStepPercentage;
	
	public CandidateLinkingStatistics() {
		this.similaritiesForExistingLinkages = new ArrayList<Double>();
		this.similaritiesForNonExistingLinkages = new ArrayList<Double>();
		this.correctLinkagesAmongCandidateSimilarities = new ArrayList<Integer>();
		
		for (int i = 0; i < CANDIDATE_LIMIT; i++) {
			this.similaritiesForExistingLinkages.add(0.0);
			this.similaritiesForNonExistingLinkages.add(0.0);
			this.correctLinkagesAmongCandidateSimilarities.add(0);
		}
	}

	
	public boolean updateStatisticsFromComplete(Entity e1, Website s2, double averageScore) {
		this.incTotalLinkages();

		boolean aCorrectLinkageExists = false;
		// VC: valuta i risultati considerando gli identifier come golden info
		for (Webpage p : s2.getOverlappingPages()) {
			if (Type.STRING.distance(p.getId(), e1.getWebpage().getId()) == 0) {
				aCorrectLinkageExists = true;
				break;
			}
		}

//		log.trace("\t\t\t\t" + e1.getWebpage().getIdentifier());
		if (aCorrectLinkageExists) {
			this.incLinkagesYes();
		}
		else {
			this.incLinkagesNo();
		}
//		log.trace("\t\t\tlinkage "+ ( linkageYes ? "YES" : "No" ) );
//		log.trace("\t\tavg score:\t" + averageScore);
		return aCorrectLinkageExists;
	}
	
	
	public void updateStatisticsFromSimilarityGraph(Entity e1, List<Entity> similarities, double averageScore) {
		Collections.sort(similarities);
		this.incTotalLinkages();
		
		boolean correctLinkageExist = false;
		
		for (int i = 0; i < similarities.size(); i++) {
			Entity sim = similarities.get(i);
			
			final String e1id = e1.getWebpage().getId();
			final String e2id = sim.getWebpage().getId();
			
			// Use soft-id as golden info
			if (Type.STRING.distance(e1id,e2id) == 0) {
				correctLinkageExist = true;
				
				if (i < CANDIDATE_LIMIT) {
					/* increment the number of correct linkages found in i-th position */
					int prevNum = this.correctLinkagesAmongCandidateSimilarities.get(i);
					this.correctLinkagesAmongCandidateSimilarities.set(i, prevNum + 1);
				}
				
				break;
			}
		}
		
		List<Double> linkageGraph;
		if (correctLinkageExist) {
			linkageGraph = this.similaritiesForExistingLinkages;
			
			this.existingLinkagesCounter++;
			this.averageSimilarityForExistingLinkages += averageScore;
		}
		else {
			linkageGraph = this.similaritiesForNonExistingLinkages;
			
			this.nonExistingLinkagesCounter++;
			this.averageSimilarityForNonExistingLinkages += averageScore;
		}
		
		for (int i = 0; i < CANDIDATE_LIMIT && i < similarities.size(); i++) {
			double sim = e1.similarity(similarities.get(i));
			double prevSim = linkageGraph.get(i);
			linkageGraph.set(i, prevSim + sim);
		}
	}


	public void finalizeStatistics() {
		this.averageSimilarityForExistingLinkages /= this.existingLinkagesCounter;
		this.averageSimilarityForNonExistingLinkages /= this.nonExistingLinkagesCounter;
		finalizeSimilarities(this.similaritiesForExistingLinkages, this.existingLinkagesCounter);
		finalizeSimilarities(this.similaritiesForNonExistingLinkages, this.nonExistingLinkagesCounter);
	}
	
	public void finalizeSimilarities(List<Double> linkageGraph, int normalizer) {
		for (int i = 0; i < CANDIDATE_LIMIT; i++) {
			double sim = linkageGraph.get(i);
			linkageGraph.set(i, sim / normalizer);
		}
	}

	/* */
	
	public void incTotalLinkages() {
		this.totalLinkages++;
	}

	public void incLinkagesYes() {
		this.linkagesYes++;
	}

	public void incLinkagesNo() {
		this.linkagesNo++;
	}

	public void printStatistics() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("\n");
		sb.append("total links: \t" + this.totalLinkages + "\n");
		sb.append("linkages YES:\t" + this.linkagesYes + "\n");
		sb.append("linkages No: \t" + this.linkagesNo + "\n");
		sb.append("linkages YES false positive:\t" + this.linkagesYesFalsePositive + "\n");
		sb.append("linkages YES false negative:\t" + this.linkagesYesFalseNegative + "\n");
		
		sb.append("\n");
		sb.append("\n");
		sb.append("false negatives due to ScoreMultiplier:    \t" + this.falseNegativeForScoreMultiplier + "\n");
		sb.append("false negatives due to FirstStepPercentage:\t" + this.falseNegativeForFirstStepPercentage + "\n");

		System.out.println(sb.toString());
	}

	public void incFalseNegatives() {
		this.linkagesYesFalseNegative++;
	}

	public void incFalsePositives() {
		this.linkagesYesFalsePositive++;		
	}

	public void incFalseNegativesForScoreMultiplier() {
		this.falseNegativeForScoreMultiplier++;
	}

	public void incFalseNegativesForFirstStepPercentage() {
		this.falseNegativeForFirstStepPercentage++;
	}

}
