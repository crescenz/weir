package it.uniroma3.weir.integration;

import static it.uniroma3.weir.Formats.tenth;
import it.uniroma3.hlog.HypertextualLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* VC: to be converted into a ScoreDistributionRenderer */
public class WeirLogger {
	
	final private HypertextualLogger log;
	
	public WeirLogger(HypertextualLogger log) {
		this.log = log;
	}
	
	
	void logScoreDistribution(List<Match> ordered) {
		log.newPage("distribution");
		Map<String, Integer> freq = new HashMap<String, Integer>();

		for (double d = 0.0; d <= 1.0; d += 0.1) {
			String doubleClass = tenth.format(d);
			freq.put(doubleClass, 0);
		}
		freq.put("" + Double.POSITIVE_INFINITY, 0);

		for (Match ad : ordered) {
			double distance = ad.distance();
			if (distance != Double.POSITIVE_INFINITY) {
				String doubleClass = tenth.format(distance);
				freq.put(doubleClass, freq.get(doubleClass) + 1);
			}
			else {
				freq.put("" + Double.POSITIVE_INFINITY, freq.get("" + Double.POSITIVE_INFINITY) + 1);
			}

		}

		StringBuilder sb = new StringBuilder("\n");
		for (double d = 0.0; d <= 1.0; d += 0.1) {
			String key = tenth.format(d);
			sb.append(key + "\t\t->\t" + freq.get(key) + "\n");
		}
		sb.append(Double.POSITIVE_INFINITY + "\t->\t" + freq.get("" + Double.POSITIVE_INFINITY) + "\n");
		log.trace(sb + "\n");
		log.endPage();
	}

}
