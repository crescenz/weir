package it.uniroma3.weir.extraction.wrapper;

import static it.uniroma3.hlog.HypertextualLogger.getLogger;
import static it.uniroma3.weir.extraction.wrapper.template.TemplateMarker.isTemplateNode;
import static it.uniroma3.weir.extraction.wrapper.template.TemplatePredicates.*;
import it.uniroma3.hlog.HypertextualLogger;
import it.uniroma3.weir.extraction.rule.ExtractionRuleClass.RuleGenerator;
import it.uniroma3.weir.model.Webpage;

import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class PositionalGenerator implements RuleGenerator {

	static final private HypertextualLogger log = getLogger();

	final private PositionalXPathBuilder builder;

	public PositionalGenerator() {
		this.builder = new PositionalXPathBuilder();
	}

	public Set<String> inferRules(List<Webpage> samples) {
		final Set<String> xpaths = new LinkedHashSet<>();
		int count = 0;
		for (Webpage page : samples) {
			log.trace();
			log.newPage();
			final Set<String> fromThisDoc = getXPathsFromDocument(page.getDocument());
			fromThisDoc.removeAll(xpaths);
			xpaths.addAll(fromThisDoc);
			log.page("<em>new</em> XPath generated: " + fromThisDoc.size(),fromThisDoc);
			log.endPage("processing doc "+ count+" - total number of XPath generated so far: " + xpaths.size());
			log.trace();
			count++;
		}

		return xpaths;
	}

	private Set<String> getXPathsFromDocument(Document doc) {
		final Set<String> xpathFound = new LinkedHashSet<>();
		final Node htmlNode = doc.getElementsByTagName("HTML").item(0);

		if (htmlNode==null) {
			return xpathFound;
		}

		final List<Text> textualLeaves = new LinkedList<>();
		final List<Text> unsuitableLeaves = new LinkedList<>();
		chooseTargetTextualValues(textualLeaves, unsuitableLeaves, htmlNode);
		log.page(textualLeaves.size()+" suitable text values found",textualLeaves);
		log.page(unsuitableLeaves.size()+" unsuitable text values found",unsuitableLeaves);

		for (Text leaf : textualLeaves) {
			if (isRootedOnAtemplateNodeIDbased(leaf)) {
				final String xpath = this.builder.getXPath(leaf);
				xpathFound.add(xpath);
			}
		}
		return xpathFound;
	}

	private boolean isRootedOnAtemplateNodeIDbased(Text leaf) {
		Node current = leaf.getParentNode();
		while (current!=null) {
			if (isNodeWithId(current) && isTemplateNode(current))
				return true;
			current = current.getParentNode();
		}
		return false;
	}


	private void chooseTargetTextualValues(List<Text> target, List<Text> unsuitable, Node node) {
		if (isText(node)) {		
			final Text text = (Text) node;		
			if (isFirstOfAsequenceOfSuitableLeafValue(text)) {
				target.add(text);
				return;
			}
			if (isNonEmptyTextualNodeOfAppropriateLength(text)) {
				unsuitable.add(text);
				return;
			}
		}
		final NodeList children = node.getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			final Node child = children.item(i);
			chooseTargetTextualValues(target, unsuitable, child);
		}
	}

	static private boolean isFirstOfAsequenceOfSuitableLeafValue(Text text) {
		final Node prev = text.getPreviousSibling();
		return isSuitableLeafValue(text) && ( prev==null || !isSuitableLeafValue(prev) );
	}

}