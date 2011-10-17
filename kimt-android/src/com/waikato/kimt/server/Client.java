package com.waikato.kimt.server;

import java.io.EOFException;
import java.io.StreamCorruptedException;
import java.net.SocketException;

import com.waikato.kimt.server.commands.MusicalCommand;
import com.waikato.kimt.server.commands.MusicalCommandFactory;
import com.waikato.kimt.server.interfaces.IClient;
import com.waikato.kimt.server.interfaces.IConnection;
import com.waikato.kimt.sync.MusicalDataFrame;

public class Client implements IClient
{
	IConnection connection;
	String name;
	ClientThread clientThread;
	
	public Client(IConnection connection, String name, ClientManager clientManager)
	{
		this.connection = connection;
		this.name = name;
	
		new ClientThread().start();
	}
	
	class ClientThread extends Thread
	{
		public void run()
		{
			boolean running = true;
			
			while(running)
			{
				try
				{
					Object read = null;
					
					while((read = getConnection().getInputStream().readObject()) != null)
					{
						if (read instanceof String)
						{
							String data = (String)read;
							
							if (data.equals("PUTSYNC"))
							{
								if (Client.this.equals(ClientManager.getSingleton().clients.get(0)))
								{
									read = getConnection().getInputStream().readObject();
									if (read instanceof MusicalDataFrame)
									{
										StateManager.getSingleton().states.put("MusicalDataFrame", read);
										//getConnection().getOutputStream().writeObject("Synced Successfully");
										break;
									}
								}
								else
								{
									//getConnection().getOutputStream().writeObject("ERROR: Not Conductor");
								}
							}
							else if (data.equals("GETSYNC"))
							{
								if (Client.this.equals(ClientManager.getSingleton().clients.get(0)))
								{
									//getConnection().getOutputStream().writeObject("ERROR: Conductors can't GETSYNC");
								}
								else
								{
									MusicalDataFrame mdf = (MusicalDataFrame)StateManager.getSingleton().states.get("MusicalDataFrame");
									if (mdf != null)
										getConnection().getOutputStream().writeObject(mdf);
								}
							}
							
							
							/*
							System.out.println(Client.this.getName() + ": " + read);
							MusicalCommand mc = MusicalCommandFactory.getMusicalCommand(data);
							mc.processAsServer(connection);
							*/
						}
					}
				}
				catch (EOFException ex) 
				{
					//EOF Exception
				}
				catch (SocketException ex)
				{
					System.err.println("Client disconnected:");
					ClientManager.getSingleton().remove(Client.this);
					running = false;
				}
				catch (StreamCorruptedException ex)
				{
					System.err.println("datastream corruption detected");
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public IConnection getConnection() 
	{
		return connection;
	}

}
