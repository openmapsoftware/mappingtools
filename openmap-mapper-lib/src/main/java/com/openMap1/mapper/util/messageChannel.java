package com.openMap1.mapper.util;

/**
*  Provide an implementation of this interface
*  if you want diagnostic, trace and error messages to go
*  anywhere except the standard system output.
*
*  If 'null' is used as an argument to constructors
*  requiring a messageChannel object, messages from those objects
*  will go to the standard system output.
*/

public interface messageChannel {

/**
*  provide this method to write messages
*  wherever you want them to go.
*/
public void message(String s);

/**
 * close down the channel
 */
public void close();

}
