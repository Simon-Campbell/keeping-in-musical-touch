package com.waikato.kimt.greenstone;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
	private String	libraryAddress;
	
	/**
	 * Internal Greenstone variable:
	 * 	Name of the folder where associated
	 * 	files are kept.
	 */
	private String	documentFolder;
	
	/**
	 * Internal Greenstone variable:
	 * 	Filename root of the document to help find
	 * 	associated files.
	 */
	private String	filenameRoot;
	
	private String title;
	private String author;

	private int currentPage;
	private int	maximumPages= 1337;
	
	private	Bitmap currentBitmap;
	private Vector<Bitmap> bitmapList;

	public MusicSheet(GreenstoneMusicLibrary owner, String sheetID) {
		this(owner, sheetID, false);
	}
	
	public MusicSheet(GreenstoneMusicLibrary owner, String sheetID, Boolean download) {
		this.sheetID		= sheetID;
		this.libraryAddress = owner.getUri();
		this.fullAddress	= owner.getUri() + "/dev?a=d&ed=1&book=off&c=musical-touch&d=" +sheetID + "&o=xml";
		this.maximumPages	= 1337;
		this.bitmapList		= new Vector<Bitmap>(16);
		
		if (download) {
			// Start actually fetching the data
			new AsyncGreenstoneXMLDownload().execute(this.fullAddress);
		}
	}
	
	public MusicSheet(GreenstoneMusicLibrary owner, Element documentElement) {
        // Iterate through all the documents contained in the search 
        // results ..
    	String sheetID = documentElement.getAttribute("nodeID");
    	NodeList metadataListNodes = documentElement.getElementsByTagName("metadataList");
		
    	this.sheetID = sheetID;
		this.libraryAddress = owner.getUri();
		this.fullAddress = owner.getUri() + "/dev?a=d&ed=1&book=off&c=musical-touch&d=" +sheetID + "&o=xml";

    	for (int i = 0; i < metadataListNodes.getLength(); i++) {
    		NodeList
    			metadataNodes = ((Element) metadataListNodes.item(i)).getElementsByTagName("metadata");
    		
    		for (int j = 0; j < metadataNodes.getLength(); j++) {
    			Element
    				finalElement = (Element) metadataNodes.item(j);
    			
    			String
    				metadataName = finalElement.getAttribute("name");
    		
    			if (metadataName.compareTo("mp.title") == 0) {
    				this.author = XMLUtilities.getCharacterDataFromElement(finalElement);
    			} else if (metadataName.compareTo("mp.composer") == 0) {
    				this.title = XMLUtilities.getCharacterDataFromElement(finalElement);
    			} else if (metadataName.compareTo("assocfilepath") == 0) {
    				this.documentFolder = XMLUtilities.getCharacterDataFromElement(finalElement);
    			} else if (metadataName.compareTo("FilenameRoot") == 0) {
    				this.filenameRoot = XMLUtilities.getCharacterDataFromElement(finalElement);
    			} else if (metadataName.compareTo("current_NumPages") == 0) {
    				this.maximumPages = Integer.parseInt(XMLUtilities.getCharacterDataFromElement(finalElement));
    				this.bitmapList = new Vector<Bitmap>(this.maximumPages);
    			}
    		}
    	}
	}
	
	public String toString() {
		if (this.maximumPages != 1337)
			return (this.title + " by " + this.author + " (" + this.maximumPages + " pages)");
		else
			return (this.title + " by " + this.author);
	}
	
	public String getFullAddress() {
		return this.fullAddress;
	}
	
	public int getNumberOfPages() {
		return this.maximumPages;
	}
	
	public String getImageLocation(int page, int x, int y) {
		//if (x < 0) 
			x = 800;
		//if (y < 0)
			y = 1280;
		
		return
			this.libraryAddress + "/cgi-bin/image-server.pl?a=fit-screen&c=musical-touch&site=localsite&pageWidth=" + Integer.toString(x) + "&pageHeight=" + Integer.toString(y) +"&assocDir=" + 
			this.documentFolder + "&assocFile=" + this.filenameRoot + "-" + Integer.toString(page) + ".png";
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
		if (this.documentFolder == null || this.filenameRoot == null) {
			new AsyncGreenstoneXMLDownload().execute(fullAddress);
		} else {
			boolean isAllCached = true ;
			
			Log.v("KeepingInMusicalTouch", "Checking if TARGET bitmap exists");
			if (page < bitmapList.size() && bitmapList.get(page) != null) {
				setBitmap(bitmapList.get(page));
			}
			
			Log.v("KeepingInMusicalTouch", "Checking if the others are cached.");
			for (int i = 1; i < 3; i++) {
				if (page + i >= bitmapList.size() || bitmapList.get(page) == null) {
					isAllCached = false; break;
				}
			}
			
			
			if (!isAllCached) {
				Log.v("KeepingInMusicalTouch", "Starting the AsyncImageDownload.");
				new AsyncImageDownload().execute(page, page + 1, page + 2);
			}
		}
		
	}
	
	public Bitmap getBitmap() {
		return this.currentBitmap;
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
 	        	   this.filenameRoot = XMLUtilities.getCharacterDataFromElement(metadataElement);
 	        	   
 	        	   // If the document title hasn't already been set then we'll 
 	        	   // set it to this one, however "mp.title" is preferred.
 	        	   if (this.title == null) {
 	        		   this.title = filenameRoot;
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

	        this.currentBitmap = BitmapFactory.decodeStream(new URL(getImageLocation(0, 0, 0)).openStream());
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
		public void onImageChanged(MusicSheet ms);
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
	
	protected void notifyImageChanged() {
		for (ImageDataDownloadListener iddl : registeredImageDataDownloadListeners)
			iddl.onImageChanged(this);
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
			notifyImageChanged();
		}
	}
	
	private class AsyncImageDownload extends AsyncTask<Integer, Integer, Bitmap> {
		private int downloadingPage;
		
		protected Bitmap doInBackground(Integer... imagePage) {
			// The image to be returned!
			Bitmap returnImage = null;
			
			// The page that is being downloaded by this AsyncTask
			downloadingPage = imagePage[0];
			
			try {

				Log.v("KeepingInMusicalTouch", "Ensuring the size of the vector!");
				ensureVectorSize(imagePage.length);
				returnImage = bitmapList.get(downloadingPage);
				
				Log.v("KeepingInMusicalTouch", "Going to download the image!");
				if (returnImage == null) {
					returnImage = BitmapFactory.decodeStream(new URL(getImageLocation(downloadingPage, 800, 1280)).openStream());
				
					MusicSheet.this.bitmapList.set(downloadingPage, returnImage);
					MusicSheet.this.currentBitmap = returnImage;
				}
				
				publishProgress(downloadingPage);
				
				for (int i = 1; i < imagePage.length; i++) { 
					if (bitmapList.get(imagePage[i]) == null)
						MusicSheet.this.bitmapList.set(imagePage[i], BitmapFactory.decodeStream(new URL(getImageLocation(imagePage[i], 800, 1280)).openStream()));
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return returnImage;
		}
		
		protected void onProgressUpdate(Integer... progress) {
			// Set the result of the current MusicSheet
			// instance.
			MusicSheet.this.setBitmap(bitmapList.get(progress[0]));
			Log.v("KeepingInMusicalTouch", "First Bitmap set");
		}
		
		
		protected void onPostExecute(Bitmap result) {
			Log.v("KeepingInMusicalTouch", "All Images set");
		}
		
		private void ensureVectorSize(int upperLimit) {
			if (upperLimit >= MusicSheet.this.bitmapList.size()) {
				MusicSheet.this.bitmapList.setSize((upperLimit * 2) + 1);
			}
		}
	}

	public void setBitmap(Bitmap bitmap) {
		this.currentBitmap = bitmap;
		
		notifyImageChanged();
	}
}
