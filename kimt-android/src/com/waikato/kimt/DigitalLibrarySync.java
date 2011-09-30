package com.waikato.kimt;

/**
 * @author  Simon
 */
public interface DigitalLibrarySync {
	/**
	 * Test if the location of the digital library syncer is valid.
	 * @param location
	 * 	The location of the library you'd like to test.
	 * @return
	 *	A boolean indicating whether or not the library is valid i.e.
	 * 	a response has been returned.
	 */
	boolean	isValidRemoteLibrary(String location);
	
	/**
	 * Returns the most up-to-date version of a MusicLibrary object from the Keeping In Musical Touch syncher.
	 * @return   An up-to-date MusicLibrary object containing the current  state of the project.
	 */
	MusicLibrary	getRemoteLibrary();
	
	/**
	 * Set the library in the library syncher if this device has permissions to do so (decided by remote device) 
	 * @param ml  The new music library for other devices to set.
	 * @return  A boolean indicating whether the process was successful   or not.
	 */
	void	setRemoteLibrary(MusicLibrary ml);
	
	/**
	 * Will download the remote library and fire off the appropriate
	 * listener event.
	 */
	void	downloadRemoteLibrary();
	
	void	setOnSyncUpdateListener(SyncedLibraryUpdateListener	slul);

	void	setRemoteView(MusicView mv);
}

