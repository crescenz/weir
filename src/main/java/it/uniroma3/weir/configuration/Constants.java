package it.uniroma3.weir.configuration;

import it.uniroma3.preferences.Constant;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

public enum Constants implements Constant {
	
//### GENERAL PROPERTIES
	EXPERIMENTS_PATH("./src/main/resources/experiments"),
	DATASET_PATH("./dataset"),
	CACHING_ENABLED("true"),
	DATATYPES("STRING,NUMBER,MONEY,SPACE,MASS,DATE,PHONE,URL"),
	
	MAX_PAGES_PER_SOURCE("0"),
	
	SOFTID_FILTER(".+"),
	
//### EXTRACTION PROPERTIES
	MIN_EXTRACTION_SAMPLES("3"),
	MAX_EXTRACTION_SAMPLES("10"),
	
//	ECGM_CONFIG("ignore_trees=script,noscript,style,object;\\"+
//                "ignore_atts=selected,bgcolor,style;\\"+
//                "attributes_values=id;\\"+
//                "supp_threshold=0.2;\\"+
//                "size_threshold=5;\\"+
//                "use_xpath_roles=true;\\"+
//                "use_roles=false;\\"+
//                "max_iterations=4;\\"),
                
	EXTRACTION_RULES_CLASSES("positional,relative"),
	MAX_PIVOT_DISTANCE("4"),
	MIN_PIVOT_OCCURRENCES("3"),
	MIN_PIVOT_LENGTH("2"),
	MAX_PIVOT_LENGTH("64"),
	MAX_VALUE_LENGTH("96"),
	MAX_PERCENTAGE_EQUALS("17"), // 17% = 0.17d 
	MAX_PERCENTAGE_NULLS("70"),  // 70% = 0.70d
	EXTRACTION_RULES_FILTERS("NUMBER_OF_INVARIANT,NUMBER_OF_NULLS,MAX_LENGTH"),
	PCDATA_UNDERSAMPLING_THRESHOLD("3"),
	PCDATA_TOKENIZATION_PATTERN("([-:\'\".\\,/0-9])+|(\\s+)|(\\w+)|\\p{Punct}"),
	PCDATA_MAX_INVARIANT_LENGTH("8"),
	PCDATA_INVARIANT_BLACKLIST(""),
	REMOVE_REFINED_RULES("true"),
	
//### INTEGRATION PROPERTIES (SCHEMA MATCHING + RECORD LINKAGE)
	MAX_DISTANCE_THRESHOLD("0.5"), 
	LOCAL_CONSISTENCY_THRESHOLD("0.9"),
		
	// loose local consistency assumption properties 
	// for detecting intra-site attribute redundancy
	MIN_LOOSE_LC_RATIO("0.5"),
	MIN_LOOSE_LC_VALUES("10"),

	MIN_OVERLAPPING_SAMPLES("16"),
	MAX_OVERLAPPING_SAMPLES("40"),
	
	ONES2ONE("-1"), // 
	
//	### LINKING PROPERTIES (RECORD LINKAGE)
	LINKING_STRATEGY("it.uniroma3.weir.linking.DomainLinker"),
//	LINKING_STRATEGY("it.uniroma3.weir.linking.LegacySoftIdsLinker"),

    ENTITY_SIM_THRESHOLD("0.5"), 
	LINKING_TOP_K("1"),
	// Altri plausibili valori dei parametri in MainDSCDOnJaccardLinkage
	LINKING_PARAMETERS("2.0, 0.2, 0.05"),
	
	ENTITY_FACTORY("SOFTID_ENTITY"),
	TYPED_ENTITIES("true"),
	
    ENTITY_TERM_SIM_THRESHOLD("0.35"), 
    ENTITY_TERM_IDF_THRESHOLD("2"),
    
    SHRINK_TO_OVERLAP("false"),
    WORK_ON_OVERLAP("true"),

//	### EVALUATION PROPERTIES
	CHECK_GOLDEN_REDUNDANCY("true"),
	REMOVE_MATCHING_ATTRIBUTE("true"),	
	MATCH_EVALUATOR("equality,distance"),
	PERFECT_MATCH_THRESHOLD("0.001"),
	GOOD_MATCH_THRESHOLD("0.1");
	
	private final String defaultValue;
															      
    public String defaultValue() {
		return defaultValue;
	}

	private Constants(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	
	/**
	 * Produces the key associated with this {@link Constants}
	 * to use it as key of a {@link Configuration}
	 * @return
	 */
	public String key() {
		return name().toLowerCase();
	}
        
	static final public Configuration defaultConfiguration() {
		Configuration defaultConfiguration = new BaseConfiguration();
		for(Constants c : values())
			defaultConfiguration.setProperty(c.name().toLowerCase(), c.defaultValue());
		return defaultConfiguration;
	}    
    
}
