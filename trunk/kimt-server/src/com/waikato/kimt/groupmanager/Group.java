package com.waikato.kimt.groupmanager;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

/**
 * A single unique group in the server
 * Can have subgroups nested within itself
 * @author Greg C
 * @param <T>
 */
public class Group implements Serializable
{
	private String groupName; public String getGroupName() { return groupName; }
	private String groupOwner; public String getGroupOwner() { return groupOwner; }
	private URL gsURL;
	
	public Group(String groupName, String groupOwner, String urlAddr)
	{
		this.groupName = groupName;
		this.groupOwner = groupOwner;
		
		try
		{
			gsURL = new URL(urlAddr);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * Inserts a new group into the selected group
	 * @param groupName
	 * @param urlAddr
	 * @param parent
	 */
	public void insert(String groupName, String urlAddr)
	{
		if (groupName != null && urlAddr != null)
		{
			Group g = new Group(groupName, null, urlAddr);
		}
	}

	/**
	 * Serialises the Group object in the parameters, by generating a string formatted as a csv line.
	 * This information can then be passed to a client when viewing different groups
	 */
	public String serialiseAsCsv() 
	{
		String csvLine = getGroupName() + "," + 
				getGroupOwner()  + "," + 
				gsURL + "\n";
		
		// TODO Auto-generated method stub
		return csvLine;
	}
	
	/**
	 * Performs an equality check by comparing group names
	 * @param other
	 * @return
	 */
	public boolean equals(Group other)
	{
		if (getGroupName().equals(other.getGroupName()))
			return true;
		return false;
	}
	
	/**
	 * Prints information about this group to the console
	 */
	public void print()
	{
		System.out.println(toString());
	}
	
	public String toString()
	{
		return "Name: " + getGroupName() + "; Owner: " + getGroupOwner() + "; URL:" + gsURL + "\n";
	}
	
	
}
