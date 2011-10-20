package com.waikato.kimt.greenstone;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.os.AsyncTask;
import com.waikato.kimt.util.XMLUtilities;

public class GreenstoneMusicLibrary implements MusicLibrary, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MusicSheet current;
	private LinkedList<MusicSheet> cache;
	private String libraryUri;
//	private	int libraryPort;
	
	/**
	 * Will connect to the specified Greenstone music library
	 * at the specified URI.
	 * @param uri
	 * 	The URI to connect to. It's expected to be a Greenstone3 server.
	 */
	public GreenstoneMusicLibrary(String uri) {
		this.libraryUri	= uri;
		this.cache		= new LinkedList<MusicSheet>();
	}
	
	@Override
	public void requestTrackList() {
		new DownloadSheetListTask().execute();
	}

	public String getUri() {
		return libraryUri;
	}
	
	@Override
	public void setCurrentSheet(String sheetID) {
		// Find the track in the cache if it is cached.
		this.current = findInCache(sheetID);
		
		// If a null value was returned then the sheet
		// wasn't cached, so create a new MusicSheet object
		// and download it.
		if (this.current == null)
			this.current = new MusicSheet(this, sheetID, true);
		
		this.current.setOnSheetMetaDataUpdateListener(new MusicSheet.MetaDataDownloadListener() {
			
			@Override
			public void onMetaDataDownloaded(MusicSheet ms) {
				notifySheetMetaDataUpdate();
			}
		});
	}

	public Boolean isCached(String sheetID) {
		return findInCache(sheetID) != null;
	}
	
	public MusicSheet findInCache(String sheetID) {
		MusicSheet found = null;
		
		if (cache != null) {
			for (MusicSheet ms : cache) {
				if (ms.getSheetID().compareTo(sheetID) == 0) {
					found = ms; break;
				}
			}
		}
		
		return found;
	}
	
	@Override
	public MusicSheet getCurrentSheet() {
		return this.current;
	}

	@Override
	public List<MusicSheet> find(String searchTerm, SearchMode sm) {
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
	
	private void setCacheFromGreenstoneXML(String uri) {
	    try {
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        
	        InputSource is	= new InputSource((new URL(uri)).openStream());
	       	Document doc	= db.parse(is);
	        NodeList nodes	= doc.getElementsByTagName("page");
  
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("pageResponse");
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("documentNodeList");
	        nodes = ((Element) nodes.item(0)).getElementsByTagName("documentNode");
	        
	        // Iterate through all the documents contained in the search 
	        // results ..
	        for (int docID = 0; docID < nodes.getLength(); docID++) {
	        	Element
	        		e  = (Element) nodes.item(docID);
	        	String
	        		sheetID = e.getAttribute("nodeID");
	        	
	        	if (!isCached(sheetID)) {
	        		cache.add(new MusicSheet(this, e));
	        	}
	        }
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }

	}
	
	private class DownloadSheetListTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			for (char search = 'a'; search <= 'z'; search++) {
				setCacheFromGreenstoneXML(GreenstoneMusicLibrary.this.getUri() + "/dev?a=q&sa=&rt=rd&s=TextQuery&c=musical-touch&startPage=1&s1.query=" + Character.toString(search) + "&s1.index=MT&o=xml");
				publishProgress((int) search - 'a');
			}
			
			return null;
		}
		
		protected void onProgressUpdate(Integer... progress) {
			notifyLibraryDownloaded();
		}
		
		@Override
		protected void onPostExecute(Void v) {
			notifyLibraryDownloaded();	
		}
	}
	
	public LinkedList<MusicSheet> getCache() {
		return cache;
	}
}
