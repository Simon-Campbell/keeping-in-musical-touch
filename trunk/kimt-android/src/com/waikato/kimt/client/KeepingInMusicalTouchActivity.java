package com.waikato.kimt.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.waikato.kimt.KIMTServer;
import com.waikato.kimt.R;
import com.waikato.kimt.greenstone.GreenstoneMusicLibrary;
import com.waikato.kimt.greenstone.GreenstoneMusicLibrary.SyncedLibraryBrowserUpdateListener;
import com.waikato.kimt.greenstone.MusicSheet;
import com.waikato.kimt.sync.MusicalSyncClient;


public class KeepingInMusicalTouchActivity extends Activity {
	GreenstoneMusicLibrary gml = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);

		final ArrayAdapter<MusicSheet> adapter = new ArrayAdapter<MusicSheet> (this, R.layout.listview, R.id.myListTextView);
		final ListView listview = (ListView)findViewById(R.id.myListView);
			
		listview.setAdapter(adapter);

		gml = new GreenstoneMusicLibrary(getString(R.string.defaultLibraryLocation) + "dev;jsessionid=08C1CB94BDBF8322F72548075D809910?a=d&ed=1&book=off&c=musical-touch&d=");
		gml.connect();
		gml.setLibraryBrowserUpdateListener(new SyncedLibraryBrowserUpdateListener() {

			@Override
			public void onLibraryDownloaded(GreenstoneMusicLibrary gml) {
				adapter.clear();

				for (MusicSheet m : gml.getCache()) {
		//			String s = m.getTitle() + " by " + m.getAuthor() + " [" + m.getSheetID() + "]";
					adapter.add(m);
				}
				listview.invalidate();
			}
		});


		MusicSheet test = new MusicSheet(gml, "000000");
		test.setAuthor("Simon");
		test.setTitle("Song Title");
		adapter.add(test);

		String musicalTouchAddress	= getString(R.string.kimt_ip);
		int musicalTouchPort		= KIMTServer.defaultServerPort;
		
		try {
			Log.v("Debugging", musicalTouchAddress + ":" + Integer.toString(musicalTouchPort));
			
			InetSocketAddress
				inetSocketAddress = new InetSocketAddress(musicalTouchAddress, musicalTouchPort);
			
			MusicalSyncClient
				musicalSyncClient = new MusicalSyncClient("TestUser", inetSocketAddress);
			
			musicalSyncClient.startListening(new Handler(), new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "UPDATE RECEIVED", Toast.LENGTH_SHORT).show();
				}
			});

			
		} catch (UnknownHostException e) {
			Toast.makeText(getApplicationContext(), "UNABLE TO CONNECT TO SYNC SERVER", Toast.LENGTH_SHORT);
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "UNABLE TO CONNECT TO SYNC SERVER", Toast.LENGTH_SHORT);
		}
			
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				//LinearLayout
				//	listLayout = (LinearLayout) view;
				
				//	String fullAddress = ((TextView) listLayout.findViewById(R.id.myListTextView)).getText().toString();
				String fullAddress = ((MusicSheet) parent.getItemAtPosition(position)).getFullAddress();
				//String fullAddress = ((TextView) view).getText().toString();
				
				//package the data to that it can be sent to next activity
				Bundle bundle = new Bundle();
				bundle.putString("url", fullAddress);

				//call the next activity (the other view) and send the data to it
				Intent myIntent = new Intent(getApplicationContext(), KeepingInMusicalTouchDisplayDataActivity.class);
				myIntent.putExtras(bundle);
				startActivityForResult(myIntent, 0);    


			}
		});

		  
	}
}