package com.waikato.kimt.client;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
		//final ScrollView scrollView = (ScrollView) findViewById(R.id.imageScrollView);
		
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
					Bitmap bit = ms.getBitmap();
					
					
					
					
					
					 //imageSheet.setLayoutParams( new ViewGroup.LayoutParams(WindowManager.LayoutParams., WindowManager.LayoutParams.FILL_PARENT));
					DisplayMetrics metrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(metrics);
					
					int newWidth, newHeight, oldHeight, oldWidth;
					newWidth = metrics.widthPixels;
					
					oldHeight = bit.getHeight();
			        oldWidth =  bit.getWidth();
			        
			        
			        Double newHeightDouble =  Math.floor((oldHeight * newWidth) / oldWidth);
			        newHeight = newHeightDouble.intValue();
			        
			        
//			        imageSheet.setMinimumHeight(newHeight);
//					imageSheet.setMinimumWidth(newWidth);
//					imageSheet.setMaxHeight(newHeight);
//					imageSheet.setMaxWidth(newWidth);
			       
			        
			        
			        
			        
			        imageSheet.setLayoutParams(new ScrollView.LayoutParams(newWidth, newHeight));
			        
					
					imageSheet.setImageBitmap(ms.getBitmap());
					imageSheet.setScaleType(ImageView.ScaleType.FIT_XY);

				}
			});
			
			// Set the bitmap from the internet ..
			selectedSheet.setBitmapFromInternet(0, imageSheet.getHeight(), imageSheet.getWidth());
		} else {
			Toast.makeText(getApplicationContext(), "Waiting for the Conductor...", Toast.LENGTH_SHORT).show();
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
