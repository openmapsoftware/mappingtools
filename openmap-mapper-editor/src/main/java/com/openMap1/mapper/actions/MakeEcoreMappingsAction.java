package com.openMap1.mapper.actions;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.views.ClassModelView;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;

/**
 * Action to make the mappings which describe how and EMF Ecore
 * instance, seen as an XML instance, represents the class model 
 * information.
 * 
 * @author robert
 *
 */

public class MakeEcoreMappingsAction extends Action implements IAction {
	
	private ClassModelView classModelView;

	public MakeEcoreMappingsAction(ClassModelView classModelView){
		super("Make Ecore Mappings");
		this.classModelView = classModelView;
	}
	
	public void run()
	{
		// (1) get a location for the Ecore mapping set from the user
	    String[] modelExts= {".mapper"};
		final String mappingSetPath = FileUtil.getFilePathFromUser(classModelView,modelExts,"Select location for Ecore mapping set",true);
		if (mappingSetPath.equals("")) return;
		//URI mappingSetURI = FileUtil.URIFromPath(mappingSetPath);
		
		// (2) If any associations in the model do not have inverse, add them
		boolean modelChanged = addInverseAssociations();
		if (modelChanged) {}
		
		createEcoreMappings();
	}

	
	/** 
	 * for any association in the ecore model which does not have an inverse, add one
	 */
	private boolean addInverseAssociations()
	{
		boolean inversesAdded = false;
		EPackage model = classModelView.ecoreRoot();

		// loop over all classes in the model
		for (Iterator<EClassifier> it = model.getEClassifiers().iterator(); it.hasNext();)
		{
			EClassifier next = it.next();
			if (next instanceof EClass)
			{
				EClass ec = (EClass)next;

				// accumulate those EReferences which will need inverses adding
				Vector<EReference> refsWithNoInverse = new Vector<EReference>();
				for (Iterator<EStructuralFeature> is = ec.getEStructuralFeatures().iterator(); is.hasNext();)
				{
					EStructuralFeature ef = is.next();
					if (ef instanceof EReference)
					{
						EReference er = (EReference)ef;
						// if the association has no opposite, note it
						if (er.getEOpposite() == null) refsWithNoInverse.add(er);
					}
				}

				// make and add all the inverses needed
				for (Iterator<EReference> ir = refsWithNoInverse.iterator(); ir.hasNext();)
				{
					inversesAdded = true;
					EReference er = ir.next();
					EReference inverse = EcoreFactory.eINSTANCE.createEReference();

					// add the inverse to the EReferences of the opposite end class
					EClassifier oppEnd = er.getEType();
					if (oppEnd instanceof EClass)
					{
						EClass oppClass = (EClass)oppEnd;
						oppClass.getEStructuralFeatures().add(inverse);
					}

					// set the target of the inverse to be the original class
					inverse.setEType(ec);

					// make it the opposite of the first EReference; do both ways
					er.setEOpposite(inverse);
					inverse.setEOpposite(er);
					
					// set its properties
					inverse.setName("inverse_" + er.getName());
					if (er.isContainment()) inverse.setUpperBound(1);					
				}
			}
		}
		
		// save the extended model, if any inverses have been added
		if (inversesAdded)
		{
			classModelView.saveAtURI(model, classModelView.classModelURI(),"ecore");
		}
		
		return inversesAdded;
	}
	
	private void createEcoreMappings()
	{
		
	}
	

}
