package com.waikato.kimt.sync;

import com.waikato.kimt.greenstone.MusicView;

/**
 * @author  Simon
 */
public interface MusicalLibrarySync {
	/**
	 * Set the view of the current sync object and
	 * update other clients if this client is the leader
	 * @param mv
	 * 	The MusicView object that other clients should
	 * 	receive.
	 */
	void setMusicView(MusicView mv);
	
	/**
	 * Gets the MusicView that is viewable on this
	 * client.
	 * @return
	 * 	The viewable MusicView
	 */
	MusicView getMusicView();
	
	/**
	 * Get the location of the music library that the
	 * server is synchronising with.
	 * @return
	 *	The location of the library that is being synchronised
	 */
	String getLibraryLocation();
	
	/**
	 * Set an update listener for the sync server
	 * @param slul
	 * 	The sync listener that will listen to
	 *	the server updates.
	 */
	void setOnSyncUpdateListener(SyncedLibraryUpdateListener slul);
}