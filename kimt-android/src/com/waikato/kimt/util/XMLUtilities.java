package com.waikato.kimt.util;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLUtilities {
	
	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		
	    if (child instanceof CharacterData) {
	    	return ((CharacterData) child).getData();
	    }
	    
	    return "?";
	}
}