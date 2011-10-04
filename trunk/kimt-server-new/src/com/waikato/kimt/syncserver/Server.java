package com.waikato.kimt.syncserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.waikato.kimt.networking.DerivedMessage;
import com.waikato.kimt.networking.NetMessage;
import com.waikato.kimt.networking.NetworkMonitor;

/**
 * The server
 * @author Greg C
 *
 */
public class Server
{
	public static void main(String[] args)
	{
		@SuppressWarnings("unused")
		Server server = new Server(12310);
	}
	
	/*
	ServerSocket ss;
	
	public Server(int port)
	{
		try
		{
			ss = new ServerSocket(port);
		}
		catch (IOException ex)
		{
			System.err.println(ex.getMessage());
		}
	}
	
	class Logic extends Thread
	{
		public void run()
		{

		}
	}
	*/

	ServerSocket ss;
	
	public Server(int port)
	{
		try
		{
			ss = new ServerSocket(port);
			new ServerThread().start();
			System.out.println("Server started");
		}
		catch (IOException ex)
		{
			ex.getStackTrace();
			System.err.println(ex.getMessage());
		}
	}
	
	class ServerThread extends Thread
	{
		public void run()
		{
			int i = 0;
			
			try
			{
				Socket client = ss.accept();
				new ServerThread().start();
				NetworkMonitor network = new NetworkMonitor(client);
				System.out.println("Client connected " + client.getInetAddress().getHostAddress());
				
				while(true)
				{
					try
					{
						//DerivedMessage msg = new DerivedMessage();
						NetMessage msg = new NetMessage("Package: " + i);
						network.sendMessage(msg);
						Thread.sleep(1000);
						i++;
					}
					catch (IOException ex)
					{
						System.err.println(ex.getMessage());
						return;
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
			}
			catch (IOException ex)
			{
				ex.getStackTrace();
				System.err.println(ex.getMessage());
			} 
		}
	}
}
