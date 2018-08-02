package it.uniroma3.weir.evaluation;

import static it.uniroma3.weir.configuration.Constants.CHECK_GOLDEN_REDUNDANCY;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.model.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Read golden mappings stored in a .csv file, and namely: a set
 * of golden {@link Mapping}s, i.e., a {@link MappingSet}, each
 * grouping a set of corresponding source {@link Attribute}s from 
 * distinct {@link Website}s.
 * 
 * TODO evaluate over overlapping pages vs evaluate over all pages!?
 */
public class GoldenReader {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	final private Experiment experiment;
	
	final private GoldenSiteReader siteReader;
	
	// check whether the golden attribute are actually redundant 
	// i.e., present at least in two sites
	private boolean checkGoldenRedundancy;
	
	public GoldenReader(Experiment experiment) {
		this.experiment = experiment;
		this.siteReader = new GoldenSiteReader(this.experiment);
		this.checkGoldenRedundancy = WeirConfig.getBoolean(CHECK_GOLDEN_REDUNDANCY);
	}
	
	public MappingSet getGoldenMappings() throws IOException {
		final Domain domain = this.experiment.getDomain();
		log.newPage("reading golden data for "+domain);
		final Map<String, Mapping> goldenMappings = readGoldenAttributes();
		logGoldenMappings(goldenMappings);
		final MappingSet golden = makeGoldenMappingSet(goldenMappings);
		log.endPage();
		return golden;		
	}
	
	private Map<String, Mapping> readGoldenAttributes() throws IOException {
		final Map<String, Mapping> goldenMappings = new HashMap<>();
		for (Website site : this.experiment.getDomain().getSites()) {
			readGoldenAttributes(site, goldenMappings);
		}		
		return goldenMappings;
	}

	private void readGoldenAttributes(final Website site, final Map<String, Mapping> mappings) throws IOException {
		log.newPage("reading golden data for "+site);
		for (Attribute golden : this.siteReader.readGoldenAttributes(site)) {
			final String label = golden.getFirstLabel();
			Mapping mapping = mappings.get(label);
			if (mapping==null) {
				mapping = new Mapping();
				mappings.put(label, mapping);
			}
			mapping.add(golden);
		}
		log.endPage();
	}

	private void logGoldenMappings(final Map<String, Mapping> golden) {
		log.newPage("golden mappings");
		log.trace("<h2>Golden mappings</h2>");
		final MappingSet goldenSet = new MappingSet();
		goldenSet.addAll(golden.values());
		for(Mapping mapping : goldenSet.getMappingsOrderedByLabel()) {
			mapping.setAsGolden();
			log.trace(mapping.getLabel());
			log.trace(mapping);
			log.trace("<hr/>");
		}
		log.endPage();
	}

	private MappingSet makeGoldenMappingSet(final Map<String, Mapping> goldenMappings) {
		final MappingSet golden = new MappingSet();
		for (String label : goldenMappings.keySet()) {
			final Mapping goldenMapping = goldenMappings.get(label);
			goldenMapping.setAsGolden();
			golden.addMapping(goldenMapping);
		}
		
		if (this.checkGoldenRedundancy) {
			removeNonRedundantGoldenAttribute(golden);
		}
		return golden;
	}

	private void removeNonRedundantGoldenAttribute(MappingSet mappingSet) {
		log.newPage("removing non-redundant attributes from golden mappings");
		final MappingSet toRemove = new MappingSet();
		for (Mapping m : mappingSet.getMappingsOrderedByLabel()) {
			final Iterator<Attribute> it = m.getAttributes().iterator();
			final Attribute attribute = it.next();
			final String label = attribute.getFirstLabel();
			log.trace("golden attribute " + label + " spans over " + m.size() + " websites");			
			if (m.size()<2) {
				log.warn("removing non-redundant golden attribute " + label);
				toRemove.addMapping(m);
			}
		}		
		mappingSet.removeMappings(toRemove);
		log.endPage();
	}

}
