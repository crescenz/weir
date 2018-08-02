package it.uniroma3.weir.linking.entity;

import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.vector.value.Value;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.wcohen.ss.UnsmoothedJS;
import com.wcohen.ss.api.StringDistance;

public class SoftIdEntity extends Entity implements Serializable {

	static final private long serialVersionUID = -26262970680949653L;
	
	static final private StringDistance JS_DISTANCE = new UnsmoothedJS();
	
	public SoftIdEntity(Webpage page) {
		super(page);
	}

	@Override
	public double similarity(Entity that) {
		final String id1 = this.getWebpage().getId();
		final String id2 = that.getWebpage().getId();
		return Math.min(JS_DISTANCE.score(id1, id2), 1.0d);
	}

	@Override
	public List<Value> getValues() {
		/* just to uniform this type of entity to others (ValueEntity) */
		return Collections.singletonList(idValue());
	}

	private Value idValue() {
		return new Value(getWebpage(), getWebpage().getId());
	}
	
}
