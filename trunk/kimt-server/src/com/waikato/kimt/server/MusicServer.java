package com.waikato.kimt.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MusicServer implements Runnable
{
	ServerSocket server;
	ArrayList<Group> groups;
	boolean serverRunning = true;
	
	public MusicServer(int port)
	{
		host(port);
		Group g = new Group("Name","Owner","http://www.test.com");
		groups.add(g);
		Group h = new Group("Name 2","Owner 2","http://www.testnumber2.com");
		groups.add(h);
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
			groups = new ArrayList<Group>();
			serverRunning = true;
			
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
		try
		{
			//Creates a new socket representing remote client connection
			//and starts a new thread listening for other clients
			Socket client = server.accept();
			Thread t = new Thread(this);
			t.start();
			System.out.println("Client connected");
				
			//Create BufferedReader and BufferedWriter pointing to clients respective input / output steams
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				
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
						processGET(reqArray, writer);
				}
				//If request is an add request - Used to add a new group to the server
				else if (reqArray[0].equals("ADD"))
				{
					processADD(reqArray);
				}
			}
			System.out.println("Connection to client closed");
			client.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * Processes a single get request
	 * @param requests
	 * @param bw
	 */
	private void processGET(String[] requests, BufferedWriter bw)
	{
		System.out.println("----------GET---------");
		
		if (requests[1].equals("/ALL"))
		{
			System.out.println("----------ALL---------");
			
			try
			{
				//respond by sending the entire collection of groups in XML form
				
				bw.write("HTTP/1.1 200 OK");
				bw.write("Content-type: application/xhtml+xml");
				bw.write("");
				//bw.write(groupDataToXML());	
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		//System.out.println(groupDataToXML());
		System.out.println(groupDataToCSV());
	}
	
	/**
	 * Processes an ADD request to the server
	 */
	private void processADD(String[] requests)
	{
		if (requests[1].equals("/GROUP"))
		{
			String gName = requests[2];
			String gOwner = requests[3];
			String gURL = requests[4];
			
			Group g = new Group(gName, gOwner, gURL);
			groups.add(g);
		}
	}
	
	/**
	 * Generates a string with simple XML formatting
	 * @return
	 */
	private String groupDataToXML()
	{
		String sxml = "<?xml version=\"1.0\"?><root>";
		for(Group g : groups)
		{
			sxml += "<group><name>"+g.getGroupName()+"</name><owner>"+g.getGroupOwner()+"</owner></group>";
		}
		sxml += "</root>";
			
		return sxml;
	}
	
	private String groupDataToCSV()
	{
		String csvData = "";
		for(Group g : groups)
		{
			csvData += g.serialiseAsCsv();
		}
		return csvData;
	}
}
