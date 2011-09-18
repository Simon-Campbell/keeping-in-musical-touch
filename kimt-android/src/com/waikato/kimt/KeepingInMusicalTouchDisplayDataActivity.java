package com.waikato.kimt;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class KeepingInMusicalTouchDisplayDataActivity extends Activity{

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        this.setContentView(R.layout.gsdisplay);
        
        //This defines and sets the button that allows the user to go back
        //Button next = (Button) findViewById(R.id.btnShow);
        Button next = (Button) findViewById(R.id.button_back); 
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        });
        
        //get the data from activity that called this one (in this case its the url full address from the main window)
        Bundle bundle = this.getIntent().getExtras();
        String url = bundle.getString("url");
        
        //process the url and get the html content
        getAndDisplayData(this.getCurrentFocus(), url);
    }
    
    public void getAndDisplayData(View view, String fullAddress) {
    	

    	// Change the content view now that we have all the data required,
    	// then we'll start editing the new content view.
//		this.setContentView(R.layout.gsdisplay);
		
		// Get the dump and title textviews via their id numbers.
    	TextView tvDump	 = (TextView) findViewById(R.id.textViewDump);
    	TextView tvTitle = (TextView) findViewById(R.id.textViewTitle);
    	
    	// Set the title to be that of the full address
    	tvTitle.setText(fullAddress);
    	
        URL url = null;

        try {
        	// Point the URL object at the full address
        	url	= new URL(fullAddress);

        	// Create a new input stream from the URL
        	InputStream input = url.openStream();
        	
        	// Create an array of bytes for the input stream to
        	// be written to
        	byte[] b = new byte[512];
        	
        	// We'll dump the byte array to this string
        	String dumpString	= "";

        	// Keep reading the input stream, then write to the array of
        	// bytes while there's data to be read.
			while (input.read(b) != -1) {
				// Loop through the byte array and append each character
				// to the dump string
				for (int i = 0; i < b.length; i++) {
					dumpString += (Character.toString((char)b[i]));
				}
			}
			input.close();
			// Set the dump text view to the value of the dump
			// string
			
			//only display the data if the fectch, else display an error. 
			if (dumpString.equals("") == false) {
				tvDump.setText(dumpString);
			} else {
				tvDump.setText("Error Getting URL");
			}

			
        } catch (MalformedURLException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            
        }

        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
    	Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        return;
    } 
}
