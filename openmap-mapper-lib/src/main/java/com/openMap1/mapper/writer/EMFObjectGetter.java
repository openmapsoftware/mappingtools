package com.openMap1.mapper.writer;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.EcoreFactoryImpl;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.reader.objectToken;
import com.openMap1.mapper.util.ModelUtil;

/**
 * objectGetter from an EMF model instance.
 * 
 * @author robert
 *
 */
public class EMFObjectGetter extends SimpleObjectGetter implements objectGetter {
	
	private EObject instance;
	
	/* lookup table from EObjects to their objectTokens;
	 * used when storing non-containment relations */
	private Hashtable<EObject,objectToken> addedObjects;
	
	/**
	 * 
	 * @param classModel
	 * @param instance
	 * @throws MapperException
	 */
	public EMFObjectGetter(EPackage classModel, EObject instance) throws MapperException
	{
		super(classModel);
		this.instance = instance;
		captureInstance();
	}
	
	/**
	 * capture the instance of the ECore class model in the internal structures
	 * of the SimpleObjectGetter, so it can be provided to an XMLWriter on demand
	 * @throws MapperException
	 */
	private void captureInstance() throws MapperException
	{
		// initialise the lookup from EObjects to objectTokens
		addedObjects = new Hashtable<EObject,objectToken>();
		
		// add the objects, properties and containment relations
		captureHierarchy(instance,null,null);
		
		// add the non-containment relations
		captureNonContainmentRefsInTree(instance);
	}
	
	/**
	 * recursive capture of all objects, properties and containment 
	 * relations in the class model instance
	 * @param obj the current object
	 * @param outerObj if non-null, the outer object containing this object
	 * @param ref if non-null, the containment relation from the outer object
	 * @throws MapperException
	 */
	private void captureHierarchy(EObject obj, objectToken outerTok, EReference ref) throws MapperException
	{
		// add this object to the saved model, and record it in the lookup table
		String className = ModelUtil.getQualifiedClassName(obj.eClass());
		objectToken oTok = addObject(className);
		addedObjects.put(obj,oTok);
		
		// if this object is contained in an outer object, record the containment association
		if (outerTok != null) addLink(outerTok,ref.getName(),oTok);
		
		// record all attributes of this object that have non-null values
		for (Iterator<EAttribute> ia = obj.eClass().getEAllAttributes().iterator();ia.hasNext();)
		{
			EAttribute att = ia.next();
			Object attVal = obj.eGet(att);
			if (attVal != null) setProperty(oTok,att.getName(),stringValue(att,attVal));
		}
		
		// descend the tree of containment associations
		for (Iterator<EReference> ir = obj.eClass().getEAllReferences().iterator(); ir.hasNext();)
		{
			EReference reff = ir.next();
			if (reff.isContainment())
			{
				Object target = obj.eGet(reff);
				if ((reff.getUpperBound() == 1) && (target != null))
				{
					EObject child = (EObject)target;
					captureHierarchy(child,oTok,reff);
				}
				else if ((reff.getUpperBound() == -1) && (target instanceof List<?>))
				{
					for (Iterator<?> ic = ((List<?>)target).iterator();ic.hasNext();)
					{
						EObject child = (EObject)ic.next();
						captureHierarchy(child,oTok,reff);
					}					
				}
			}
		}
	}
	
	/**
	 * convert the value of any EAttribute of an EObject to a string
	 * @param att
	 * @param attValue
	 * @return
	 */
	private String stringValue(EAttribute att, Object attValue)
	{
		String val = "";
		EDataType type = att.getEAttributeType();
		String typeName = type.getName();
		EcoreFactoryImpl fac = new EcoreFactoryImpl();
		if (typeName.equals("EInt")) 
			val = fac.convertEIntToString(type, attValue);
		else if (typeName.equals("EString")) 
			val = fac.convertEStringToString(type, attValue);
		//  ... etc; add more as needed
		else {System.out.println("Cannot yet handle Ecore type " + typeName);}
		return val;
	}
	
	// recursive descent capturing all non-containment associations
	private void captureNonContainmentRefsInTree(EObject obj) throws MapperException
	{
		// capture non-containment refs of this object
		captureNonContainmentRefs(obj);

		// descend the tree of containment associations
		if (obj != null) for (Iterator<EReference> ir = obj.eClass().getEAllReferences().iterator(); ir.hasNext();)
		{
			EReference reff = ir.next();
			if (reff.isContainment())
			{
				Object target = obj.eGet(reff);
				if (reff.getUpperBound() == 1)
				{
					EObject child = (EObject)target;
					captureNonContainmentRefsInTree(child);
				}
				else if ((reff.getUpperBound() == -1) && (target instanceof List<?>))
				{
					for (Iterator<?> ic = ((List<?>)target).iterator();ic.hasNext();)
					{
						EObject child = (EObject)ic.next();
						captureNonContainmentRefsInTree(child);
					}					
				}
			}
		}
	}

	/**
	 * capture all the non-containment association links of this object
	 * @param obj
	 * @throws MapperException
	 */
	private void captureNonContainmentRefs(EObject obj) throws MapperException
	{
		if (obj != null)
		{
			for (Iterator<EReference> ir = obj.eClass().getEAllReferences().iterator(); ir.hasNext();)
			{
				EReference reff = ir.next();
				if (!reff.isContainment())
				{
					Object target = obj.eGet(reff);
					if (reff.getUpperBound() == 1)
					{
						EObject other = (EObject)target;
						captureOneNonContainmentRef(obj,reff,other);
					}
					else if ((reff.getUpperBound() == -1) && (target instanceof List<?>))
					{
						for (Iterator<?> ic = ((List<?>)target).iterator();ic.hasNext();)
						{
							EObject other = (EObject)ic.next();
							captureOneNonContainmentRef(obj,reff,other);
						}					
					}
				}
			}			
		}
	}
	
	/**
	 * capture one non-containment link
	 * @param obj
	 * @param reff
	 * @param other
	 */
	private void captureOneNonContainmentRef(EObject obj,EReference reff,EObject other)
	throws MapperException
	{
		objectToken oTok = addedObjects.get(obj);
		if (oTok == null) 
			throw new MapperException("Cannot find object token of class " + obj.eClass().getName());

		objectToken otherTok = addedObjects.get(other);
		if (otherTok == null) 
			throw new MapperException("Cannot find object token of class " + other.eClass().getName());
		
		/* this captures the link and its inverse link. 
		 * Therefore each link is captured twice; this does not matter as they 
		 * are in Hashtables, where Elements can be written a second time with no effect.  */
		addLink(oTok, reff.getName(), otherTok);
	}

}
