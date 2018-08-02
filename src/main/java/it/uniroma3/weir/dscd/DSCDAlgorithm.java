package it.uniroma3.weir.dscd;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.linking.entity.ValueEntity;
import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.linking.linkage.PageLinkageRepository;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.value.Value;

import java.util.List;

import org.jblas.DoubleMatrix;
import static it.uniroma3.weir.dscd.DomainFusion.WebsiteFusionBuilder;

public class DSCDAlgorithm extends WebsiteFusionBuilder
                           implements DSCDScorer<PageLinkage,Match> {

	static final private long serialVersionUID = -3443388370717247565L;

	static final private HypertextualLogger log = HypertextualLogger.getLogger();
	
	/* factories to create DSCDAlgorithm candidate domainLinkages/matches   */
	final private DSCDLinkages linkagesFactory;
	final private DSCDMatches matchesFactory;
	
	public DSCDAlgorithm() {
		this.linkagesFactory = initCandidateLinkagesFactory();
		this.matchesFactory  = initCandidateMatchesFactory();

	}

	private List<PageLinkage> candidateLinkages;

	private List<Match> candidateMatches;	
	
	@Override
	public WebsiteFusion createPair(Website s_i, Website s_j) {
		log.newPage("dscd for " + s_i + " & " + s_j);
		
		/* create candidate for this pair of sites */
		this.candidateLinkages = this.linkagesFactory.createLinkages(s_i, s_j).getAllLinkages();
		this.candidateMatches  = this.matchesFactory.createMatches(s_i, s_j).getAllMatches();
		
		/* execute DSCD iteration to converge */
		DSCDIterator<PageLinkage,Match> iterator = 	
				new DSCDIterator<>(candidateLinkages, candidateMatches, this);
		iterator.iterate();

		/* move C/D output matrix into match/page-linkage repositories */
		final MatchRepository matches = getMatchesFromCmatrix(iterator.getC());		
		final PageLinkageRepository linkages = getLinkagesFromDmatrix(iterator.getD());
		
		log.endPage();
		
//		final DoubleMatrix C = iterator.getC();
//		final DoubleMatrix D = iterator.getD();
//		lEval.evaluate(D, rows, exp); la valutazione stava qui dopo ...
//		mEval.evaluate(C, cols, exp);..ogni coppia di siti
		/* save them */
		return new WebsiteFusion(s_i, s_j, linkages, matches);
	}

	protected DSCDLinkages initCandidateLinkagesFactory() {
		return DSCDLinkages.ALL; // TODO Inject by means of WeirConfig...
	}
	
	protected DSCDMatches initCandidateMatchesFactory() {
		return DSCDMatches.ALL;  // TODO Inject by means of WeirConfig...
	}		
	
	/* il problema di questi due metodi è che trattano i valori di C/D come valori
	 * di similarità in realtà sono solo degli score utili a fissare un ranking !   */
	public MatchRepository getMatchesFromCmatrix(final DoubleMatrix C) {
		final MatchRepository result = new MatchRepository();
		for (int i=0; i<C.length; i++) {
			result.add( new Match( this.candidateMatches.get(i), 1-C.get(i) ) );
		}
		return result;
	}
	
	public PageLinkageRepository getLinkagesFromDmatrix(final DoubleMatrix D) {
		final PageLinkageRepository result = new PageLinkageRepository();
		for (int i=0; i<D.length; i++) {
			result.add( new PageLinkage(this.candidateLinkages.get(i), D.get(i) ) );
		}
		return result;
	}

	@Override
	public double score(PageLinkage link, Match match) {
		final ValueEntity e1 = (ValueEntity) link.getMin().getEntity();
		final ValueEntity e2 = (ValueEntity) link.getMax().getEntity();
		final Value value1 = e1.getValue(match.getMin());
		final Value value2 = e2.getValue(match.getMax());

		if (value1.isNull() || value2.isNull()) return 0d;

		final Type t1 = match.getMin().getVector().getType();// VC: nb. tipo del vettore
		final Type t2 = match.getMax().getVector().getType();//         e non del valore!
		return 1 - Type.getCommonAncestor(t1, t2).distance(value1.getValue(), value2.getValue());
	}

}
