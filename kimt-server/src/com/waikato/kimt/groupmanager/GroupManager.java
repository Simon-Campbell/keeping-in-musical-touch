package com.waikato.kimt.groupmanager;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

public class GroupManager extends Observable implements Serializable 
{
	ArrayList<Group> groups;
	Socket connection;
	
	/**
	 * Instantiates a new GroupManager object, with a connection to a remote GroupManager
	 * @param connection
	 */
	public GroupManager()
	{
		groups = new ArrayList<Group>();
		this.connection = connection;
	}
	
	/**
	 * Inserts a new group, first checking for duplicates.
	 * @param groupName Name of new group
	 * @param groupOwner Name of group owner
	 * @param gsURL URL of greenstone page
	 * @return True if group inserted
	 */
	public boolean insert(String groupName, String groupOwner, String gsURL)
	{
		Group newGroup = new Group(groupName, groupOwner, gsURL);
		return insert(newGroup);
	}
	
	/**
	 * Inserts a new group, first checking for duplicates.
	 * @param newGroup Group object to insert
	 * @return True if group inserted
	 */ 
	public boolean insert(Group newGroup)
	{
		for(Group g : groups)
		{
			if (g.equals(newGroup))
				return false;
		}
		groups.add(newGroup);
		setChanged();
		notifyObservers();
		return true;
	}
	
	/**
	 * Removes the oldest group with the specified name from the groups list
	 * @param groupName Name of group to remove
	 * @return True if a group with the specified name has been removed
	 */
	public boolean remove(String groupName)
	{
		for(int i = 0; i < groups.size(); i++)
		{
			Group g = groups.get(i);
			if (g.getGroupName().equals(groupName))
			{
				groups.remove(i);
				setChanged();
				notifyObservers();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a copy of an ArrayList containing all groups
	 * @return
	 */
	public ArrayList<Group> getGroups()
	{
		if (groups == null)
			groups = new ArrayList<Group>();
		return groups;
	}
	
	/**
	 * Prints the contents of the GroupManager to the console
	 */
	public void print()
	{
		System.out.println("Contents of groupmanager:");
		
		for(Group g : groups)
			g.print();
		
		System.out.println("/tEnd print");
	}
	
	public String toString()
	{
		String s = "";
		for(Group g : groups)
			s += g.toString();
				
		return s;
	}
 }
