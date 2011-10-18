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
	
	public SyncServer()
	{
		
	}
	
	ServerSocket server;
	ClientManager clientManager;
	volatile MusicalDataFrame current;
	
	public void start(int port) throws IOException
	{
		server = new ServerSocket(port);
		System.out.println("[KIMT]: Started server on port: " + server.getLocalPort());
		
		new ConnectionThread().start();
		new ConsoleListener().start();
	}
	
	public synchronized ClientManager getClientManager()
	{
		return clientManager;
	}
	
	public void setSync(MusicalDataFrame sync) throws IOException {
        synchronized (current) {
                this.current = sync;
                
                // TODO:
                //      Test this ability to broadcast sync to all
                //      clients.
                Iterator<IClient> it = clientManager.getClients().iterator();
				while(it.hasNext())
				{
					IClient c = (IClient)it.next();
					c.getConnection().getOutputStream().writeObject(VERSION);
					c.getConnection().getOutputStream().writeObject("PUT SYNC");
					c.getConnection().getOutputStream().writeObject(this.current);
				}

        	}
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
								System.out.println("Getting the musical command ..");
								
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
								
								
								/*
								data = readString(c.getInputStream());
								if (data.equals("LOGIN"))
								{
									data = readString(c.getInputStream());
									IClient client = new Client(c, data, ClientManager.getSingleton());
									ClientManager.getSingleton().insert(client);
									
								}
								*/
							}
						}
						
						
						
						
						/*
						Object read = null;
						String data;
						data = readString(c.input);
						if (!data.equals("null"))
						{
							data = (String)read;
							
							if (data.compareTo(VERSION) != 0) 
							{
								System.out.println("This is not supported KIMT protocol-- read stopping.");
								System.out.println("Protocol: " + read);
								

								
							} 
							else
							{
								System.out.println("Accepted " + VERSION + " connection data frame.");
								System.out.println("Getting the musical command ..");
								

								
								
								
								MusicalLoginCommand mc = new MusicalLoginCommand();
								mc.processAsServer(c);
								clientManager.insert(mc.getClient());
							
								running = false;
							}
						}
							
						*/
						/*
						read = (String)c.getInputStream().readObject();
						
						if (read.compareTo(VERSION) != 0) {
							System.out.println("This is not supported KIMT protocol-- read stopping.");
							System.out.println("Protocol: " + read);
						} else {
							System.out.println("Accepted " + VERSION + " connection data frame.");
							System.out.println("Getting the musical command ..");
							*/

						/*
					
						while(running && (read = c.getInputStream().readObject()) != null)
						{
							System.out.println(read);
							String data = (String)read;

							// Reading the protocol, should probably put into a new
							// method when possible.
							if (data.compareTo(VERSION) != 0) {
								System.out.println("This is not supported KIMT protocol-- read stopping.");
								System.out.println("Protocol: " + data);
							} else {
								System.out.println("Accepted " + VERSION + " connection data frame.");
								System.out.println("Getting the musical command ..");

								MusicalCommand
									mc = MusicalCommandFactory.getMusicalCommand(c.getInputStream());
								
								if (mc instanceof MusicalLoginCommand)
								{
									MusicalLoginCommand mlc = (MusicalLoginCommand)mc;
									mlc.processAsServer(c.getInputStream(), c);
									clientManager.insert(mlc.getClient());
								
									running = false;
								}
							}
						}
						*/
						
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
			
			System.out.println("Server commands:" + 
			"\nlist: Lists all logged in clients ");
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
						if (data.length >= 2 && data[0].equals("kick"))
						{
							if (clientManager.remove(data[1]))
								System.out.println("Kicked: " + data[1]);
								else
									System.err.println("Failed to kick: " + data[1]);
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
