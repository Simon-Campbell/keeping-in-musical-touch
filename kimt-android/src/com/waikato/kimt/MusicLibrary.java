package com.waikato.kimt;

import android.app.Activity;

public interface MusicLibrary {
	/**
	 * Connect to an online music library.
	 * @param uri The URI of the online music library
	 */
	public void			connect(String uri);
	
	/**
	 * Set the music sheet that is focused in the library
	 * @param sheetID The ID of the music sheet to focus on
	 */
	public void			setCurrentSheet(String sheetID);
	
	/**
	 * Gets the music sheet that current has focus
	 * @return The music sheet that currently has focus
	 */
	public MusicSheet	getCurrentSheet();
	
	/**
	 * Gets the current view that will be synchronised with the
	 * rest of the
	 * @return
	 */
	public MusicView	getCurrentView();
	
	/**
	 * Search for the specified music sheet using the
	 * specified search mode
	 * @param searchTerm The term to search for
	 * @param sm The type of search to search for
	 * @return The first music sheet to match the search terms
	 */
	public MusicSheet	find(String searchTerm, SearchMode sm);
}
