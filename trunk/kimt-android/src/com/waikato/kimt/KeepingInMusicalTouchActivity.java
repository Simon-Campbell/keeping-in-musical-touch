package com.waikato.kimt;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class KeepingInMusicalTouchActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        this.setContentView(R.layout.main);
        
        
        // Manually set up the onClick event for the show button,
        // we can do this through main.xml also.
        final Button btnShow = (Button) findViewById(R.id.btnShow);
        
        // Create an click listener and make it run our custom
        // method elsewhere.
        btnShow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				btnShow_onClick(v);
			}
		});
    }
    
    /**
     * The click event handler for the show button.
     * @param view
     */
    public void btnShow_onClick(View view) {
    	// Find the edit box for address and port, then assign them to
    	// these local variables.
    	EditText editAddress= (EditText) findViewById(R.id.editTextAddress);
    	EditText editPort	= (EditText) findViewById(R.id.editTextPort);

    	// Get an integer representation of the port number:
    	//	This is not required now but if we wish to send binary
    	//	data via sockets it will be.
    	int port = Integer.valueOf(editPort.getText().toString());
    	
    	// Get the address that has been entered into the address textbox
    	String address		= editAddress.getText().toString();
    	
    	// Put the full address together so we can open it via URL()
    	String fullAddress	= "http://" + address + ":" + Integer.toString(port);

    	// Change the content view now that we have all the data required,
    	// then we'll start editing the new content view.
		this.setContentView(R.layout.gsdisplay);
		
		// Get the dump and title textviews via their id numbers.
    	TextView tvDump	 = (TextView) findViewById(R.id.textViewDump);
    	TextView tvTitle = (TextView) findViewById(R.id.textViewTitle);
    	
    	// Set the title to be that of the full address
    	tvTitle.setText(fullAddress);
    	
        URL		 url	= null;

        try {
        	// Point the URL object at the full address
        	url						= new URL(fullAddress);

        	// Create a new input stream from the URL
        	InputStream input		= url.openStream();
        	
        	// Create an array of bytes for the input stream to
        	// be written to
        	byte[]		 b			= new byte[512];
        	
        	// We'll dump the byte array to this string
        	String		 dumpString	= "";

        	// Keep reading the input stream, then write to the array of
        	// bytes while there's data to be read.
			while (input.read(b) != -1) {
				// Loop through the byte array and append each character
				// to the dump string
				for (int i = 0; i < b.length; i++) {
					dumpString += (Character.toString((char)b[i]));
				}
			}
            
			// Set the dump text view to the value of the dump
			// string
			tvDump.setText(dumpString);
			
			// Close the input stream now that all data has been read
			input.close();
        } catch (MalformedURLException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    	
    }
}