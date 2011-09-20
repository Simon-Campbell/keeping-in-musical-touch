package com.waikato.kimt;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.widget.TextView;

class GreenstoneUtilities {
	/**
	 * A method that will parse an input string in an XML format and place the
	 * data in the textview
	 * @param tv	The textview to assign the formatted data to
	 * @param input	The string input formatted as an XML string
	 */
	public static void formatTextView(TextView tv, String xmlString) {
		StringBuilder formattedString = new StringBuilder(1024); 	
		
	    try {
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xmlString));
	        
	        Document doc = db.parse(is);
	        NodeList nodes = doc.getElementsByTagName("page");
           
	        /**
	         * <page> ..
	         * 	<pageResponse> ..
	         * 		<document> ..
	         * 			<documentNode> ..
	         * 				<metadataList>
	         * 					<metadata name="mp.title">READING THIS VALUE</metadata>
	         * 			..
	         * </page>
	         */
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
        	           formattedString.append("mp.title: " + getCharacterDataFromElement(metaInfo) + "\r\n");
    	           }
	           }
	        }
	        
	        tv.setText(formattedString);
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		
	    if (child instanceof CharacterData) {
	    	return ((CharacterData) child).getData();
	    }
	    
	    return "?";
	}
}