package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.weir.fixture.WebpageFixture.webpage;

import org.w3c.dom.Document;

public class DocumentFixtures {
	
	static public Document document(String content) {
		return webpage(content).getDocument();
	}
	
	static public String _HTML_TREE_(String inner) {
		return "<HTML><BODY>" + inner + "</BODY></HTML>";
	}

	static public String _HTML_TABLE_(String inner) {
		return _HTML_TREE_("<TABLE><TBODY>"+inner+"</TBODY></TABLE>");
	}

	static final String TEXT_AND_WHITESPACES = _HTML_TREE_("" +
			"<DIV id='001'/>" +
			"<DIV id='002'>" +
			/**/"text" +
			"</DIV>" +
			"<DIV id='003'>" +
			/**/"   " +
			"</DIV>");

	static final String TEXTS_OF_DISTINCT_PATHS = _HTML_TREE_("" +
			"<DIV id='001'>" +
			/**/"text_1" +
			"</DIV>" +
			"<DIV>" +
			/**/"<SPAN id='002'>" +
			/*   */"text_2" +
			/**/"</SPAN>" +
			"</DIV>");

	public static final String _PIVOTED_(String pivot, String text) {
		return _HTML_TREE_( "<P>" + pivot +	"</P>" + text );
	}

	static final String UP_2_VALUES(String t1, String t2) { 
		return _HTML_TREE_( "" +
				 t1 +
				"<BR/>" +
				 t2 +
				"<P>" +
				/*  */ "pivot" +
				"</P>");
	}

	static final String DOWN_2_VALUES(String t1, String t2) { 
		return _HTML_TREE_( "" +
				 "pivot" +
				"<P>" +
				/*  */ t1 +
				"</P>" +
				"<P>" +
				/*  */ t2 +
				"</P>");
	}

	static final String SIBLING_DOWN(String t1, String t2) { 
		return _HTML_TREE_("" +
				 "pivot" +
				"<P>" +
				/*  */ t1 +
				/*  */"<BR/>" +
				/*  */ t2 +
				"</P>");
	}

	static final String TEXT_DX(String text) { 
		return N_PARS("pivot", text);
	}
	
	static final String TEXT_SX(String text) { 
		return N_PARS(text, "pivot");
	}

	static final String MULTI_SIBLING_SX(String t1, String t2) { 
		return N_PARS(t1+"<BR/>"+t2, "pivot");
	}

	static final String MULTI_SIBLING_DX(String t1, String t2) {
		return N_PARS("pivot", t1+"<BR/>"+t2);
	}

	static final String SURROUNDED_PIVOT(String... t) { 
		return _HTML_TREE_("" +
				 t[0] +
				"<P>" +
				/*  */ t[1] +
				"</P>" +
				"<P>" +
				/*  */ "pivot" +
				/*  */"<B>" +
				/*      */ t[2] +
				/*  */"</B>" +
				"</P>" +
				"<P>" +
				/*  */ t[3] +
				"</P>");
	}

	static final String DOUBLE_PIVOT(String t) { 
		return _HTML_TREE_( "" +
				"pivot_1" +
				"<P>" +
				/*  */ t +
				"</P>" +
				"<P>" +
				/*  */ "pivot_2" +
				"</P>");
	}

	static final String DOUBLE_PIVOT_DOUBLE_TEXT(String t1, String t2) { 
		return _HTML_TREE_( "" +
				"pivot_1" +
				"<P>" +
				/*  */ t1 +
				"</P>" +
				"<P>" +
				/*  */ "pivot_2" +
				/*  */ "<B>" +
				/*      */ t2 +
				/*  */ "</B>" +
				"</P>");
	}

	static final String _DOUBLE_PIVOT_MULTI_(String... t) { 
		return _HTML_TREE_( "" +
				"pivot_1" +
				"<P>" +
				/*  */ t[0] +
				/*  */"<BR/>" +
				/*  */ t[1] +
				"</P>" +
				"<P>" +
				/*  */ "pivot_2" +
				/*  */"<B>" +
				/*      */ t[2] +
				/*      */"<BR/>" +
				/*      */ t[3] +
				/*  */"</B>" +
				"</P>");
	}

	static final String INTRA_PCDATA_PIVOT(String leftOfPivot, String rightOfPivot) {
		return _HTML_TREE_( "" +
				"<P>" +
				/**/leftOfPivot+"pivot"+rightOfPivot +
				"</P>");
	}

	static final String TEXT_UP(String t0) { 
		return _HTML_TREE_("" +
				"<P>" +
				 t0 +
				/*  */"<B>" +
				/*      */"pivot" +
				/*  */"</B>" +
				/*  */"<BR/>" +
				"</P>");
	}


	static final String TEXT_DOWN(String t0) { 
		return _HTML_TREE_("" +
				 "pivot" +
				"<P>" +
				/*  */ t0 +
				"</P>" +
				"<HR/>");
	}

	static final String MULTI_SX(String t1, String t2) {
		return N_PARS(t1,t2,"pivot");
	}
	static final String MULTI_DX(String t1, String t2) { 
		return N_PARS("pivot",t1,t2);
	}

	static final private String N_PARS(String...pars) { 
		StringBuilder result = new StringBuilder();
		for(String p : pars)
			result.append(
					"<P>" +
					/**/ p +
					"</P>"
			);
		return _HTML_TREE_(result.toString());
	}

	static final String LONG_PATHS(String t0, String t1) { 
		return _HTML_TREE_("" +
				/**/"<C><F><L><N>" +
				/*    */ t0 +
				/**/"</N></L></F></C>" +
				/**/"<D><G><M><O>pivot</O></M></G></D>" +
				/**/"<P><H/><I><H/><I>"+
				/*    */ t1+
				/**/"</I></I></P>"
				);
	}

	static String pivotXPath(String name) {
		/* e.g.: //P[contains(text(),'pivot')]" */
		return pivotXPath(name,"pivot");
	}

	static  String pivotXPath(String pivotTagName, String pivotText) {
		return "//"+pivotTagName.toUpperCase()+"[contains(text(),'"+pivotText+"')]";
	}

}
