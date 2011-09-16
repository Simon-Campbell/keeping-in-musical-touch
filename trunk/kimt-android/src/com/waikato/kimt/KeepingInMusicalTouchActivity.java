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
        
        final Button btnShow = (Button) findViewById(R.id.btnShow);
        
        btnShow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				btnShow_onClick(v);
			}
		});
    }
    
    public void btnShow_onClick(View view) {
    	EditText editAddress= (EditText) findViewById(R.id.editTextAddress);
    	EditText editPort	= (EditText) findViewById(R.id.editTextPort);

    	int port = Integer.valueOf(editPort.getText().toString());
    	
    	String address		= editAddress.getText().toString();
    	String fullAddress	= "http://" + address + ":" + Integer.toString(port);

		this.setContentView(R.layout.gsdisplay);
		
    	TextView tvDump	 = (TextView) findViewById(R.id.textViewDump);
    	TextView tvTitle = (TextView) findViewById(R.id.textViewTitle);
    	
    	tvTitle.setText(fullAddress);
    	
        URL		 url	= null;

        try {
        		
             url					= new URL(fullAddress);
     
             InputStream input		= url.openStream();        
             byte[]		 b			= new byte[512];
             String		 dumpString	= "";
             
             while (input.read(b) != -1) {
            	 for (int i = 0; i < b.length; i++) {
            		 dumpString += (Character.toString((char)b[i]));
            	 }
             }
             
             tvDump.setText(dumpString);
             input.close();
             
        } catch (MalformedURLException e) {
             e.printStackTrace();
        } catch (IOException e) {
             e.printStackTrace();
        }
    	
    }
}