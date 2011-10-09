package com.waikato.kimt.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;

public class MusicalGetSyncCommand implements MusicalCommand {

	@Override
	public void process(ObjectInputStream in, SyncServer server, Client client)
			throws OptionalDataException, ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		System.out.println("MusicalGetSyncCommand().process(..) is not finished");
	}

}
