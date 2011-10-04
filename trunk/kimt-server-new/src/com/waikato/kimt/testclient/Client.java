package com.waikato.kimt.testclient;

import java.net.Socket;

import com.waikato.kimt.networking.DerivedMessage;
import com.waikato.kimt.networking.NetMessage;
import com.waikato.kimt.networking.NetworkMonitor;

/**
 * An example client
 * @author Greg C
 *
 */
public class Client 
{
	public static void main(String[] args)
	{
		try
		{
			Socket s = new Socket("localhost",12310);
			NetworkMonitor network = new NetworkMonitor(s);
			NetMessage m;
			System.out.println("Client started");

			while(true)
			{
				m = network.readMessage();
				if (m != null)
				{	
					if (m instanceof DerivedMessage)	//If message is derived
					{
						DerivedMessage d = (DerivedMessage)m;
						System.out.println(d.message + " | " + d.msg.message);
						network.disposeMessage();
					}
					else	//Basic NetMessage
					{
						System.out.println(m.message);
						network.disposeMessage();
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
