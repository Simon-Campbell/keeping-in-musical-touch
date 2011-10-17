package com.waikato.kimt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.waikato.kimt.server.commands.MusicalCommand;
import com.waikato.kimt.server.commands.MusicalCommandFactory;
import com.waikato.kimt.server.commands.MusicalLoginCommand;
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
	Map<String, IClient> clients;
	volatile MusicalDataFrame current;
	
	public void start(int port) throws IOException
	{
		server = new ServerSocket(port);
		clients = new HashMap<String, IClient>();
		System.out.println("[KIMT]: Started server on port: " + server.getLocalPort());
		
		new ConnectionThread().start();
		new ConsoleListener().start();
	}
	
	public synchronized void insertClient(IClient c)
	{
		clients.put(c.getName(), c);
	}
	
	public synchronized Map<String, IClient> getClients()
	{
		return clients;
	}
	
	public void setSync(MusicalDataFrame sync) throws IOException {
        synchronized (current) {
                this.current = sync;
                
                // TODO:
                //      Test this ability to broadcast sync to all
                //      clients.
                Iterator it = getClients().entrySet().iterator();
				while(it.hasNext())
				{
					Map.Entry pairs = (Map.Entry)it.next();
					IClient c = (IClient)pairs.getValue();

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
				
				int i = 0;
				
				while(running)
				{
					try
					{
						Object read = null;
						
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
									insertClient(mlc.getClient());
								
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
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
			while(true)
			{
				try
				{
					String s;
					while((s = reader.readLine()) != null)
					{
						String[] data = s.split(" ");
						
						if (data[0].equals("list"))
						{
							Iterator it = clients.entrySet().iterator();
							
							while(it.hasNext())
							{
								Map.Entry pairs = (Map.Entry)it.next();
								System.out.println("Client: " + ((IClient)pairs.getValue()).getName());
							}
							System.out.println("---");
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
