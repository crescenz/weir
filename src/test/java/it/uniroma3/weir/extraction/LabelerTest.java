package it.uniroma3.weir.extraction;

import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.extraction.rule.RelativeRule;
import static it.uniroma3.weir.fixture.VectorFixture.*;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.Label;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class LabelerTest extends WeirTest {

	
	// FIXME not effectively used.. why this assert method is here ?
	static private void assertLabels(List<Label> expected, String xpath) {
		ExtractionRule rule = new RelativeRule(xpath);
		ExtractedVector extracted = createExtractedVector(rule, "").getOriginatingVector();
		
		final Labeler labeler = new Labeler();
		labeler.label(extracted);

		assertEquals(expected, extracted.getLabels());
	}
	
	private static void assertLabel(String label, int distance, String xpath) {
		assertLabels(Collections.singletonList(new Label(label, distance)), xpath);
	}
	
	@Test
	public void testLabeling_positional_rule() {
		assertLabel(null, -1, "/HTML[1]/BODY[1]/text()");
	}
	
	@Test
	public void testLabeling_minimal_rule() {
		assertLabel("l", 0, "//TR[contains(text(),'l')]/text()");
	}

	@Test
	public void testLabeling_label_with_non_word_characters() {
		assertLabel("l", 0, "//TR[contains(text(),'#l:')]/text()");
	}

	@Test
	public void testLabeling_label_with_numer() {
		assertLabel("l1", 0, "//TR[contains(text(),'l1')]/text()");
	}

	@Test
	public void testLabeling_rule_with_child() {
		assertLabel("l", 1, "//TR[contains(text(),'l')]/child::*[1]/text()");
	}

	@Test
	public void testLabeling_rule_with_far_child() {
		assertLabel("l", 1, "//TR[contains(text(),'l')]/child::*[100]/text()");
	}

	@Test
	public void testLabeling_rule_with_more_use_of_child() {
		assertLabel("l", 6, "//TR[contains(text(),'l')]/../following-sibling::*[1]/child::*[1]/child::*[1]/child::*[2]/child::*[2]/text()");
	}
	
	@Test
	public void testLabeling_rule_with_following_sibling() {
		assertLabel("l", 1, "//TR[contains(text(),'l')]/following-sibling::*[1]/text()");
	}

	@Test
	public void testLabeling_rule_with_preceding_sibling() {
		assertLabel("l", 1, "//TR[contains(text(),'l')]/preceding-sibling::*[1]/text()");
	}

	@Test
	public void testLabeling_rule_using_parent_axes() {
		assertLabel("l", 2, "//TR[contains(text(),'l')]/../child::*[1]/text()");
	}

	@Test
	public void testLabeling_rule_using_multiple_parents() {
		assertLabel("l", 6, "//TR[contains(text(),'l')]/../../../../preceding-sibling::*[1]/child::*[1]/text()");
	}
	
}
