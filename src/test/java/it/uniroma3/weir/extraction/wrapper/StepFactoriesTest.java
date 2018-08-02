package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures._HTML_TREE_;
import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures.document;
import static it.uniroma3.weir.extraction.wrapper.DocumentUtils.getUniqueElement;
import static it.uniroma3.weir.extraction.wrapper.Step.DWE;
import static it.uniroma3.weir.extraction.wrapper.Step.DWT;
import static it.uniroma3.weir.extraction.wrapper.StepFactories.*;
import static org.junit.Assert.*;
import it.uniroma3.weir.fixture.WeirTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.w3c.dom.Document;

public class StepFactoriesTest extends WeirTest {

	@Test
	public void testNext_explorationStep_UP() {
		final Document doc = document(_HTML_TREE_("<LEAF_NODE/>"));
		assertFalse(UP.from(getUniqueElement(doc,"LEAF_NODE")).isEmpty());
		assertTrue(UP.from(getUniqueElement(doc, "HTML")).isEmpty());
	}
	
	@Test
	public void testNext_explorationStep_DW() {
		final Document doc = document(_HTML_TREE_("<LEAF_NODE/>"));
		assertTrue(DW.from(getUniqueElement(doc, "LEAF_NODE")).isEmpty());
		assertFalse(DW.from(getUniqueElement(doc,"HTML")).isEmpty());
	}
		
	@Test
	public void testNext_explorationStep_DX() {
		final Document doc = document(_HTML_TREE_("<SX_NODE/><DX_NODE/>"));
		assertFalse(DX.from(getUniqueElement(doc,"SX_NODE")).isEmpty());
		assertTrue(DX.from(getUniqueElement(doc, "DX_NODE")).isEmpty());
	}

	@Test
	public void testNext_explorationStep_SX() {
		final Document doc = document(_HTML_TREE_("<SX_NODE/><DX_NODE/>"));
		assertTrue(SX.from(getUniqueElement(doc,"SX_NODE")).isEmpty());
		assertFalse(SX.from(getUniqueElement(doc, "DX_NODE")).isEmpty());
	}

	@Test
	public void testNext_explorationStep_DW_children_text() {
		final Document doc = document(
				_HTML_TREE_("<PARENT>text</PARENT>")
		);
		assertEquals( set(DWT(1)), 
					  DW.from(getUniqueElement(doc, "PARENT")));
	}
	
	@Test
	public void testNext_explorationStep_DW_children_tag() {
		final Document doc = document(
				_HTML_TREE_("<PARENT><KID/></PARENT>")
		);
		assertEquals( set(DWE(1)), 
					  DW.from(getUniqueElement(doc, "PARENT")));
	}
	
	@Test
	public void testNext_explorationStep_DW_2children() {
		final Document doc = document(_HTML_TREE_("<PARENT><KID1/><KID2/></PARENT>"));
		assertEquals( set(DWE(1), DWE(2)), 
				 	  DW.from(getUniqueElement(doc, "PARENT")));
	}

	@Test
	public void testNext_explorationStep_DW_children_textAndTags() {
		final Document doc = document(
				_HTML_TREE_("<PARENT><KID1/>text1<KID2/>text2</PARENT>")
		);
		assertEquals( set(DWE(1), DWT(1), DWE(2), DWT(2)), 
					  DW.from(getUniqueElement(doc, "PARENT")));
	}

	final static private Set<Step> set(Step ...steps) {
		return new HashSet<>(Arrays.asList(steps));		
	}
	
}