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
	
	public final String version = "VERSION: kimt 0.1\n";
	
	/**
	 * Sends a request to the remote server.
	 * e.g. GET LIBRARY
	 * @param msg The string command to send
	 */
	public void sendRequest(String msg)
	{
		try
		{
			msg += "\n";
			writer.write(version);
			writer.write(msg);
			writer.flush();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * Reads an object from the specified stream
	 * @param input ObjectInputStream to read from
	 * @return Returns an object or null
	 */
	public Object readObject(ObjectInputStream input)
	{
		try
		{
			return input.readObject();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void run()
	{
		try
		{
			//debug stuff
			FileWriter w = new FileWriter("E:\\GroupManagerClientState.txt");
			
			while(true)
			{
				//Reads a generic object from the server
				//Object o = ois.readObject();
				Object o = readObject(ois);
				
				//If the received object is an instance of GroupManager, then update groupmanager
				if (o instanceof GroupManager)
					groupManager = (GroupManager)o;

				//debug stuff
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
