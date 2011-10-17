package com.waikato.kimt.server;

import java.util.Dictionary;
import java.util.Hashtable;

public class StateManager 
{
	private StateManager()
	{
		states = new Hashtable<String,Object>();
	}
	
	static StateManager manager;
	Dictionary<String,Object> states;
	
	public static StateManager getSingleton()
	{
		if (manager == null)
			manager = new StateManager();
		return manager;
	}
	
	public Dictionary<String, Object> getHashTable()
	{
		return states;
	}
}
