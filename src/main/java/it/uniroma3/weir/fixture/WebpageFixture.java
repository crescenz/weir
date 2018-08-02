package it.uniroma3.weir.fixture;

import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures.*;
import static it.uniroma3.weir.fixture.WebsiteFixture.createWebsite;
import it.uniroma3.util.FixtureUtils;
import it.uniroma3.weir.model.Webpage;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class WebpageFixture {

	static private Long actualId = 0l;

	static public Webpage webpage() {
		return webpage(null);
	}
	
	static public Webpage webpage(String content) {
		long id;
		synchronized (actualId) {
			id = actualId++;			
		}
		// n.b. File materialization during testing ensures
		//      we use the regular tokenization process
		final URI uri = FixtureUtils.makeTmpFile(content, "page_" + id+".html").toURI();
		final Webpage webpage = new Webpage("page_" + id, uri);
		webpage.loadDocument();
		return webpage;
	}

	static public List<Webpage> webpages(String... contents) {
		final List<Webpage> pages = new ArrayList<>();
		for (String content : contents) {
			pages.add(webpage(content));
		}
		createWebsite(pages); /* create their own website */
		return pages;
	}

	static public List<Webpage> pagesWithId(String... identifiers) {
		final List<Webpage> pages = new ArrayList<Webpage>();
		for (String id : identifiers) {
			pages.add(new Webpage(id));
		}
		return pages;
	}

	static public List<Webpage> pivotedPagesWithText(String text) {
		final String page1 = _PIVOTED_("pivot",text+"_a");
		final String page2 = _PIVOTED_("pivot",text+"_b");
		return webpages(page1, page2);
	}

	static public List<Webpage> pivotedPagesWithPivot(String pivot) {
		final String page1 = _PIVOTED_(pivot,"text_a");
		final String page2 = _PIVOTED_(pivot,"text_b");
		return webpages(page1, page2);
	}

}
