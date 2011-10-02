package com.waikato.kimt;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.os.AsyncTask;
import android.util.Log;


public class MusicSheet {
	private String	sheetID;
	private String 	fullAddress;

	private String title;
	private String author;
	
	private int	pages;
	private int currentPage;
	
	public MusicSheet(GreenstoneMusicLibrary owner, String sheetID) {
		this(owner, sheetID, false);
	}
	
	public MusicSheet(GreenstoneMusicLibrary owner, String sheetID, Boolean download) {
		this.sheetID		= sheetID;
		this.fullAddress	= owner.getUri() + sheetID + "&o=xml";
		
		this.currentPage= 0;
		this.pages		= 1337;
		
		if (download)
			new AsyncGreenstoneXMLDownload().execute(this.fullAddress);	
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
	
	private void setDataFromGreenstoneXML(String uri) {
	    try {
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource((new URL(uri)).openStream());
	        Document doc = db.parse(is);

	        Log.v("Unbugging", uri);
	        NodeList nodes = doc.getElementsByTagName("page");

	        Log.v("Unbugging", "Test 2");
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("pageResponse");
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("document");
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("documentNode");
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("metadataList");

	        Log.v("Unbugging", "Test 3");
	        // Iterate the parameter list
	        for (int i = 0; i < nodes.getLength(); i++) {
	        	Log.v("Unbugging", Integer.toString(i));
	           Element	element = (Element) nodes.item(i);
	           NodeList	metaInfos = element.getElementsByTagName("metadata");
	           
	           Log.v("Unbugging", element.toString() + " | " + XMLUtilities.getCharacterDataFromElement(element));
	           
	           for (int j = 0; j < metaInfos.getLength(); j++) {
	        	   Element metaInfo	= (Element) metaInfos.item(j);
    	           String name		= metaInfo.getAttribute("name");
	        	   
    	           Log.v("Unbugging", name + " | " + XMLUtilities.getCharacterDataFromElement(metaInfo));
    	           
    	           if (name.compareTo("mp.title") == 0 || (title == null && name.compareTo("Title") == 0)) {
    	        	   this.title = XMLUtilities.getCharacterDataFromElement(metaInfo);   
    	           } else if (name.compareTo("mp.composer") == 0 || (title == null && name.compareTo("Composer") == 0)) {
    	        	   this.author= XMLUtilities.getCharacterDataFromElement(metaInfo);
    	           } else {
    	           }
	           }
	        }
	        
	        String
	        	defaultNoMetaString = "N/A";
	        
	        if (this.author == null) {
	        	this.author	= defaultNoMetaString;
	        }
	        if (this.title == null) {
	        	this.title	= defaultNoMetaString;
	        }

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
			// Notify anybody who is subscribed to the MusicSheet that
			// the meta-data has been downloaded.
			notifyMetaDataDownloaded();
		}
	}
}
