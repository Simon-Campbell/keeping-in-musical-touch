package com.waikato.kimt.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import com.waikato.kimt.groupmanager.Group;
import com.waikato.kimt.groupmanager.GroupManager;

public class SyncServer implements Runnable
{
	ServerSocket server;
	GroupManager groupManager;
	
	public SyncServer(int port)
	{
		host(port);
		groupManager = new GroupManager();
		Group g = new Group("Name","Owner","http://www.test.com");
		groupManager.insert(g);
		Group h = new Group("Name 2","Owner 2","http://www.testnumber2.com");
		groupManager.insert(h);
		groupManager.print();
	}
	
	/**
	 * Hosts a new instance of a server
	 * @param port
	 */
	public void host(int port)
	{
		try
		{
			server = new ServerSocket(port);
			
			Thread t = new Thread(this);
			t.start();
			
			System.out.println("Server hosted on port " + server.getLocalPort());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@Override
	/**
	 * Implements threaded server logic.
	 * Listens for incoming clients, decodes requests and returns the relevant information
	 */
	public void run()
	{
		boolean serverRunning = true;
		
		try
		{
			//Creates a new socket representing remote client connection
			//and starts a new thread listening for other clients
			Socket client = server.accept();
			Thread t = new Thread(this);
			t.start();
			System.out.println("Client connected - thread started");
				
			//Create BufferedReader and BufferedWriter pointing to clients respective input / output steams
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			//BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
				
			while(serverRunning)	//Used to terminate the server
			{
				//Decodes the received request
				String request;
				while ((request = reader.readLine()) != null)
				{
					System.out.println("Received request " + request);
					String[] reqArray = request.split(" ");
						
					//Uncomment to get printout of client request strings
					for(String s : reqArray)
					{
						System.out.println(s);
					}
						
					//If request is a get request - used to provide clients with group information
					if (reqArray[0].equals("GET"))
					{
						//If request is for library
						if (reqArray[1].equals("LIBRARY"))
						{
							//Write the groupManager to the object stream
							System.out.println("Sending deflated GroupManager");
							oos.writeObject(groupManager);
						}
					}
				}
			}
			System.out.println("Connection to client closed");
			client.close();
		}
		catch (SocketException ex)
		{
			System.err.println("Client disconnected - thread closing " + "Remaining threads: " + Thread.activeCount());
			serverRunning = false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
