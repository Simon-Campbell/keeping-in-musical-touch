package com.waikato.kimt.client;

import java.io.IOException;
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

import com.waikato.kimt.R;
import com.waikato.kimt.greenstone.GreenstoneMusicLibrary;
import com.waikato.kimt.greenstone.GreenstoneMusicLibrary.SyncedLibraryBrowserUpdateListener;
import com.waikato.kimt.greenstone.MusicSheet;
import com.waikato.kimt.sync.KIMTSync;


public class KeepingInMusicalTouchActivity extends Activity {
	GreenstoneMusicLibrary gml = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);

		final ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, R.layout.listview, R.id.myListTextView);
		final ListView listview = (ListView)findViewById(R.id.myListView);
		
		listview.setAdapter(adapter);

		gml = new GreenstoneMusicLibrary(getString(R.string.defaultLibraryLocation) + "dev;jsessionid=08C1CB94BDBF8322F72548075D809910?a=d&ed=1&book=off&c=musical-touch&d=");
		gml.connect();
		gml.setLibraryBrowserUpdateListener(new SyncedLibraryBrowserUpdateListener() {

			@Override
			public void onLibraryDownloaded(GreenstoneMusicLibrary gml) {
				adapter.clear();

				for (MusicSheet m : gml.getCache()) {
					String s = m.getTitle() + " by " + m.getAuthor() + " [" + m.getSheetID() + "]";
					adapter.add(s);
				}
				listview.invalidate();
			}
		});

		String musicalTouchAddress = getString(R.string.kimt_ip);
		int musicalTouchPort = Integer.parseInt(getString(R.string.kimt_port));
		
		try {
			Log.v("Debugging", musicalTouchAddress + ":" + Integer.toString(musicalTouchPort));
			
			KIMTSync
				ks = new KIMTSync(musicalTouchAddress, musicalTouchPort);
			
			ks.startListening(new Handler(), new Runnable() {
				
				@Override
				public void run() {
					Log.v("Debugging", "IN UR THREADS");
				}
			});
			
			ks.login("Simon");
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				LinearLayout
					listLayout = (LinearLayout) view;
				
				String fullAddress = ((TextView) listLayout.findViewById(R.id.myListTextView)).getText().toString();
				
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