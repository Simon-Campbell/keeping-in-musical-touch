package com.waikato.kimt.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;

public class MusicalLoginCommand implements MusicalCommand {

	@Override
	public void process(ObjectInputStream in, SyncServer server, Client client) throws OptionalDataException, ClassNotFoundException, IOException {
		Object obj = in.readObject();
		
		if (obj instanceof String) {
			client.name = (String) obj;
			
			// If the client is the first in the array then we'll tell the client
			// that it is the leader
			client.getObjectOutput().writeObject(server.clients.get(0) == client);
			client.getObjectOutput().flush();
		}
	}

}
