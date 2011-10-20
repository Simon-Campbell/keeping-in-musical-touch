package com.waikato.kimt.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.waikato.kimt.KIMTClient;
import com.waikato.kimt.R;
import com.waikato.kimt.greenstone.GreenstoneMusicLibrary;
import com.waikato.kimt.greenstone.MusicSheet;
import com.waikato.kimt.greenstone.MusicSheet.MetaDataDownloadListener;
import com.waikato.kimt.greenstone.MusicView;
import com.waikato.kimt.sync.MusicalDataFrame;
import com.waikato.kimt.sync.MusicalSyncClient;
import com.waikato.kimt.sync.SyncedLibraryUpdateListener;

public class KeepingInMusicalTouchDisplayDataActivity extends Activity {
	/* http://www.codeshogun.com/blog/2009/04/16/how-to-implement-swipe-action-in-android/ */
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Locate the SensorManager using Activity.getSystemService
		super.onCreate(savedInstanceState);    
		this.setContentView(R.layout.gsdisplay);

		final ImageView imageSheet = (ImageView) findViewById(R.id.imageSheet);
		final TextView formattedText = (TextView) findViewById(R.id.textViewFormatted);
		final ScrollView scrollView = (ScrollView) findViewById(R.id.imageScrollView);

		KIMTClient kimtClient = (KIMTClient) getApplication();

		final MusicalSyncClient musicalSyncClient = kimtClient.getSyncClient();
		final GreenstoneMusicLibrary greenstoneMusicLibrary = kimtClient.getLibrary();

		if (musicalSyncClient == null || greenstoneMusicLibrary == null) {
			onBackPressed(); return;
		}

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
				public void onImageChanged(MusicSheet ms) {
					imageSheet.setLayoutParams(new ScrollView.LayoutParams(800, 1280));
					imageSheet.setImageBitmap(ms.getBitmap());
				}
			});
			
			final GestureDetector gs = new GestureDetector(new PageTurnDetector(selectedSheet, musicalSyncClient));

			scrollView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return gs.onTouchEvent(event);
				}
			});


			// Set the bitmap from the internet ..
			selectedSheet.setBitmapFromInternet(0);
		} else {
			formattedText.setText("Status:\n\tWaiting for the conductor to select a sheet ..");
			musicalSyncClient.setOnSyncUpdateListener(new SyncedLibraryUpdateListener() {

				@Override
				public void onSyncViewUpdate(MusicView mv) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onSyncUploaded(Boolean uploaded) {
					// TODO Auto-generated method stub
				}
				@Override
				public void onMusicalDataFrameUpdated(MusicalDataFrame mdf) {
					KIMTClient kimtClient = (KIMTClient) getApplication();					
					GreenstoneMusicLibrary gml = kimtClient.getLibrary();

					if (gml == null || gml.getUri().compareTo(mdf.getLibraryLocation()) != 0) {
						// This is a new library
						gml = new GreenstoneMusicLibrary(mdf.getLibraryLocation());
						gml.requestTrackList();
						
						kimtClient.setLibrary(gml);
					}
					
					MusicSheet currentSheet;
					MusicSheet olderSheet = gml.getCurrentSheet();
					
					if (olderSheet == null || olderSheet.getSheetID().compareTo(mdf.getSheetID()) != 0) {
						// Set the music sheet to a newer one ..
						gml.setCurrentSheet(mdf.getSheetID());
						
						if (gml.getCurrentSheet().getAuthor() == null && gml.getCurrentSheet().getTitle() == null)
							formattedText.setText("Getting metadata for sheet ..");
					} 

					currentSheet = gml.getCurrentSheet();
					currentSheet.setOnSheetMetaDataUpdateListener(new MetaDataDownloadListener() {
						@Override
						public void onMetaDataDownloaded(MusicSheet ms) {
							formattedText.setText(ms.toString());
						}
					});

					currentSheet.setOnImageDownloadedListener(new MusicSheet.ImageDataDownloadListener() {
						@Override
						public void onImageChanged(MusicSheet ms) {
							imageSheet.setLayoutParams(new ScrollView.LayoutParams(800, 1280));
							imageSheet.setImageBitmap(ms.getBitmap());
						}
					});
		
					// Set the bitmap from the internet ..
					currentSheet.setBitmapFromInternet(mdf.getPage());
					Toast.makeText(getApplicationContext(), "Getting page " + Integer.toString(mdf.getPage()), Toast.LENGTH_SHORT).show();
				}
			});

			Toast.makeText(getApplicationContext(), "Waiting for the conductor...", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		KIMTClient kimtClient = (KIMTClient) getApplication();
		MusicalSyncClient musicalSyncClient = kimtClient.getSyncClient();

		if (musicalSyncClient == null || musicalSyncClient.isLeader()) {
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			finish();
		} else {
			Intent intent = new Intent();
			setResult(0xF, intent);
			finish();
		}

		return;
	}
	

	private class PageTurnDetector extends SimpleOnGestureListener {
		private int currentPage = 0;
		
		private MusicSheet selectedSheet;
		private MusicalSyncClient musicalSyncClient;
		
		public PageTurnDetector(MusicSheet selectedSheet, MusicalSyncClient musicalSyncClient) {
			this.selectedSheet = selectedSheet;
			this.musicalSyncClient = musicalSyncClient;
			
			Log.v("KeepingInMusicalTouch", "Created PageTurnDetector " + selectedSheet.toString() + " " + musicalSyncClient.toString());
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			MusicalDataFrame newMusicalDataFrame = new MusicalDataFrame();
			MusicalDataFrame oldMusicalDataFrame = musicalSyncClient.getDataFrame();

			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// left swipe

					if (currentPage > 0) {
						selectedSheet.setBitmapFromInternet(--currentPage);
						Toast.makeText(getApplicationContext(), "LS/Setting page " + Integer.toString(currentPage), Toast.LENGTH_SHORT).show();
					
						newMusicalDataFrame.setLibraryLocation(oldMusicalDataFrame.getLibraryLocation());
						newMusicalDataFrame.setSheetID(selectedSheet.getSheetID());
						newMusicalDataFrame.setTrackLocation(selectedSheet.getFullAddress());
						newMusicalDataFrame.setPage(currentPage);

						musicalSyncClient.setMusicalDataFrame(newMusicalDataFrame);		
						
						return true;
					}
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// right swipe

					if ((currentPage + 1) < selectedSheet.getNumberOfPages()) {
						selectedSheet.setBitmapFromInternet(++currentPage);
						Toast.makeText(getApplicationContext(), "RS/Setting page " + Integer.toString(currentPage), Toast.LENGTH_SHORT).show();
						
						newMusicalDataFrame.setLibraryLocation(oldMusicalDataFrame.getLibraryLocation());
						newMusicalDataFrame.setSheetID(selectedSheet.getSheetID());
						newMusicalDataFrame.setTrackLocation(selectedSheet.getFullAddress());
						newMusicalDataFrame.setPage(currentPage);

						musicalSyncClient.setMusicalDataFrame(newMusicalDataFrame);
						
						return true;
					}
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}
}

