package com.waikato.kimt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class KeepingInMusicalTouchDisplayDataActivity extends Activity {
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);    
		this.setContentView(R.layout.gsdisplay);

		//This defines and sets the button that allows the user to go back
		//Button next = (Button) findViewById(R.id.btnShow);
		Button next = (Button) findViewById(R.id.button_back); 
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}

		});

		GreenstoneMusicLibrary
			gml = new GreenstoneMusicLibrary("http://www.nzdl.org/greenstone3-nema/dev;jsessionid=29143F04EB8E354FB59B9A41204DB97A?a=d&ed=1&book=off&c=musical-touch&d=");
		
		gml.setMetaDataUpdateListener(new GreenstoneMusicLibrary.SyncedSheetUpdateListener() {
			
			@Override
			public void onMetaDataUpdate(MusicSheet ms) {
				Log.v("onMetaDataUpdate", "The music data has been downloaded ..");
				
				if (ms != null) {
					Log.v("onMetaDataUpdate", ms.getFullAddress());
					Log.v("onMetaDataUpdate", ms.getTitle());
					Log.v("onMetaDataUpdate", ms.getAuthor());
	
					// Set the dump text view to the value of the dump
					// string
					TextView
						tvFormatted=	(TextView) findViewById(R.id.textViewFormatted);
					
					tvFormatted.setText("ID: " + ms.getSheetID() + "\nAuthor: " + ms.getAuthor() + "\nTitle: " + ms.getTitle() + "\n");
				}
			}

		});
		


		//get the data from activity that called this one (in this case its the url full address from the main window)
	//	Bundle bundle = this.getIntent().getExtras();
	//	String url= bundle.getString("url");
		
		gml.setCurrentSheet("HASH2749a69bf54b32c3e4fee4");
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

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
		return;
	}
}
