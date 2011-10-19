package com.waikato.kimt.client;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
			onBackPressed();
			return;
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
			
			imageSheet.setOnTouchListener(new OnTouchListener() {
				int currentPage = 0;
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					Log.v("KeepingInMusicalTouch", "The image was touched ..");
					MusicalDataFrame newMusicalDataFrame = musicalSyncClient.getDataFrame();
					
					if (event.getRawX() > 400) {
						selectedSheet.setBitmapFromInternet(++currentPage, 800, 1280);
						newMusicalDataFrame.setPage(currentPage);
						musicalSyncClient.setMusicalDataFrame(newMusicalDataFrame);
						Toast.makeText(getApplicationContext(), "Page " + Integer.toString(currentPage), Toast.LENGTH_SHORT).show();
						return true;
					} else {
						if (currentPage > 0) {
							selectedSheet.setBitmapFromInternet(--currentPage, 800, 1280);
							newMusicalDataFrame.setPage(currentPage);
							musicalSyncClient.setMusicalDataFrame(newMusicalDataFrame);
							Toast.makeText(getApplicationContext(), "Page " + Integer.toString(currentPage), Toast.LENGTH_SHORT).show();
							return true;
						}
						
					}
					return false;
				}
			});


			// Set the bitmap from the internet ..
			selectedSheet.setBitmapFromInternet(0, 800, 1280);
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
					final GreenstoneMusicLibrary gml = new GreenstoneMusicLibrary(mdf.getLibraryLocation());
					gml.setCurrentSheet(mdf.getSheetID());

					final MusicSheet currentSheet = gml.getCurrentSheet();
					formattedText.setText("<track> by <name> .. fetching data");

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
					currentSheet.setBitmapFromInternet(mdf.getPage(), 800, 1280);
					Toast.makeText(getApplicationContext(), "Page " + Integer.toString(mdf.getPage()), Toast.LENGTH_SHORT).show();
				}
			});

			Toast.makeText(getApplicationContext(), "Waiting for the conductor...", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		KIMTClient kimtClient = (KIMTClient) getApplication();
		MusicalSyncClient musicalSyncClient = kimtClient.getSyncClient();
		if (musicalSyncClient.isLeader() == true) {
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "Can not load list item. You are not the conductor.", Toast.LENGTH_SHORT).show();
		}

		return;
	}
}

