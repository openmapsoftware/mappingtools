package com.openMap1.mapper.util;

/**
 * a simple implementation of a messageChannelwhich just writes to
 * the system console
 * @author robert
 *
 */
public class SystemMessageChannel implements messageChannel{
	
	public void message(String s) {System.out.println(s);}

	/**
	 * close down the channel
	 */
	public void close() {}

}
