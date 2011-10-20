package com.waikato.kimt.sync;

import java.io.Serializable;

public class MusicalDataFrame implements Serializable {
//	private MusicView	currentView;
	
	private String	libraryLocation;
	private String	trackLocation;
	private String	trackIdentifier;
//	private byte[]	bitmapBytes;
	
	private Integer currentPage;
	/**
	 * The serial version of this object. Update when the object changes.
	 */
	private static final long serialVersionUID = 1L;

	public MusicalDataFrame() {
		currentPage = new Integer(0);
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
	
	public Integer getPage() {
		return currentPage;
	}
	
	public void setPage(int page) {
		this.currentPage = new Integer(page);
	}
	
	public void setTrackLocation(String location) {
		this.trackLocation = location;
	}
	
	public String getTrackLocation() {
		return trackLocation;
	}
	
	public String toString() {
		return "MusicDataFrame: " + " LibraryLocation: " + getLibraryLocation() + " TrackLocation: " + getTrackLocation() + " Current Page: " + Integer.toString(getPage());
	}
	
	public String getSheetID() {
		return this.trackIdentifier;
	}
	
	public void setSheetID(String sheetID) {
		trackIdentifier = sheetID;
	}
}
