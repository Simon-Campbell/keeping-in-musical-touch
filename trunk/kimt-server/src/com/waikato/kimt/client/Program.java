package com.waikato.kimt.client;

public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client c = new Client("localhost",8002);
		
		c.sendRequest("GET LIBRARY");
		
	}

}
