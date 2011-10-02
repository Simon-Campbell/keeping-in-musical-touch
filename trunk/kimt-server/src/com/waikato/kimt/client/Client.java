package com.waikato.kimt.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.waikato.kimt.groupmanager.GroupManager;

public class Client extends Thread
{
	public Client(String host, int port)
	{
		try
		{
			Socket server = new Socket(host,port);
			ois = new ObjectInputStream(server.getInputStream());
			writer = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		this.start();
		
		//sendRequest("GET LIBRARY");
	}

	GroupManager groupManager;
	
	Socket server;
	ObjectInputStream ois;
	BufferedWriter writer;
	
	/**
	 * Sends a request to the remote server.
	 * e.g. GET LIBRARY
	 * @param msg
	 */
	public void sendRequest(String msg)
	{
		try
		{
			msg += "\n";
			writer.write(msg);
			writer.flush();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void run()
	{
		try
		{
			FileWriter w = new FileWriter("D:\\gm.txt");
			
			while(true)
			{
				//Reads a generic object from the server
				Object o = ois.readObject();
				
				//If the received object is an instance of GroupManager, then update groupmanager
				if (o instanceof GroupManager)
					groupManager = (GroupManager)o;

				groupManager.print();
				
				w.write(groupManager.toString());
				w.close();
				
				System.out.println("derp");
			}
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
}
