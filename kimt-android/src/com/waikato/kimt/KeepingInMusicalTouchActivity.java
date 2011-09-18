package com.waikato.kimt;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class KeepingInMusicalTouchActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        this.setContentView(R.layout.main);
        
        
        // Manually set up the onClick event for the show button,
        // we can do this through main.xml also.
        Button btnShow = (Button) findViewById(R.id.btnShow);
        // Create an click listener and make it run our custom
        // method elsewhere.
        btnShow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//get the data that the user entered
				String fullAddress = btnShow_onClick(v);
				
				//package the data to that it can be sent to next activity
				Bundle bundle = new Bundle();
				bundle.putString("url", fullAddress);
				
				//call the next activity (the other view) and send the data to it
				Intent myIntent = new Intent(v.getContext(), KeepingInMusicalTouchDisplayDataActivity.class); //creating
				myIntent.putExtras(bundle); //data
				startActivityForResult(myIntent, 0); //starting
                
				
			}
		});
    }
    
    /**
     * The click event handler for the show button.
     * @param view
     */
    
    public String btnShow_onClick(View v)
    {
    	// Find the edit box for address and port, then assign them to
    	// these local variables.
    	EditText editAddress= (EditText) findViewById(R.id.editTextAddress);
    	EditText editPort = (EditText) findViewById(R.id.editTextPort);

    	// Get an integer representation of the port number:
    	//	This is not required now but if we wish to send binary
    	//	data via sockets it will be.
    	@SuppressWarnings("unused")
		int port = Integer.valueOf(editPort.getText().toString());
    	
    	// Get the address that has been entered into the address textbox
    	String address	= editAddress.getText().toString();
    	
    	// Put the full address together so we can open it via URL()
    	String fullAddress;
    	
    	if (!address.startsWith("http://"))
    		fullAddress = "http://" + address;
    	else
    		fullAddress = address;
    	
    	return fullAddress;
    }
    
}