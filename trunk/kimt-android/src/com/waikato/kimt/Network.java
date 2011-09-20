package com.waikato.kimt;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class Network {
	
	public static String getData(String fullAddress) {
		StringBuilder dumpString = null;
		URL url = null;

		try {
			// Point the URL object at the full address
			Log.v("debugging", "before open URL");
			url	= new URL(fullAddress);
			Log.v("debugging", url.toString());

			// Create a new input stream from the URL
			InputStream input = url.openStream();
			Log.v("debugging", "after openStream()");
			
			// Create an array of bytes for the input stream to
			// be written to
			byte[] b = new byte[1024];
			int bytesRead;

			// We'll dump the byte array to this string
			dumpString = new StringBuilder(1024);

			do {
				// Keep reading the input stream, then write to the array of
				// bytes while there's data to be read.
				bytesRead = input.read(b);

				// Loop through the byte array and append each character
				// to the dump string
				for (int i = 0; i < bytesRead; i++) {
					dumpString.append((char)b[i]);
				}
			} while (bytesRead != -1);

			
			input.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Log.v("debugging", e.toString());
			e.printStackTrace();
		}

		return
				dumpString.toString();
	}

}
