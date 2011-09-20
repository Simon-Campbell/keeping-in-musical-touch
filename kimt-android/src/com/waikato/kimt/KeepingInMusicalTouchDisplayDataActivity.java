package com.waikato.kimt;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;

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

		//set the title
		TextView tvTitle = (TextView) findViewById(R.id.textViewTitle);
		if (url.length() > 32) {
			tvTitle.setText(url.substring(0, 32) + " ...");
		}


		//create the class that does the loading of the data
		new getAndDisplayData().execute(url);

	}

	//using class Async to do the work
	private class getAndDisplayData extends AsyncTask<String, Void, String> {

		//get the data in the background. This means that the thread with UI will not be blocked 
		protected String doInBackground(String... fullAddress) {
			String result = Network.getData(fullAddress[0]);
			return result;
		}

		//the result of the above method will call this method and pass in the result. 
		//I belive it is ok for this method to update the UI (only the main UI thread (...DisplayDataActivity thread) should be updating UI elements)
		protected void onPostExecute(String result) {

			// Set the dump text view to the value of the dump
			// string
			GreenstoneUtilities.formatTextView((TextView) findViewById(R.id.textViewFormatted), result);
			TextView tvDump = (TextView) findViewById(R.id.textViewDump);
			//only display the data if the fetch, else display an error. 
			if (result.equals("") == false) {
				//				tvDump.loadData(dumpString.toString(), "", "");
				tvDump.setText(result);
				tvDump.setMovementMethod(new ScrollingMovementMethod());
			} else {
//								tvDump.loadData("<h1> Error parsing data </h1>", "text/html", "utf-8");
			}		
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
