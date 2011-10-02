package com.waikato.kimt;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.widget.TextView;

public class XMLUtilities {
	
	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		
	    if (child instanceof CharacterData) {
	    	return ((CharacterData) child).getData();
	    }
	    
	    return "?";
	}
}