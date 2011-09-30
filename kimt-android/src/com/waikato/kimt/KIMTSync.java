package com.waikato.kimt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Handler;

public class KIMTSync implements DigitalLibrarySync, Serializable {
	private String	connectionLocation	= "localhost";
	private int		connectionPort		= 55936;
	
	private MusicLibrary	current		;
	private Socket			kimtSocket	;
	
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
		this.kimtSocket			= new Socket(location, port);
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
	public boolean isValidRemoteLibrary(String location) {
		// TODO Auto-generated method stub
		return false;
	}

	public MusicLibrary getRemoteLibrary() {
		return this.current;
	}
	@Override
	public void downloadRemoteLibrary() {
		new DownloadLibraryTask().execute(kimtSocket);
	}

	@Override
	public void setRemoteLibrary(MusicLibrary ml) {
		new UploadLibraryTask().execute(kimtSocket);
	}
	
	@Override
	public void setRemoteView(MusicView mv) {
		
	}
	
	@Override
	public String toString() {
		return this.connectionLocation + ":" + Integer.toString(this.connectionPort) + " " + this.current.toString();
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
				oos.writeObject(current);
				
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
		private ServerSocket listenSocket;
		
		public BroadcastListener(Handler handler, Runnable updater) throws IOException {
			this.handler		= handler;
			this.updater		= updater;
			this.listenSocket	= new ServerSocket(connectionPort);
		}
		
		public void run() {
			Socket s			= null;
			BufferedReader br	= null;
			
			while (true) {
				try {
					s			= listenSocket.accept();
					br			= new BufferedReader(new InputStreamReader(s.getInputStream()));
					
					String line = br.readLine();
					
					if (line != null) {
						handler.post(updater);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (s != null)
						try {
							s.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
			}
		}
	}

}
