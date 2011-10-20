package com.waikato.kimt.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.SocketException;

import com.waikato.kimt.server.commands.MusicalCommand;
import com.waikato.kimt.server.commands.MusicalCommandFactory;
import com.waikato.kimt.server.interfaces.IClient;
import com.waikato.kimt.server.interfaces.IConnection;
import com.waikato.kimt.sync.MusicalDataFrame;

/**
 * Client represents a single remote device that has been logged in
 * @author Greg
 *
 */
public class Client implements IClient
{
	IConnection connection;
	String name;
	ClientThread clientThread;
	//boolean running = true;
	
	public Client(IConnection connection, String name, ClientManager clientManager)
	{
		this.connection = connection;
		this.name = name;
	
		new ClientThread().start();
	}
	
	/**
	 * Returns the name of the client
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * Returns this clients connection object
	 */
	public IConnection getConnection() 
	{
		return connection;
	}
	
	/**
	 * Internal class used for threading.
	 * @author Greg
	 *
	 */
	class ClientThread extends Thread
	{
		private String read(ObjectInputStream ois) throws Exception
		{
			String s = "";
			Object o = ois.readObject();
			if (o instanceof String)
				s = (String)o;
			
			return s;
		}
		
		/**
		 * Helper method to check the passed string against the version number
		 * @param s String to compare
		 * @return True if versions match
		 */
		private boolean versionCheck(String s)
		{
			if (s.equals("KIMT 1.0"))
				return true;
			return false;
		}
		
		/**
		 * Reads a Serialized String object and returns true whether the versions match up
		 * @param ois ObjectInputStream
		 * @return True if versions match
		 * @throws Exception
		 */
		private boolean readVersionCheck(ObjectInputStream ois) throws Exception
		{
			String s = read(ois);
			if (s.equals("") == false)
			{
				if (versionCheck(s))
				{
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Threaded client logic. Used to listen for commands and perform the appropriate action
		 */
		public void run()
		{
			this.setName("ClientThread: " + name);
			
			boolean running = true;
			
			while(running)
			{
				try
				{
					if (readVersionCheck(connection.getInputStream()))	//If versions are compatible
					{
						if (read(connection.getInputStream()).equals("PUT SYNC"))	//If commandtype is PUT SYNC
						{
							if (Client.this.equals(ClientManager.getSingleton().clients.get(0)))	//If Client is the conductor
							{
								Object read = getConnection().getInputStream().readObject();
								if (read instanceof MusicalDataFrame)
								{
									MusicalDataFrame newState = (MusicalDataFrame) read;
									
									//Stores the MusicalDataFrame, overwriting the previous value
									//and broadcasts this to all non-conductor clients
									System.out.println("<GOT SYNC> " + newState);
									StateManager.getSingleton().states.put("MusicalDataFrame", newState);
									StateManager.getSingleton().setSync(newState);
								}
							}
						}	
						else if (read(connection.getInputStream()).equals("GET SYNC"))	//If commandtype is GET SYNC
						{
							if (Client.this.equals(ClientManager.getSingleton().clients.get(0)))	//If Client is not the conductor
							{
								//getConnection().getOutputStream().writeObject("ERROR: Conductors can't GETSYNC");
							}
							else
							{
								MusicalDataFrame mdf = (MusicalDataFrame)StateManager.getSingleton().states.get("MusicalDataFrame");
								if (mdf != null)
								{
									//Sends the version number, GET SYNC command and then the MusicalDataFrame
									getConnection().getOutputStream().writeObject("KIMT 1.0");
									getConnection().getOutputStream().writeObject("GET SYNC");
									getConnection().getOutputStream().writeObject(mdf);
								}
							}
						}
					}
				}
				catch (Exception ex)
				{
					//ex.printStackTrace();
					ClientManager.getSingleton().clients.remove(Client.this); 
					running = false;
					Logger.insert(Client.this, ex.getMessage());
					connection.kill();
				}
			}
			
		}
	}
}
