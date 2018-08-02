package it.uniroma3.weir.evaluation.linking;

import static jxl.write.WritableFont.ARIAL;
import static jxl.write.WritableFont.BOLD;
import static jxl.format.Alignment.CENTRE;
import it.uniroma3.weir.configuration.WeirConfig;
import it.uniroma3.weir.evaluation.PRF;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jxl.JXLException;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;
/**
 * Output an excel spreadsheet file reporting an evaluation of top-k linkages.
 * @see {@link it.uniroma3.weir.evaluation.RepositoryEvaluator<P, T>}
 * 
 *   Serve a graficare F del linkage (in realtà F=P=R) ed anche N,
 *   ovvero il numero di coppie non ordinate di siti, in funzione
 *   dell'"overlap", ovvero il numero minimo di istanze comuni.
 * 
 */
public class TopKXLSExporter {

	static final private int MAX_NUMBER_OF_LINKAGES = 100;

	static final private String SHEETNAME = "top-k linkage";
	
	static final private WritableCellFormat HEADER_FORMAT = 
			new WritableCellFormat(new WritableFont(ARIAL, 11, BOLD));

	private boolean creatingOrRecycling; // true iff we're creating a new .xls file
	                                     // false iff we're recycling a file
	
	public void export(final List<PRF> result) throws IOException, JXLException {
		final WritableWorkbook wworkbook = makeJXLworkbook();
		final WritableSheet sheet = makeJXLsheet(wworkbook);
		exportData(sheet, result);

		wworkbook.write();
		wworkbook.close();
	}
	
	private WritableWorkbook makeJXLworkbook() throws IOException, JXLException {
		WritableWorkbook wworkbook = null;
		final File xlsfile = getOutputFile();
		/* copy everything from the old file if any 
		 * (it could have been edited to add charts, for example)
		 */
		if (xlsfile.exists()) {
			this.creatingOrRecycling = false;
			Workbook workbook =  Workbook.getWorkbook(xlsfile);
			wworkbook = Workbook.createWorkbook(xlsfile, workbook);
		} else {
			this.creatingOrRecycling = true;
			wworkbook = Workbook.createWorkbook(xlsfile); // make a new one
		}
		return wworkbook;
	}

	private WritableSheet makeJXLsheet(final WritableWorkbook workbook)
			throws WriteException, RowsExceededException {
		WritableSheet sheet = null;
		if (this.creatingOrRecycling) {
			// create sheet ex-novo
			sheet = workbook.createSheet(SHEETNAME, 0) ;
			// add header labels, specifying content and format
			HEADER_FORMAT.setAlignment(CENTRE);
			sheet.addCell(new Label(0, 0, "k", HEADER_FORMAT));
			sheet.addCell(new Label(1, 0, "F", HEADER_FORMAT));
			sheet.addCell(new Label(2, 0, "N", HEADER_FORMAT));
		} else sheet = workbook.getSheet(SHEETNAME); // get an existing sheet
		return sheet;
	}

	/**
	 * @param workbook
	 * @param result
	 * @throws JXLException
	 */
	private void exportData(final WritableSheet sheet, List<PRF> result) throws JXLException {
		for(int i=0; i<result.size() && i<MAX_NUMBER_OF_LINKAGES; i++) {
			final int row = i+1;
			final PRF prf = result.get(i);
			final double fMeasure   = prf.getFMeasure();
			final int n = prf.getN();
			sheet.addCell(new Number(0, row, row));
			sheet.addCell(new Number(1, row, fMeasure));
			sheet.addCell(new Number(2, row, n));
		}
	}
	
	private File getOutputFile() {
		/* TODO filename resolution logics should be centralized into 'Experiment' class */
		final String dataset = WeirConfig.getCurrentExperiment().getDataset().getName();
		final String domain  = WeirConfig.getCurrentExperiment().getDomain().getName();
		return new File("./top-k-linkage" + "_" + dataset + "-" + domain+".xls");
	}

}
