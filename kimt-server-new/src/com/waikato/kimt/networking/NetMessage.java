package com.waikato.kimt.networking;

import java.io.Serializable;

/**
 * A basic message that provides a way to send a string across the network
 * All messages will derive from this class
 * @author Greg C
 *
 */
public class NetMessage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String message = "";
	
	/**
	 * Clones a new object from the original
	 * @param original
	 */
	public NetMessage(NetMessage original)
	{
		this.message = original.message;
	}
	
	public NetMessage(String msg)
	{
		this.message = msg;
	}
}
