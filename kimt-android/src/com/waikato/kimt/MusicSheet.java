package com.waikato.kimt;

import java.io.StringReader;
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
	private String					sheetID;
	private String 					fullAddress;
	private GreenstoneMusicLibrary	owner;

	private MusicSheet				async;

	private String title;
	private String author;
	
	private int	pages;
	private int currentPage;
	
	private boolean isSynced = false;
	
	public MusicSheet(GreenstoneMusicLibrary owner, String sheetID) {
		this.sheetID		= sheetID;
		this.fullAddress	= owner.getUri() + sheetID + "&o=xml";
		
		this.currentPage= 0;
		this.pages		= 1337;
	
		new AsyncGreenstoneXMLDownload().execute(this.fullAddress);
	}
	
	public String toString() {
		return "MusicSheet" + this.title + " " + this.currentPage / this.pages;
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
	
	private void setTitle(String title) {
		this.title = title;
	}
	
	private void setDataFromGreenstoneXML(String xmlString) {
	    try {
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xmlString));
	        
	        Document doc = db.parse(is);
	        NodeList nodes = doc.getElementsByTagName("page");
          
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("pageResponse");
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("document");
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("documentNode");
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("metadataList");
	        
	        Log.v("onMetaDataUpdate", "Before iterating parameters ..");
	        Log.v("onMetaDataUpdate", nodes.item(0).toString());
	        
	        // Iterate the parameter list
	        for (int i = 0; i < nodes.getLength(); i++) {
	           Element	element = (Element) nodes.item(i);
	           NodeList	metaInfos = element.getElementsByTagName("metadata");
	           
	           for (int j = 0; j < metaInfos.getLength(); j++) {
	        	   Element metaInfo	= (Element) metaInfos.item(j);
    	           String name		= metaInfo.getAttribute("name");
    	           
    	           if (name.compareTo("mp.title") == 0) {
    	        	   setTitle(GreenstoneUtilities.getCharacterDataFromElement(metaInfo));   
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

			// Notify anybody who is subscribed to the MusicSheet that
			// the meta-data has been downloaded.
			this.notifyMetaDataDownloaded();
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
	private class AsyncGreenstoneXMLDownload extends AsyncTask<String, Void, String> {
		//get the data in the background. This means that the thread with UI will not be blocked 
		protected String doInBackground(String... address) {
			return Network.getData(address[0]);		
		}

		//the result of the above method will call this method and pass in the result. 
		//I belive it is ok for this method to update the UI (only the main UI thread (...DisplayDataActivity thread) should be updating UI elements)
		protected void onPostExecute(String result) {
			setDataFromGreenstoneXML(result);
		}
	}
}
