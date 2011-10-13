package com.waikato.kimt;

import com.waikato.kimt.server.testclient.TestClient;

public class KIMTTestClient 
{
	public static final String HOSTNAME = "localhost";
	public static final int defaultServerPort = ('k' + 'i' + 'm' + 't') * 128;
	
	public static void main(String[] args)
	{
		TestClient c = new TestClient(HOSTNAME, defaultServerPort);
	}
}
