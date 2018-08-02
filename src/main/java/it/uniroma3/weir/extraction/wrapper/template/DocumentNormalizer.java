package it.uniroma3.weir.extraction.wrapper.template;

import static it.uniroma3.weir.extraction.wrapper.template.TemplateMarker.INVARIANT_MARKER;
import static it.uniroma3.weir.extraction.wrapper.template.TemplateMarker.VARIANT_MARKER;
import static it.uniroma3.weir.vector.value.ExtractedValue.OCCURRENCE_MARK;
import static it.uniroma3.token.dom.node.DOMNode.DOMTOKEN_HANDLER_KEY;
import static it.uniroma3.weir.extraction.wrapper.StepFactories.USERDATA_KEY_DOWNWARDSTEPS;
import static it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates.isText;
import it.uniroma3.dom.visitor.DOMVisitor;
import it.uniroma3.dom.visitor.DOMVisitorListenerAdapter;
import it.uniroma3.weir.extraction.wrapper.RelativeGenerator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Since the evaluation of XPath expression requires as input normalized DOM
 * tree documents, (otherwise unpredictable behaviors occurs: check {@link 
 * it.uniroma3.weir.extraction.wrapper.pcdata.XPathAssumesNormalizedDocumentsSpike})
 * but the normalization procedure implemented by the method 
 * {@link Document.#normalize()} is not required to preserve the DOM user-data 
 * annotations (see {@link Node.#getUserData(String)}; used for instance by 
 * {@link RelativeGenerator}) of a sequence of contiguous sibling 
 * text leaves, we leverage the fact that the standard distribution provides
 * and implementation that preserves the annotation of the first text node
 * of each sequence: this class merges all the user-data annotations and
 * save them on the first node of each sequence of contiguous sibling texts
 */
public class DocumentNormalizer  extends DOMVisitorListenerAdapter {

	final private String[] userdataKeys;

	public DocumentNormalizer() {
		this(INVARIANT_MARKER,VARIANT_MARKER,
//			 OCCURRENCE_MARK,
//			 DOMTOKEN_HANDLER_KEY,
			 USERDATA_KEY_DOWNWARDSTEPS);
	}
	
	/**
	 * @param keys the DOM user-data to merge.
	 */
	public DocumentNormalizer(String...keys) {
		this.visitor = new DOMVisitor();
		this.visitor.setListener(this);
		this.setDOMVisitor(this.visitor);
		this.userdataKeys = keys;
	}

	public void normalize(Document doc) {
		this.visitor.visit(doc);
		doc.normalize();
	}

	@Override
	public void text(Text text) {
		if (isFirstOfAseriesOfContiguousTextSiblings(text)) {
			mergeDOMuserDatas(text);
		}
	}

	private boolean isFirstOfAseriesOfContiguousTextSiblings(Text text) {
		final Node prev = text.getPreviousSibling();
		return ( prev==null || !isText(prev) );
	}

	private void mergeDOMuserDatas(Text first) {
		/* accumulate the nodes and their annotations to merge */
		Map<String,List<Object>> acc = mergeDOMuserDatas(null, first, first);
		Node current = first.getNextSibling();
		while (current!=null && isText(current) ) {
			acc = mergeDOMuserDatas(acc, current, first);
			current = current.getNextSibling();
		}
		setUserdata(first, acc);
	}

	private Map<String, List<Object>> mergeDOMuserDatas(final Map<String, List<Object>> acc, Node current, Node first) {
		Map<String, List<Object>> result = acc;
		// for each user-data-key to preserve, it accumulates
		// several user-data-values into a single list of values
		for(String key : this.userdataKeys) {
			final Object value = current.getUserData(key);
			if (value!=null) {
				/* remove once used but keep track of the father */
				current.setUserData(key, null, null);
				if (current!=first)
					current.setUserData(key, first, null);
				if (result==null) result = new HashMap<>();

				List<Object> values = result.get(key);
				if (values==null) {
					values = new LinkedList<>();
					result.put(key, values);
				}
				// keep it flat
				if (value instanceof List)
					values.addAll((List<?>)value);
				else values.add(value);
			}
		}
		return result;
	}

	private void setUserdata(Text text, final Map<String, List<Object>> acc) {
		if (acc==null) return;
		for(String key : this.userdataKeys) {
			final Object value = acc.get(key);
			if (value!=null)
				text.setUserData(key, value, null);
		}
	}

}
