package com.waikato.kimt.server;

import java.net.Socket;

import com.waikato.kimt.server.commands.MusicalCommand;
import com.waikato.kimt.server.commands.MusicalCommandFactory;
import com.waikato.kimt.server.interfaces.IClient;
import com.waikato.kimt.server.interfaces.IConnection;

public class Client implements IClient
{
	IConnection connection;
	String name;
	ClientThread clientThread;
	
	public Client(IConnection connection, String name)
	{
		this.connection = connection;
		this.name = name;
		new ClientThread().start();
	}
	
	class ClientThread extends Thread
	{
		public void run()
		{
			while(true)
			{
				try
				{
					Object read = null;
					
					while((read = getConnection().getInputStream().readObject()) != null)
					{
						if (read instanceof String)
						{
							String data = (String)read;
							
							MusicalCommand mc = MusicalCommandFactory.getMusicalCommand(data);
						}
					}
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
