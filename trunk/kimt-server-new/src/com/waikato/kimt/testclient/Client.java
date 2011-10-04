package com.waikato.kimt.testclient;

import java.net.Socket;

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
			Socket s = new Socket("localhost",12345);
			NetworkMonitor network = new NetworkMonitor(s);
			NetMessage m;
			System.out.println("Client started");

			while(true)
			{
				m = network.readMessage();
				if (m != null)
				{
					System.out.println(m.message);
					network.disposeMessage();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
