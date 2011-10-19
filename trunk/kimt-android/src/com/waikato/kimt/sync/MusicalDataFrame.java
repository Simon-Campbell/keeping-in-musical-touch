package com.waikato.kimt.sync;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MusicalDataFrame implements Serializable {
//	private MusicView	currentView;
	
	private String	libraryLocation;
	private String	trackLocation;
	private String	trackIdentifier;
	private byte[]	bitmapBytes;
	
	/**
	 * The serial version of this object. Update when the object changes.
	 */
	private static final long serialVersionUID = 1L;

	public MusicalDataFrame() {
		
	}
	
	public MusicalDataFrame(String libraryLocation) {
		this.libraryLocation = libraryLocation;
	}
	
	public void setLibraryLocation(String location) {
		this.libraryLocation = location;
	}
	
	public String getLibraryLocation() {
		return libraryLocation;
	}
	
	public void setTrackLocation(String location) {
		this.trackLocation = location;
	}
	
	public String getTrackLocation() {
		return trackLocation;
	}
	
	public String toString() {
		return "MusicDataFrame: " + "LibraryLocation: " + getLibraryLocation() + " TrackLocation: " + getTrackLocation();
	}
	
	public String getSheetID() {
		return this.trackIdentifier;
	}
	
	public void setSheetID(String sheetID) {
		trackIdentifier = sheetID;
	}
	
	public void setBitmap(Bitmap bmp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		bitmapBytes = stream.toByteArray();
	}

	public Bitmap getBitmap() {
		return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
	}
}
