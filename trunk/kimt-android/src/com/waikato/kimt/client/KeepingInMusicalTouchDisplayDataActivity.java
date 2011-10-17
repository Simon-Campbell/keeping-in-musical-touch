package com.waikato.kimt.client;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.waikato.kimt.R;
import com.waikato.kimt.greenstone.MusicSheet;
import com.waikato.kimt.sync.MusicalSyncClient;

public class KeepingInMusicalTouchDisplayDataActivity extends Activity {
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);    
		this.setContentView(R.layout.gsdisplay);

		final ImageView imageSheet = (ImageView) findViewById(R.id.imageSheet);
		final TextView formattedText = (TextView) findViewById(R.id.textViewFormatted);
		final ScrollView scrollView = (ScrollView) findViewById(R.id.imageScrollView);
		
		// Get the extra data bundled with this activities
		// intent
		Bundle bundle = this.getIntent().getExtras();

		boolean isLeader = bundle.getBoolean("is_leader");

		if (isLeader) { 
			// Unserialize the MusicSheet that was sent in the bundle,
			// this MusicSheet was the sheet that was selected earlier.
			final MusicSheet selectedSheet = (MusicSheet) bundle.getSerializable("selected_sheet");
			
			formattedText.setText(selectedSheet.toString());
			selectedSheet.setOnImageDownloadedListener(new MusicSheet.ImageDataDownloadListener() {
				@Override
				public void onImageDownloaded(MusicSheet ms) {
					imageSheet.setImageBitmap(ms.getBitmap());
				}
			});
			
			// Set the bitmap from the internet ..
			selectedSheet.setBitmapFromInternet(0, imageSheet.getHeight(), imageSheet.getWidth());
		} else {
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
		return;
	}
}
