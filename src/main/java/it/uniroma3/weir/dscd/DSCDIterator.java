package it.uniroma3.weir.dscd;

import it.uniroma3.hlog.HypertextualLogger;

import java.util.List;

import org.jblas.DoubleMatrix;

public class DSCDIterator<R,C> {

	static final private HypertextualLogger log = HypertextualLogger.getLogger();
	
	/**
	 * D: a vector of scores for rows
	 */
	private DoubleMatrix resultingD;
	
	/**
	 * C: a vector of scores for cols
	 */
	private DoubleMatrix resultingC;

	final private List<R> rows; 

	final private List<C> cols;	
	
	final private DoubleMatrix evidenceMatrix;
	
	public DSCDIterator(List<R> rows, List<C> cols, DSCDScorer<R,C> s) {
		this.rows = rows;
		this.cols = cols;
		DSCDMatrixInitializer<R,C> emi = new DSCDMatrixInitializer<>(this.rows, this.cols, s);
		this.evidenceMatrix = emi.initMatrix();
	}
	
	public void iterate() {
		final int n = this.cols.size();
		log.newPage("DSCD Iterations");

		DoubleMatrix C = initC0(n);
		DoubleMatrix D = null;

		int i = 0;
		DoubleMatrix previous = new DoubleMatrix();
		while (!previous.equals(C)) {
			previous = new DoubleMatrix(C.data);

			D = evidenceMatrix.mmul(C);
			D = normalize(D);

			C = evidenceMatrix.transpose().mmul(D);
			C = normalize(C);

			i++;
		}
		log.endPage();
		
		log.trace("...total iterations: " + i);
		this.resultingC = C;
		this.resultingD = D;
	}

	private DoubleMatrix initC0(int size) {
		final double[] data = new double[size];
		for (int i=0; i<size; i++) {
			data[i] = 0.5d;
		}
		final DoubleMatrix C = new DoubleMatrix(data);
		return C;
	}

	private DoubleMatrix normalize(DoubleMatrix m) {
		double normalizer = m.normmax();

		if (normalizer==0d) {
			throw new IllegalArgumentException("cannot normalize all-zero matrix");
		}

		return m.div(normalizer);
	}

	public DoubleMatrix getD() {
		return this.resultingD;
	}

	public DoubleMatrix getC() {
		return this.resultingC;
	}
	
}
