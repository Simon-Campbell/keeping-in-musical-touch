package com.waikato.kimt.server.commands;

import java.io.IOException;
import java.io.OptionalDataException;

import com.waikato.kimt.server.StateManager;
import com.waikato.kimt.server.interfaces.IConnection;
import com.waikato.kimt.sync.MusicalDataFrame;

public class MusicalPutSyncCommand implements MusicalCommand {

	@Override
	public void processAsServer(IConnection conn)
			throws OptionalDataException, ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		Object obj = conn.getInputStream().readObject();
		
		if (obj instanceof MusicalDataFrame) 
		{
			System.out.println("PUT CURRENT SYNC");
			StateManager.getSingleton().getHashTable().put("MusicalDataFrame", obj);
		}
	}
}
