package com.waikato.kimt.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;

import com.waikato.kimt.sync.MusicalDataFrame;

public class MusicalPutSyncCommand implements MusicalCommand {

	@Override
	public void process(ObjectInputStream in, SyncServer server, Client client) throws OptionalDataException, ClassNotFoundException, IOException {
		Object obj = in.readObject();
		
		if (obj instanceof MusicalDataFrame) {
			server.setSync((MusicalDataFrame) obj);
		}
	}

}
