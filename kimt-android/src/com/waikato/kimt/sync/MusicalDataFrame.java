package com.waikato.kimt.sync;

import java.io.Serializable;

public class MusicalDataFrame implements Serializable {
//	private MusicView	currentView;
	
	/**
	 * The serial version of this object. Update when the object changes.
	 */
	private static final long serialVersionUID = -4924532309690665833L;
	
	private String	libraryLocation;
	private String	trackLocation;
	private String	trackIdentifier;
//	private byte[]	bitmapBytes;
	private int currentPage;

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
	
	public synchronized int getPage() {
		return currentPage;
	}
	
	public synchronized void setPage(int page) {
		this.currentPage = page;
	}
	
	public void setTrackLocation(String location) {
		this.trackLocation = location;
	}
	
	public String getTrackLocation() {
		return trackLocation;
	}
	
	public String toString() {
		return "MusicDataFrame: " + "\r\nLibraryLocation: " + getLibraryLocation() + "\r\nTrackLocation: " + getTrackLocation() + "\r\nCurrent Page: " + Integer.toString(getPage());
	}
	
	public String getSheetID() {
		return this.trackIdentifier;
	}
	
	public void setSheetID(String sheetID) {
		trackIdentifier = sheetID;
	}
}
