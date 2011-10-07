package com.waikato.kimt.sync;

import com.waikato.kimt.greenstone.MusicView;

/**
 * An interface for objects who wish to listen to upload
 * and downloaded sync updates.
 * @author Simon
 *
 */
public interface SyncedLibraryUpdateListener {
	/**
	 * An event that is called when a new sync object
	 * is ready to be downloaded. It is automatically broadcasted 
	 * to all clients-- here they can chose to accept or reject 
	 * the update.
	 */
	public void onSyncUpdateNotification();
	
	/**
	 * An event that is called when a new sync object is downloaded
	 * from the sync server. This allows the receiver to decide whether
	 * to overwrite their current sync object with the new one.
	 * @param k
	 * 	The new sync object that was downloaded from the sync server.
	 */
	public void onSyncDownloaded(KIMTSync k);
	
	/**
	 * An event that is called when a new sync object has been uploaded
	 * to the sync server. This allows the sender to know that their update
	 * was or wasn't successful.
	 * @param k
	 * 	Whether or not the sync-object was actually uploaded.
	 */
	public void onSyncUploaded(Boolean uploaded);
	
	/**
	 * An event that is called when the synced view has been updated
	 * from the sync server.
	 * @param mv
	 * 	The new music view that all clients share.
	 */
	public void onSyncViewUpdate(MusicView mv);
}