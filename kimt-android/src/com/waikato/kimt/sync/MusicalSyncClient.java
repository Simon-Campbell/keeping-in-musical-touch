package com.waikato.kimt.sync;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import com.waikato.kimt.server.commands.MusicalCommandFactory;

public class MusicalSyncClient implements MusicalLibrarySync {
	private String	 userName;
	
	private MusicalDataFrame dataframe;
	private InetSocketAddress kimtAddress;

	private volatile ObjectOutputStream objectOutput;
	private volatile Socket mainSyncSocket;	
	
	private boolean isLeader;
	private boolean	inSync;
	
	private BroadcastListener broadcastListener;
	private MusicLibrary currentLibrary;
	
	public MusicalSyncClient(String userName, InetSocketAddress location) throws UnknownHostException, IOException {
		this.userName	= userName;
		this.dataframe	= new MusicalDataFrame();
		this.inSync		= false;
		this.kimtAddress= location;
	}
	
	public boolean isLeader() {
		return isLeader;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public MusicalDataFrame getDataFrame() {
		return dataframe;
	}

	private synchronized ObjectOutputStream getObjectOutputStream() throws IOException {
		if (objectOutput == null) {
			objectOutput = new ObjectOutputStream(mainSyncSocket.getOutputStream());
		}
		
		return objectOutput;
	}
	
	private synchronized Socket getMainSocket() throws IOException {
		if (mainSyncSocket == null) {
			mainSyncSocket = new Socket(kimtAddress.getAddress(), kimtAddress.getPort());
		}
		
		return mainSyncSocket;
	}
	
	@Override
	public MusicView getMusicView() {
		return null;
//		return dataframe.getMusicView();
	}
	
	@Override
	public void setMusicView(MusicView mv) {
//		this.dataframe.setMusicView(mv);
		setMusicalDataFrame(dataframe);
	}
	
	public synchronized void setMusicalDataFrame(MusicalDataFrame mdf) {
		this.dataframe = mdf;
		
		if (isLeader) {
			new UploadSyncTask().execute(mdf);
		} else {
			notifyMusicalDataFrameUpdated(mdf);
		}
	}
	
	@Override
	public String getLibraryLocation() {
		return dataframe.getLibraryLocation();
	}
	
	private void writeHeaders(ObjectOutputStream out, String command) throws IOException {
		out.writeObject("KIMT 1.0");
		out.writeObject(command);
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
	public void startListening(Handler handler) throws IOException, IllegalThreadStateException {
		if (broadcastListener == null) {
			broadcastListener = new BroadcastListener(handler);
			broadcastListener.start();
		} else {
			throw new IllegalThreadStateException("This client is already listening to the MusicalSyncServer.");
		}
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
		private Handler handler;

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
		public BroadcastListener(Handler handler) throws IOException {
			this.handler = handler;
		}
		
		public synchronized void close() throws IOException {
			if (mainSyncSocket != null)
				mainSyncSocket.close();
			mainSyncSocket = null;
		}
		
		public void run() {
			ObjectInputStream in;
			ObjectOutputStream out;
			
			try {
				mainSyncSocket = new Socket(kimtAddress.getAddress(), kimtAddress.getPort());
				
				in	= new ObjectInputStream(mainSyncSocket.getInputStream());
				out = getObjectOutputStream();
			
				writeHeaders(out, "LOGIN");
				out.writeObject(MusicalSyncClient.this.userName);
				out.flush();
				
				while (mainSyncSocket != null) {
					Object obj = null;
				
					try {
						
						while ((obj = in.readObject()) != null) {
							if (obj instanceof String) {
								String msg = (String) obj;
								
								if (msg.compareTo("KIMT 1.0") == 0) {
									Log.v("KeepingInMusicalTouch", "I have a KIMT 1.0 header");
									CommandType commandType = MusicalCommandFactory.getCommandType(in);
									Log.v("KeepingInMusicalTouch", "I have a command type of:" + commandType.toString());
									switch (commandType) {
										case LOGIN: {
											Object extra = in.readObject();

											if (extra instanceof Boolean) {
												MusicalSyncClient.this.isLeader = (Boolean) extra;
												
												Log.v("KeepingInMusicalTouch", "I am the leader? " + MusicalSyncClient.this.isLeader);

												handler.post(new Runnable() {
													@Override
													public void run() {
														notifyLoggedIn(MusicalSyncClient.this.isLeader());
													}
												});
											}
											
											continue;
										}
										
										case PUT_SYNC: {
											Object extra = in.readObject();
											
											if (extra instanceof MusicalDataFrame) {
												final MusicalDataFrame musicalDataFrame = (MusicalDataFrame) extra;
												
												if (!isLeader) {
													handler.post(new Runnable() {													
														@Override
														public void run() {
															setMusicalDataFrame(musicalDataFrame);
														}
													});
												} else {
													
												}
											}
										}
									}
								}
							}
							
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				
			} catch (EOFException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
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
			Socket s = null;	
			ObjectOutputStream out = null;

			try {
				s = getMainSocket();
				out = getObjectOutputStream();

				writeHeaders(out, "PUT SYNC");
				out.flush();
				out.writeObject(params[0]);
				out.flush();
				
				Log.v("KeepingInMusicalTouch", "Uploaded Dataframe " + params[0].toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		}
	}
	
	private List<SyncedLibraryUpdateListener>
		registeredLibraryUpdateListeners = new ArrayList<SyncedLibraryUpdateListener>();
	private List<SyncedLoginListener>
		registeredLoginListeners = new ArrayList<SyncedLoginListener>();
	
	public void setOnSheetMetaDataUpdateListener(SyncedLibraryUpdateListener slul) {
		registeredLibraryUpdateListeners.add(slul);
	}
	
	public void setOnLoginUpdateListener(SyncedLoginListener sll) {
		registeredLoginListeners.add(sll);
	}
	
	/**
	 * Notify all listeners that there has been a new sync object that has been
	 * downloaded from the sync server.
	 * @param k
	 * 	The sync object that was downloaded from the sync server.
	 */
	private void notifyMusicalDataFrameUpdated(MusicalDataFrame musicalDataFrame) {
		for (SyncedLibraryUpdateListener s : registeredLibraryUpdateListeners) {
			s.onMusicalDataFrameUpdated(musicalDataFrame);
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
	
	private void notifyLoggedIn(boolean isLeader) {
		for (SyncedLoginListener s : registeredLoginListeners) {
			s.onLoggedIn(isLeader);
		}
	}

	@Override
	public void setOnSyncUpdateListener(SyncedLibraryUpdateListener slul) {
		registeredLibraryUpdateListeners.add(slul);
	}

	public void close() throws IOException {
		if (mainSyncSocket != null)
			mainSyncSocket.close();
		
		if (objectOutput != null)
			objectOutput.close();
		
		if (broadcastListener != null)
			broadcastListener.close();
	}


}
