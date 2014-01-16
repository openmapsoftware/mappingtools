package com.openMap1.mapper.core;

/**
 * combination of a classSet and a property name for the class.
 * 
 * @author robert
 *
 */
public class propSet
{
    private ClassSet cSet;
    private String propName;

    public ClassSet sCet() {return cSet;}
    public String propName() {return propName;}

    public propSet(ClassSet cs, String pn)
    {
        cSet = cs;
        propName = pn;
    }
}
