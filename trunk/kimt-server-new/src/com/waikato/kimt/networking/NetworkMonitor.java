package com.waikato.kimt.networking;

import java.io.BufferedReader;
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
	boolean sending = false;
	boolean reading = true;
	
	public synchronized void setSending(boolean val)
	{
		sending = val;
	}
	
	public synchronized boolean getSending()
	{
		return sending;
	}

	ArrayList<NetMessage> outgoingQueue;
	
	public NetworkMonitor(Socket socket) throws IOException
	{
		this.socket = socket;
		outgoingQueue = new ArrayList<NetMessage>();
		
		output = new ObjectOutputStream(socket.getOutputStream());
		input = new ObjectInputStream(socket.getInputStream());
		
		new NetworkReader().start();
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
		
		if (getSending() == false)
		{
			//setSending(true);
			new NetworkQueueTransmitter().start();	//Only way to call transmitter is via this method
		}
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
	
	ObjectInputStream input;
	
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
				//ObjectInputStream b = new ObjectInputStream(new BufferedReader(socket.getInputStream()));
				
				
				//ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				NetMessage newMsg;
				
				while(reading) //While thread should poll for incoming data (i.e. for the lifetime of this NetworkMonitor)
				{	
					try	//Try to read incoming message, handle any exceptions
					{
						newMsg = (NetMessage)input.readObject();	//Block until message is read
						if (newMsg != null)	//If a message has been read, store and notify observers
						{
							//System.out.println(newMsg + " ");
							setMessage(newMsg);
							setChanged();
							notifyObservers();
							Thread.sleep(10);
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
					catch (InterruptedException ex)
					{
						System.err.println("Interrupted sleep: " + ex.getMessage());
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
	
	ObjectOutputStream output;
	
	/**
	 * Responsible for writing contents of queue to the network connection
	 * Oldest data is written first
	 * @author Greg C
	 *
	 */
	class NetworkQueueTransmitter extends Thread
	{
		public NetworkQueueTransmitter()
		{
			setSending(true);
		}
		
		public void run()
		{
			boolean looped = true;
			
			try
			{
				//ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				output.flush();
				//System.out.println("output created and flushed");
				
				while(outgoingQueue.size() > 0)	//While there are items to write
				{
					try
					{
						//System.out.println("Sent item size: " + outgoingQueue.size());
						NetMessage msg = outgoingQueue.remove(0);
						output.writeObject(msg);
						output.flush();
					}
					catch (IOException ex)
					{
						System.err.println("Error sending message: " + ex.getMessage());
						looped = false;
					}
				}
				
				/*
				while(looped)	//Loop while there is stuff to send
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
							looped = false;
						}
					}
					else
					{
						output.flush();
						output.close();
						looped = false;
					}
				}
				*/
			}
			catch (IOException ex)
			{
				System.err.println("Error with ObjectOutputStream: " + ex.getMessage() + " " + outgoingQueue.size() + " items left in the queue");
			}
			finally
			{
				setSending(false);
			}
		}
	}
	

	/**
	 * Responsible for writing contents of queue to the network connection
	 * Oldest data is written first
	 * @author Greg C
	 *
	 */
	/*
	class NetworkQueueTransmitter extends Thread
	{
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
	*/
}
