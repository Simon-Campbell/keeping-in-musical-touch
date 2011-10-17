package com.waikato.kimt.server.commands;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;

import com.waikato.kimt.sync.CommandType;

public class MusicalCommandFactory {
	private static String lastCommand = null;
	
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
		lastCommand = cmd;
		
		switch (getCommandType(cmd)) {
		case LOGIN:
			return new MusicalLoginCommand();
		case PUT_SYNC:
			return new MusicalPutSyncCommand();
		case GET_SYNC:
			return new MusicalGetSyncCommand();
		default:
			return null;
		}
	}
	
	public static CommandType getCommandType(String cmd) {
		
		if (cmd.compareTo("LOGIN") == 0) {
			return CommandType.LOGIN;
		} else if (cmd.compareTo("PUT SYNC") == 0) {
			return CommandType.PUT_SYNC;
		} else if (cmd.compareTo("GET SYNC") == 0) {
			return CommandType.GET_SYNC;
		} else {
			return CommandType.UNKNOWN;
		}
	}
	
	public static CommandType getCommandType(ObjectInputStream in) throws OptionalDataException, ClassNotFoundException, IOException {
		Object obj = in.readObject();
		
		if (obj instanceof String) {
			return getCommandType((String) obj);
		} else {
			return CommandType.UNKNOWN;
		}
	}
	
	public static String getLastCommand() {
		return lastCommand;
	}
}
