package com.waikato.kimt;

import android.app.Application;

import com.waikato.kimt.greenstone.GreenstoneMusicLibrary;
import com.waikato.kimt.sync.KIMTSyncClient;

public class KIMTClient extends Application {
	GreenstoneMusicLibrary gml;
	KIMTSyncClient ks;
	
	public void setLibrary(GreenstoneMusicLibrary gml) {
		this.gml = gml;
	}
	
	public GreenstoneMusicLibrary getLibrary() {
		return this.gml;
	}
}
