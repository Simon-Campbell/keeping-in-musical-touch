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

		final ArrayAdapter<MusicSheet> adapter = new ArrayAdapter<MusicSheet> (this, R.layout.listview, R.id.myListTextView);
		final ListView listview = (ListView) findViewById(R.id.myListView);
			
		listview.setAdapter(adapter);
//		listview.setEnabled(false);

		gml = new GreenstoneMusicLibrary(getString(R.string.defaultLibraryLocation));
		gml.connect();
		gml.setLibraryBrowserUpdateListener(new SyncedLibraryBrowserUpdateListener() {

			@Override
			public void onLibraryDownloaded(GreenstoneMusicLibrary gml) {
				adapter.clear();

				for (MusicSheet m : gml.getCache()) {
					adapter.add(m);
				}
				
				listview.invalidate();
			}
		});

		String musicalTouchAddress	= getString(R.string.kimt_ip);
		int musicalTouchPort		= KIMTServer.defaultServerPort;
		
		try {
			InetSocketAddress
				inetSocketAddress = new InetSocketAddress(musicalTouchAddress, musicalTouchPort);
			
			MusicalSyncClient musicalSyncClient = new MusicalSyncClient("TestUser", inetSocketAddress);
			musicalSyncClient.startListening(new Handler());
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
				public void onSyncDownloaded(MusicalDataFrame k) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLoggedIn(boolean isLeader) {
					if (isLeader) {
						listview.setEnabled(true);
					} else {
						Bundle bundle = new Bundle();
						bundle.putBoolean("is_leader", false);
						
						// Call the next activity (the other view) and send the data to it
						Intent myIntent = new Intent(getApplicationContext(), KeepingInMusicalTouchDisplayDataActivity.class);
						myIntent.putExtras(bundle);
						
						startActivityForResult(myIntent, 0);    
					}
				}
			});

			
		} catch (UnknownHostException e) {
			Toast.makeText(getApplicationContext(), "UNABLE TO CONNECT TO SYNC SERVER", Toast.LENGTH_SHORT);
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "UNABLE TO CONNECT TO SYNC SERVER", Toast.LENGTH_SHORT);
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