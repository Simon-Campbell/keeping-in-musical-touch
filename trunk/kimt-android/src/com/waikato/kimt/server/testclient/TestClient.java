package com.waikato.kimt.server.testclient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class TestClient extends Thread
{
	Socket s;
	String[] names = {"All", "right", "stop", "collaborate", "and", "listen",
			"Ice", "is", "back", "with", "my", "brand", "new", "invention", 
			"Oden!", "Guide", "our", "ships",
			"Our", "axes,", "spears", "and", "swords",
			"Guide", "us", "through", "storms", "that", "whip",
			"And", "in", "brutal", "war"};
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
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

			System.out.println("Logging in as: " + name);
			Thread.sleep(rand.nextInt(100));	//Simulate lag
			
			oos.writeObject("KIMT 1.0");
			oos.flush();
			Thread.sleep(rand.nextInt(100));	//Simulate lag
			
			oos.writeObject("LOGIN");
			oos.flush();
			Thread.sleep(rand.nextInt(100));	//Simulate lag
			
			oos.writeObject(name);
			oos.flush();
			Thread.sleep(rand.nextInt(100));	//Simulate lag
			
			oos.flush();
			Thread.sleep(rand.nextInt(100));	//Simulate lag
			
			while(true)
			{
				Object o = ois.readObject();
				System.out.println((String)o);
				
				Thread.sleep(1000);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
