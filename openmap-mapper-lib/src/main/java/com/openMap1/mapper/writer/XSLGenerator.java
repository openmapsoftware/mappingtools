package com.openMap1.mapper.writer;

import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Element;

import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.mapping.AssociationMapping;
import com.openMap1.mapper.mapping.objectMapping;
import com.openMap1.mapper.mapping.propertyMapping;
import com.openMap1.mapper.reader.MDLXOReader;
import com.openMap1.mapper.util.XSLOutputFile;
import com.openMap1.mapper.util.messageChannel;

/**
 * This interface is implemented only by the class XSLGeneratorImpl.
 * 
 * It purpose is purely to enable that class to be put in a different plugin,
 * while satisfying the compiler dependencies of classes that use these methods

 * @author Robert
 *
 */

public interface XSLGenerator {
	
    public XSLOutputFile xslout();

    public MDLXOReader baseInputReader();
    
    public MappedStructure ms();
    
    public boolean filterbyDoubleClassMappings(String className);
    
    public String convertPathPrefixes(String path);
    
	public propertyMapping getInputPropertyMapping(ClassSet XSLCSet, String pName) throws MapperException;
	
	public String newVariable(String s);

	public String newVariable();

	public String newMode(String s);

	public String newName();
	
    public ClassSet trueClassSet(ClassSet XSLClassSet) throws MapperException;

    public MDLXOReader inputReader(ClassSet XSLCSet) throws MapperException;

    public String convertStepPrefix(String step);

    WProc findProcedure(boolean createdElement, Xpth newPath, subtreeContext context)
    throws MapperException;
    
	public void addReaderToStack(objectMapping om) throws MapperException;
	
	public objectMapping getInputObjectMapping(ClassSet XSLCSet) throws MapperException;
	
	public ClassSet getParameterClassSet(MDLXOReader reader) throws MapperException;
	
    public Vector<AssociationMapping> inputAssocMappings(ClassSet endXSLCSet, 
  		   String assocName, String targetClassName, int targetEnd, boolean topCall)
  		   throws MapperException;
    
    public messageChannel mChan();
    
    public String variableName(ClassSet cSet, boolean in);
    
    public objectMapping namedObjectMapping(ClassSet cs);
    
    public Hashtable<String,Vector<objectMapping>> objectMappingFullPaths(String className); 
    
	void rememberTemplate(String fullName,Element template);
	
	public MDLXOReader topInputReader();









}
