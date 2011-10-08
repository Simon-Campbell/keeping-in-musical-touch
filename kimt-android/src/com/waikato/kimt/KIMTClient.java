package com.waikato.kimt;

import android.app.Application;

import com.waikato.kimt.greenstone.GreenstoneMusicLibrary;
import com.waikato.kimt.sync.MusicalSyncClient;

public class KIMTClient extends Application {
	GreenstoneMusicLibrary gml;
	MusicalSyncClient ks;
	
	public void setLibrary(GreenstoneMusicLibrary gml) {
		this.gml = gml;
	}
	
	public GreenstoneMusicLibrary getLibrary() {
		return this.gml;
	}
}
