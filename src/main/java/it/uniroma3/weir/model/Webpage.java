package it.uniroma3.weir.model;

//import org.w3c.dom.ls.DOMImplementationLS;
//import org.w3c.dom.ls.LSSerializer;
import static it.uniroma3.hlog.HypertextualUtils.linkTo;
import it.uniroma3.hlog.render.Renderable;
import it.uniroma3.weir.linking.entity.Entity;

import java.io.Serializable;
import java.net.URI;

import org.w3c.dom.Document;
/**
 * 
 * A web page from a {@link Website} of a vertical {@link Domain} in a {@link Dataset}
 */
public class Webpage extends WeirId 
                     implements Serializable, Renderable {

	static final private long serialVersionUID = 5551298054345382885L;
	
	private Website website;
		
	private URI uri;
	
	// An entity collect all the information used for 
	private Entity entity; 	// page-linkage over several sites
	
	/* This is the index of this page over the collection of all 
	 * the pages from its site
	 */
	private int index;
	
	/* When this page has been selected as page overlapping 
	 * with at least another site, this is the index of this 
	 * page over the collection of all the *overlapping* pages 
	 * from its site; -1 otherwise
	 */
	private int overlapIndex;       // <--- ambiguous
	
	private transient Document doc; // full DOM-representation

	//FIXME soft-id are dangerous: they might be not unique within a website
	public Webpage(String identifier) {
		this(identifier,URI.create(identifier + ".html"));
	}
	
	public Webpage(String identifier, URI uri) {
		super(identifier);
		this.uri = uri;
		this.overlapIndex = -1;
	}

	public URI getURI() {
		return this.uri;
	}
	

	@Override
	public String getWeirId() {
		final int siteId = this.getWebsite().getIndex();
		return this.getId()+"<sup>" + siteId + "</sup>";
	}
	/**
	 * 
	 * @param website
	 * @param pageIndex the index of this page within the list of pages in its site
	 */
	public void setWebsite(Website website, int pageIndex) {
		this.website = website;
		this.index   = pageIndex;
	}

	public Website getWebsite() {
		return this.website;
	}
	
	/**
	 * @return true iff it has been selected as an overlapping page
	 */
	public boolean isOverlapping() {
		return ( this.overlapIndex != -1 ) ;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public int getOverlapIndex() {
		return this.overlapIndex;
	}
	
	public void setOverlapIndex(int index) {
		this.overlapIndex = index;
	}

	public Entity getEntity() {
		return this.entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Document getDocument() {
		return this.doc;
	}
	
	public void loadDocument() {
		if (this.doc==null)
			this.doc = WebFetcher.getInstance().fetchDocument(this);
	}
	
	/**
	 * Get rid of the heavy {@linkplain org.w3c.dom.Document} representation
	 */
	public void releaseDocument() {
		this.doc = null;
	}

//	public String getContent() {
//		DOMImplementationLS domImplLS = (DOMImplementationLS) this.doc.getImplementation();
//		LSSerializer serializer = domImplLS.createLSSerializer();
//		return serializer.writeToString(this.doc);
//	}
	
	/**
	 * 
	 * @return the file name associated with this page, useful for pages
	 *  in the local file-system, e.g., for locally stored datasets
	 */
	public String getName() {
		final String pathSteps[] = this.getURI().getPath().split("/");
		return pathSteps[pathSteps.length-1]; //last step is the filename
	}

	@Override
	public String toHTMLstring() {
		return overlapIndex() + 
				linkTo(this.getURI()).withAnchor(this.getId()+siteIndex());
	}

	@Override
	public int hashCode() {
		return super.hashCode()+this.getWebsite().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) return false;
		final Webpage that = (Webpage)o;
		return this.getWebsite().equals(that.getWebsite());
	}
	
	@Override
	public String toString() {
		return overlapIndex() + "'" + this.getId() + "'"+siteIndex();
	}

	private String overlapIndex() {
		return this.overlapIndex!=-1 ? this.overlapIndex + "-": "";
	}
	
	private String siteIndex() {
		return "<sub>"+getWebsite().getIndex()+"</sub>";
	}

}