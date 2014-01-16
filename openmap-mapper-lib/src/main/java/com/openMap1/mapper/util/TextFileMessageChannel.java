package com.openMap1.mapper.util;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;

/**
 * An implementation of the MessageChannel interface which writes
 * messages to a text file.
 * 
 * @author robert
 *
 */

public class TextFileMessageChannel implements messageChannel{

    private FileOutputStream textLog;

	public TextFileMessageChannel(String location)
	{
		try{
			textLog = new FileOutputStream(location);
		}
		catch (FileNotFoundException ex) 
		  {System.out.println("Failed to open text log: " + ex.getMessage());}
	}

	private byte[] newLine = {13,10};

	public void message(String s) 
	{
		if (textLog != null)
        try{
        	textLog.write(s.getBytes());
        	textLog.write(newLine);
            }
            catch (Exception ex)
                {System.out.println("Exception writing line '" + s + "': " + ex.getMessage());}
	}

	/**
	 * close down the channel
	 */
	public void close()
	{
		message("Closing message file");
		try {textLog.close();} 
		catch (Exception ex) 
        	{System.out.println("Exception closing text log: " + ex.getMessage());}		
	}

}
