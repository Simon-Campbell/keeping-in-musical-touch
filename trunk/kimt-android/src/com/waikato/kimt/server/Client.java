package com.waikato.kimt.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client 
{
	String name;	public String getName() { return name; }
	Socket socket;
	ArrayList<Object> received;
	ObjectInputStream input;
	ObjectOutputStream output;
	BufferedReader reader;
	BufferedWriter writer;
	
	public int i = 0;
	
	public Client(Socket socket) throws IOException
	{
		this.socket = socket;
		input = new ObjectInputStream(this.socket.getInputStream());
		output = new ObjectOutputStream(this.socket.getOutputStream());
	}
	
	/**
	 * Gets the current ObjectInputStream for this client
	 * @return ObjectInputStream
	 */
	public ObjectInputStream getObjectInput()
	{
		return input;
	}
	
	/***
	 * Gets the current ObjectOutputStream for this client
	 * @return ObjectOutputStream
	 */
	public ObjectOutputStream getObjectOutput()
	{
		return output;
	}
	
	/**
	 * Gets this clients socket
	 * @return Copy of socket
	 */
	public Socket getSocket()
	{
		return socket;
	}
	
	/**
	 * Compares this client to another specified client
	 * @param other Client to compare to.
	 * @return True if the two objects have the same data
	 */
	public boolean equals(Client other)
	{
		if (other.name.equals(name))
			return true;
		return false;
	}
	
	/**
	 * Pops the oldest message object off the stack
	 * @return Null if empty or the oldest object
	 */
	public Object pop()
	{
		if (received.size() > 0)
			return received.remove(0);
		return null;
	}
	
	/**
	 * Pushes a new message object onto the stack
	 * @param o New message object to push onto the stack
	 */
	public void push(Object o)
	{
		if (received == null)
			received = new ArrayList<Object>();
		received.add(o);
	}
	
	/**
	 * Closes this Client. Readying it for disposal
	 * @throws IOException Error closing client
	 */
	public void close() throws IOException
	{
		input.close();
		output.flush();
		output.close();
	}
}

