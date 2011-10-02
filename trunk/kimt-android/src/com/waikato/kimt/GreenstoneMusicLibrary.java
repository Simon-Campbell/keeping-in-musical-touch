package com.waikato.kimt;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.os.AsyncTask;
import android.util.Log;

public class GreenstoneMusicLibrary implements MusicLibrary {
	private MusicSheet				current;
	private LinkedList<MusicSheet>	cache;
	
	private String				trackUri;
	private DigitalLibrarySync	dls;
	
	private String[]			trackList;
	
	/**
	 * Will connect to the specified Greenstone music library
	 * at the specified URI.
	 * @param uri
	 * 	The URI to connect to. It's expected to be a Greenstone3 server.
	 */
	public GreenstoneMusicLibrary(String uri) {
		this.trackUri = uri;
	}
	
	@Override
	public void connect() {
		new DownloadSheetListTask().execute(this);
	}

	public String getUri() {
		return trackUri;
	}
	
	public DigitalLibrarySync getSyncer() {
		return null;
	}
	
	@Override
	public void setCurrentSheet(String sheetID) {
		this.current = new MusicSheet(this, sheetID);
		this.current.setOnSheetMetaDataUpdateListener(new MusicSheet.MetaDataDownloadListener() {
			
			@Override
			public void onMetaDataDownloaded(MusicSheet ms) {
				notifySheetMetaDataUpdate();
			}
		});
	}

	@Override
	public MusicSheet getCurrentSheet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MusicSheet find(String searchTerm, SearchMode sm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MusicView getCurrentView() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private List<SyncedSheetUpdateListener>
		registeredMetaUpdateListeners = new ArrayList<SyncedSheetUpdateListener>();
	
	private List<SyncedLibraryBrowserUpdateListener>
		registeredLibraryDownloadListeners = new ArrayList<SyncedLibraryBrowserUpdateListener>();
	
	public void setMetaDataUpdateListener(SyncedSheetUpdateListener syncedSheetUpdateListener) {
		registeredMetaUpdateListeners.add(syncedSheetUpdateListener);
	}
	
	public void setLibraryBrowserUpdateListener(SyncedLibraryBrowserUpdateListener slbul) {
		registeredLibraryDownloadListeners.add(slbul);
	}
	
	private void notifySheetMetaDataUpdate() {
		for (SyncedSheetUpdateListener g : registeredMetaUpdateListeners) {
			g.onMetaDataUpdate(current);
		}
	}
	
	private void notifyLibraryDownloaded() {
		for (SyncedLibraryBrowserUpdateListener g : registeredLibraryDownloadListeners) {
			g.onLibraryDownloaded(this);
		}
	}
	
	public interface SyncedSheetUpdateListener {
		public void onMetaDataUpdate(MusicSheet m);
	}
	
	public interface SyncedLibraryBrowserUpdateListener {
		public void onLibraryDownloaded(GreenstoneMusicLibrary gml);
	}
	
	private void setCacheFromGreenstoneXML(String xmlString) {
	    try {
	    	cache = new LinkedList<MusicSheet>();
	    	
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xmlString));
	        
	        Document doc = db.parse(is);
	        NodeList nodes = doc.getElementsByTagName("page");
          
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("pageResponse");
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("documentNodeList");
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("documentNode");
	        
	        // Iterate through all the documents contained in the search 
	        // results ..
	        for (int docID = 0; docID < nodes.getLength(); docID++) {
	        	Element
	        		e  = (Element) nodes.item(docID);
	        	
	        	MusicSheet
	        		ms = new MusicSheet(this, e.getAttribute("nodeID"));
	        	
	        	NodeList
	        		metadataListNodes = e.getElementsByTagName("documentNode");
		        
	        	for (int i = 0; i < metadataListNodes.getLength(); i++) {
	        		NodeList
	        			metadataNodes = ((Element) metadataListNodes.item(i)).getElementsByTagName("metadata");
	        		
	        		for (int j = 0; j < metadataNodes.getLength(); j++) {
	        			Element
	        				finalElement = (Element) metadataNodes.item(j);
	        			
	        			String
	        				metaType = finalElement.getAttribute("name");
	        			
	        			if (metaType.compareTo("mp.title") == 0) {
	        				ms.setAuthor(GreenstoneUtilities.getCharacterDataFromElement(finalElement));
	        			} else if (metaType.compareTo("mp.composer") == 0) {
	        				ms.setTitle(GreenstoneUtilities.getCharacterDataFromElement(finalElement));
	        			}
	        		}
	        	}
	        	Log.v("Debugging", ms.getTitle() + " " + ms.getAuthor());
	        	cache.add(ms);
	        }
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }

	}
	
	private class DownloadSheetListTask extends AsyncTask<GreenstoneMusicLibrary, Long, Boolean> {

		@Override
		protected Boolean doInBackground(GreenstoneMusicLibrary... params) {
			String
				xml = Network.getData("http://www.nzdl.org/greenstone3-nema/dev?a=q&sa=&rt=rd&s=TextQuery&c=musical-touch&startPage=1&s1.query=b&s1.index=MT&o=xml");
		
			setCacheFromGreenstoneXML(xml);
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean isSuccessful) {
			notifyLibraryDownloaded();
			
		}
	}
	
	public LinkedList<MusicSheet> getCache() {
		return cache;
	}
}
