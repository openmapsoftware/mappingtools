package com.openMap1.mapper.health.commands;


import java.util.Iterator;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.ObjMapping;

/**
 * This command is used on a mapping set to some V3 RMIM.
 * Wherever the mapping set has a mapping to some RMIM class, 
 * and where the annotations on that class in the ECore model say
 * that some RIM structural attribute of the class has a fixed value, 
 * this command adds a fixed value property mapping on the same node as the class
 * mapping.
 * 
 * The effect is that an RMIM instance made from the mapped XML through the 
 * mappings will have the correct values of RIM structural attributes, for all RMIM 
 * class instances.
 * 
 * Conversely, when writing the XML, the fixed values are required on an RMIM instance for
 * the XML writer to make the mapped node. 
 * 
 */

public class MapStructuralAttributesCommand extends CompoundCommand {
	
	public MapStructuralAttributesCommand(EditingDomain domain, MappedStructure mappedStructure)
	{
		super(0);
		
		try
		{
			// find the class model
			EPackage classModel = mappedStructure.getClassModelRoot();
			
			// iterate over all object mappings in the mapping set
			for (Iterator<EObject> it = ModelUtil.getEObjectsUnder(mappedStructure, MapperPackage.eINSTANCE.getObjMapping()).iterator();it.hasNext();)
			{
				ObjMapping om = (ObjMapping)it.next();
				
				// find the class and iterate over its attributes with fixed values
				EClass mappedClass = ModelUtil.getNamedClass(classModel, om.getQualifiedClassName());
				if (mappedClass != null)
				{
					for (Iterator<EAttribute> ia = mappedClass.getEAllAttributes().iterator();ia.hasNext();)
					{
						EAttribute att = ia.next();
						String attName = att.getName();
						String fixedValue = ModelUtil.getEAnnotationDetail(att,"fixed value");
						if (fixedValue != null)
						{
							// do not add a fixed value mapping if there is one already for this attribute
							boolean hasFixedValue = false;
							for (Iterator<FixedPropertyValue> iv = om.getFixedPropertyValues().iterator();iv.hasNext();)
								if (iv.next().getMappedProperty().equals(attName)) hasFixedValue = true;
							
							if (!hasFixedValue)
							{
								FixedPropertyValue fpv = MapperFactory.eINSTANCE.createFixedPropertyValue();
								fpv.setMappedProperty(attName);
								fpv.setFixedValue(fixedValue);
								fpv.setValueType("string");
								append(new AddCommand(domain,om,
										MapperPackage.eINSTANCE.getObjMapping_FixedPropertyValues(),
										fpv));			
							}
						}
					}
				}
			}
			
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			// showMessage("Failed to map RIM Structural attributes: " + ex.getMessage());
		}
		
	}

}
