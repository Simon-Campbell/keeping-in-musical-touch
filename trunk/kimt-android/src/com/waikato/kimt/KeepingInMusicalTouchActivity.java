package com.waikato.kimt;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class KeepingInMusicalTouchActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);    
		this.setContentView(R.layout.main);


		//UI Buttons
		Button btnShow = (Button) findViewById(R.id.btnShow);
		Button btnLoad = (Button) findViewById(R.id.btnLoad);


		//Listner for button
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

		//Listner for button
		btnLoad.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				//get the url from the screen
				String fullAddress = btnShow_onClick(v);

				//display the url in the webview
				WebView myWebView = (WebView) findViewById(R.id.myWebView);
				myWebView.getSettings().setJavaScriptEnabled(true);
				myWebView.getSettings().setDefaultFontSize(19);
				String data = Network.getData(fullAddress);
				//        		myWebView.loadData(data, "text/html", "utf-8");
				myWebView.loadUrl("http://www.nzdl.org/greenstone3-nema/dev;jsessionid=08C1CB94BDBF8322F72548075D809910?a=d&amp;ed=1&amp;book=off&amp;c=musical-touch&amp;d=HASH0151f62687a74edac75640ee&amp;excerptid=gs-document-text&amp;view=simple&amp;pagePrefix=dvorak-als-die-alte-mutter-a4&amp;pageSuffix=.png&amp;numPages=3&amp;pageWidth=426&amp;pageHeight=603&amp;scaleFactor=0.7161520190023754&amp;o=xml");
				myWebView.getSettings().setSupportZoom(true);

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
		TextView editAddress= (TextView) findViewById(R.id.editTextAddress);
		//    	EditText editPort = (EditText) findViewById(R.id.editTextPort);

		// Get an integer representation of the port number:
		//	This is not required now but if we wish to send binary
		//	data via sockets it will be.
		//    	@SuppressWarnings("unused")
		//		int port = Integer.valueOf(editPort.getText().toString());

		return
				editAddress.getText().toString();
	}

}