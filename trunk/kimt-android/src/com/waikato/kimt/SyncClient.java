package com.waikato.kimt;

public class SyncClient {
	private String	 userName;
	private KIMTSync syncServer;
	private boolean  isLeader;
	
	public SyncClient(KIMTSync server, String userName) {
		this.userName	= userName;
		this.syncServer	= server;
		
		this.isLeader	= server.login(userName);
	}
	
	public boolean isLeader() {
		return isLeader;
	}
	
	public String getUserName() {
		return userName;
	}
}
