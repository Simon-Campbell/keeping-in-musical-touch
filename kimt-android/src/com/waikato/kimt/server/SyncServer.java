package com.waikato.kimt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import com.waikato.kimt.server.interfaces.IClient;
import com.waikato.kimt.sync.MusicalDataFrame;

public class SyncServer 
{
	public static final String VERSION = "KIMT 1.0";
	public static final int defaultServerPort = ('k' + 'i' + 'm' + 't') * 128;
	int port;
	
	public SyncServer()
	{
		
	}
	
	ServerSocket server;
	
	public void start(int port) throws IOException
	{
		this.port = port;
		
		server = new ServerSocket(port);
		System.out.println("[KIMT]: Started server on port: " + server.getLocalPort());
		
		new ConnectionThread().start();
		new ConsoleListener().start();
	}
	
	class ConnectionThread extends Thread
	{
		public void run()
		{
			System.out.println("[KIMT]: Now listening for connections");
			try
			{
				boolean running = true;
				
				Socket s = server.accept();
				Connection c = new Connection(s);
				
				new ConnectionThread().start();
				
				System.out.println("[KIMT]: Connection found - Address: " + 
						s.getInetAddress().getCanonicalHostName() + " port: " + s.getPort());
				
				while(running)
				{
					try
					{
						Object read = null;
						read = c.getInputStream().readObject();
						String data;
						
						if (read instanceof String)
						{
							data = (String)read;
							
							//Version check
							if (data.compareTo(VERSION) != 0)
							{
								System.out.println("This is not supported KIMT protocol-- read stopping.");
								System.out.println("Protocol: " + read);
							}
							else
							{
								System.out.println("Accepted " + VERSION + " connection data frame.");
								
								read = c.getInputStream().readObject();
								data = (String)read;
								
								//Login check
								if (data.equals("LOGIN"))
								{
									read = c.getInputStream().readObject();
									data = (String)read;
									
									IClient cli = new Client(c, data, ClientManager.getSingleton());
									ClientManager.getSingleton().insert(cli);
									
									boolean isLeader = ClientManager.getSingleton().clients.get(0).equals(cli);

									System.out.println("Notified client they were leader? " + isLeader);
									
									c.output.writeObject(VERSION);
									c.output.flush();
									c.output.writeObject("LOGIN");
									c.output.flush();
									c.output.writeObject(new Boolean(isLeader));
									c.output.flush();
									
									running = false;	
								}
							}
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
				
				System.out.println("Exiting Connection thread");
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	class ConsoleListener extends Thread
	{
		public void run()
		{
			System.out.println("-----" +
					"Server commands:" + 
					"\nlist: Lists all logged in clients" +
					"\nupdate: Updates all clients" +
					"\nviewstate: View the current MusicalDataFrame object" +
					"\nkick [name]: Kicks the first client with that name" +
					"\nrestart: Restarts the server" +
					"\n-----");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
			while(true)
			{
				try
				{
					String s;
					while((s = reader.readLine()) != null)
					{
						String[] data = s.split(" ");
						ArrayList<IClient> clients = ClientManager.getSingleton().getClients();
						
						if (data[0].equals("list"))
						{
							for(int i = 0; i < clients.size(); i++)
							{
								System.out.println(i + ": " + clients.get(i).getName());
							}
						}
						else if (data.length >= 2 && data[0].equals("kick"))
						{
							System.out.println("Removing " + data[1] + " size: " + ClientManager.getSingleton().clients.size());
							if (ClientManager.getSingleton().remove(data[1]))
								System.out.println("Kicked: " + data[1]);
								else
									System.err.println("Failed to kick: " + data[1]);
						}
						else if (data[0].equals("update"))
						{
							System.out.println("[kimt] forcing update to all clients");
							StateManager.getSingleton().setSync((MusicalDataFrame)StateManager.getSingleton().states.get("MusicalDataFrame"));
						}
						else if (data[0].equals("viewstate"))
						{
							System.out.println(StateManager.getSingleton().states.get("MusicalDataFrame"));
						}
						else if (data[0].equals("restart"))
						{
							System.out.println("[kimt] restarting server");
							ClientManager.clear();
							StateManager.clear();
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
}
