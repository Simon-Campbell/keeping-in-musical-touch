package com.waikato.kimt;

import java.io.IOException;

import android.app.Application;

import com.waikato.kimt.greenstone.GreenstoneMusicLibrary;
import com.waikato.kimt.sync.MusicalSyncClient;

public class KIMTClient extends Application {
	GreenstoneMusicLibrary gml;
	MusicalSyncClient musicalSyncClient;

	public void setSyncClient(MusicalSyncClient musicalSyncClient) {
		this.musicalSyncClient = musicalSyncClient;
	}
	
	/**
	 * Get the stored MusicalSyncClient for all activities
	 * @return
	 */
	public MusicalSyncClient getSyncClient() {
		return musicalSyncClient;
	}
	
	/**
	 * Set the stored library for all activities
	 * @param gml
	 */
	public void setLibrary(GreenstoneMusicLibrary gml) {
		this.gml = gml;
	}
	
	/**
	 * Get the stored library for all activities
	 * @return
	 */
	public GreenstoneMusicLibrary getLibrary() {
		return this.gml;
	}
	
	public void close() throws IOException {
		musicalSyncClient.close();
		
	}
}
