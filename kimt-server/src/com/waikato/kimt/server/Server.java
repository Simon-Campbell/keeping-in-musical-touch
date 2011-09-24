package com.waikato.kimt.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Contains main server logic
 * @author Greg C
 *
 */
public class Server 
{
	private	ArrayList<Group> groups;
	
	/**
	 * The port that the server will listen for data on.
	 */
	private final int	socketListenPort = ('k' + 'i' + 'm' + 't') * 128; /* 55,936 */
	
	/**
	 * The maximum number of bytes that can be read at any given time
	 * from the server socket.
	 */
	@SuppressWarnings("unused") // We may need to use a byte reader in future: will keep this.
	private final int	socketBufferSize = 1024;
	
	public Server()
	{
		groups = new ArrayList<Group>();
		
		// Start executing the socket thread so that
		// data can be read
		new SocketReadingThread().run();
	}

	private class SocketReadingThread extends Thread {
		/**
		 * If this is true then this thread is actively
		 * reading data from the socket; set to false to
		 * stop reading.
		 */
		private boolean isReading = true;
		
		public SocketReadingThread() {
		}
		
		public void run() {
			ServerSocket ss = null;
			
			try {
				ss = new ServerSocket(socketListenPort);
				
				System.out.println("KIMTServer: The server is listening on port " + ss.getLocalPort());
				
				while (isReading) {
					Socket
						s = ss.accept();
					
					BufferedReader
						br = new BufferedReader(new InputStreamReader(s.getInputStream()));
					
					String
						line = br.readLine();
					
					while (line != null) {
						// Print the data that has been sent to the server
						// for debugging purposes.
						System.out.println(this + ": " + line);
						
						// Start a new thread to write to the socket. This should probably
						// be parametised with the data that we'd like to send back to the
						// client.
						new SocketWritingThread(s).start();
						
						// Read the next line from the socket
						line = br.readLine();
					} 
					
				}
			} catch (IOException e) {
				System.err.println("error: Unable to create a socket to read on " + socketListenPort);
			} finally {
				if (ss != null) {
					try {
						ss.close();
					} catch (IOException e) {
						System.err.println("error: The socket could not be closed. It may already be closed.");
					}
				}
			}

		}
	}
	
	private class SocketWritingThread extends Thread {
		Socket s = null;
		
		public SocketWritingThread(Socket s) {
			this.s = s;
		}
		
		public void run() {
			try {
				BufferedWriter
					bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				
				// Write a small header so that we know it's a KIMT
				// response. The version number is included in-case of protocol
				// changes which are very likely to happen during development.
				bw.write("KIMT 0.1\r\n");
				
				// Write a custom string, this can be anything (for v0.1 of the protocol)
				bw.write("Test response\r\n");
				
			} catch (IOException e) {
				System.err.println("error: Could not write data to socket.");
			}
		}
		
	}
}
