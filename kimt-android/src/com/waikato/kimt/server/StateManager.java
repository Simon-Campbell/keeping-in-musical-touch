package com.waikato.kimt.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import com.waikato.kimt.server.interfaces.IClient;
import com.waikato.kimt.sync.MusicalDataFrame;

public class StateManager
{
	private StateManager()
	{
		states = new Hashtable<String,Object>();
	}
	
	static StateManager manager;
	Dictionary<String,Object> states;
	
	public static synchronized StateManager getSingleton()
	{
		if (manager == null)
			manager = new StateManager();
		return manager;
	}
	
	public static synchronized void clear()
	{
		manager = null;
	}
	
	/**
	 * Returns a copy of the dictionary that contains all states (e.g. MusicalDataFrame)
	 * @return
	 */
	public Dictionary<String, Object> getHashTable()
	{
		return states;
	}
	
	/**
	 * Synchronizes all clients
	 * @param sync MusicalDataFrame instance to sync
	 * @throws IOException
	 */
	public void setSync(MusicalDataFrame sync) throws IOException 
	{
        ArrayList<IClient> clients = ClientManager.getSingleton().clients;
        if (clients.size() > 1)
        {
        	for(int i = 1; i < clients.size(); i++)
        	{
        		clients.get(i).getConnection().getOutputStream().writeObject("KIMT 1.0");
        		clients.get(i).getConnection().getOutputStream().writeObject("PUT SYNC");
        		clients.get(i).getConnection().getOutputStream().writeObject(StateManager.getSingleton().states.get("MusicalDataFrame"));
        		clients.get(i).getConnection().getOutputStream().flush();
        		System.out.println("[kimt]: Synced " + clients.get(i).getName());
        	}
        }
	}
}
