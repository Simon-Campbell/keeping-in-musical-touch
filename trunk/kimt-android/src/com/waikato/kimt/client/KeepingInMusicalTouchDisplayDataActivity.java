package com.waikato.kimt.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.waikato.kimt.R;
import com.waikato.kimt.greenstone.MusicSheet;
import com.waikato.kimt.greenstone.MusicSheet.MetaDataDownloadListener;

public class KeepingInMusicalTouchDisplayDataActivity extends Activity {
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);    
		this.setContentView(R.layout.gsdisplay);

		final ImageView imageSheet = (ImageView) findViewById(R.id.imageSheet);
		final TextView formattedText = (TextView) findViewById(R.id.textViewFormatted);
		
		Bitmap sheetImage;

	
		//get the data from activity that called this one (in this case its the url full address from the main window)
		Bundle bundle = this.getIntent().getExtras();
		MusicSheet selectedSheet = (MusicSheet) bundle.getSerializable("selected_sheet");
		
		formattedText.setText(selectedSheet.toString());
		
		try { // nothing
			sheetImage = BitmapFactory.decodeStream(new URL("http://upload.wikimedia.org/wikipedia/commons/0/02/NES_Super_Mario.png").openStream());
			imageSheet.setImageBitmap(sheetImage);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
