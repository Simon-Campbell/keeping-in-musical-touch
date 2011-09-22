package com.waikato.kimt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class KeepingInMusicalTouchLoginActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		this.setContentView(R.layout.loginview);
		
		Button
			setLibraryButton = (Button) findViewById(R.id.btnSetLibrary);
		
		setLibraryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Make a bundle of data with the location of the library inside
				// of it
				Bundle
					bundle = new Bundle();
				
				bundle.putString("library_location", getString(R.id.autoCompleteTextViewLibraryLocation));
				
				// Create an intent for the main menu and pass the location of
				// the library to it.
				Intent
					mainMenuIntent = new Intent(v.getContext(), KeepingInMusicalTouchActivity.class);
				
				mainMenuIntent.putExtras(bundle);
				
				// Start the activity and expect a normal return result
				// of 0
				startActivityForResult(mainMenuIntent, 0);
			}
		});

	}
}
