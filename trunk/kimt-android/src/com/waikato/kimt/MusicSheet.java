package com.waikato.kimt;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.os.AsyncTask;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import android.app.Activity;


public class MusicSheet {
	private String					sheetID;
	private String 					fullAddress;
	private GreenstoneMusicLibrary	owner;
	private Activity				displayActivity;
	private MusicSheet				async;

	private String title;
	private String author;
	
	private int	pages;
	private int currentPage;
	
	private boolean isSynced = false;
	
	public MusicSheet(GreenstoneMusicLibrary owner, Activity displayActivity, String sheetID) {
		this.owner			= owner;
		this.sheetID		= sheetID;
		this.fullAddress	= owner.getUri() + sheetID + "&o=xml";
		this.displayActivity= displayActivity;
		
		this.currentPage= 0;
		this.pages		= 1337;
		
		Log.v("debugging", this.sheetID);
		Log.v("debugging", this.fullAddress);
	
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
	        
	        // Iterate the parameter list
	        for (int i = 0; i < nodes.getLength(); i++) {
	           Element element = (Element) nodes.item(i);
	           NodeList metaInfos = element.getElementsByTagName("metadata");
	           
	           for (int j = 0; j < metaInfos.getLength(); j++) {
	        	   Element metaInfo	= (Element) metaInfos.item(j);
    	           String name		= metaInfo.getAttribute("name");
    	           
    	           if (name.compareTo("mp.title") == 0) {
    	        	   setTitle(GreenstoneUtilities.getCharacterDataFromElement(metaInfo));
    	           }
	           }
	        }
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }

	}
	
	//using class Async to do the work
	private class AsyncGreenstoneXMLDownload extends AsyncTask<String, Void, String> {
		MusicSheet ms = null;
		
		//get the data in the background. This means that the thread with UI will not be blocked 
		protected String doInBackground(String... address) {
			Log.v("debugging", address[0]);
			return Network.getData(address[0]);		
		}

		//the result of the above method will call this method and pass in the result. 
		//I belive it is ok for this method to update the UI (only the main UI thread (...DisplayDataActivity thread) should be updating UI elements)
		protected void onPostExecute(String result) {
			// Set the dump text view to the value of the dump
			// string
			TextView tvFormatted=	(TextView) displayActivity.findViewById(R.id.textViewFormatted);
			TextView tvDump		=	(TextView) displayActivity.findViewById(R.id.textViewDump);
			
			setDataFromGreenstoneXML(result);
			tvFormatted.setText(getTitle() + "\r\n");
			
			
			
			//only display the data if the fetch, else display an error. 
			if (result.equals("") == false) {
				//				tvDump.loadData(dumpString.toString(), "", "");
				tvDump.setText(result);
				tvDump.setMovementMethod(new ScrollingMovementMethod());
			} else {
//								tvDump.loadData("<h1> Error parsing data </h1>", "text/html", "utf-8");
			}	
			
			isSynced = true;
		}

	}

}
