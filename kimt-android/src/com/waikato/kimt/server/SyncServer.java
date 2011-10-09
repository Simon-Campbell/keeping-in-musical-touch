package com.waikato.kimt.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
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
import com.waikato.kimt.sync.CommandType;

public class SyncServer 
{
	public static final String SETTINGS_FOLDER = "settings";
	public static final String SETTINGS_FILENAME = "kimt-server.settings";
	public static final String VERSION = "KIMT 1.0";
	
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
					Client newCli = new Client(soc);
					addClient(newCli);
					System.out.println("Added client: " + newCli);
					
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
				while(isRunning())
				{
					int objectCount = 0;
					CommandType commandType = CommandType.UNKNOWN;
					
					//Read any received data
					Object read = null;
					
					//Decode received data if available
					while ((read = client.getObjectInput().readObject()) != null)
					{
						if (read instanceof String) 
						{
							String data = (String) read;
							System.out.println("READ: " + read + "OC: " + objectCount);
							
							// Reading the protocol, should probably put into a new
							// method when possible.
							if (objectCount == 0) 
							{
								if (data.compareTo("KIMT 1.0") != 0) 
								{
									System.out.println("This is not supported KIMT protocol-- read stopping.");
									System.out.println("Protocol: " + data);
									
									break;
								}
							} 
							else if (objectCount == 1) 
							{
								// Reading the command ..
								if (data.compareTo("LOGIN") == 0)
									commandType = CommandType.LOGIN;
								
								if (data.compareTo("PUT_SYNC") == 0)
									commandType = CommandType.PUT_SYNC;
								
								if (data.compareTo("GET_SYNC") == 0)
									commandType = CommandType.GET_SYNC;
								
								if (commandType == CommandType.PUT_SYNC)
								{
									System.out.println("Put sync");
									objectCount = 0;
								}
								if (commandType == CommandType.GET_SYNC)
								{
									System.out.println("Get sync");
									objectCount = 0;
								}
								if (commandType == CommandType.UNKNOWN)
								{
									System.out.println("Unknown command type");
									objectCount = 0;
								}
							}
							else 
							{
								if (commandType == CommandType.LOGIN) 
								{
									// Next in line should be the name of the person
									// connecting.
									client.name = data;
									System.out.println(client.name + " has connected");
									
									// TODO:
									//	Possibly improve this if we get time. For now, index 0
									//	in the array seems fair to be the owner.
									client.getObjectOutput().writeObject(clients.get(0) == client);
									client.getObjectOutput().flush();
									
									objectCount = 0;
								}
							}
							
							objectCount++;
						}
					}
					
					Thread.sleep(10);
				}
			}
			catch (SocketException ex)
			{
				
			}
			catch (Exception ex)
			{
				
			}
		}
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
					//System.out.println("fdsfafdasfdasfas");
					
					for(Client c : getClients())
					{				
						currentClient = c;
						
						String read = (String)currentClient.getObjectInput().readObject();

						System.out.println("READ: " + read);
						
						//System.out.println("______");
						//System.out.println("fdsfafdasfdasfas");
						
						/*
						
						*/
					}
				}
			} 
			catch (EOFException ex) 
			{
				System.out.println("End of file reached");
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