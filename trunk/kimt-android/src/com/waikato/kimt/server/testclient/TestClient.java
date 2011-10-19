package com.waikato.kimt.server.testclient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

import com.waikato.kimt.sync.MusicalDataFrame;

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
	
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	public void run()
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.flush();
			new ListenThread().start();

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
			
			int i = 0;
			
			while(true)
			{
				oos.writeObject("KIMT 1.0");
				oos.flush();
				Thread.sleep(rand.nextInt(100));	//Simulate lag
				oos.writeObject("PUT SYNC");
				oos.flush();
				Thread.sleep(rand.nextInt(100));	//Simulate lag
				
				MusicalDataFrame mdf = new MusicalDataFrame();
				mdf.setTrackLocation("helloworld " + i);
				i++;
				oos.writeObject(mdf);
				oos.flush();
				
				//System.out.println(mdf);
				
				Thread.sleep(5000);
				
				//oos.writeObject("GET SYNC");
				//oos.flush();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	class ListenThread extends Thread
	{
		public void run()
		{
			try
			{
				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
				
				while(true)
				{
					Object o = ois.readObject();
					//System.out.println("GOT: " + o);
				}
			}
			catch (Exception ex)
			{
				//Do nothing
			}
		}
	}
}
