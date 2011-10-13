package com.waikato.kimt.server.interfaces;

import java.net.Socket;

public interface IClient 
{
	public String getName();
	
	public IConnection getConnection();
}
