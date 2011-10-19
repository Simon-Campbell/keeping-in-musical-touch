package com.waikato.kimt.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.waikato.kimt.KIMTClient;
import com.waikato.kimt.KIMTServer;
import com.waikato.kimt.R;
import com.waikato.kimt.greenstone.GreenstoneMusicLibrary;
import com.waikato.kimt.greenstone.MusicView;
import com.waikato.kimt.greenstone.GreenstoneMusicLibrary.SyncedLibraryBrowserUpdateListener;
import com.waikato.kimt.greenstone.MusicSheet;
import com.waikato.kimt.sync.MusicalDataFrame;
import com.waikato.kimt.sync.MusicalSyncClient;
import com.waikato.kimt.sync.SyncedLibraryUpdateListener;


public class KeepingInMusicalTouchActivity extends Activity {
	GreenstoneMusicLibrary gml = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);

		final KIMTClient kimtClient = (KIMTClient) getApplication();
		final ArrayAdapter<MusicSheet> adapter = new ArrayAdapter<MusicSheet> (this, R.layout.listview, R.id.myListTextView);
		final ListView listview = (ListView) findViewById(R.id.myListView);
				
		// Get the musical sync client and greenstone library that have been
		// created for this application.
		MusicalSyncClient musicalSyncClient = kimtClient.getSyncClient();
		GreenstoneMusicLibrary greenstoneMusicLibrary = kimtClient.getLibrary();

		listview.setAdapter(adapter);
//		listview.setEnabled(false);
		
		if (greenstoneMusicLibrary == null) {
			Toast.makeText(getApplicationContext(), "Greenstone was null", Toast.LENGTH_SHORT).show();
			greenstoneMusicLibrary = new GreenstoneMusicLibrary(getString(R.string.defaultLibraryLocation));
			greenstoneMusicLibrary.requestTrackList();
			greenstoneMusicLibrary.setLibraryBrowserUpdateListener(new SyncedLibraryBrowserUpdateListener() {

				@Override
				public void onLibraryDownloaded(GreenstoneMusicLibrary gml) {
					adapter.clear();

					for (MusicSheet m : gml.getCache()) {
						adapter.add(m);
					}
					
					listview.invalidate();
				}
			});
			
			kimtClient.setLibrary(greenstoneMusicLibrary);
			listview.setEnabled(true); 
		}
//		 else {
//			Toast.makeText(getApplicationContext(), "Greenstone has been created, listview is disabled", Toast.LENGTH_SHORT).show();
//		}
		
		if (musicalSyncClient == null) {
			Toast.makeText(getApplicationContext(), "MusicalSync was null", Toast.LENGTH_SHORT).show();
			// Get the musical touch address, port and then
			// create a socket address from it.
			InetSocketAddress inetSocketAddress = new InetSocketAddress(getString(R.string.kimt_ip), KIMTServer.defaultServerPort);
			
			try {
				// Get a random serial number and append it to the username,
				// this is lazy but it SHOULD work most of the time.
				int randomSerialNumber =  (int)(Math.random() * 1000000);
				String userName = "TestUser" + Integer.toString(randomSerialNumber);
				
				// Set the application title to reflect the fact we now
				// have a username.
				this.setTitle("Keeping In Musical Touch: " + userName);
				
				// Instantiate the musical sync client with the now generated username
				// and socket address.
				musicalSyncClient = new MusicalSyncClient(userName, inetSocketAddress);
				
				// Set the sync client to start listening using the handler provided
				// from the UI thread
				musicalSyncClient.startListening(new Handler());
				
				// Set the on sync update listeners of this musical sync client.
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
					public void onSyncUpdateNotification() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onMusicalDataFrameUpdated(MusicalDataFrame k) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onLoggedIn(boolean isLeader) {
						// If the user is the leader then the listview will be enabled
						// for them to use
						if (isLeader) {
							listview.setEnabled(true);
							
							Toast.makeText(getApplicationContext(), "You have been logged in as the conductor.", Toast.LENGTH_SHORT).show();
						} else {
							Bundle bundle = new Bundle();
							bundle.putBoolean("is_leader", false);
							
							// Call the next activity (the other view) and send the data to it
							Intent myIntent = new Intent(getApplicationContext(), KeepingInMusicalTouchDisplayDataActivity.class);
							myIntent.putExtras(bundle);
							
							startActivityForResult(myIntent, 0);    
							Toast.makeText(getApplicationContext(), "You have been logged in as a band member.", Toast.LENGTH_SHORT).show();
						}
					}
				});

				
			} catch (UnknownHostException e) {
				Toast.makeText(getApplicationContext(), "UNABLE TO CONNECT TO SYNC SERVER", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "UNABLE TO CONNECT TO SYNC SERVER", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "musical sync had already been set", Toast.LENGTH_SHORT).show();
		}

			
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				// Package the data to that it can be sent to next activity
				Bundle bundle = new Bundle();
				bundle.putBoolean("is_leader", true);
				bundle.putSerializable("selected_sheet", (Serializable) parent.getItemAtPosition(position));
				
				// Call the next activity (the other view) and send the data to it
				Intent myIntent = new Intent(getApplicationContext(), KeepingInMusicalTouchDisplayDataActivity.class);
				myIntent.putExtras(bundle);
				
				startActivityForResult(myIntent, 0);    


			}
		});

		  
	}
}