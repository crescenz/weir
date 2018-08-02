package it.uniroma3.weir.fixture;

import it.uniroma3.weir.linking.linkage.PageLinkage;
import it.uniroma3.weir.linking.linkage.WebsiteLinkage;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.value.Value;

import java.util.ArrayList;
import java.util.List;

public class WebsiteLinkageFixture {
	
	public static final int MAX_SAMPLES = 3;

	public static WebsiteLinkage createWebsiteLinkage(int numberOfLinkages) {
		int[][] links = new int[numberOfLinkages][];
		for (int i = 0; i < numberOfLinkages; i++) {
			links[i][0] = i;
			links[i][1] = i;
		}
		return createWebsiteLinkage(links);
	}

	public static Vector[] createWebsiteLinkage(Attribute a1, Attribute a2, int numberOfLinkages) {
		int[][] links = new int[numberOfLinkages][];
		for (int i = 0; i < numberOfLinkages; i++) {
			links[i][0] = i;
			links[i][1] = i;
		}
		return createWebsiteLinkage(a1, a2, links);
	}
	
	public static WebsiteLinkage createWebsiteLinkage(int[]...  links) {
		Website w1 = WebsiteFixture.createWebsite(links.length, 2);
		Website w2 = WebsiteFixture.createWebsite(links.length, 2);
		return createWebsiteLinkage(w1, w2, links);
	}

	public static WebsiteLinkage createWebsiteLinkage(Website w1, Website w2, int[]... links) {
		List<PageLinkage> linkage = new ArrayList<PageLinkage>();
		for (int[] link : links) {
			Webpage p1 = w1.getOverlappingPages().get(link[0]);
			Webpage p2 = w2.getOverlappingPages().get(link[1]);
			linkage.add(new PageLinkage(p1, p2, 1.0));
		}		
		return new WebsiteLinkage(w1, w2, linkage);
	}

	
	public static Vector[] createWebsiteLinkage(Attribute a1, Attribute a2, int[]...  links) {
		Website w1 = WebsiteFixture.createWebsite(links.length, 2);
		Website w2 = WebsiteFixture.createWebsite(links.length, 2);
		WebsiteLinkage linkage =  createWebsiteLinkage(w1, w2, links);
		return getValuesByLinkage(linkage, a1, a2);
	}

	
	//VC: non si fa nulla per verificare la mancanza di conflitti tra i PageLinkage coinvolti 
	//VC: i soft-id non dovrebbero produrli, mentre jaccard si...
	private static Vector[] getValuesByLinkage(WebsiteLinkage linkage, Attribute a1, Attribute a2) {
		final Website s1 = linkage.getMin();
		final Website s2 = linkage.getMax();	
		final int size = linkage.getPageLinkages().size();
		
		Value[] values1 = new Value[size];
		Value[] values2 = new Value[size];
		int count = 0;
		
		for (PageLinkage pair : linkage) {
			values1[count] = a1.getVector().get(pair.from(s1).getOverlapIndex());
			values2[count] = a2.getVector().get(pair.from(s2).getOverlapIndex());
			count++;
		}

		Vector v1 = makeVector(a1.getVector().getType(), values1);
		Vector v2 = makeVector(a2.getVector().getType(), values2);
		return new Vector[] { v1, v2 };
	}
	
	// here only to support legacy code
	static public Vector makeVector(final Type type, final Value[] values) {
		@SuppressWarnings("serial")
		final Vector result = new Vector(type) {{
			this.elements = values;			
		}};
		return result;
	}
	
}
