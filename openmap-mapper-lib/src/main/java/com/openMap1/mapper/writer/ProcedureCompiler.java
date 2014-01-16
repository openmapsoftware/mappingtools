package com.openMap1.mapper.writer;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.resources.IFile;

import com.openMap1.mapper.core.CompilationIssue;
import com.openMap1.mapper.core.MapperException;

import org.w3c.dom.Element;

/**
 * Interface for generating XML writing procedures (Wproc files)
 * from a mapping set, using the translation compiler web service.
 * 
 * @author robert
 *
 */
public interface ProcedureCompiler {

	/**
	 * Generate XML writing procedures from a mapper file 
	 * (supplied in the constructor)
	 * @param proceduresFile the IFile to write the result out to
	 * @param codeTrace if true, write out a code trace file (on the server)
	 * @throws MapperException
	 */
	public void generateProcedures(IFile proceduresFile, boolean codeTrace) 
    throws MapperException;

	/**
	 * Generate XML writing procedures from a mapper file 
	 * (supplied in the constructor)
	 * @param codeTrace  if true, write out a code trace file (on the server)
	 * @return the root Element of the procedures file
	 * @throws MapperException
	 */
	public Element generateProcedures(boolean codeTrace) 
    throws MapperException;

	
    /** Code generation warnings, indexed by XPath to the node involved  */
    public Hashtable<String,List<CompilationIssue>> getCompilationIssues(); 

}
