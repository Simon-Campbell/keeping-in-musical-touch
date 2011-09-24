package com.waikato.kimt.server;

import java.net.URL;
import java.util.ArrayList;

/**
 * A single unique group in the server
 * Can have subgroups nested within itself
 * @author Greg C
 * @param <T>
 */
public class Group implements Networkable<Group>
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
	 * Serialises the Group object in the parameters, by generating a string formatted as a csv line.
	 * This information can then be passed to a client when viewing different groups
	 */
	public String serialiseAsCsv(Group objToSerialise) 
	{
		// TODO Auto-generated method stub
		return null;
	}
}
