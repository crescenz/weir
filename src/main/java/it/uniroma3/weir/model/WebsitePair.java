package it.uniroma3.weir.model;

import java.io.Serializable;

import it.uniroma3.weir.structures.Pair;

public class WebsitePair extends Pair<Website> implements Serializable {

	private static final long serialVersionUID = 6630393480270486644L;

	public WebsitePair(Website w1, Website w2) {
		super(w1,w2);
	}
	
}
