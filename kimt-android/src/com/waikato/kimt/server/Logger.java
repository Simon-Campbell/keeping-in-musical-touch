package com.waikato.kimt.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Logger 
{
	private Logger()
	{
		
	}
	
	static BufferedWriter writer;

	private static boolean ready() throws IOException
	{
		if (writer == null)
		{
			writer = new BufferedWriter(new FileWriter("log.txt"));
			writer.write("---LOG: " + System.currentTimeMillis() + "---");
			writer.flush();
			return true;
		}
		return false;
	}
	
	public static void insert(Object source, String s)
	{
		try
		{
			if (ready())
			{
				writer.append(source.getClass().getCanonicalName() + ": " + s + "\n");
				writer.flush();
				writer.close();
			}
		}
		catch (Exception ex)
		{
			
		}
	}
	
	public static void printLog() throws IOException
	{
		if (new File("log.txt").exists())
		{
			BufferedReader reader = new BufferedReader(new FileReader("log.txt"));
			
			String s;
			while((s = reader.readLine()) != null)
			{
				System.out.println(s);
			}
		}
		else
		{
			System.out.println("[logger] no log found");
		}
	}
	
	public static void clearLog() throws IOException
	{
		File f = new File("log.txt");
		
		if (f.exists())
		{
			f.delete();
		}
	}
}
