package it.uniroma3.weir.extraction;

import static java.util.concurrent.TimeUnit.SECONDS;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.extraction.rule.ExtractionRule;
import it.uniroma3.weir.extraction.wrapper.template.DocumentNormalizer;
import it.uniroma3.weir.model.Webpage;
import it.uniroma3.weir.model.Website;
import it.uniroma3.weir.vector.ExtractedVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
/**
 * Extract {@link ExtractedVector}s by parallelizing the
 * application of a set of {@link ExtractionRule}s over its {@link Webpage}s.
 */
public class ParallelExtractor {

	static final private int EXTRACTION_TIMEOUT = 1800; //secs = 1/2 h 
	
	static final private HypertextualLogger log = HypertextualLogger.getLogger();

	static final private int NCPU = Runtime.getRuntime().availableProcessors();

	final private ExecutorService pool = Executors.newFixedThreadPool(NCPU);
	
	// switch to this to deal with Eclipse and jwpd bugs when 
	// using conditional breakpoint during concurrent executions
//	final private ExecutorService pool = Executors.newSingleThreadExecutor();

	final CompletionService<List<ExtractedVector>> ecs = new ExecutorCompletionService<>(pool);

	final private List<Webpage> webpages;

	final private int npages;

	public ParallelExtractor(Website website) {
		this( website.getWorkingPages() );
	}

	public ParallelExtractor(List<Webpage> webpages) {
		this.webpages = webpages;
		log.trace(getClass()+" working over "+this.webpages.size()+" pages");
		this.npages = webpages.size();
		//N.B.: XPath evaluation requires normalized DOM documents
		normalize(webpages);
	}

	/* normalize the documents saving the annotations   */
	private void normalize(List<Webpage> webpages) {
		/* normalize the documents by saving the annotations   */
		final DocumentNormalizer normalizer = new DocumentNormalizer();
		for(Webpage page : webpages)
			normalizer.normalize(page.getDocument());
	}

	public List<ExtractedVector> parallelExtraction(Set<ExtractionRule> rules) {
		/* extract */
		log.trace("Available processors: " + NCPU);

		final List<ExtractedVector> extracted = new ArrayList<>();

		try {
			/* slice the rules in NCPU chunks */
			final int total = rules.size();
			log.trace("applying " + total+" extraction rules over "+webpages.size()+" pages");

			final Iterator<ExtractionRule> it = rules.iterator();
			int taskCounter=0;
			while (it.hasNext()) {
				ecs.submit(makeExtractionTask(it, total));
				taskCounter++;
			}

			int counter = 0;
			// n.b. initial \n should flush log msgs..
			log.trace("\nStarting parallel extraction ...");
			while (taskCounter-- > 0) {
				final Future<List<ExtractedVector>> f = ecs.take();
				extracted.addAll(f.get());
				counter += f.get().size();
				log.trace("\napplied extraction rules (" + counter + "/" + total + ")");
			}
			log.trace("\n...parallel extraction finished.");
			this.pool.shutdown();
			// otherwise pages' docs could be released too soon...
			this.pool.awaitTermination(EXTRACTION_TIMEOUT, SECONDS);
		}
		catch (InterruptedException | ExecutionException e) {
			log.error("parallel extraction failed");
			e.printStackTrace();
			log.trace(e);
			this.pool.shutdownNow();
			throw new IllegalStateException(e);
		}
		return extracted;
	}

	private Callable<List<ExtractedVector>> makeExtractionTask(Iterator<ExtractionRule> it, int totalSize) {
		final int chunkSize = totalSize / NCPU;
		List<ExtractionRule> chunk = new ArrayList<ExtractionRule>(chunkSize);
		int count = 0;
		while (it.hasNext() && count++ < chunkSize) {
			chunk.add(it.next());
		}
		if (totalSize % NCPU > 0 && it.hasNext()) chunk.add(it.next()); // one extra to consume odd rules

		return new ApplyRulesTask(chunk);
	}

	static private int lastOffset = 0;

	public class ApplyRulesTask implements Callable<List<ExtractedVector>> {

		
		final private List<ExtractionRule> rules;

		// a task processes incrementally the n pages starting from this offset to
		// reduce the probability that it will meet other threads on the same doc
		final private int offset ;

		public ApplyRulesTask(List<ExtractionRule> rules) {
			this.rules  = rules;
			this.offset = (npages / NCPU) * (lastOffset++);
		}

		@Override
		public List<ExtractedVector> call() throws Exception {
			final List<ExtractedVector> result = new ArrayList<>(rules.size());

			for (ExtractionRule rule : this.rules) {
				result.add(apply(rule));
			}
			return result;
		}

		final private ExtractedVector apply(ExtractionRule rule) {
			// the offset improve the likeness that different
			// threads will work on different docs
			return rule.applyTo(webpages, offset);
		}
	}

}