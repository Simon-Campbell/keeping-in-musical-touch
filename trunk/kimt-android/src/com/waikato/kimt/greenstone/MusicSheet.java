package com.waikato.kimt.greenstone;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.waikato.kimt.util.XMLUtilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class MusicSheet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String	sheetID;
	private String 	fullAddress;

	private String title;
	private String author;
	
	private int	pages;
	private int currentPage;
	
	private Bitmap bitmap;
	private boolean isFetchingData = false;
	
	public MusicSheet(GreenstoneMusicLibrary owner, String sheetID) {
		this(owner, sheetID, false);
	}
	
	public MusicSheet(GreenstoneMusicLibrary owner, String sheetID, Boolean download) {
		this.sheetID		= sheetID;
		this.fullAddress	= owner.getUri() + sheetID + "&o=xml";
		
		this.currentPage= 0;
		this.pages		= 1337;
		
		if (download) {
			// We're fetching the data so we'll set this to true.
			this.isFetchingData = true;
			
			// Start actually fetching the data
			new AsyncGreenstoneXMLDownload().execute(this.fullAddress);
		}
	}
	
	public String toString() {
		return "MusicSheet" + this.title + " " + this.author + " " + this.currentPage + "/" + this.pages;
	}
	
	public String getFullAddress() {
		return this.fullAddress;
	}
	
	public String getTitle() {
		return this.title;
	}

	public String getAuthor() {
		return this.author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSheetID() {
		return this.sheetID;
	}
	
	public Bitmap getBitmap() {
		if (bitmap == null) {
			// If not fetching data then try fetching the bitmap in
			// a task
			if (!this.isFetchingData) {
				new AsyncGreenstoneXMLDownload().execute(this.fullAddress);
			}
			
			return null;
		} else {
			return bitmap;
		}
	}
	
	private void setDataFromGreenstoneXML(String uri) {
	    try {
	        DocumentBuilderFactory dbf	= DocumentBuilderFactory.newInstance();
	        DocumentBuilder db			= dbf.newDocumentBuilder();
	        
	        InputSource is				= new InputSource((new URL(uri)).openStream());
	        Document doc				= db.parse(is);
	        
	        NodeList nodes				= doc.getElementsByTagName("page");
	        NodeList documentElems		= null;
	        
	        String documentTitle		= null;
	        String documentAssocDir		= null;
	        
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("pageResponse");
	        
	        // We wish to save the nodelist that contains <documentNode /> and <metadataList />
	        documentElems = nodes = ((Element) nodes.item(0)).getElementsByTagName("document");

	        // Drill down and get the meta data for this music sheet ..
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("documentNode");
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("metadataList");

	        // Iterate the elements to find the metadata of the music sheet
	        for (int i = 0; i < nodes.getLength(); i++) {
	           Element	element = (Element) nodes.item(i);
	           NodeList	metaInfos = element.getElementsByTagName("metadata");
	           
	           for (int j = 0; j < metaInfos.getLength(); j++) {
	        	   Element metaInfo	= (Element) metaInfos.item(j);
    	           String name		= metaInfo.getAttribute("name");
	        	 
    	           if (name.compareTo("mp.title") == 0) {
    	        	   this.title = XMLUtilities.getCharacterDataFromElement(metaInfo);   
    	           } else if (name.compareTo("Title") == 0) {
    	        	   // Set this document title to use so that the image can be
    	        	   // retrieved
    	        	   documentTitle = XMLUtilities.getCharacterDataFromElement(metaInfo);
    	        	   
    	        	   // If the document title hasn't already been set then we'll 
    	        	   // set it to this one, however "mp.title" is preferred.
    	        	   if (this.title == null) {
    	        		   this.title = documentTitle;
    	        	   }
    	        	   
    	           } else if (name.compareTo("mp.composer") == 0 || (title == null && name.compareTo("Composer") == 0)) {
    	        	   this.author= XMLUtilities.getCharacterDataFromElement(metaInfo);
    	           }
	           }
	        }


	        // If the author or title has not been set then
	        // we'll set them to a default value.
	        if (this.author == null) {
	        	this.author	= "N/A";
	        }
	        if (this.title == null) {
	        	this.title	= "N/A";
	        }
	        
	        // Iterate the document meta data to help find the associated PNG files
	        // then download it e.g.
	        //<metadataList>
	        //	<metadata name="assocfilepath">HASH4f41.dir</metadata>
	        //</metadataList>
	        documentElems = ((Element) documentElems.item(1)).getElementsByTagName("metadataList");
	        
	        for (int i = 0; i < documentElems.getLength(); i++) {
	        	Element	element	= (Element) documentElems.item(i);
	        	String	name	= element.getAttribute("name");
	        	
	        	// Check if the metadata name is equal to "assocfilepath"
	        	if (name.compareTo("assocfilepath") == 0) {
	        		documentAssocDir = XMLUtilities.getCharacterDataFromElement(element);
	        	}
	        }
	        
	        // The required data has been found. Now the image can be downloaded
	        // and the bitmap downloaded.
		    int sheetPage = 0;
		    
	        this.bitmap = BitmapFactory.decodeStream(
	        	new URL(
	        			"http://www.nzdl.org/greenstone3-nema/cgi-bin/image-server.pl?a=fit-screen&c=musical-touch&site=localsite&pageWidth=426&pageHeight=603&assocDir=" + 
	        					documentAssocDir+ 
	        					"&assocFile="	+
	        					documentTitle	+
	        					"-"				+
	        					Integer.toString(sheetPage) + 
	        					".png"
	        		).openStream()
	        	);

	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }

	}
	
	/**
	 * This region of code takes care of the
	 */
	private List<MetaDataDownloadListener>
		registeredMetaDataListeners = new ArrayList<MetaDataDownloadListener>();
	
	public interface MetaDataDownloadListener {
		public void onMetaDataDownloaded(MusicSheet ms);
	}
	
	public void setOnSheetMetaDataUpdateListener(MetaDataDownloadListener mddl) {
		registeredMetaDataListeners.add(mddl);
	}
	
	protected void notifyMetaDataDownloaded() {
		for (MetaDataDownloadListener mddl : registeredMetaDataListeners)
			mddl.onMetaDataDownloaded(this);
	}
	
	//using class Async to do the work
	private class AsyncGreenstoneXMLDownload extends AsyncTask<String, Void, Void> {
		//get the data in the background. This means that the thread with UI will not be blocked 
		protected Void doInBackground(String... address) {
			setDataFromGreenstoneXML(address[0]); return null;
		}

		//the result of the above method will call this method and pass in the result. 
		//I belive it is ok for this method to update the UI (only the main UI thread (...DisplayDataActivity thread) should be updating UI elements)
		protected void onPostExecute(Void result) {
			// The data is no-longer being fetched
			isFetchingData = false;
			
			// Notify anybody who is subscribed to the MusicSheet that
			// the meta-data has been downloaded.
			notifyMetaDataDownloaded();
		}
	}
}
