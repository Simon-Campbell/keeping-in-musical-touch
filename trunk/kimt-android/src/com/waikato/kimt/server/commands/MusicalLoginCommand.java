package com.waikato.kimt.server.commands;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;

import com.waikato.kimt.server.Client;
import com.waikato.kimt.server.ClientManager;
import com.waikato.kimt.server.SyncServer;
import com.waikato.kimt.server.interfaces.IClient;
import com.waikato.kimt.server.interfaces.IConnection;
import com.waikato.kimt.sync.MusicalSyncClient;

public class MusicalLoginCommand implements MusicalCommand {
	IClient client;
	
	public IClient getClient()
	{
		return client;
	}
	
	@Override
	public void processAsServer(IConnection conn)
			throws OptionalDataException, ClassNotFoundException, IOException {
		Object obj = conn.getInputStream().readObject();
		
		if (obj instanceof String) {
			IClient client = new Client(conn, (String) obj, ClientManager.getSingleton());
			
			this.client = client;
			
			System.out.println("User " + client.getName() + " has logged in.");

			ObjectOutputStream out = null ;
			
			// If the client is the first in the array then we'll tell the client
			// that it is the leader
			out = client.getConnection().getOutputStream();
			
			out.writeObject(SyncServer.VERSION);
			out.writeObject("LOGIN");
			out.writeObject(new Boolean(true)); // setting by default to leader
			
			out.flush();
		}
	}

}
