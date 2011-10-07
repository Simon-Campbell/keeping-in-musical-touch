package com.waikato.kimt.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.waikato.kimt.R;

public class KeepingInMusicalTouchDisplayDataActivity extends Activity {
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);    
		this.setContentView(R.layout.gsdisplay);
		

		//get the data from activity that called this one (in this case its the url full address from the main window)
		Bundle bundle = this.getIntent().getExtras();
		String url= bundle.getString("url");
		TextView tvFormatted = (TextView) findViewById(R.id.textViewFormatted);
		tvFormatted.setText(url);
		
		ImageView image = (ImageView) findViewById(R.id.imageSheet);		
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
		return;
	}
}
