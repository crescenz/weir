package it.uniroma3.weir.evaluation;

import static it.uniroma3.util.CollectionsCSVUtils.collection2csv;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.model.Attribute;
import it.uniroma3.weir.model.Experiment;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.ExtractedVector;
import it.uniroma3.weir.vector.Label;
import it.uniroma3.weir.vector.Vector;
import it.uniroma3.weir.vector.value.GoldenValue;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import com.csvreader.CsvReader;

import static it.uniroma3.hlog.HypertextualUtils.linkTo;

/**
 * Read the golden data for a {@link Website} from a .csv file following
 * a basic format: the header line in the .csv contains the attribute labels
 * (e.g., url,position,height,birthdate,birthplace) and it follows one 
 * comma-separated line of values per every tuple in the golden data.
 * 
 */
public class GoldenSiteReader {

	static final private String NULL_MARKER = "<NULL>";

	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	private Experiment experiment;

	private File goldenFile;
	
	private Website website;
	
	private String[] labels; 

	private GoldenValue[][] goldenTable; // an array of golden tuples

	public GoldenSiteReader(Experiment exp) {
		this.experiment = exp;
	}

	public List<Attribute> readGoldenAttributes(Website website) throws IOException {
		this.goldenTable = null;
		this.website = website;
		this.labels = null;
		this.goldenFile = this.experiment.getWebsiteGoldenCSVFile(website);
		try (final FileReader reader = new FileReader(goldenFile)) {
			return readGoldenAttributes(reader);
		}
	}

	private List<Attribute> readGoldenAttributes(Reader reader) throws IOException {
		this.readGoldenTable(reader);
		return this.createGoldenAttributes();
	}

	private void readGoldenTable(final Reader reader) throws IOException {
		final CsvReader csvReader = new CsvReader(reader);

		/* the header line in the .csv contains the labels */
		/* e.g., url,position,height,birthdate,birthplace  */
		csvReader.readHeaders();
		this.labels = csvReader.getHeaders();
		
		/* Init a Golden Table with golden values from pages to evaluate */
		
		/* A table column for each golden attribute */
		final int nColumns = this.labels.length;
		
		/* A table row for each page */
//		final int nRows = this.website.getOverlappingPages().size();
		final int nRows = this.website.getWebpages().size();
		this.goldenTable = initGoldenTable(nRows, nColumns);

		/* Fill the Golden Table (returns available page names) */
		final List<String> found = fillGoldenTable(csvReader);		
		csvReader.close();
		logAndSaveMissingPages(found);// log pages missing from the golden
	}

	private GoldenValue[][] initGoldenTable(final int rows, final int columns) {
		final GoldenValue[][] table = new GoldenValue[columns][];
		for (int i=1; i<columns; i++) {
			// skip the URL as a header (index=0) (it won't be a vector) and
			// prepare an array to host a golden value for each evaluated page
			table[i] = new GoldenValue[rows];
		}
		return table;
	}	

	// Which golden tuples are needed to the evaluation?
	//
	// It depends on how we evaluate the results: if we evaluate 
	// only on overlapping pages, during the processing we work 
	// only on values from those. Otherwise, for a full evaluation
	// we should expand the extracted vectors to cover all pages
	// just after the processing, not only those overlapping
	private List<String> fillGoldenTable(CsvReader reader) throws IOException {
		final List<String> found = new ArrayList<>();
		while (reader.readRecord()) {
			/* read next tuple of golden values */
			final String[] tuple = reader.getValues();
			// save the flat name of the file with the page source code
			found.add(tuple[0]); 
			fillGoldenTuple(tuple);
		}
		return found;
	}

	// spread the golden values over the table-row of their page, if any
	private void fillGoldenTuple(final String[] tuple) {
		final String pagefile = tuple[0];  // flat name of the file with the page source code
		final Webpage page = this.website.findPageByName(pagefile);
		if (page!=null) {
			for (int j=1; j<tuple.length; j++) {
				goldenTable[j][page.getIndex()] = new GoldenValue(page,emptyStringOrNULLasNullMarker(tuple[j]));
			}
		}
	}

	static final private String emptyStringOrNULLasNullMarker(final String string) {
		return string.trim().isEmpty() || string.equals(NULL_MARKER) ? null : string;
	}

	private void logAndSaveMissingPages(List<String> found) {		
		/* found=names of the page .html source files  available */
//		final List<String> wanted = this.getPagenames(this.website.getOverlappingPages());
		final List<String> wanted = this.getPagenames(this.website.getWebpages());

		final List<String> missing = new ArrayList<>(wanted);
		missing.removeAll(found);
		if (wanted.isEmpty()) {
			log.trace("no page from this site");
			return;
		}
		if (missing.isEmpty()) {
			log.trace("all pages needed for evaluation found "
					+ "in the golden csv file "+linkTo(this.goldenFile));
		} else {
			log.warn("<b>the golden "
					 + linkTo(this.goldenFile).withAnchor("csv file")
					 + " misses a few page URLs</b>");
			/* save missing page urls in a .txt file */
			final File missingUrlFiles = new File("./missing_urls.txt");
			try (BufferedWriter out = new BufferedWriter(new FileWriter(missingUrlFiles))) {
				log.warn("missing page url ");
				log.warn(collection2csv(missing, "\n", "\n\t", "\n"));
				out.write(collection2csv(missing, "\n", "\n\t", "\n"));
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			log.warn("Missing urls have been save in file : "+linkTo(missingUrlFiles));
			throw new NoSuchElementException(
					"Golden values not found for pages "
					+ missing + " from site "+this.website);
		}
	}

	private List<String> getPagenames(List<Webpage> pages) {
		final List<String> pagenames = new LinkedList<>();
		for(Webpage page : pages)
			pagenames.add(page.getName());
		return pagenames;
	}

	
	private List<Attribute> createGoldenAttributes() {
		final List<Attribute> attributes = new ArrayList<>();
		final int natts = this.goldenTable.length;
		final int nvals = this.goldenTable[1].length;
		if (nvals==0) {
			log.trace("No golden values available");
			return attributes;
		}
		log.trace("creating "+ (natts-1) +" golden attribute(s) of values from "
				  +nvals+" <em>input</em> pages");
		log.trace();
		/* skip URL column */
		for (int i=1; i<natts; i++) {
			final String label = labels[i].toUpperCase();
			log.newPage("normalizing golden attribute "+label);
			final Vector goldenVector = ExtractedVector.makeGoldenVector(this.website, goldenTable[i]);
			goldenVector.addLabel(new Label(label));
			final Attribute goldenAttribute = createGoldenAttribute(goldenVector);
			attributes.add(goldenAttribute);
			log.endPage();
			log.trace();
			log.trace(goldenAttribute);
			log.trace("<hr/>");
		}
		return attributes;
	}

	private Attribute createGoldenAttribute(final Vector golden) {
		final Attribute attribute = new Attribute(golden);
		attribute.setWebsite(this.website);
		return attribute;
	}

}
