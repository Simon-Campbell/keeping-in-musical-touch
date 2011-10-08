package com.waikato.kimt.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import com.waikato.kimt.networking.NetMessage;

public class SyncServer 
{
	public static final String SETTINGS_FOLDER = "settings";
	public static final String SETTINGS_FILENAME = "kimt-server.settings";
	
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
			new ClientThread().start();
			
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
		
		//clients.add(c);
	}
	
	/**
	 * Returns a copy of the arraylist containing all clients
	 * @return
	 */
	private synchronized ArrayList<Client> getClients()
	{
		return clients;
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
	
	/**
	 * Threaded child listening and registering any new received connections.
	 * Connected clients are stored in an ArrayList<Client> for easy access.
	 * @author greg
	 *
	 */
	class ClientListenerThread extends Thread
	{
		public ClientListenerThread()
		{
			
		}
		
		public void run()
		{
			System.out.println("Server client listener thread started");
			
			try
			{
				while(isRunning())
				{
					Socket soc = ss.accept();
					Client newCli = new Client(soc);
					addClient(newCli);
					System.out.println("Added client: " + newCli);
					
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
	}
	
	/**
	 * Threaded Server logic. One thread handles many clients. 
	 * Clients are spread evenly across threads
	 * @author greg
	 *
	 */
	class ClientThread extends Thread
	{
		public ClientThread()
		{
		
		}
		
		/**
		 * Threaded logic for clients. A single thread handles multiple clients
		 */
		public void run()
		{
			System.out.println("Client logic thread started");
			Client currentClient = null;
			
			try
			{
				while(isRunning())	//while server is running
				{					
					for(Client c : getClients())
					{
						currentClient = c;	//save current client for exception handler
						
						//Read any received data
						Object read = c.getObjectInput().readObject();
						
						//Decode received data if available
						if (read != null)
						{
							if (read instanceof NetMessage)
							{
								NetMessage m = (NetMessage)read;
								//System.out.println(c + " sent: " + m.message);
							}
						}
						
						c.push(new NetMessage("MSG: " + c.i)); c.i++;
						c.getObjectOutput().writeObject(c.pop());
						c.getObjectOutput().flush();
					}
				}
				
				//Thread.sleep(10);
			}
			catch (SocketException ex)
			{
				System.out.println("Remote connection problem: assuming client has closed");
				
				if (currentClient != null)
				{	
					try 
					{
						currentClient.close();
					} 
					catch (IOException e) 
					{
						System.out.println("Problems closing: " + currentClient);
					}
					
					removeClient(currentClient);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
}