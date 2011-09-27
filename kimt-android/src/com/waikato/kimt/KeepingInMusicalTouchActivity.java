package com.waikato.kimt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class KeepingInMusicalTouchActivity extends Activity {
	GreenstoneMusicLibrary
		gml = null;

	/** Called when the activity is first created. */
	//@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.main);

		gml = new GreenstoneMusicLibrary(getString(R.string.defaultLibraryLocation) + "dev;jsessionid=08C1CB94BDBF8322F72548075D809910?a=d&ed=1&book=off&c=musical-touch&d=");

		String[] items = {"Bass", "old school", "drums", "guitar"}; 
		ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, items);
		ListView listview = (ListView)findViewById(R.id.myListView);
		listview.setAdapter(adapter);
		
		//UI Buttons
		Button btnShow = (Button) findViewById(R.id.btnShow);
		Button btnLoad = (Button) findViewById(R.id.btnLoad);
		//Button btnBack = (Button) findViewById(R.id.btnBack);

		//Listner for button
		btnShow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//get the data that the user entered
				String fullAddress = getString(R.id.textViewTitle);
				
				//package the data to that it can be sent to next activity
				Bundle bundle = new Bundle();
				bundle.putString("url", fullAddress);
				//bundle.put

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

				//updateWebView();
			}
		});      
	}

	/**
	 * The click event handler for the show button.
	 * @param view
	 */
	
//	public void updateWebView() {
//		//display the url in the webview
//		WebView myWebView = (WebView) findViewById(R.id.myWebView);
//		
//		
//		final Activity activity = this;
//		
//		 myWebView.setWebChromeClient(new WebChromeClient() {
//		   public void onProgressChanged(WebView view, int progress) {
//		     // Activities and WebViews measure progress with different scales.
//		     // The progress meter will automatically disappear when we reach 100%
//		     activity.setProgress(progress * 1000);
//		   }
//		 });
//		 
//		 myWebView.setWebViewClient(new WebViewClient() {
//			   public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//			     Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
//			   }
//			 });
//		
//		myWebView.getSettings().setJavaScriptEnabled(true);
//		myWebView.getSettings().setDefaultFontSize(19);
//		myWebView.setVisibility(1);
//		
//		String string = "http://www.nzdl.org/greenstone3-nema/dev";
//		myWebView.loadUrl(string);
//		myWebView.getSettings().setSupportZoom(true);
//	}

}