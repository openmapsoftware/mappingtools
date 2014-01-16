package com.openMap1.mapper.presentation;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.emf.ecore.impl.EObjectImpl;


/**
 * used to modify a property source provider derived from an EMF model,
 * replacing the cell editors for some properties
 * @author robert
 *
 */


public class PropertySourceProviderWrapper
	implements IPropertySourceProvider {
	
	private PropertyValueSetProvider pvsp;

	// the supplied property source provider for which this class is a wrapper
	private IPropertySourceProvider psp;
	
	public PropertySourceProviderWrapper(IPropertySourceProvider psp, PropertyValueSetProvider pvsp)
	{
		this.psp = psp;
		this.pvsp = pvsp;
	}

	@Override
	public IPropertySource getPropertySource(Object object) {
		if (object instanceof EObjectImpl) // superclass for model classes such as NodeImpl
		{
			String cName = ((EObjectImpl)object).eClass().getName();
			IPropertySource ps = psp.getPropertySource(object);
			PropertySourceWrapper psw = new PropertySourceWrapper(ps,cName,pvsp);
			return psw;
		}
		return psp.getPropertySource(object);
	}
	

}
