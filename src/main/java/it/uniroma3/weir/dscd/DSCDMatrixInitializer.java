package it.uniroma3.weir.dscd;

import it.uniroma3.hlog.HypertextualLogger;

import java.util.List;

import org.jblas.DoubleMatrix;

/**
 * @see <a href="http://dl.acm.org/citation.cfm?id=1247541">
 * <i>Query relaxation using malleable schemas</i></a>
 * <br/>
 * Xuan Zhou, Julien Gaugaz, Wolf-tilo Balke, Wolfgang Nejdl.<br/>
 * ACM SIGMOD 2007<br/>
 * 
 * <em>Section 2.1: The Data Model</em>
 *
 */
public class DSCDMatrixInitializer<R,C> {

	static final protected HypertextualLogger log = HypertextualLogger.getLogger();

	final private List<R> rows;
	final private List<C> cols;

	final private DSCDScorer<R,C> dSCDScorer;
	
	public DSCDMatrixInitializer(List<R> r, List<C> c, DSCDScorer<R,C> s) {
		this.rows = r;
		this.cols  = c;
		this.dSCDScorer = s;
	}

	public DoubleMatrix initMatrix() {
		log.trace("init evidence matrix");
		final double[][] data = new double[this.rows.size()][this.cols.size()];

		for (int i = 0; i < this.rows.size(); i++) {
			R row = this.rows.get(i);

			for (int j = 0; j < this.cols.size(); j++) {
				C col = this.cols.get(j);
				data[i][j] = this.dSCDScorer.score(row, col);
			}
		}

		DoubleMatrix S = new DoubleMatrix(data);
		log.trace("S dimension: " + S.rows + " x " + S.columns);
		return S;
	}
	
}
