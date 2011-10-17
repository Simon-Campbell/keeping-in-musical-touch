package com.waikato.kimt.server.commands;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;

import com.waikato.kimt.server.SyncServer;
import com.waikato.kimt.server.interfaces.IConnection;
import com.waikato.kimt.sync.MusicalDataFrame;
import com.waikato.kimt.sync.MusicalSyncClient;

public class MusicalPutSyncCommand implements MusicalCommand {

	MusicalDataFrame mdf;
	
	public MusicalDataFrame getMusicalDataFrame()
	{
		return mdf;
	}
	
	@Override
	public void processAsServer(IConnection conn)
			throws OptionalDataException, ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		Object obj = conn.getInputStream().readObject();
		
		if (obj instanceof MusicalDataFrame) 
		{
			this.mdf = (MusicalDataFrame)obj;
		}
	}
}
