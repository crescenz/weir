package it.uniroma3.weir.main;

import static it.uniroma3.weir.Formats.percentage;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.Mapping;
import it.uniroma3.weir.model.MappingSet;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.value.Value;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/**
 * Find optimal thresholds for the extraction rules filters.
 * <br/>
 * The tuning process is based on the availability of golden information.
 * <br/>
 */
public class TUNE extends ExperimentMainTemplate {

	static public void main(String[] args) {
		try {
			new TUNE().run(args);
		} catch (IllegalStateException e) {
			final String msg = TUNE.class.getSimpleName()+ " needs golden data to run";
			System.err.println(msg);
			throw new IllegalStateException(msg,e);
		}
	}

	public void logProperties(MappingSet goldenMappings) {
		log.trace("Thresholds tuning on golden data:");
		findLongestValueLength(goldenMappings);
		findBiggestInvariantPercentage(goldenMappings);
		findBiggestNullPercentage(goldenMappings);
	}

	private void findLongestValueLength(MappingSet goldenMappings) {
		Value longest = new Value(null, "not found");
		for (Mapping m : goldenMappings.getMappings()) {
			for (Attribute a : m.getAttributes()) {
				for (Value v : a.getVector().getOriginatingVector().getElements()) {
					if (v.toString() != null && v.toString().length() > longest.toString().length()) {
						longest = v;
					}
				}
			}
		}
		log.trace("The longest value in golden attributes is " + longest);
		log.trace("...of length " + longest.toString().length());
	}

	private void findBiggestInvariantPercentage(MappingSet golden) {
		Attribute most = null;
		double mostPercentage = -1;
		for (Mapping m : golden.getMappings()) {
			for (Attribute a : m.getAttributes()) {
				double percentage = getInvariantPercentage(a.getVector());
				if (percentage>mostPercentage) {
					mostPercentage = percentage;
					most = a;
				}
			}
		}
		log.trace("The most invariant golden attributes is " + most.getID());
		log.trace("..." + Arrays.toString(most.getVector().getElements()));
		log.trace("...with percentage of invariants" + mostPercentage);
	}

	private double getInvariantPercentage(Vector extractedVector) {
		Map<String, Integer> value2occurence = new HashMap<String, Integer>();
		for (Value elem : extractedVector.getElements()) {
			String value = elem.toString();
			if (value!=null) {
				Integer occurrence = value2occurence.get(value);
				value2occurence.put(value, ( occurrence==null ? 1 : occurrence + 1 ) );
			}
		}

		double maxOccurrence = -1;
		for (String value : value2occurence.keySet()) {
			Integer occurrence = value2occurence.get(value);
			if (occurrence > maxOccurrence) {
				maxOccurrence = occurrence;
			}
		}

		return maxOccurrence / extractedVector.countNonNulls();
	}

	private void findBiggestNullPercentage(MappingSet golden) {
		Attribute most = null;
		double mostPercentage = -1;
		for (Mapping m : golden.getMappings()) {
			for (Attribute a : m.getAttributes()) {
				double percentage = a.getVector().nulls() / (double)a.getVector().size();
				if (percentage>mostPercentage) {
					mostPercentage = percentage;
					most = a;
				}
			}
		}
		log.trace("The golden attribute with most nulls is " + most.getID());
		log.trace(Arrays.toString(most.getVector().getElements()));
		log.trace("...null percentage is " + percentage.format(mostPercentage));
	}

	@Override
	protected void execute(Experiment experiment) {
		try {
			MappingSet golden = experiment.getGoldenMappingSet();
			TUNE tuner = new TUNE();
			tuner.logProperties(golden);
		} catch (IllegalStateException e) {
			throw new IllegalStateException(this.getClass().getSimpleName()+" needs golden data",e);
		}
	}

}
