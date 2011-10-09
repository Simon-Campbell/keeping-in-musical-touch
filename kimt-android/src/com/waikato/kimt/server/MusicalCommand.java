package com.waikato.kimt.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;

public interface MusicalCommand {
	/**
	 * Will process the input stream and manipulate the processObject as
	 * required.
	 * @param in
	 * 	The input stream to process.
	 * @param server
	 *	The server the command was sent to.
	 * @param client
	 * 	The client who sent the command.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws OptionalDataException 
	 */
	public void process(ObjectInputStream in, SyncServer server, Client client) throws OptionalDataException, ClassNotFoundException, IOException;
}
