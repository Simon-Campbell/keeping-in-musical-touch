package com.waikato.kimt.server;

/**
 * Interface which defines all behaviours of a networkable class
 * @author Greg C
 *
 */
public interface Networkable 
{
	/**
	 * Allows the class to serialise itself to csv form
	 */
	String serialiseAsCsv();
}
