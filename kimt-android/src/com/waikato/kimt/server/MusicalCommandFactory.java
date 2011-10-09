package com.waikato.kimt.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;

public class MusicalCommandFactory {

	public static MusicalCommand getMusicalCommand(ObjectInputStream in) throws OptionalDataException, ClassNotFoundException, IOException {
		Object obj = in.readObject();
		
		if (obj instanceof String) {
			return MusicalCommandFactory.getMusicalCommand((String) obj);
		} else {
			// Not a valid command so null was returned ..
			return null;
		}
	}
	
	public static MusicalCommand getMusicalCommand(String cmd) {
		if (cmd.compareTo("LOGIN") == 0) {
			return new MusicalLoginCommand();
		} else if (cmd.compareTo("PUT SYNC") == 0) {
			return new MusicalPutSyncCommand();
		} else if (cmd.compareTo("GET SYNC") == 0) {
			return new MusicalGetSyncCommand();
		} else {
			// Not a valid command so null was returned ..
			return null;
		}
	}
}
