package com.waikato.kimt.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.waikato.kimt.greenstone.MusicLibrary;
import com.waikato.kimt.greenstone.MusicView;

public class KIMTSync implements DigitalLibrarySync, Serializable {
	private String	connectionLocation	= "localhost";
	private int		connectionPort		= 55936;
	
	private MusicLibrary	currentLibrary;
	private MusicView		currentView;
	private Socket			kimtSocket;
	
	/**
	 * The serial version of this object. Update when the object changes.
	 */
	private static final long serialVersionUID = 1L;

	public KIMTSync() throws UnknownHostException, IOException {
		this("localhost", 55936);
	}
	
	public KIMTSync(String location, int port) throws UnknownHostException, IOException {
		this.connectionLocation	= location;
		this.connectionPort		= port;
		
		Log.v("Debugging", "Creating connection");
		kimtSocket			= new Socket(InetAddress.getByName(location), port);
		Log.v("Debugging", "KIMTSync loaded ..");
	}
	
	/**
	 * Starts the listening thread which will callback use the specified
	 * handler to update using the updater.
	 * @param handler
	 * 	The handler to handle the posting of the data to UI thread.
	 * @param updater
	 * 	The class to run in order to update the UI.
	 * @throws IOException
	 */
	public void startListening(Handler handler, Runnable updater) throws IOException {
		new BroadcastListener(handler, updater).start();
	}
	
	@Override
	public boolean isValidSyncLocation(String location) {
		// TODO Auto-generated method stub
		return false;
	}

	public MusicLibrary getRemoteLibrary() {
		return this.currentLibrary;
	}
	
	@Override
	public void downloadRemoteLibrary() {
		new DownloadLibraryTask().execute(kimtSocket);
	}

	public void setRemoteLibrary(MusicLibrary ml) {
		new UploadLibraryTask().execute(kimtSocket);
	}
	
	@Override
	public void setRemoteView(MusicView mv) {
		
	}
	
	@Override
	public String toString() {
		return this.connectionLocation + ":" + Integer.toString(this.connectionPort) + " " + this.currentLibrary.toString();
	}
	
	private class LoginUserTask extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {
			try {
				OutputStream os = kimtSocket.getOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(os);
				
				out.writeChars(new String("KIMT 1.0"));
				out.writeChars(new String("LOGIN " + params[0]));
				out.flush();
				
			//	out.close();
				os.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
	
	private class UploadLibraryTask extends AsyncTask<Socket, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Socket... params) {
			Socket
				s = params[0];
			
			ObjectOutputStream
				oos		= null;
			
			Boolean
				uploaded= true;
			
			try {
				oos = new ObjectOutputStream(s.getOutputStream());
				
				// The program name/version
				oos.writeChars("KIMT 1.0");
				// The text command that is sent over the network
				oos.writeChars("LIBRARY UPLOAD");
				
				oos.writeObject(currentLibrary);
				
			} catch (IOException e) {
				uploaded = false;
				
				e.printStackTrace();
			} finally {
				if (oos != null)
					try {
						oos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
			return uploaded; 
		}
		
		@Override
		protected void onPostExecute(Boolean uploaded) {
			notifySyncLibraryUploaded(uploaded);
		}
		
	}
	
	private class DownloadLibraryTask extends AsyncTask<Socket, Integer, KIMTSync> {

		@Override
		protected KIMTSync doInBackground(Socket... params) {
			Socket
				s	= params[0];
			
			ObjectInputStream
				ois = null;
			
			KIMTSync
				ks	= null;
			
			try {
				ois	= new ObjectInputStream(s.getInputStream());
				ks	= (KIMTSync) ois.readObject();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (ois != null)
					try {
						ois.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
			return
					ks;
		}
		
		@Override
		protected void onPostExecute(KIMTSync result) {
			notifySyncLibraryDownloaded(result);
		}
	}

	private List<SyncedLibraryUpdateListener>
		registeredLibraryUpdateListeners = new ArrayList<SyncedLibraryUpdateListener>();
	
	public void setOnSheetMetaDataUpdateListener(SyncedLibraryUpdateListener slul) {
		registeredLibraryUpdateListeners.add(slul);
	}
	
	/**
	 * Notify all listeners that there has been a new sync object that has been
	 * downloaded from the sync server.
	 * @param k
	 * 	The sync object that was downloaded from the sync server.
	 */
	private void notifySyncLibraryDownloaded(KIMTSync k) {
		for (SyncedLibraryUpdateListener s : registeredLibraryUpdateListeners) {
			s.onSyncDownloaded(k);
		}
	}
	
	/**
	 * Notify all listeners that the library on this client has been
	 * uploaded.
	 * @param k
	 * 	The library that was uploaded.
	 */
	private void notifySyncLibraryUploaded(Boolean uploaded) {
		for (SyncedLibraryUpdateListener s : registeredLibraryUpdateListeners) {
			s.onSyncUploaded(uploaded);
		}
	}
	
	/**
	 * Notify all listeners that the synced view has changed on the
	 * server.
	 * @param mv
	 * 	The new music view that is to be syncronised.
	 */
	private void notifySyncedViewChanged(MusicView mv) {
		for (SyncedLibraryUpdateListener s : registeredLibraryUpdateListeners) {
			s.onSyncViewUpdate(mv);
		}
	}
	
	private void notifySyncedNotification() {
		for (SyncedLibraryUpdateListener s : registeredLibraryUpdateListeners) {
			s.onSyncUpdateNotification();
		}
	}

	@Override
	public void setOnSyncUpdateListener(SyncedLibraryUpdateListener slul) {
		registeredLibraryUpdateListeners.add(slul);
	}
	
	private class BroadcastListener extends Thread {
		/**
		 * http://developer.android.com/resources/faq/commontasks.html#threading
		 */
		private Handler		 handler;
		private Runnable	 updater;
		
		public BroadcastListener(Handler handler, Runnable updater) throws IOException {
			this.handler		= handler;
			this.updater		= updater;
		}
		
		public void run() {
			BufferedReader br ;
			String line ;
			
			Log.v("Debugging", "Connecting socket ..");
		
			Log.v("Debugging", "Starting BroadcastListener ..");
			
			try {
		
				char[] buf	= new char[1024];

				Log.v("Debugging", "Before creating reader ...");
				br = new BufferedReader(new InputStreamReader(kimtSocket.getInputStream()));
				Log.v("Debugging", "Before reading ..");
				Log.v("Debugging", "Created reader on " + kimtSocket.getInetAddress().toString() + ":" + Integer.toString(kimtSocket.getPort()));
				
				int bytesRead = br.read(buf);
				
				while (bytesRead != -1) {
					line = new String(buf);

					Log.v("Debugging", "KIMTSync heard: " + line);	
					handler.post(updater);
					
					bytesRead = br.read(buf);
				}
					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean login(String userName) {
		new LoginUserTask().execute(userName);
		
		return false;
	}

	@Override
	public void downloadRemoteSync() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void downloadRemoteView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MusicView getRemoteView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocalView(MusicView mv) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MusicView getLocalView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLibraryLocation() {
		// TODO Auto-generated method stub
		return null;
	}

}
