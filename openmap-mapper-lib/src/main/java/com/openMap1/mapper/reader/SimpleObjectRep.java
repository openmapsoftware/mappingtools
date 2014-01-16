package com.openMap1.mapper.reader;

import com.openMap1.mapper.core.ClassSet;

/**
 * a basic implementation of objectToken, using Strings
 * as the unique keys of the tokens.
 * 
 * For use by SimpleObjectGetter
 * @author robert
 *
 */
public class SimpleObjectRep implements objectToken {
	
	private String className;
	public String className() {return className;}
	
	private String key;
	public Object objectKey() {return key;}
	
	public XOReader reader() {return null;}
	
	public SimpleObjectRep(String className, String key)
	{
		this.className = className;
		this.key = key;
	}
	
    
    /** a SimpleObjectRep can never be empty */
    public boolean isEmpty() {return false;}

	
	/** 
	 * the subset of the represented object 
	 * */
	public String subset() {return "";}
	
    /** return ClassSet ( = class and subset) in the source of the object - e.g. the XML
    source document where the object is represented. */
    public ClassSet cSet()
    {
    	ClassSet cs = null;
        try {cs = new ClassSet(className,"");} 
        catch (Exception ex) {} 
        return cs;
    }



}
