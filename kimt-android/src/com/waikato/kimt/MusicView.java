package com.waikato.kimt;

import java.io.Serializable;

public class MusicView implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double x;
	private double y;
	
	private MusicSheet ms;
	
	public MusicView(MusicSheet ms, double x, double y) {
		this.ms = ms;
		
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return  The x-coordinate of where the synchronised view is.
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return  The y-coordinate of where the synchronised view is.
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * @return The music sheet that this view is looking at.
	 */
	public MusicSheet getMusicSheet() {
		return ms;
	}
	
	/**
	 * Set the view of the current client.
	 * @param x The x-coordinate of the new view.
	 * @param y The y-coordinate of the new view.
	 */
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
		
		// TODO: Create event so that UI can subscribe to it and update the visual position.
	}
	
	/**
	 * Set the view of the current client and broadcast it to all of the
	 * other clients in the group.
	 * @param dls
	 * 	The location of the library object that will synchronise.
	 * @param x
	 * 	The x-coordinate of the new view.
	 * @param y
	 * 	The y-coordinate of the new view.
	 */
	public void setPosition(KIMTSync ks, double x, double y) {
		this.setPosition(x, y);
		
		// Get the syncher in GreenstoneMusicLibrary and set
		// it's library ..
		ks.setRemoteView(this);
	}

}
