package it.uniroma3.weir.fixture;

import static it.uniroma3.weir.fixture.AttributeFixture.createRandomAttribute;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;

import java.util.ArrayList;
import java.util.List;

public class WebsiteFixture {

	static private Long actualId = 0l;

	static public Website createWebsite(int numberOfPages, int nAttributes) {
		final List<Attribute> attributes = new ArrayList<>(nAttributes);
		for (int i=0; i<nAttributes; i++) {
			attributes.add(createRandomAttribute(numberOfPages));
		}		
		return createWebsite(numberOfPages,attributes.toArray(new Attribute[0]));
	}
	
	static public Website createWebsite(int numberOfPages, Attribute[] attrs) {
		final List<Webpage> pages = new ArrayList<>(numberOfPages);
		for (int i=0; i<numberOfPages; i++) {
			pages.add(new Webpage("p" + (i+1)));
		}
		return createWebsite(pages, attrs);
	}

	static public Website[] createWebsites(int numberSites, int numberOfAttributes, int common) {
		final Website[] sites = new Website[numberSites];
		final int numberOfPages = 2;

		for (int i=0; i<numberSites; i++) {
			int notInCommon = numberOfAttributes - common;
			final Website site = createWebsite(numberOfPages, notInCommon);
			for (int j=notInCommon; j<numberOfAttributes; j++) {
				site.addAttribute(createRandomAttribute(numberOfPages));
			}
			sites[i] = site;
		}
		
		return sites;
	}
	
	static public Website createWebsite(String... pageIds) {
		final List<Webpage> pages = new ArrayList<>(pageIds.length);
		for (int i=0; i<pageIds.length; i++) {
			pages.add(new Webpage(pageIds[i]));
		}		
		return createWebsite(pages);
	}

	static public Website createWebsite(List<Webpage> pages, Attribute... attrs) {
		final Website site = createEmptySite();
		
		for (Webpage page : pages) {
			site.addPage(page);
		}
		
		for (Attribute a : attrs) {
			site.addAttribute(a);
		}
		
		return site;
	}
	
	static public Website createWebsite(int numberOfPages) {
		final Website site = createEmptySite();
		for(int i=0; i<numberOfPages; i++)
			site.addPage(new Webpage("test_page"+i));
		return site;
	}

	static public Website createEmptySite() {
		long id;
		synchronized (actualId) {
			id = actualId++;			
		}
		Website site = new Website("www.site_" + id + ".com");
		return site;
	}
}
