package com.waikato.kimt.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import com.waikato.kimt.sync.MusicalDataFrame;

public class SyncServer 
{
	public static final String SETTINGS_FOLDER = "settings";
	public static final String SETTINGS_FILENAME = "kimt-server.settings";
	public static final String VERSION = "KIMT 1.0";
	
	/**
	 * The MusicalDataFrame that a new client will be sent
	 * when connecting to the server.
	 */
	private volatile MusicalDataFrame current ;
	
	ServerSocket ss;
	int port;

	ArrayList<Client> clients;
	
	boolean running = true;
	
	public SyncServer(int port)
	{
		this.port = port;
		clients = new ArrayList<Client>();
	}
	
	/**
	 * Starts the server
	 */
	public void start()
	{
		try
		{
			System.out.print("Starting server");
			ss = new ServerSocket(port);
			System.out.print("... done!\n");
			
			new ClientListenerThread().start();
			new ServerConsoleUI().start();
			//new ClientThread().start();
			
			System.out.print("Ready for client connections\n");
		}
		catch (IOException ex)
		{
			System.err.println("Could not start ServerSocket");
		}
	}
	
	/**
	 * Saves the current server settings to local harddisk.
	 * @param fos
	 */
	public void saveSettings(FileOutputStream fos)
	{
		try
		{
			BufferedWriter w = new BufferedWriter(new FileWriter(SETTINGS_FILENAME));
			w.write("port " + port);
			
			w.close();
		}
		catch (FileNotFoundException ex)
		{
			System.err.println("The settings file does not exist!: " + ex.getMessage());
		}
		catch (IOException ex)
		{
			System.err.println("Error in reading the settings file: " + ex.getMessage());
		}
	}
	
	/**
	 * Loads Settings from an existing file
	 * @return New SyncServer object
	 */
	public static SyncServer loadSettings()
	{
		int s_port = 12345;
		
		try
		{
			BufferedReader r = new BufferedReader(new FileReader(SETTINGS_FILENAME));
			String line;
			
			while((line = r.readLine()) != null)
			{
				String[] data = line.split(" ");
				
				if (data[0].equals("port"))
					s_port = Integer.valueOf(data[1]);		
			}
			
			r.close();
		}
		catch (FileNotFoundException ex)
		{
			System.err.println("The settings file does not exist!: " + ex.getMessage());
		}
		catch (IOException ex)
		{
			System.err.println("Error in reading the settings file: " + ex.getMessage());
		}
		
		return new SyncServer(s_port);
	}
	
	/**
	 * Check whether the server is currently running. Used to terminate
	 * infinite loops in child threads
	 * @return
	 */
	public synchronized boolean isRunning()
	{
		return running;
	}
	
	/**
	 * Set the server status.
	 * A false value will terminate any infinite loop depending on this variable
	 * @param val State of server (false terminates server)
	 */
	public synchronized void setRunning(boolean val)
	{
		this.running = val;
	}
	
	/**
	 * Registers a new client with the server
	 */
	private synchronized void addClient(Client c)
	{
		clients.add(c);
	}
	
	/**
	 * Returns a copy of the arraylist containing all clients
	 * @return
	 */
	private synchronized ArrayList<Client> getClients()
	{
		return clients;
	}
	
	public synchronized ArrayList<Client> getLoggedInClients()
	{
		ArrayList<Client> loggedIn = new ArrayList<Client>();
		for(Client c : getClients())
		{
			if (c.name != null)
				loggedIn.add(c);
		}
		
		return loggedIn;
	}
	
	/**
	 * Removes an existing client from the server. Returns a boolean indicating whether this operation
	 * was successful
	 * @param cli
	 * @return True if client instance was removed
	 */
	private synchronized boolean removeClient(Client cli)
	{
		return clients.remove(cli);
	}
	
	public void setSync(MusicalDataFrame sync) throws IOException {
		synchronized (current) {
			this.current = sync;
			
			// TODO:
			//	Test this ability to broadcast sync to all
			//	clients.
			for (Client c : clients) {
				c.getObjectOutput().writeObject(VERSION);
				c.getObjectOutput().writeObject("PUT SYNC");
				c.getObjectOutput().writeObject(this.current);
			}
		}
	}
	
	/**
	 * Threaded child listening and registering any new received connections.
	 * Connected clients are stored in an ArrayList<Client> for easy access.
	 * @author greg
	 */
	class ClientListenerThread extends Thread
	{
		public void run()
		{
			System.out.println("Server client listener thread started");
			
			try
			{
				while(isRunning())
				{
					Socket soc = ss.accept();
					System.out.println("Accepted socket connection from " + soc.toString());
					
					Client newCli = new Client(soc);
					
					System.out.println("New client has been created ..");
					addClient(newCli);
					System.out.println("Added client " + newCli);
					
					new SingleClientThread(newCli).start();
					
					Thread.sleep(10);
				}
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Shuts down the server
	 */
	public void shutdown()
	{
		setRunning(false);
		
		for(Client c : getClients())
		{
			c.socket = null;
		}
	}
	
	class SingleClientThread extends Thread
	{
		public SingleClientThread(Client c)
		{
			this.client = c;
		}
		
		Client client;
		
		public void run()
		{
			System.out.println("Client logic thread started");
	
			try
			{
				ObjectOutputStream out = new ObjectOutputStream(client.socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(client.socket.getInputStream());	

				while(isRunning())
				{
					//Read any received data
					Object read = null;
					
					// Decode received data if available
					while ((read = in.readObject()) != null)
					{
						if (read instanceof String)
						{
							String data = (String) read;
							
							// Reading the protocol, should probably put into a new
							// method when possible.
							if (data.compareTo(VERSION) != 0) {
								System.out.println("This is not supported KIMT protocol-- read stopping.");
								System.out.println("Protocol: " + data);
							} else {
								System.out.println("Accepted " + VERSION + " connection data frame.");
								System.out.println("Getting the musical command ..");
								
								MusicalCommand
									mc = MusicalCommandFactory.getMusicalCommand(in);
								
								if (mc != null) { 
									try {
										mc.process(in, SyncServer.this, client); 
									} catch (Exception e) {
										System.err.println(e.toString());
									}
								} else {
									System.err.println("The command '" + MusicalCommandFactory.getLastCommand() + "' is not recognized.");
								}
							}
							
						}
					}
					
					Thread.sleep(10);
				}
				
				removeClient(client);
			}
			catch (SocketException ex)
			{
				ex.printStackTrace();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Thread dedicated to monitoring console input - used to control server
	 * @author greg
	 */
	class ServerConsoleUI extends Thread
	{	
		public void run()
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
			while(isRunning())
			{
				try
				{
					String line;
					while((line = reader.readLine()) != null)
					{
						String[] data = line.split(" ");
						
						if (data[0].equals("list"))
						{
							if (data[1].equals("all"))
							{
								for(int i = 0; i < getClients().size(); i++)
								{
									Client c = getClients().get(i);
									
									if (i == 0)
										System.out.println("LEADER: Name: " + c.getName());
									else
										System.out.println("MEMBER: Name: " + c.getName());
								}
							}
							else if (data[1].equals("log"))
							{
								for(Client c : getLoggedInClients())
								{
									System.out.println(c.getName());
								}
							}

							System.out.println("__________");
						}
						if (data[0].equals("kick"))
						{
							String name = data[1];
							
							for(Client c : getClients())
							{
								if (c.getName().equals(name))
								{
									removeClient(c);
									System.out.println("Removed: " + c.getName() + " from connected clients");
									return;
								}
							}
							System.err.println("Failed to removed: " + name + " from connected clients");
							System.out.println("__________");
						}
						if (data[0].equals("quit"))
						{
							System.out.println("Shutting down server");
							shutdown();
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