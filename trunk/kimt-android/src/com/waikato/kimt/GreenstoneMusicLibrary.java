package com.waikato.kimt;

import android.app.Activity;

public class GreenstoneMusicLibrary implements MusicLibrary {
	private MusicSheet			current;
	private String				trackUri;
	private DigitalLibrarySync	dls;
	
	/**
	 * Will connect to the specified Greenstone music library
	 * at the specified URI.
	 * @param uri
	 * 	The URI to connect to. It's expected to be a Greenstone3 server.
	 */
	public GreenstoneMusicLibrary(String uri) {
		this.connect(uri);
	}
	
	@Override
	public void connect(String uri) {
		this.trackUri = uri;
		
		if (dls == null) {
		}
	}

	public String getUri() {
		return trackUri;
	}
	
	public DigitalLibrarySync getSyncer() {
		return null;
	}
	
	@Override
	public void setCurrentSheet(String sheetID, Activity displayActivity) {
		this.current = new MusicSheet(this, displayActivity, sheetID);
	}

	@Override
	public MusicSheet getCurrentSheet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MusicSheet find(String searchTerm, SearchMode sm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MusicView getCurrentView() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public class GreenstoneEventListener {
		
	}

}
