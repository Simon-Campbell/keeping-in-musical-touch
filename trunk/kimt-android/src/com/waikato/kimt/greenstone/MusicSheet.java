package com.waikato.kimt.greenstone;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
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
import android.util.Log;

public class MusicSheet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String	sheetID;
	private String 	fullAddress;
	
	/**
	 * Internal Greenstone variable:
	 * 	Name of the folder where associated
	 * 	files are kept.
	 */
	private String	documentFolder;
	
	/**
	 * Internal Greenstone variable:
	 * 	Title of the document to help find
	 * 	associated files.
	 */
	private String	documentTitle;
	
	private String title;
	private String author;
	
	private int	pages;
	
	private Bitmap bitmap;
	private ArrayList<Bitmap> bitmapList;

	public MusicSheet(GreenstoneMusicLibrary owner, String sheetID) {
		this(owner, sheetID, false);
	}
	
	public MusicSheet(GreenstoneMusicLibrary owner, String sheetID, Boolean download) {
		this.sheetID		= sheetID;
		this.fullAddress	= owner.getUri() + sheetID + "&o=xml";
		this.pages			= 1337;
		
		if (download) {
			// Start actually fetching the data
			new AsyncGreenstoneXMLDownload().execute(this.fullAddress);
		}
	}
	
	public String toString() {
		return (this.title + " by " + this.author + " (" + this.pages + ")");
	}
	
	public String getFullAddress() {
		return this.fullAddress;
	}
	
	public String getImageLocation(int page) {
		return
			"http://www.nzdl.org/greenstone3-nema/cgi-bin/image-server.pl?a=fit-screen&c=musical-touch&site=localsite&pageWidth=426&pageHeight=603&assocDir=" + 
			this.documentFolder + "&assocFile=" + this.documentTitle + "-" + Integer.toString(page) + ".png";
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
	
	public void setBitmapFromInternet(int page) {
		if (this.documentFolder == null || this.documentTitle == null) {
			new AsyncGreenstoneXMLDownload().execute(fullAddress);
		} else {
			new AsyncImageDownload().execute(getImageLocation(page));
		}
	}
	
	public Bitmap getBitmap() {
		return this.bitmap;
	}
	
	private void setDataFromGreenstoneXML(String uri) {
	    try {
	        DocumentBuilderFactory dbf	= DocumentBuilderFactory.newInstance();
	        DocumentBuilder db			= dbf.newDocumentBuilder();
	        
	        InputSource is				= new InputSource((new URL(uri)).openStream());
	        Document doc				= db.parse(is);
	        
	        NodeList nodes				= doc.getElementsByTagName("metadata");
	        NodeList documentElems		= null;
	        
//	        nodes = ((Element) nodes.item(0)).getElementsByTagName("pageResponse");
	        
	        // We wish to save the nodelist that contains <documentNode /> and <metadataList />
//	        nodes = ((Element) nodes.item(0)).getElementsByTagName("document");
//	        nodes = ((Element) nodes.item(0)).getElementsByTagName("metadataList");

	        for (int i = 0; i < nodes.getLength(); i++) {
	        	Element	metadataElement = (Element) nodes.item(i);
	        	String	metadataName = metadataElement.getAttribute("name");
	        
	        	if (metadataName.compareTo("mp.title") == 0) {
 	        	   this.title = XMLUtilities.getCharacterDataFromElement(metadataElement);   
 	           	}
	        	
	        	if (metadataName.compareTo("Title") == 0) {
 	        	   // Set this document title to use so that the image can be
 	        	   // retrieved
 	        	   this.documentTitle = XMLUtilities.getCharacterDataFromElement(metadataElement);
 	        	   
 	        	   // If the document title hasn't already been set then we'll 
 	        	   // set it to this one, however "mp.title" is preferred.
 	        	   if (this.title == null) {
 	        		   this.title = documentTitle;
 	        	   }
 	        	   
 	           	}
	        	
	        	if (metadataName.compareTo("mp.composer") == 0 || (title == null && metadataName.compareTo("Composer") == 0)) {
 	        	   this.author= XMLUtilities.getCharacterDataFromElement(metadataElement);
 	           	}
	        	
	        	if (metadataName.compareTo("assocfilepath") == 0) {
	        		this.documentFolder = XMLUtilities.getCharacterDataFromElement(metadataElement);
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

	        this.bitmap = BitmapFactory.decodeStream(new URL(getImageLocation(0)).openStream());
	    }
	    
	    catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	    }

	}
	
	/**
	 * This region of code takes care of the different listeners in
	 * the MusicSheet.
	 */
	private List<MetaDataDownloadListener>
		registeredMetaDataListeners = new ArrayList<MetaDataDownloadListener>();
	private List<ImageDataDownloadListener>
		registeredImageDataDownloadListeners = new ArrayList<ImageDataDownloadListener>();
	
	/**
	 * An interface for classes that listen to when meta-data
	 * has been downloaded.
	 * @author Simon
	 *
	 */
	public interface MetaDataDownloadListener {
		public void onMetaDataDownloaded(MusicSheet ms);
	}
	
	/**
	 * An interface for classes who listen to when an image
	 * has been downloaded.
	 * @author Simon
	 *
	 */
	public interface ImageDataDownloadListener {
		public void onImageDownloaded(MusicSheet ms);
	}
	
	/**
	 * Set an object to listen to when meta-data has been updated.
	 * @param mddl
	 * 	A listening object which listens to the
	 */
	public void setOnSheetMetaDataUpdateListener(MetaDataDownloadListener mddl) {
		registeredMetaDataListeners.add(mddl);
	}
	
	public void setOnImageDownloadedListener(ImageDataDownloadListener imageDataDownloadListener) {
		registeredImageDataDownloadListeners.add(imageDataDownloadListener);
	}
	
	protected void notifyMetaDataDownloaded() {
		for (MetaDataDownloadListener mddl : registeredMetaDataListeners)
			mddl.onMetaDataDownloaded(this);
	}
	
	protected void notifyImageDownloaded() {
		for (ImageDataDownloadListener iddl : registeredImageDataDownloadListeners)
			iddl.onImageDownloaded(this);
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
			notifyImageDownloaded();
		}
	}
	
	private class AsyncImageDownload extends AsyncTask<String, Void, Bitmap> {
		protected Bitmap doInBackground(String... imageAddress) {
			Bitmap returnImage = null;
			
			try {
				Log.v("Debugging", imageAddress[0]);
				returnImage = BitmapFactory.decodeStream(new URL(imageAddress[0]).openStream());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return
				returnImage;
		}
		
		protected void onPostExecute(Bitmap result) {
			// Set the result of the current MusicSheet
			// instance.
			MusicSheet.this.bitmap = result ;
			
			// Notify the listeners that the image is
			// ready to be shown
			notifyImageDownloaded();
		}
	}
}
