package it.uniroma3.weir.vector;

import java.util.Collections;
import java.util.List;

import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.vector.type.Type;
import static it.uniroma3.weir.vector.type.Type.availableDatatypes;

public class VectorCaster {
	
	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	// All available types, 
	// they must be ordered by the their specificity in ascending order,
	// i.e., from the most specific (least generic) type to the least specific 
	//       (most generic type)
	private List<Type> datatypes;
	
	public VectorCaster() {
		this(availableDatatypes());
	}

	public VectorCaster(List<Type> dataTypes) {
		this.datatypes = dataTypes;
		Collections.reverse(this.datatypes);
	}

	public List<Type> getAvailableDatatypes() {
		return this.datatypes;
	}
	
	/**
	 * Find the most specific type to cast the vector.
	 * 
	 * @param the vector of values to cast
	 * @return the most specific type of this vector
	 */
	public Type findMostSpecificType(String...values) {
		// iterate from the most specific to the most general type
		for(Type type : this.getAvailableDatatypes()) {
			log.trace("Checking "+type);
			if (allInstanceOf(type, values))
				return type;
			log.trace();
		}
		throw new IllegalStateException("Any vector should be at least be of the root type!");
	}

	private boolean allInstanceOf(Type type, String...values) {
		for(String value : values) {
			if (!type.instanceOf(value)) {
				log.trace("\'"+value+"\' is not a "+type);
				return false;
			}
		}
		log.trace("All values are "+type);
		return true;
	}
		
}
