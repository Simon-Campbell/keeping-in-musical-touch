package com.waikato.kimt.server.commands;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;

import com.waikato.kimt.server.Client;
import com.waikato.kimt.server.SyncServer;
import com.waikato.kimt.server.interfaces.IClient;
import com.waikato.kimt.server.interfaces.IConnection;

public class MusicalLoginCommand implements MusicalCommand {

	IClient client;
	
	public IClient getClient()
	{
		return client;
	}
	
	@Override
	public void process(ObjectInputStream in, IConnection conn)
			throws OptionalDataException, ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		Object obj = in.readObject();
		
		if (obj instanceof String) {
			IClient client = new Client(conn, (String) obj);
			this.client = client;
			
			System.out.println("User " + client.getName()+ " has logged in.");

			ObjectOutputStream out = null ;
			
			// If the client is the first in the array then we'll tell the client
			// that it is the leader
			out = new ObjectOutputStream(client.getConnection().getOutputStream());
			out.writeObject(SyncServer.VERSION);
			out.writeObject("WELCOME");
			out.writeObject(true); // setting by default to leader
			out.flush();
	}
	}

}
