package com.waikato.kimt;

public class MusicView {
	private double x;
	private double y;
	
	/**
	 * @return The x-coordinate of where the synchronised view is.
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return The y-coordinate of where the synchronised view is.
	 */
	public double getY() {
		return y;
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
	public void setPosition(GreenstoneMusicLibrary gml, double x, double y) {
		this.setPosition(x, y);
		
		// Get the syncher in GreenstoneMusicLibrary and set
		// it's library ..
		//gml.getSyncer().setLibrary(gml);
	}

}
