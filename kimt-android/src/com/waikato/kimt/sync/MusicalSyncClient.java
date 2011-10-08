package com.waikato.kimt.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.waikato.kimt.greenstone.MusicLibrary;
import com.waikato.kimt.greenstone.MusicView;

public class MusicalSyncClient implements MusicalLibrarySync {
	private String	 userName;
	
	private MusicalDataFrame dataframe;
	private Socket kimtSocket;
	
	private boolean isLeader;
	private boolean	inSync;
	
	private MusicLibrary currentLibrary;
	
	public MusicalSyncClient(String userName, InetSocketAddress location) throws UnknownHostException, IOException {
		this.userName	= userName;
		
		this.dataframe	= new MusicalDataFrame();
		this.inSync		= false;
		this.kimtSocket = new Socket(location.getAddress(), location.getPort());
	
		// this.isLeader	= this.sync.login(userName);
	}
	
	public boolean isLeader() {
		return isLeader;
	}
	
	public String getUserName() {
		return userName;
	}

	@Override
	public void login(String userName) {
		new LoginUserTask().execute(userName);
	}

	@Override
	public MusicView getMusicView() {
		return dataframe.getMusicView();
	}
	
	@Override
	public void setMusicView(MusicView mv) {
		this.dataframe.setMusicView(mv);
		
		if (isLeader) {
			new UploadSyncTask().execute(dataframe);
		}
	}
	
	@Override
	public String getLibraryLocation() {
		return dataframe.getLibraryLocation();
	}
	
	private void writeHeaders(ObjectOutputStream out, String command) throws IOException {
		out.writeChars("KIMT 1.0");
		out.writeChars(command);
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
	
	/**
	 * Listens to broadcasts from the Keeping In Musical Touch
	 * socket. Posts the data back to the UI thread via the Handler, 
	 * which then runs the Runnable object in the UI thread.
	 * @author Simon
	 *
	 */
	private class BroadcastListener extends Thread {
		/**
		 * This class relies on information from the Android development
		 * database. If there's any problems then check here for the official
		 * guide:
		 * 	http://developer.android.com/resources/faq/commontasks.html#threading
		 */
		
		/**
		 * The handler created in the UI thread that will push the data
		 * from this thread back to the UI thread.
		 */
		private Handler		 handler;
		
		/**
		 * The runnable that the handler will call. Use this to run any
		 * code that needs to be run.
		 */
		private Runnable	 updater;
		
		/**
		 * Create a BroadcastListener with the handler that will push the
		 * data back to the UI thread. The runnable will be run when the handler
		 * is ready.
		 * @param handler
		 * 	The Handler that will invoke the runnable on the UI thread
		 * @param updater
		 * 	The Runnable that will be invoked when the handler is ready
		 * 	to run code
		 * @throws IOException
		 */
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

	/**
	 * An AsyncTask that will upload this clients MusicalDataFrame
	 * to the Keeping In Musical Touch server.
	 * @author Simon
	 */
	private class UploadSyncTask extends AsyncTask<MusicalDataFrame, Void, Boolean> {
		@Override
		protected Boolean doInBackground(MusicalDataFrame... params) {
			OutputStream os;
			ObjectOutputStream out;
			
			try {
				os	= kimtSocket.getOutputStream();
				out = new ObjectOutputStream(os);
				
				writeHeaders(out, "PUT SYNC");
				out.writeObject(dataframe);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		}
	}
	
	/**
	 * An AsyncTask to log the user into the Keeping In Musical Touch
	 * server.
	 * @author Simon
	 *
	 */
	private class LoginUserTask extends AsyncTask<String, Void, Integer> {
		
		@Override
		protected Integer doInBackground(String... params) {
			try {
				OutputStream os = kimtSocket.getOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(os);
				
				writeHeaders(out, "LOGIN");
				out.writeChars(params[0]);
				
				out.flush();
				//out.close();
				os.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			return 1;
		}
	
		// TODO:
		//	Add notification so that UI knows when user has been logged
		//	into the KIMT server.
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
	
	private class DownloadLibraryTask extends AsyncTask<Socket, Integer, MusicalDataFrame> {

		@Override
		protected MusicalDataFrame doInBackground(Socket... params) {
			Socket
				s	= params[0];
			
			ObjectInputStream
				ois = null;
			
			MusicalDataFrame
				ks	= null;
			
			try {
				ois	= new ObjectInputStream(s.getInputStream());
				ks	= (MusicalDataFrame) ois.readObject();
				
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
		protected void onPostExecute(MusicalDataFrame result) {
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
	private void notifySyncLibraryDownloaded(MusicalDataFrame k) {
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

}
