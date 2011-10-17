package com.waikato.kimt.server.commands;

import java.io.IOException;
import java.io.OptionalDataException;

import com.waikato.kimt.server.interfaces.IConnection;

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
	public void processAsServer(IConnection conn) throws OptionalDataException, ClassNotFoundException, IOException;
}
