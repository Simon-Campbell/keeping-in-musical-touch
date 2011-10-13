package com.waikato.kimt.server.testclient;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class TestClient extends Thread
{
	Socket s;
	String[] names = {"All", "right", "stop", "collaborate", "and", "listen",
			"Ice", "is", "back", "with", "my", "brand", "new", "invention" };
	Random rand;
	String name;
	
	public TestClient(String host, int port)
	{
		try
		{
			s = new Socket(host, port);
			rand = new Random();
			name = names[rand.nextInt(names.length)];	//Choose random name
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		this.start();
	}
	
	public void run()
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.flush();

			System.out.println("Logging in as: " + name);
			Thread.sleep(rand.nextInt(100));	//Simulate lag
			
			oos.writeObject("KIMT 1.0");
			Thread.sleep(rand.nextInt(100));	//Simulate lag
			
			oos.writeObject("LOGIN");
			Thread.sleep(rand.nextInt(100));	//Simulate lag
			
			oos.writeObject(name);
			Thread.sleep(rand.nextInt(100));	//Simulate lag
			
			oos.flush();
			Thread.sleep(rand.nextInt(100));	//Simulate lag
			
			while(true)
			{
				oos.writeObject("HEARTBEAT");
				
				Thread.sleep(1000);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
