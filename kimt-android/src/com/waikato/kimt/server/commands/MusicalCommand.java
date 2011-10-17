package com.waikato.kimt.server.commands;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;

import com.waikato.kimt.server.interfaces.IClient;
import com.waikato.kimt.server.interfaces.IConnection;
import com.waikato.kimt.sync.MusicalSyncClient;

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
	public void processAsServer(ObjectInputStream in, IConnection conn) throws OptionalDataException, ClassNotFoundException, IOException;
}
