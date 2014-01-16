package com.openMap1.mapper.reader;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.EPackage;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.RunIssue;
import com.openMap1.mapper.util.Timer;

/**
 * This interface has one main method, to make a resource
 * containing an instance of an EMF model, using an
 * XML document which represents an instance of the model
 * and a set of mappings which define how the XML instance 
 * represents the instance of the model
 * 
 * @author robert
 *
 */
public interface EMFInstanceFactory {
	
	/**
	 * @param xor the reader which uses mappings to extract EMF model instance information 
	 * @param modelInstanceURI the URI at which the new model instance resource is to be stored;
	 * or null if it is not to be stored anywhere
	 * @param topObjectRep the objectRep of the instance which is to be the root of the Ecore tree
	 */
	public Resource createModelInstance(XOReader xor, URI modelInstanceURI, objectToken topObjectToken)
		throws MapperException;
	
	/**
	 * 
	 * @param xor the reader which uses mappings to extract EMF model instance information 
	 * @param modelInstanceURI the URI at which the new model instance resource is to be stored;
	 * or null if it is not to be stored anywhere
	 * @param forceContainer: if forceContainer is true, the Factory will act as if
	 * there is a new root class,
	 * (called 'Container' or 'Container_N' to avoid class name clashes) in the EMF model.
	 * This class has a containment relation 'contains' to any class which is not contained
	 * in some other class. This is done so that all object instances can be shown in the EMF instance
	 * with a single root.
	 * <p>
	 * When forceContainer is false, the Factory will only add a new root class 'Container'
	 * if it is necessary - i.e. if there more than one 'root' class, and so there
	 * is no one class in the Ecore model which has
	 * a containment relation (direct or indirect) to all the other classes. 
	 * <p>
	 * In this case, when the Factory does not introduce a new Container class
	 * it may fail and throw a MapperException if there is not 
	 * exactly one instance of the top class of the ECore model represented in the XML.
	 * <p>
	 * Whether forceContainer is true or false, the Factory does not alter the stored EMF model; 
	 * it uses an in-memory modified copy, and makes that publicly accessible.
	 * 
	 */
	public Resource createModelInstanceInTranslationTest(XOReader xor, URI modelInstanceURI, boolean forceContainer)
		throws MapperException;
	
	/**
	 * @return a summary of any exceptions thrown or reader problems encountered
	 * when making the instance, with duplicates removed.
	 * The RunIssues do not contain the XPaths in the source where the exception occurred.
	 */
	public List<RunIssue> runIssues();
	
	/**
	 * See the description of 'forceContainer' in method CreateModelInstance.
	 * @return true if the Factory-generated instance has an extra class 'Container' (or 'Container_N')
	 * which has a containment association to every class not otherwise contained.
	 */
	public boolean hasAddedContainerClass();
	
	
	/**
	 * See the description of 'forceContainer' in method CreateModelInstance.
	 * @return The Ecore model for the  Factory-generated instance.
	 * Compared to the input Ecore model, this model may have an extra class 'Container' (or 'Container_N')
	 * which has a containment association to every class not otherwise contained.
	 */
	public EPackage modelForInstance();
	
	/**
	 * Set the namespace prefix for the model
	 * @param NSPrefix
	 */	
	public void setNsPrefix(String NSPrefix);
	
	/**
	 * Set the namespace URI for the model
	 * @param NSUri
	 */	
	public void setNsUri(String NSUri);
	
	/**
	 * set up a timer for the factory
	 * @param timer
	 */
	public void giveTimer(Timer timer);


}
