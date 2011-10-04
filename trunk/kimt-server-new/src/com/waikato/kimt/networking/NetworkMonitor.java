package com.waikato.kimt.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

/**
 * A NetworkMonitor. Sits on either end of a 1 : 1 connection and mediates data transfer between the two.
 * Messages can be retrieved via the readMessage() method
 * Messages can be sent via the sendMessage() method
 * It is important to disposeMessage() after reading it, to prevent duplication (until I fix this)
 * @author Greg C
 *
 */
public class NetworkMonitor extends Observable
{
	Socket socket;
	boolean sending = true;
	boolean reading = true;

	ArrayList<NetMessage> outgoingQueue;
	
	public NetworkMonitor(Socket socket) throws IOException
	{
		this.socket = socket;
		outgoingQueue = new ArrayList<NetMessage>();
		
		new NetworkReader().start();
		new NetworkQueueTransmitter().start();
	}
	
	NetMessage latest;
	
	/**
	 * Reads the last received message and disposes of it
	 * @return
	 */
	public synchronized NetMessage readMessage()
	{
		return latest;
		
		/*
		NetMessage dup = new NetMessage(latest);
		latest = null;
		System.out.println("READ CONTENTS: " + dup.message);
		return dup;
		*/
	}
	
	private synchronized void setMessage(NetMessage msg)
	{
		latest = msg;
	}
	
	public void disposeMessage()
	{
		latest = null;
	}
	
	/**
	 * Sends a serialized NetMessage
	 * @param msg
	 * @throws IOException
	 */
	public void sendMessage(NetMessage msg) throws IOException
	{
		outgoingQueue.add(msg);
	}
	
	/**
	 * Shuts down the server, killing all pipes and sockets. Flushes connections if necessary
	 */
	public void shutdown()
	{
		try
		{
			socket.close();
		}
		catch (IOException ex)
		{
			System.err.println("NetworkMonitor shutdown problem: " + ex.getMessage());
		}
	}
	
	/***
	 * Responsible for reading data from the network. 
	 * This data is then made available for other objects to access
	 * @author Greg C
	 *
	 */
	class NetworkReader extends Thread
	{
		/**
		 * Threaded NetworkMonitor code, for polling input streams for new data
		 * Thread is blocked while waiting for a new message
		 */
		public void run()
		{
			try
			{
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				NetMessage newMsg;
				
				while(reading) //While thread should poll for incoming data (i.e. for the lifetime of this NetworkMonitor)
				{	
					try	//Try to read incoming message, handle any exceptions
					{
						
						newMsg = (NetMessage)input.readObject();	//Block until message is read
						if (newMsg != null)	//If a message has been read, store and notify observers
						{
							setMessage(newMsg);
							//System.out.println(newMsg.message);
							setChanged();
							notifyObservers();
						}
					}
					catch (IOException ex)
					{
						System.err.println("Error reading incoming object: " + ex.getMessage());
					}
					catch (ClassNotFoundException ex)
					{
						System.err.println("Received message is of non supported type: " + ex.getMessage());
						latest = null;
					}
				}
				
				input.close();	//Close the input stream
			}
			catch (IOException ex)
			{
				System.err.println("Error opening ObjectInputStream: " + ex.getMessage());
			}

		}
	}
	
	/**
	 * Responsible for writing contents of queue to the network connection
	 * Oldest data is written first
	 * @author Greg C
	 *
	 */
	class NetworkQueueTransmitter extends Thread
	{
		/**
		 * Threaded NetworkMonitor code, for writing the contents of the local NetMessage queue to the network
		 */
		public void run()
		{
			try
			{
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				
				while(sending)	//Continuously loop
				{
					if (outgoingQueue.size() > 0)	//If there is data to send
					{
						try	//Try to send the oldest item in the queue, exceptions are then handled
						{
							System.out.println("Sent item size: " + outgoingQueue.size());
							NetMessage msg = outgoingQueue.remove(0);
							output.writeObject(msg);
							output.flush();
						}
						catch (IOException ex)
						{
							System.err.println("Error sending message: " + ex.getMessage());
						}
					}
				}
			}
			catch (IOException ex)
			{
				System.err.println("Error opeining ObjectOutputStream: " + ex.getMessage());
			}
		}
	}
}
