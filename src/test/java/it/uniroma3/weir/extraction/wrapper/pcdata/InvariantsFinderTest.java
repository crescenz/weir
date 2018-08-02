package it.uniroma3.weir.extraction.wrapper.pcdata;

import static it.uniroma3.weir.fixture.VectorFixture.createExtractedVector;
import static org.junit.Assert.*;
import it.uniroma3.weir.fixture.WeirTest;
import it.uniroma3.weir.vector.ExtractedVector;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class InvariantsFinderTest extends WeirTest {

	private InvariantsFinder finder;
	
	@Before
	public void setUp() {
		this.finder = new InvariantsFinder();
	}

	@Test
	public void testFindInvariantTokens_constant_one_invariant() {
		testShot(createExtractedVector(
				"inv0",
				"inv0",
				"inv0"
				));
	}

	@Test
	public void testFindInvariantTokens_constant_two_invariants() {
		testShot(createExtractedVector(
				"inv0 inv1",
				"inv0 inv1",
				"inv0 inv1"
				));
	}
		
	@Test
	public void testFindInvariantTokens_simple() {
		testShot(createExtractedVector(
				"inv0 var00 inv1",
				"inv0 var01 inv1",
				"inv0 var02 inv1"
				),
				"inv0","inv1");
	}

	@Test
	public void testFindInvariantTokens_optional_inv() {
		testShot(createExtractedVector(
				"inv0 var00 inv1",
				"inv0 var01",
				"inv0 var02 inv1"
				),
				"inv0");
	}

	@Test
	public void testFindInvariantTokens_out_of_order() {
		testShot(createExtractedVector(
				"inv0 var00 inv1",
				"inv1 var01 inv0",
				"inv0 var02 inv1"
				)); // this fails!
	}

	@Test
	public void testFindInvariantTokens_variantPositions() {
		List<Invariant> invs =
		testShot(createExtractedVector(
				"inv0 var00 inv1",
				"inv0 var01 inv1",
				"inv0 var02 inv1"
				),
				"inv0","inv1");
		final Invariant inv0 = invs.get(0);
		final Invariant inv1 = invs.get(1);
		assertFalse(inv0.isAdjacentToVariantsOnTheLeft());
		assertTrue(inv0.isAdjacentToVariantsOnTheRight());
		assertTrue(inv1.isAdjacentToVariantsOnTheLeft());
		assertFalse(inv1.isAdjacentToVariantsOnTheRight());		
	}

	@Test
	public void testFindInvariantTokens_double() {
		testShot(createExtractedVector(
				"inv0 var00 inv1 var10 inv2",
				"inv0 var01 inv1 var11 inv2",
				"inv0 var02 inv1 var12 inv2"
				),
				"inv0","inv1","inv2");
		testShot(createExtractedVector(
				"inv0 var00 inv1 var10 inv2",
				"inv0 var01      var11 inv2",
				"inv0 var02 inv1 var12 inv2"
				),
				"inv0","inv2");
		testShot(createExtractedVector(
				"inv0 var00 inv1 var10 inv2",
	   "noise "+"inv0 var01 inv1 var11 inv2",
				"inv0 var02 inv1 var12 inv2"
				),
				"inv0","inv1","inv2");
	}

	@Test
	public void testFindInvariantTokens_duplicate() {
		List<Invariant> invs =
		testShot(createExtractedVector(
				"inv0 var00 inv0",
				"inv0 var01 inv0",
				"inv0 var02 inv0"
				),
				"inv0","inv0");
		final Invariant inv0 = invs.get(0);
		final Invariant inv1 = invs.get(1);
		assertFalse(inv0.isAdjacentToVariantsOnTheLeft());
		assertTrue(inv0.isAdjacentToVariantsOnTheRight());
		assertEquals(0, inv0.getSelfIndex());
		assertEquals(1, inv1.getSelfIndex());
		assertTrue(inv1.isAdjacentToVariantsOnTheLeft());
		assertFalse(inv1.isAdjacentToVariantsOnTheRight());		
	}
	
	@Test
	public void testFindInvariantTokens_parenthesis() {
		testShot(createExtractedVector(
				"( 2000 )",
				"( 2001 )",
				"( 2002 )"
				),
				"(",")");
	}

	@Test
	public void testFindInvariantTokens_parenthesis_real() {
		testShot(createExtractedVector(
				"(2nd overall by the Grizzlies)",
				"(18th overall by the Rockets)",
				"(8th overall by the Cavaliers)"
				),
				"(","overall","the",")");
	}

	@Test
	public void testFindInvariantTokens_variant_numbers() {
		testShot(createExtractedVector(
				"1,00.11",
				"2,00.12",
				"3,00.13"
				));
	}
	
	@Test
	public void testFindInvariantTokens_just_one_invariant_number() {
		testShot(createExtractedVector(
				"vara 1,000.11",
				"varb 1,000.11",
				"varc 1,000.11"
				),
				"1,000.11"				
				);
	}

	@Test
	public void testFindInvariantTokens_numberAndSymbols() {
		testShot(createExtractedVector(
				"1,000.11 (var0)",
				"2,000.12 (var1)",
				"3,000.13 (var2)"
				),
				"(",")"
		);
	}

	@Test
	public void testFindInvariantTokens_numberSymbolsAndMarkers() {
		testShot(createExtractedVector(
				"6\'2\"",			
				"7\'0\"",
				"6\'5\""
				));
	}

	@Test
	public void testFindInvariantTokens_numbersAndColon() {
		testShot(createExtractedVector(
				"6:2",			
				"7:0",
				"6:5"
				));
	}

	@Test
	public void testFindInvariantTokens_numbersAndSlash() {
		testShot(createExtractedVector(
				"6/2",			
				"7/0",
				"6/5"
				));
	}

	@Test
	public void testFindInvariantTokens_numbersAndHype() {
		testShot(createExtractedVector(
				"6-2",
				"7-0",
				"6-5"
				));
	}
	
	private List<Invariant> testShot(ExtractedVector ev, String...expected) {
		final List<Invariant> invariants = this.finder.findInvariantTokens(ev);
		assertInvariants(expected, invariants);	
		return invariants;
	}
	
	static final void assertInvariants(String[] expected, List<Invariant> actual) {
		assertEquals(
				"wrong number of invariants produced:\n"+
				"\texpected: "+Arrays.toString(expected)+";\n"+
				"\t  actual: "+actual+";\n", 
				expected.length, actual.size());
		for(int i=0; i<expected.length; i++) {
			final String expectedToken = expected[i];
			assertEquals(expectedToken, actual.get(i).getToken());
		}
	}

}
