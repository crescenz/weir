package it.uniroma3.weir.fixture;

import static it.uniroma3.weir.vector.type.Type.NUMBER;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.junit.Assert.assertTrue;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.NumericVector;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.type.Type;
import it.uniroma3.weir.vector.value.ExtractedValue;
import it.uniroma3.weir.vector.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static it.uniroma3.weir.fixture.WebsiteFixture.*;

public class VectorFixture {

	static public Vector createVector(String... values) {
		return createVector(Type.STRING, values);
	}

	static public Vector createVector(Type type, String... values) {
		return createVector(type, null, webpages(values.length), values);
	}

	static public Vector createVector(Webpage[] pages, String... values) {
		return createVector(Type.STRING, null, pages, values);
	}
	// MGM
	static public Vector createVector(Type type, ExtractionRule rule, Webpage[] pages, String... values) {
		return new Vector(createExtractedVector(rule,pages,values), type,values);
	}

	static public ExtractedVector createExtractedVector(String... values) {
		return createExtractedVector(webpages(values.length), values);
	}

	static public ExtractedVector createExtractedVector(ExtractionRule rule, String... values) {
		return createExtractedVector(rule, webpages(values.length), values);
	}
	
	static public ExtractedVector createExtractedVector(Webpage[] pages, String... values) {
		return createExtractedVector(null, pages, values);
	}
	// MGM
	static public ExtractedVector createExtractedVector(ExtractionRule rule, Webpage[] pages, String... values) {
		return new ExtractedVector(toExtractedValuesArray(pages,values), rule);
	}
	
	static public NumericVector createNumericVector(Object... values) {
		final String[] stringValues = number2string(values);
		final ExtractedVector ev = createExtractedVector(stringValues);
		return new NumericVector(ev, NUMBER, stringValues);
	}

	static final private String[] number2string(Object[] values) {
		String[] result = new String[values.length];
		for(int i=0; i<values.length; i++) {
			final String stringValue = values[i].toString();
			assertTrue(NUMBER.instanceOf(stringValue));
			result[i] = stringValue;
		}
		return result;
	}	
		
	static public Vector createRandomStringVector(int length) {
		final String[] values = new String[length];
		final Webpage[] pages = webpages(length);
		for (int i=0; i<length; i++) {
			values[i] = randomString();
		}
		return createVector(pages, values);
	}

	static private Webpage[] webpages(int number) {
		final Website site = createWebsite(number);
		return 	site.getWebpages().toArray(new Webpage[0]);
	}

	static private String randomString() {
		final Random r = new Random();
		final StringBuilder s = new StringBuilder();
		for (int i=0; i<5; i++) {
			final char randomChar = (char) ('a' + r.nextInt(26));
			s.append(randomChar);
		}
		return s.toString();
	}

	static private ExtractedValue[] toExtractedValuesArray(Webpage[] pages, String... values) {
		final ExtractedValue[] evs = new ExtractedValue[values.length];
		for (int i=0; i<values.length; i++) {
			evs[i] = new TestExtractedValue(pages[i], values[i]);
		}
		return evs;
	}

	static private class TestExtractedValue extends ExtractedValue {
		static final private long serialVersionUID = 1L;
		
		TestExtractedValue(Webpage page, String value) {
			super(page,value);			
		}
		
//		@Override
//		public Webpage getPage() {
//			return new Webpage("http://page_of_value/"+escapeHtml4(this.getValue().replaceAll("\\s", ""))) {
//
//				static final private long serialVersionUID = 1L;
//				
//			};
//		}
		
		@Override
		public int compareTo(Value that) {
			return System.identityHashCode(this)-System.identityHashCode(that);
		}
	}
	
	static public List<Vector> createVectorList(Vector... vectors) {
		return Arrays.asList(vectors);
	}

	@SafeVarargs
	static public List<List<Vector>> createVectorClusters(
			 List<Vector>... groupsOfVectors) {
		final List<List<Vector>> clusters = new ArrayList<>();
		for (List<Vector> cluster : groupsOfVectors) {
			clusters.add(cluster);
		}
		return clusters;
	}
	
}
