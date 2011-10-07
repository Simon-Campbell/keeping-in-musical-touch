package com.waikato.kimt.sync;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class KIMTSyncClient {
	private String	 userName;
	private KIMTSync syncServer;
	private boolean  isLeader;
	
	public KIMTSyncClient(String userName) throws UnknownHostException, IOException {
		this.userName	= userName;
		
		this.syncServer	= new KIMTSync();
		this.isLeader	= this.syncServer.login(userName);
	}
	
	public KIMTSyncClient(String userName, InetSocketAddress location) throws UnknownHostException, IOException {
		this.userName	= userName;
		this.syncServer	= new KIMTSync(location.getAddress().toString(), location.getPort());
	}
	
	public boolean isLeader() {
		return isLeader;
	}
	
	public String getUserName() {
		return userName;
	}
}
