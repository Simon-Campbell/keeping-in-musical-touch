package com.waikato.kimt.server;

import java.net.URL;
import java.util.ArrayList;

/**
 * A single unique group in the server
 * @author Greg C
 *
 */
public class Group implements Networkable
{
	private String groupName; public String getGroupName() { return groupName; }
	private String groupOwner; public String getGroupNOwner() { return groupOwner; }
	private ArrayList<Group> subgroups;
	private URL gsURL;
	private Group parent;
	
	public Group(String groupName, String groupOwner, String urlAddr, Group parent)
	{
		this.groupName = groupName;
		this.groupOwner = groupOwner;
		if (parent != this)
			this.parent = parent;
		
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
	public void insert(String groupName, String urlAddr, Group parent)
	{
		if (groupName != null && urlAddr != null && parent != null)
		{
			Group g = new Group(groupName, null, urlAddr, parent);
		}
	}

	@Override
	/**
	 * Returns a string containing a csv form of this Group instance
	 * This information can be sent to the client when selecting groups to join
	 */
	public String serialiseAsCsv() 
	{
		return null;
	}
}
