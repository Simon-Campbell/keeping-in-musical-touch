package com.waikato.kimt.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.waikato.kimt.server.interfaces.IConnection;

public class Connection implements IConnection
{
	Socket socket;
	ObjectInputStream input;
	ObjectOutputStream output;
	
	public Connection(Socket socket) throws IOException
	{
		this.socket = socket;
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
	}

	@Override
	public Socket getSocket() 
	{
		return socket;
	}

	@Override
	public ObjectInputStream getInputStream() 
	{
		return input;
	}

	@Override
	public ObjectOutputStream getOutputStream() 
	{
		return output;
	}
	
	
}
