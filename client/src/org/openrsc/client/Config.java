package org.openrsc.client;

import java.net.InetSocketAddress;

public class Config 
{
	public static String IP = "localhost";
	public static int PORT = 53595;
	public static InetSocketAddress ADDR = new InetSocketAddress(IP, PORT);	
	public static int CLIENT_VERSION = 7;

	public final static String TOO_MANY_CHARS_IN_WILDERNESS_TEXT_A = "Wilderness IP Limit Exceeded";
	public final static String TOO_MANY_CHARS_IN_WILDERNESS_TEXT_B = "";
    
    public final static int DEFAULT_WINDOW_WIDTH = 512; // Original for OpenRSC: 800
	public final static int DEFAULT_WINDOW_HEIGHT = 346; // Original for OpenRSC: 534
}
