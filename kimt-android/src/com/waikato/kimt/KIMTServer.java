package com.waikato.kimt;

import com.waikato.kimt.server.SyncServer;

public class KIMTServer {
	public static final int defaultServerPort = ('k' + 'i' + 'm' + 't') * 128;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SyncServer ss;
		
		System.out.println("[KIMTServer] Loading ..");
		
		try {
			ss = new SyncServer(defaultServerPort);
			ss.start();
		
			System.out.println("[KIMTServer] KIMTSyncServer loaded");
			
		} finally {
		
		}
	}

}
