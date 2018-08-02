package it.uniroma3.weir.extraction.wrapper.template;

import static it.uniroma3.util.CollectionsCSVUtils.collection2csv;
import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures._HTML_TREE_;
import static it.uniroma3.weir.extraction.wrapper.DocumentUtils.getTextByContent;
import static it.uniroma3.weir.extraction.wrapper.DocumentUtils.getUniqueElement;
import static it.uniroma3.weir.fixture.WebpageFixture.webpages;
import static it.uniroma3.weir.extraction.wrapper.template.TemplateMarker.*;
import static it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates.*;
import static org.junit.Assert.*;
import it.uniroma3.token.Constants;
import it.uniroma3.token.dom.DOMToken;
import it.uniroma3.token.dom.node.DOMNode;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.extraction.wrapper.template.TemplateFinder;
import it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.model.Webpage;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class TemplateFinderTest extends WeirTest {

	
	static final String LONG_STRING_30 = "012345678901234567890123456789";

	static final String LONG_STRING_64 = LONG_STRING_30 + LONG_STRING_30 + "0123";

	
	static final private String DOC0 = _HTML_TREE_(
			"-<BR/>variant0<P>s</P>invariantPivot<IMG id='id'/><BR/>" + LONG_STRING_64);

	static final private String DOC1 = _HTML_TREE_(
			"-<BR/>text1<P> </P>invariantPivot<BR/>" + LONG_STRING_64);

	protected List<Webpage> webpages;

	protected TemplateFinder finder;
	
	protected TemplatePredicates predicates;

	
	@Before
	public void setUp() throws Exception {
		this.setUpDocs(DOC0, DOC1);
	}
	protected void setUpDocs(String... docs) {
		WeirConfig.getInstance().setProperty(Constants.MAX_PCDATA_LENGTH, "96");
		this.webpages = webpages(docs[0],docs[1]);
		this.finder = new TemplateFinder();
		this.finder.findTemplateTokens(this.webpages);
		this.predicates = new TemplatePredicates();
	}

	@Test
	public void testIsTemplateToken() {
		assertTrue(isTemplateNode(getElement(0, "HTML")));
		assertTrue(isTemplateNode(getElement(1, "BODY")));
		assertFalse(isTemplateNode(getText(0, "variant0")));
		assertFalse(isTemplateNode(getText(1, "text1")));
		assertTrue(isTemplateNode(getText(1, "invariantPivot")));	
		assertFalse(isTemplateNode(getElement(0, "IMG")));
		assertTrue(isTemplateNode(getText(0, LONG_STRING_64)));
	}

	@Test
	public void testIsSuitableLeafValue_shortValue() {
		assertTrue(isSuitableLeafValue(getText(0, "s")));
	}

	@Test
	public void testIsSuitableLeafValue_trimmableToEmptyString() {
		assertFalse(isSuitableLeafValue(getText(1, " ")));
	}
	
	@Test
	public void testIsSuitablePivotOccurrence_shortValue() {
		assertFalse(isSuitablePivotOccurrence(getText(0, "-")));
	}

	@Test
	public void testIsSuitablePivotOccurrence_tooLongValue() {
		assertFalse(isSuitablePivotOccurrence(getText(0, LONG_STRING_64)));
	}
	
	@Test
	public void testIsSuitableLeafValue() {
		assertTrue(isSuitableLeafValue(getText(1, "text1")));		
		assertFalse(isSuitableLeafValue(getElement(0, "IMG")));
		assertFalse(isSuitableLeafValue(getText(1, "invariantPivot")));
	}

	@Test
	public void testIsSuitablePivotToken() {
		assertTrue(isSuitablePivotToken(getElementToken(0, "IMG")));
		assertTrue(isSuitablePivotToken(getTextToken(1, "invariantPivot")));
		
		assertFalse(isSuitablePivotToken(getElementToken(1, "BODY")));
		assertFalse(isSuitablePivotToken(getTextToken(0, "variant0")));
	}

	@Test
	public void testIsSuitablePivotOccurrence() {
		assertTrue(isSuitablePivotOccurrence(getElement(0, "IMG")));
		assertTrue(isSuitablePivotOccurrence(getText(1, "invariantPivot")));
		
		assertFalse(isSuitablePivotOccurrence(getElement(1, "BODY")));
		assertFalse(isSuitablePivotOccurrence(getText(0, "variant0")));
	}

	@Test
	public void testGetOccurrencesOfTemplateToken() {
		final List<DOMToken> templateTokens = finder.getTemplateTokens();
		final DOMToken wanted = searchTextualToken("invariantPivot", templateTokens);
		assertNotNull(wanted);
		assertEquals(2, finder.getOccurrences(wanted).size());	
	}	

	@Test
	public void testGetTemplateTokens() {
		final List<DOMToken> templateTokens = finder.getTemplateTokens();
		assertEquals(collection2csv(templateTokens,"","\n",""), 
					 7, templateTokens.size());
	}
	
	protected Element getElement(int docIndex, String tag) {
		return getUniqueElement(this.webpages.get(docIndex).getDocument(), tag);
	}
	
	protected Text getText(int docIndex, String text) {
		return getTextByContent(this.webpages.get(docIndex).getDocument(), text);
	}
	
	protected DOMToken getElementToken(int docIndex, String tag) {
		final Element node = getUniqueElement(this.webpages.get(docIndex).getDocument(), tag);		
		return ((DOMNode)node).asDOMToken();
	}

	protected DOMToken getTextToken(int docIndex, String text) {
		final Text node = getTextByContent(this.webpages.get(docIndex).getDocument(), text);
		return ((DOMNode)node).asDOMToken();
	}

	private DOMToken searchTextualToken(String wanted, List<DOMToken> tokens) {
		for(DOMToken token : tokens) {
			if (token.isText() && token.getValue().equals(wanted))
				return token;
		}
		return null;
	}

}
