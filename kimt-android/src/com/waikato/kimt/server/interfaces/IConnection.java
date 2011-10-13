package com.waikato.kimt.server.interfaces;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public interface IConnection 
{
	public Socket getSocket();
	
	public ObjectInputStream getInputStream();
	
	public ObjectOutputStream getOutputStream();
}
