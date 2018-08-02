package it.uniroma3.weir.extraction.rule;

import static org.junit.Assert.assertEquals;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import it.uniroma3.weir.fixture.WebpageFixture;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.vector.value.ExtractedValue;

import org.junit.Test;

import static javax.xml.xpath.XPathConstants.STRING;

public class ExtractionRuleTest extends WeirTest {

	static final private String TEXT_1 = "text_1";
	static final private String TEXT_2 = "text_2";

	public static void assertExpectedValue(String document, ExtractionRule rule, String expectedValue, String expectedMark) {
		Webpage page = WebpageFixture.webpage(document);
		ExtractedValue value = rule.applyTo(page);
		assertEquals(expectedValue, value.getValue());
		assertEquals(expectedMark, value.getOccurrenceMark());
	}

	static private void assertExpectedPositionalValue(String xpath, String document, String expectedValue) {
		assertExpectedValue(document, new PositionalRule(xpath), expectedValue, xpath);
	}

	static private void assertExpectedRelativeValue(String xpath, String document, String expectedValue, String expectedMark) {
		assertExpectedValue(document, new RelativeRule(xpath), expectedValue, expectedMark);
	}

	static final private String SIMPLE_TREE =
	"<HTML>" +
		"<BODY>" +
			"<C>" +
				TEXT_1 +
			"</C>" +
		"</BODY>" +
	"</HTML>";

	@Test
	public void testApplyTo_positional_simple_tree() {
		assertExpectedPositionalValue("/HTML[1]/BODY[1]/C[1]/text()[1]", SIMPLE_TREE, TEXT_1);
	}

	@Test
	public void testApplyTo_relative_simple_tree() {
		assertExpectedRelativeValue("//C[contains(text(),'text_1')]/text()", SIMPLE_TREE, TEXT_1, "/HTML[1]/BODY[1]/C[1]/text()[1]");
	}

	static final private String MULTI_ELEMENT_NODE_TREE =
	"<HTML>" +
		"<BODY>" +
			"<C>" +
				TEXT_1 +
				"<D/>" +
				TEXT_2 +
			"</C>" +
		"</BODY>" +
	"</HTML>";
	
	@Test
	public void testApplyTo_positional_multi_element_node_first_occurrence() {
		assertExpectedPositionalValue("/HTML[1]/BODY[1]/C[1]/text()[1]", MULTI_ELEMENT_NODE_TREE, TEXT_1);
	}

	@Test
	public void testApplyTo_positional_multi_element_node_second_occurrence() {
		assertExpectedPositionalValue("/HTML[1]/BODY[1]/C[1]/text()[2]", MULTI_ELEMENT_NODE_TREE, TEXT_2);
	}

	@Test
	public void testApplyTo_relative_multi_element_node_first_occurrence() {
		assertExpectedRelativeValue("//C[contains(text(),'text_1')]/text()[1]", MULTI_ELEMENT_NODE_TREE, TEXT_1, "/HTML[1]/BODY[1]/C[1]/text()[1]");
	}

	@Test
	public void testApplyTo_relative_multi_element_node_second_occurrence() {
		assertExpectedRelativeValue("//C[contains(text(),'text_1')]/text()[2]", MULTI_ELEMENT_NODE_TREE, TEXT_2, "/HTML[1]/BODY[1]/C[1]/text()[2]");
	}
	
	static final private String DUPLICATE_LEAVES_TREE =
	"<HTML>" +
		"<BODY>" +
			"<C>" +
				TEXT_1 +
			"</C>" +
			"<C>" +
				TEXT_2 +
			"</C>" +
		"</BODY>" +
	"</HTML>";

	@Test
	public void testApplyTo_positional_duplicate_leaves_first_occurrence() {
		assertExpectedPositionalValue("/HTML[1]/BODY[1]/C[1]/text()[1]", DUPLICATE_LEAVES_TREE, TEXT_1);
	}

	@Test
	public void testApplyTo_positional_duplicate_leaves_second_occurrence() {
		assertExpectedPositionalValue("/HTML[1]/BODY[1]/C[2]/text()[1]", DUPLICATE_LEAVES_TREE, TEXT_2);
	}

	@Test
	public void testApplyTo_relative_duplicate_leaves_node_first_occurrence() {
		assertExpectedRelativeValue("//C[contains(text(),'text_1')]/text()[1]", DUPLICATE_LEAVES_TREE, TEXT_1, "/HTML[1]/BODY[1]/C[1]/text()[1]");
	}

	@Test
	public void testApplyTo_relative_duplicate_leaves_node_second_occurrence() {
		assertExpectedRelativeValue("//C[contains(text(),'text_2')]/text()[1]", DUPLICATE_LEAVES_TREE, TEXT_2, "/HTML[1]/BODY[1]/C[2]/text()[1]");
	}
	
	static final private String DUPLICATE_NODES_IN_THE_PATH_TREE =
	"<HTML>" +
		"<BODY>" +
			"<C>" +
				TEXT_1 +
			"</C>" +
			"<D>" +
				"<C>" +
					TEXT_2 +
				"</C>" +
			"</D>" +
		"</BODY>" +
	"</HTML>";

	@Test
	public void testApplyTo_positional_duplicate_modes_in_the_path_first_occurrence() {
		assertExpectedPositionalValue("/HTML[1]/BODY[1]/C[1]/text()[1]", DUPLICATE_NODES_IN_THE_PATH_TREE, TEXT_1);
	}
	
	@Test
	public void testApplyTo_positional_duplicate_modes_in_the_path_second_occurrence() {
		assertExpectedPositionalValue("/HTML[1]/BODY[1]/D[1]/C[1]/text()[1]", DUPLICATE_NODES_IN_THE_PATH_TREE, TEXT_2);
	}

	@Test
	public void testApplyTo_relative_duplicate_modes_in_the_path_first_occurrence() {
		assertExpectedRelativeValue("//C[contains(text(),'text_1')]/text()[1]", DUPLICATE_NODES_IN_THE_PATH_TREE, TEXT_1, "/HTML[1]/BODY[1]/C[1]/text()[1]");
	}

	@Test
	public void testApplyTo_relative_duplicate_modes_in_the_path_second_occurrence() {
		assertExpectedRelativeValue("//C[contains(text(),'text_2')]/text()[1]", DUPLICATE_NODES_IN_THE_PATH_TREE, TEXT_2, "/HTML[1]/BODY[1]/D[1]/C[1]/text()[1]");
	}

	static final private String ELEMENT_WITH_ATTRIBUTE_TREE =
	"<HTML>" +
		"<BODY>" +
			"<C att=\"value\">" +
				TEXT_1 +
			"</C>" +
		"</BODY>" +
	"</HTML>";

	@Test
	public void testApplyTo_positional_element_with_attribute() {
		assertExpectedPositionalValue("/HTML[1]/BODY[1]/C[@att='value']/text()[1]", ELEMENT_WITH_ATTRIBUTE_TREE, TEXT_1);
	}
	
	@Test
	public void testApplyTo_relative_element_with_attribute() {
		assertExpectedRelativeValue("//C[contains(text(),'text_1')]/text()[1]", ELEMENT_WITH_ATTRIBUTE_TREE, TEXT_1, "/HTML[1]/BODY[1]/C[@att='value']/text()[1]");
	}

	static final private String ELEMENT_WITH_ID_TREE =
	"<HTML>" +
		"<BODY>" +
			"<C id=\"001\">" +
				TEXT_1 +
			"</C>" +
		"</BODY>" +
	"</HTML>";

	@Test
	public void testApplyTo_positional_element_with_id() {
		assertExpectedPositionalValue("/HTML[1]/BODY[1]/C[@id='001']/text()[1]", ELEMENT_WITH_ID_TREE, TEXT_1);
	}
	
	@Test
	public void testApplyTo_relative_element_with_id() {
		assertExpectedRelativeValue("//C[contains(text(),'text_1')]/text()[1]", ELEMENT_WITH_ID_TREE, TEXT_1, "/HTML[1]/BODY[1]/C[@id='001']/text()[1]");
	}

	@Test
	public void spikeOnXPathStringFunctions() throws XPathExpressionException {
		final String document = 
		"<HTML>" +
			"<BODY>" +
				"inv0:var1:inv1" +
			"</BODY>" +
		"</HTML>";
		final Webpage page = WebpageFixture.webpage(document);
//		"/HTML/BODY/text()[substring-after(.,'inv:')='var']",
//		"substring-after(/HTML/BODY/text(),'inv')",
//		"substring-after(//text(),':')",
//		"/HTML/BODY/text()",
		final XPath compiler = XPathFactory.newInstance().newXPath();
		XPathExpression exp = compiler.compile("substring-after(//text(),'inv0:')");
		
		String actual = (String) exp.evaluate(page.getDocument(), STRING);
		assertEquals("var1:inv1", actual);
		
		exp = compiler.compile("substring-before(substring-after(//text(),'inv0:'),':inv1')");
		
		actual = (String) exp.evaluate(page.getDocument(), STRING);
		assertEquals("var1", actual);
	}
}
