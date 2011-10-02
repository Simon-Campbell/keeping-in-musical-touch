package com.waikato.kimt.client;

public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client c = new Client("localhost",8000);
		
		c.sendRequest("GET LIBRARY");
		
	}

}
