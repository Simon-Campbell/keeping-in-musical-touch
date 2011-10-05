package com.waikato.kimt;

/**
 * @author  Simon
 */
public interface DigitalLibrarySync {
	/**
	 * Log the username onto the server.
	 * @param c
	 *	The username to log into the server.
	 * @return
	 * 	A boolean indicating whether the client
	 * 	is the leader or not.
	 */
	boolean login(String userName);
	
	/**
	 * Test if the location of the digital library syncer is valid.
	 * @param location
	 * 	The location of the library you'd like to test.
	 * @return
	 *	A boolean indicating whether or not the library is valid i.e.
	 * 	a response has been returned.
	 */
	boolean	isValidSyncLocation(String location);

	/**
	 * Will download the remote library and fire off the appropriate
	 * listener event.
	 */
	void downloadRemoteLibrary();
	void downloadRemoteSync();
	void downloadRemoteView();

	void setRemoteView(MusicView mv);
	MusicView getRemoteView();
	
	void setLocalView(MusicView mv);
	MusicView getLocalView();
	
	/**
	 * Get the location of the music library that the
	 * server is synchronising with.
	 * @return
	 * 	The location of the library that is being synchronised
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