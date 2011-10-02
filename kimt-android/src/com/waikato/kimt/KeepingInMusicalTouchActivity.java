package com.waikato.kimt;

import com.waikato.kimt.GreenstoneMusicLibrary.SyncedLibraryBrowserUpdateListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class KeepingInMusicalTouchActivity extends Activity {
	GreenstoneMusicLibrary
	gml = null;

	/** Called when the activity is first created. */
	//@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);

		final ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, R.layout.listview);
		final ListView listview = (ListView)findViewById(R.id.myListView);

		listview.setAdapter(adapter);

		gml = new GreenstoneMusicLibrary(getString(R.string.defaultLibraryLocation) + "dev;jsessionid=08C1CB94BDBF8322F72548075D809910?a=d&ed=1&book=off&c=musical-touch&d=");
		gml.connect();
		gml.setLibraryBrowserUpdateListener(new SyncedLibraryBrowserUpdateListener() {

			@Override
			public void onLibraryDownloaded(GreenstoneMusicLibrary gml) {
				adapter.clear();

				for (MusicSheet m : gml.getCache()) {
					String
					s = m.getTitle() + " by " + m.getAuthor() + " [" + m.getSheetID() + "]";

					adapter.add(s);
				}

				listview.invalidate();
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
//				Toast.makeText(getApplicationContext(), ,
//						Toast.LENGTH_SHORT).show();
				
				String fullAddress = ((TextView) view).getText().toString();
				
				//package the data to that it can be sent to next activity
				Bundle bundle = new Bundle();
				bundle.putString("url", fullAddress);
				//bundle.put

				//call the next activity (the other view) and send the data to it
				Intent myIntent = new Intent(getApplicationContext(), KeepingInMusicalTouchDisplayDataActivity.class); //creating
				myIntent.putExtras(bundle); //data
				startActivityForResult(myIntent, 0); //starting    


			}
		});

		//UI Buttons
		Button btnShow = (Button) findViewById(R.id.btnShow);
		//Button btnBack = (Button) findViewById(R.id.btnBack);

		//Listner for button
		btnShow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


			}
		});   
	}
}