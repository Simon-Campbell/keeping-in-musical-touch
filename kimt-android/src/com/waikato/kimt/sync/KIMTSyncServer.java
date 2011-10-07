package com.waikato.kimt.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class KIMTSyncServer {
	ServerSocket ss;
	ArrayList<Socket> clientList;
	
	public KIMTSyncServer(int port) throws IOException {
		ss = new ServerSocket(port);
		clientList = new ArrayList<Socket>();
	}
	
	public void start() throws IOException {
		System.out.println("[KIMTSyncServer] Started accepting connections on port " + Integer.toString(ss.getLocalPort()) + " ..");
		
		while (ss != null) {
			Socket client = ss.accept();
			
			System.out.println("[KIMTSyncServer] Connection from " + client.getInetAddress() + ":" + client.getPort());
			
			new ConnectionReader(client).start();
			clientList.add(client);
		}
	}

	private class ConnectionReader extends Thread {
		Socket client;
		
		public ConnectionReader(Socket client) {
			this.client = client;
		}
		
		public void run() {
			ObjectInputStream in;
			BufferedReader br;
			
			System.out.println("[KIMTSyncServer > ConnectionReader] Now reading connection ..");
			
			try {
				String protocol;
				String command;
				
				in = new ObjectInputStream(client.getInputStream());
			//	br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
				while (client != null && in.available() > 0) {
					protocol= in.readUTF();
				//	command	= in.readUTF();
					
					
					if (protocol != null/* ||  command != null */)
						System.out.println("[ConnectionReader] " + protocol + " "/* + command*/);
				}
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			

		}
		
	}
}
