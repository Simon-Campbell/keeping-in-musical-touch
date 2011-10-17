package com.waikato.kimt.server.commands;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;

import com.waikato.kimt.server.interfaces.IClient;
import com.waikato.kimt.server.interfaces.IConnection;
import com.waikato.kimt.sync.MusicalSyncClient;

public class MusicalGetSyncCommand implements MusicalCommand {

	@Override
	public void processAsServer(IConnection conn)
			throws OptionalDataException, ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		System.out.println("MusicalGetSyncCommand().process(..) is not finished");
		
	}
}
