package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.weir.extraction.wrapper.DocumentFixtures._HTML_TREE_;
import static it.uniroma3.weir.extraction.wrapper.Step.*;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import it.uniroma3.weir.extraction.wrapper.Navigation;
import it.uniroma3.weir.extraction.wrapper.Step;
import it.uniroma3.weir.extraction.wrapper.TreeExplorer;
import it.uniroma3.weir.extraction.wrapper.template.TemplateFinderTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.w3c.dom.Node;


public class TreeExplorerTest extends TemplateFinderTest {

	private TreeExplorer explorer;

	@Test
	public void testExploreWithoutMoving() {
		setUpDocs(_HTML_TREE_("pivot"),_HTML_TREE_("pivot"));
		final Node start = this.getText(0, "pivot");
		assertEquals(Collections.emptySet(), exploreFrom(start, 0));
	}

	@Test
	public void testGotIt_DX() {
		setUpDocs(
				_HTML_TREE_("<PIVOT/>text0"),
				_HTML_TREE_("<PIVOT/>text1"));
		final Node start = this.getElement(0, "PIVOT");
		assertEquals(setOfNav(DX), exploreFrom(start, 1));
	}

	@Test
	public void testGotIt_SX() {
		setUpDocs(
				_HTML_TREE_("text0<PIVOT/>"),
				_HTML_TREE_("text1<PIVOT/>"));
		Node start = this.getElement(1, "PIVOT");
		assertEquals(setOfNav(SX), exploreFrom(start, 1));
	}


//	@Test
//	public void testGotIt_UP() {} /* this does not make any sense /

	@Test
	public void testGotIt_DWtext_fromPivotWithId() {
		setUpDocs(
				_HTML_TREE_("<PIVOT id='p'>text0</PIVOT>"),
				_HTML_TREE_("<PIVOT id='p'>text1</PIVOT>"));
		Node start = this.getElement(1, "PIVOT");
		assertEquals(setOfNav(DWT(1)), exploreFrom(start, 1));
	}	

	@Test
	public void testGotIt_DWtext1_fromTemplatePivot() {
		setUpDocs(
				_HTML_TREE_("<PIVOT>text0</PIVOT>"),
				_HTML_TREE_("<PIVOT>text1</PIVOT>"));
		Node start = this.getElement(1, "PIVOT");
		assertEquals(setOfNav(DWT(1)), exploreFrom(start, 1));
	}	

	@Test
	public void testGotIt_DWtext1_fromTemplatePivot_siblingElement() {
		setUpDocs(
				_HTML_TREE_("<PIVOT><BR>text0</PIVOT>"),
				_HTML_TREE_("<PIVOT><BR>text1</PIVOT>"));
		Node start = this.getElement(0, "PIVOT");
		assertEquals(setOfNav(DWT(1)), exploreFrom(start, 1));
	}

	@Test
	public void testGotIt_DWtext1_fromTemplatePivot_nodes() {
		setUpDocs(
				_HTML_TREE_("<PIVOT><!-- annoying comments! --><BR>text0</PIVOT>"),
				_HTML_TREE_("<PIVOT><BR><!-- comment -->text1</PIVOT>"));
		Node start = this.getElement(0, "PIVOT");
		assertEquals(setOfNav(DWT(1)), exploreFrom(start, 1));
	}

	@Test
	public void testGotIt_DWtext2_fromTemplatePivot() {
		setUpDocs(
				_HTML_TREE_("<PIVOT>invariant<BR/>text0</PIVOT>"),
				_HTML_TREE_("<PIVOT>invariant<BR/>text1</PIVOT>"));
		Node start = this.getElement(1, "PIVOT");
		assertEquals(setOfNav(DWT(2)), exploreFrom(start, 1));
	}	

	@Test
	public void testGotIt_DWtext2_fromTemplatePivot_multiWordsPCDATA() {
		setUpDocs(
				_HTML_TREE_("<PIVOT>invariant multiword<BR/>text0</PIVOT>"),
				_HTML_TREE_("<PIVOT>invariant multiword<BR/>text1</PIVOT>"));
		Node start = this.getElement(1, "PIVOT");
		assertEquals(setOfNav(DWT(2)), exploreFrom(start, 1));
	}	
	
	@Test
	public void testGotIt_DWtext1_fromTemplatePivot_separatedByIgnoredComment() {
		setUpDocs(
				_HTML_TREE_("<PIVOT>invariant multiword<!-- comment -->text0</PIVOT>"),
				_HTML_TREE_("<PIVOT>invariant multiword<!-- comment -->text1</PIVOT>"));
		Node start = this.getElement(1, "PIVOT");
//was:	assertEquals(setOfNav(DWT(1)), exploreFrom(start, 1)); //n.b. before intra PCDATA handling
		assertEquals(Collections.emptySet(), exploreFrom(start, 1)); 
		// empty 'cause the intra-PCDATA invariant is a closer pivot
	}

	@Test
	public void testGotIt_textualPivot_upAndDown() {
		setUpDocs(
				_HTML_TREE_("<P><I>invariant</I></P><B>text0</B>"),
				_HTML_TREE_("<P><I>invariant</I></P><B>text1</B>"));
		assertEquals(setOfNavs(
					setOfNav(UP, UP, UP, DWE(2), DWT(1)), 
					setOfNav(UP, UP, DX, DWT(1))
				),
				exploreFrom(this.getText(1, "invariant"), 3));
		assertEquals(setOfNavs(
//				path(UP, UP, UP, DWE(2), DWT(1)), 
				setOfNav(DWE(1), DX, DWT(1)),
				setOfNav(DWE(2), DWT(1))
			),
			exploreFrom(this.getElement(1, "BODY"), 3));	
	}

	@Test
	public void testGotIt_2DX() {
		setUpDocs(
				_HTML_TREE_("<PIVOT/><BR/>text0"),
				_HTML_TREE_("<PIVOT/><BR/>text1"));
		Node start = this.getElement(0, "PIVOT");
		assertEquals(setOfNavs(
					setOfNav(DX, DX),
					setOfNav(UP, DWT(1))
				),
				exploreFrom(start, 2));
	}

	@Test
	public void testGotIt_2SX() {
		setUpDocs(
				_HTML_TREE_("text0<BR/><PIVOT/>"),
				_HTML_TREE_("text1<BR/><PIVOT/>"));
		Node start = this.getElement(1, "PIVOT");
		assertEquals(setOfNavs(
					setOfNav(SX, SX),
					setOfNav(UP, DWT(1))
				),
				exploreFrom(start, 2));
	}

	
	public Set<Navigation> exploreFrom(Node start, int range) {
		this.explorer = new TreeExplorer(range);
		return explorer.explore(start);
	}
	
	static private Set<Navigation> setOfNav(Step...steps) {
		return singleton(new Navigation(steps));
	}
	
	@SafeVarargs
	static private Set<Navigation> setOfNavs(Set<Navigation>...navs) {
		final Set<Navigation> result = new HashSet<>();
		for(Set<Navigation> paths : navs)
			result.addAll(paths);			
		return result;
	}

}
