package it.uniroma3.weir.evaluation;

import static it.uniroma3.weir.fixture.AttributeFixture.createAttribute;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static it.uniroma3.weir.configuration.Constants.*;
import it.uniroma3.util.FixtureUtils;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.fixture.WebsiteFixture;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.Vector;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class GoldenSiteReaderTest {

	@BeforeClass
	public static void setUp() {
		WeirConfig.getInstance().setProperty(DATATYPES, "STRING,NUMBER,MONEY,SPACE,MASS,DATE,PHONE,URL");
	}
	
	//TODO ExperimentFixture
	static private Experiment makeExperiment() {
		return Experiment.makeExperiment(null, null);
	}
	
	static private void assertAttribute(String csvContent, Attribute... expected) {
		final Website site = WebsiteFixture.createWebsite(2, expected);

		final Experiment experiment = makeExperiment();
		final GoldenSiteReader goldenReader = new GoldenSiteReader(experiment);
		final File csvFile = FixtureUtils.makeTmpFile(csvContent, ".csv");
		try (final FileReader reader = new FileReader(csvFile)) {
			final List<Attribute> goldenAttrs = goldenReader.readGoldenAttributes(site);
			for (int i=0; i<expected.length; i++) {
				Vector expect = expected[i].getVector();
				Vector golden = goldenAttrs.get(i).getVector();
				assertArrayEquals(expect.getElements(), golden.getElements());
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetAttributes_simple() {
		assertAttribute("url,ATTR\np1.html,a\np2.html,b", createAttribute("a", "b"));
	}

	@Test
	public void testGetAttributes_order_of_records_not_influence_the_result() {
		assertAttribute("url,ATTR\np2.html,b\np1.html,a", createAttribute("a", "b"));
	}

	@Test
	public void testGetAttributes_null_value() {
		assertAttribute("url,ATTR\np1.html,a\np2.html,", createAttribute("a", null));
	}

	@Test
	public void testGetAttributes_two_attributes() {
		assertAttribute("url,ATTR1,ATTR2\np1.html,a,c\np2.html,b,d", createAttribute("a", "b"), createAttribute("c", "d"));
	}

	@Test
	public void testGetAttributes_values_with_double_quote() {
		assertAttribute("url,ATTR1,ATTR2\np1.html,a,\"c\"\np2.html,b,\"d\"", createAttribute("a", "b"), createAttribute("c", "d"));
	}

	@Test
	public void testGetAttributes_values_with_double_quote_and_commas_and_whitespace() {
		assertAttribute("url,ATTR1,ATTR2\np1.html,a,\"c,d, e\"\np2.html,b,\"f,g, h\"", createAttribute("a", "b"), createAttribute("c,d, e", "f,g, h"));
	}

}
