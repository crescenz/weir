package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures.*;
import static it.uniroma3.weir.extraction.wrapper.DocumentUtils.getText;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.fixture.WeirTest;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class PositionalXPathBuilderTest extends WeirTest {

	static private void assertPositionalXPath(
			String expected,   // expected positional XPath expression
			String parentName, // tag name of the parent of the target node
			int parentIndex,   // node index of the parent of the target node
			int textIndex,     // index of the target org.w3c.dom.Text node
			String contents    // input document content as a string
			) {
		final Document doc = document(contents);

		final Node text = getText(doc, parentName, parentIndex, textIndex);

		String actual = new PositionalXPathBuilder().getXPath((Text) text);
		
		assertEquals("Wrong Positional XPath expression", expected, actual);
	}
	
	private static final String BASE_XPATH = "/HTML[1]/BODY[1]";

	@Test
	public void test_basic() throws Exception {
		assertPositionalXPath(BASE_XPATH+"/P[1]/text()[1]", "P", 0, 0, _HTML_TREE_("<P>text_1</P>"));
	}

	@Test
	public void test_multiLeaves() throws Exception {
		final String multiLeaves = _HTML_TREE_("<P>text_1</P><P>text_2</P>");
		assertPositionalXPath(BASE_XPATH+"/P[1]/text()[1]", "P", 0, 0, multiLeaves);
		assertPositionalXPath(BASE_XPATH+"/P[2]/text()[1]", "P", 1, 0, multiLeaves);
	}

	@Test
	public void test_distinctPaths() throws Exception {
		final String distinctPaths = _HTML_TREE_( "" +
				"<P>" +
				/**/"text_1" +
				"</P>" +
				"<B>" +
				/**/"<I>" +
				/*    */"text_2" +
				/**/"</I>" +
				"</B>");
		assertPositionalXPath(BASE_XPATH+"/P[1]/text()[1]", "P", 0, 0, distinctPaths);
		assertPositionalXPath(BASE_XPATH+"/B[1]/I[1]/text()[1]", "I", 0, 0, distinctPaths);
	}

	@Test
	public void test_siblingTexts() throws Exception {
		final String siblingTexts = _HTML_TREE_("<P>text_1<BR/>text_2</P>");
		assertPositionalXPath(BASE_XPATH+"/P[1]/text()[1]", "P", 0, 0, siblingTexts);
		assertPositionalXPath(BASE_XPATH+"/P[1]/text()[2]", "P", 0, 2, siblingTexts);
	}

}
