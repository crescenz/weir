package it.uniroma3.weir.model;

import static it.uniroma3.hlog.HypertextualLogger.getLogger;
import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import static it.uniroma3.weir.configuration.Constants.SOFTID_FILTER;
import static it.uniroma3.weir.configuration.Constants.MAX_PAGES_PER_SOURCE;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.cache.CachedComputation;
import it.uniroma3.weir.cache.Fingerprint;
import it.uniroma3.weir.cache.Fingerprinter;
import it.uniroma3.weir.configuration.WeirConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;
import java.util.regex.Pattern;

public class WebsiteLoader extends CachedComputation<String, Website> {

	static final private HypertextualLogger log = getLogger();

	private final Experiment experiment;
	
	// max number of pages to load from each source
	private int maxNumberOfPages = WeirConfig.getInteger(MAX_PAGES_PER_SOURCE);
	
	final private Pattern softIdRegexp;
	
	public WebsiteLoader(Experiment experiment) {
		this.experiment = experiment;
		final String regexp = WeirConfig.getString(SOFTID_FILTER);
		if (regexp!=null && !regexp.trim().isEmpty()) {
			this.softIdRegexp = Pattern.compile(regexp);
			log.trace("selecting only pages whose soft-id matches with: "+this.softIdRegexp);
		} else this.softIdRegexp=null;
	}

	public Website loadWebsite(String sitename) {
		return cachedComputation(sitename);
	}
	
	@Override
	public Website uncachedComputation(String sitename) {
		// e.g., dataset/swde/nbaplayer/nbaplayer-espn/
		final File siteFolder = this.experiment.getWebsiteFolder(sitename);

		// e.g., dataset/swde/nbaplayer/nbaplayer-espn/_id2name.txt
		final File pagesIndex = this.experiment.getWebsitePageIndex(sitename);

		try (final Reader reader = new FileReader(pagesIndex)) {
			final Website website = new Website(sitename);
			log.newPage();
			log.trace("loading website " + sitename);
			log.newTable();
			int filtered = this.loadPages(reader, website, siteFolder);
			log.endTable();
			log.endPage("loading website " + sitename 
						+ " ("+website.getWebpages().size()+" pages loaded,"
						+ filtered+" filtered out)");
			return website;
		}
		catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private int loadPages(Reader id2UrlReader, Website website, File siteFolder) {
		log.trace("<em>Id</em>","<em>file</em>");
		int counter = 0;
		int filtered = 0;
		try (Scanner scanner = new Scanner(id2UrlReader)) {
			while (scanner.hasNextLine()) {
				/* a line per page */
				final String line = scanner.nextLine();
				final String[] split = line.split("\t");
				if (split.length==2) {
					final String softId   = split[0].trim();
					final String filename = split[1].trim();
					final File file = new File(siteFolder, filename);
					if (filter(softId)) {
						final Webpage page = new Webpage(softId, file.toURI());
						website.addPage(page);
						log.trace(page.getId(),linkTo(file));
					} else {
						log.trace(softId,"filtered out");
						filtered++;						
					}
				} else {
					log.warn("@skipping malformed line " + line);
				}
				if (++counter == maxNumberOfPages) break;
			}
		}		
		log.trace(counter+" pages loaded");
		log.trace(filtered+" pages filtered out");
		return filtered;
	}
	
	private boolean filter(String softId) {
		return softIdRegexp==null || this.softIdRegexp.matcher(softId).matches();
	}

	@Override
	public Fingerprint fingerprint(String sitename) {
		final Fingerprinter printer = new Fingerprinter();
		printer.fingerprint(this.experiment.getDataset().getName());
		printer.fingerprint(this.experiment.getDomain().getName());
		printer.fingerprint(this.experiment.getDomain().getName());
		printer.fingerprint(sitename);
		printer.fingerprint(Integer.toString(maxNumberOfPages));
		printer.fingerprint(this.softIdRegexp);
		return printer.getFingerprint("site");
	}

}
