package com.openMap1.mapper.mapping;

import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.util.messageChannel;

import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.ModelPropertyFilter;
import com.openMap1.mapper.ObjMapping;

import java.util.Vector;

/**
 * represents one property inclusion filter in an object mapping;
 * the object must have the property value in order to be represented in the XML
 * 
 * This is sometimes a wrapper around the model class ModelPropertyFilter, but is
 * not so for fixed property values.
 *
 * @author Robert Worden
 * @version 1.0
 */

public class filterProp extends filter
{
    private String property, value, test;
    public String property() {return property;}
    public String value() {return value;}
    public String test() {return test;}

    // called with an me:filterProp element as argument
    public filterProp (messageChannel mChan , ModelPropertyFilter mp, ClassSet onCSet)
    {
        super(mChan, onCSet);

        property = mp.getPropertyName();
        value = mp.getValue();
        test = mp.getTest().getLiteral();
        if (test.equals("")) {test = "=";}
    }

    // called with a fixed property mapping as argument
    public filterProp(messageChannel mChan , FixedPropertyValue fpv)
    {
        super(mChan,((ObjMapping)fpv.eContainer()).getClassSet());
        property = fpv.getMappedProperty();
        value = fpv.getFixedValue();
        test = "=";
    }

    // true if some other filter is the same as this one
    public boolean sameFilter(filterProp fp)
    {
        return ((property.equals(fp.property))
            && (value.equals(fp.value))
            && (test.equals(fp.test)));
    }

    // true if this filter is in a vector of filters
    public boolean inFilters(Vector<filter> filters)
    {
        int i;
        filter f;
        filterProp fp;
        boolean res = false;
        for (i = 0; i < filters.size(); i++)
        {
            f = (filter)filters.elementAt(i);
            if (f instanceof filterProp)
            {
                fp = (filterProp)f;
                if (sameFilter(fp)) res = true;
            }
        }
        return res;
    }

    public void write()
    {
        mChan().message("Class " + onCSet().stringForm() + " requires "
            +  property + " " + test + " " + value + ".");
    }

    /* true if two property filters are mutually exclusive
    i.e can never both be satisfied by the same object. */
    boolean mutuallyExclusive(filterProp fp)
    {
        return ((test.equals("="))
                && (fp.test.equals("="))
                && (property.equals(fp.property))
                && !(value.equals(fp.value)));
    }
}
