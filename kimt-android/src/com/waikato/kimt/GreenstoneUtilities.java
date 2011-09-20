package com.waikato.kimt;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.widget.TextView;

class GreenstoneUtilities {
	/**
	 * A method that will parse an input string in an XML format and place the
	 * data in the textview
	 * @param tv	The textview to assign the formatted data to
	 * @param input	The string input formatted as an XML string
	 */
	public static void formatTextView(TextView tv, String xmlString) {
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		
	    if (child instanceof CharacterData) {
	    	return ((CharacterData) child).getData();
	    }
	    
	    return "?";
	}
}