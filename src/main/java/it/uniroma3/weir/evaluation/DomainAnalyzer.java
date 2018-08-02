package it.uniroma3.weir.evaluation;

import static it.uniroma3.util.CollectionsCSVUtils.collection2csv;
import static it.uniroma3.hlog.HypertextualUtils.*;
import static it.uniroma3.weir.Formats.percentage_f;
import static it.uniroma3.weir.Formats.thousandth;
import static it.uniroma3.weir.MatchRepository.finiteMatchRepository;
import static it.uniroma3.weir.model.log.WeirCSSclasses.RESULT_CSS_CLASS;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.hlog.Logpage;
import it.uniroma3.weir.MatchRepository;
import it.uniroma3.weir.MatchRepository.MatchBuilder;
import it.uniroma3.weir.integration.Match;
import it.uniroma3.weir.model.*;
import it.uniroma3.weir.vector.value.Value;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/**
 * @see <a href="http://www.vldb.org/pvldb/vol6/p805-bronzi.pdf">
 * <i>Extraction and integration of partially overlapping web sources</i></a>.
 * <br/>
 * Mirko Bronzi, Valter Crescenzi, Paolo Merialdo, Paolo Papotti.<br/>
 * Proceedings of the VLDB Endowment VLDB.<br/>
 * Volume 6 Issue 10, August 2013 (pages 805-816).<br/>
 * <em>Section 4.2 and Section 5.2 - Definitions 1 and 2</em>
 * <BR/>
 * TODO La stessa analisi sul problema di linkage duale:
 * 		- due pagine dallo stesso sito non possono pubblicare informazioni della stessa istanza (duale di LC)
 * 		- due pagine (da siti diversi) della stessa istanza sono più vicine di due pagine di istanze distinte
 */
public class DomainAnalyzer {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	private Experiment experiment;
	
	public DomainAnalyzer(Experiment experiment) {
		this.experiment = experiment;
	}

	public void logReport() {
		final List<String> meeaViolator = new LinkedList<>();
		final MappingSet goldenSet = experiment.getGoldenMappingSet();

		log.trace("domain analysis");
		/* n.b. this print a table with a link to a page in a cell ! */
		log.newTable();
		log.trace("Golden", "d_A", "D_A", "e_A");
		for (final Mapping golden : goldenSet.getMappingsOrderedByLabel()) {
			log.newPage();                         /* n.b. null-msg here */
			log.trace(golden);
			final String name = golden.getLabel();
			double d_A = get_dA(golden);
			double D_A = get_DA(golden,goldenSet);
			double e_A = get_eA(golden);
			log.trace(dA(golden)+"="+percentage(d_A));
			log.trace(DA(golden)+"="+percentage(D_A));
			log.trace(eA(golden)+"="+percentage(e_A));
			log.trace();
			
			if (d_A >= e_A) {
				log.trace(dA(golden)+"&ge;"+eA(golden));
				log.trace(meea()+" is <em>violated</em>");
				meeaViolator.add(golden.getLabel());
			}
			else {
				log.trace(dA(golden)+"&lt;"+eA(golden)+" ");
				log.trace(meea()+" does hold");
			}
			final Logpage logpage = log.endPage(); /* n.b. null-msg here */
			log.trace(	linkTo(logpage).withAnchor(name), d(d_A), d(D_A), d(e_A));
			// linkTo(log.newPage()).withAnchor(name)  does not work; why?
		}
		log.endTable();
		if (meeaViolator.isEmpty()) {
			log.trace(meea()+" does hold");			
		} else {
			log.trace(meea()+" is <em>violated</em> by "+collection2csv(meeaViolator));			
		}
		log.trace("<B>TODO log domain separability analysis</B>");
	}

	private Object d(double d) {
		return styled(RESULT_CSS_CLASS,format(percentage_f,d));
	}

	final private String meea() {
		return "the minimum extraction error assumption ";
	}
	
	final private String qA(String q, Mapping golden) {
		final String name = golden.getLabel();
		return q+"<sub>"+name+"</sub>";
	}	
	
	// CHECK vanno calcolate sul golden o sul target (best matching output attribute)????
	private double get_DA(Mapping golden, MappingSet goldenMappingSet) {
		log.newPage("computing "+DA(golden));
		
		final List<Match> interAbstractMatches = new LinkedList<>();

		for(Mapping other : goldenMappingSet.keepAllBut(golden)) {
			final String goldenLabel = golden.getLabel();
			final String otherLabel  = other.getLabel();
			
			final MatchRepository interAbstractRepository = finiteMatchRepository();
			interAbstractRepository.addCartesianProduct(golden.getAttributes(),other.getAttributes());
			if (!interAbstractRepository.isEmpty()) {
				final Match min = interAbstractRepository.min();
				log.trace("the shortest distance from golden "+goldenLabel
						+" to mapping "+otherLabel+" is set by "+compact(min));
				interAbstractMatches.add(min);
			}
			log.trace();
		}
		/* a inter-abstract distance between golden mappings */
		double D_A = Double.NaN;
		if (!interAbstractMatches.isEmpty()) {
			final Match inter_D_A = Collections.min(interAbstractMatches);
			D_A = inter_D_A.distance();
			log.trace(DA(golden) + "=" + format(thousandth,D_A) + " set by:");
			log.trace(inter_D_A);
		} else log.trace("no finite inter-abstract distance");
		log.endPage();
		return D_A;
	}

	private double get_dA(Mapping golden) {
		log.newPage("computing "+dA(golden)); //CHECK move into Mapping?
		final MatchRepository intraAbstractRepository = finiteMatchRepository();
		intraAbstractRepository.addAllUnorderedPairs(golden.getAttributes());
		
		double d_A = Double.NaN;
		/* max inter-sites distance within a golden mapping */
		if (!intraAbstractRepository.isEmpty()) {
			final Match diameter = intraAbstractRepository.max();
			d_A = diameter.distance();
			log.trace(dA(golden) + "=" + format(thousandth,d_A) + " set by:");
			log.trace(diameter);
		} else log.trace("no intra-abstract finite distance!");
		log.endPage();
		return d_A;
	}

	private double get_eA(Mapping goldenMapping) {
		log.newPage("computing "+eA(goldenMapping));
		final List<Match> siteExtractionErrors = new LinkedList<>();
		for (Attribute golden : goldenMapping.getAttributes()) {
			final MatchRepository notOverlapping = nonOverlappingMatches(golden);
			if (!notOverlapping.isEmpty()) {
				final Match site_e_a = notOverlapping.min();
				siteExtractionErrors.add(site_e_a);
				log.trace("Extraction error from "+golden.getWebsite()+" set by "+compact(site_e_a));
			}
		}
		/* a distance between the golden attribute *
		 * and any other non overlapping attribute */
		double e_A = Double.POSITIVE_INFINITY;
		if (!siteExtractionErrors.isEmpty()) {
			final Match min = Collections.min(siteExtractionErrors);
			e_A = min.distance();
			log.trace(eA(goldenMapping) + "=" + format(thousandth,e_A) + " set by:");
			log.trace(min);
		} else log.trace("all extraction rules overlap: none extraction error set");
		log.endPage();
		return e_A;
	}

	static public String compact(Match match) {
		return popup(match.toString(), match);
	}
	
	private MatchRepository nonOverlappingMatches(Attribute golden) {
		final MatchRepository result = new MatchRepository( new MatchBuilder() {

			static final private long serialVersionUID = 2049463909019840226L;

			@Override
			public Match createPair(Attribute a_i, Attribute a_j) {
				if (overlap(a_i,a_j)) return null; /* exclude overlapping */
				else return super.createPair(a_i, a_j);
			}
		});
		/* include only same site attributes */
		final List<Attribute> siteAttributes = golden.getWebsite().getAttributes();
		result.addCartesianProduct(Collections.singleton(golden), siteAttributes);

		return result;
	} 

	private boolean overlap(Attribute golden, Attribute output) {
		final Website website = golden.getWebsite();
		if (!website.equals(output.getWebsite()))
			throw new IllegalArgumentException("only attributes from the same site can overlap");
		
		final List<Webpage> pages  = website.getOverlappingPages();// ... .getWebpages();
		final Iterator<Webpage> it = pages.iterator();

		while (it.hasNext()) {
			final Webpage page = it.next();

			final Value g = golden.get(page);
			final Value v = output.get(page);
			if (g.isNull() || v.isNull()) continue;
			
			if (g.toString().equals(v.toString()))
				return true;
		}
		return false;
	}

	final private String DA(Mapping golden) {
		return qA("D",golden);
	}
	final private String dA(Mapping golden) {
		return qA("d",golden);
	}
	final private String eA(Mapping golden) {
		return qA("e",golden);
	}	

}
