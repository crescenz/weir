package it.uniroma3.weir.extraction.wrapper.template;


import static it.uniroma3.weir.fixture.WebpageFixture.webpage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;
import it.uniroma3.weir.extraction.wrapper.DocumentUtils;
import it.uniroma3.weir.extraction.wrapper.template.DocumentNormalizer;
import it.uniroma3.weir.fixture.WeirTest;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import static java.util.Collections.singletonList;

public class DocumentNormalizerTest extends WeirTest {

	static final private String TEXT_X = "x";
	static final private String TEXT_Y = "y";
	
	static final private String KEY_X = "keyX";
	static final private String KEY_Y = "keyY";
	
	static final private String VALUE_X = "valueX";
	static final private String VALUE_Y = "valueY";

	private Document doc;
	private Element body;

	private Text first;
	private Text last;

	private DocumentNormalizer normalizer;

	@Before
	public void setUp() {
		this.doc = webpage("<HTML><BODY>"+TEXT_X+TEXT_Y+"</BODY></HTML>").getDocument();
		this.body = DocumentUtils.getElement(this.doc, "BODY", 0);
		assumeTrue(this.body.getChildNodes().getLength() == 1);
		final Text text = (Text) this.body.getFirstChild();
		text.splitText(1);
		assumeTrue(this.body.getChildNodes().getLength() == 2);
		this.first = (Text) this.body.getFirstChild();
		assertEquals(TEXT_X, this.first.getNodeValue());
		this.last = (Text) this.body.getLastChild();
		assertEquals(TEXT_Y, this.last.getNodeValue());
		this.normalizer = new DocumentNormalizer(KEY_X, KEY_Y);
	}

	@Test
	public void testNormalizeAndPreserveDOMuserdata() {
		this.first.setUserData(KEY_X, VALUE_X, null);
		this.last.setUserData(KEY_Y, VALUE_Y, null);
		assertEquals(VALUE_X, this.first.getUserData(KEY_X));
		assertEquals(VALUE_Y, this.last.getUserData(KEY_Y));
		this.normalizer.normalize(this.doc);
		assertEquals(1, this.body.getChildNodes().getLength());
		
		final Text merged = (Text) this.body.getFirstChild();
		final String msg = "DOM user-data should have been preserved!";
		assertEquals(msg, singletonList(VALUE_X), merged.getUserData(KEY_X));
		assertEquals(msg, singletonList(VALUE_Y), merged.getUserData(KEY_Y));
	}

}
