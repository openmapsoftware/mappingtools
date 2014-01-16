/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.provider;


import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MapperPackage;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;


/**
 * This is the item provider adapter for a {@link com.openMap1.mapper.ElementDef} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ElementDefItemProvider
	extends NodeDefItemProvider
	implements	
		IEditingDomainItemProvider,	
		IStructuredItemContentProvider,	
		ITreeItemContentProvider,	
		IItemLabelProvider,	
		IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ElementDefItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addExpandedPropertyDescriptor(object);
			addMaxMultiplicityPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Expanded feature.
	 * <!-- begin-user-doc -->
	 * At one stage I changed to make this property not settable - first boolean false
	 * but now (6/09) I want it to be editable - first boolean true.
	 * <!-- end-user-doc -->
	 */
	protected void addExpandedPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Element_expanded_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Element_expanded_feature", "_UI_Element_type"),
				 MapperPackage.Literals.ELEMENT_DEF__EXPANDED,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Max Multiplicity feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addMaxMultiplicityPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_ElementDef_maxMultiplicity_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_ElementDef_maxMultiplicity_feature", "_UI_ElementDef_type"),
				 MapperPackage.Literals.ELEMENT_DEF__MAX_MULTIPLICITY,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(MapperPackage.Literals.ELEMENT_DEF__CHILD_ELEMENTS);
			childrenFeatures.add(MapperPackage.Literals.ELEMENT_DEF__ATTRIBUTE_DEFS);
			childrenFeatures.add(MapperPackage.Literals.ELEMENT_DEF__IMPORT_MAPPING_SET);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns a gif for the Element Icon.
	 * <!-- begin-user-doc -->
	 * If there are any mappings in the subtree below an Element, show the icon yellow-highlighted
	 * <!-- end-user-doc -->
	 */
	@Override
	public Object getImage(Object object) {
		if ((object instanceof  ElementDef) && (((ElementDef)object).hasMappingsOnNode()))
			return overlayImage(object, getResourceLocator().getImage("full/obj16/ElementGreen"));
		if ((object instanceof  ElementDef) && (((ElementDef)object).hasMappingsInSubTree()))
			return overlayImage(object, getResourceLocator().getImage("full/obj16/ElementWithMappings"));
		else return overlayImage(object, getResourceLocator().getImage("full/obj16/Element"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * RW : as we have a decent icon for Elements, no need for the word 'Element'
	 * <!-- end-user-doc -->
	 */
	public String getText(Object object) {
		ElementDef ed = (ElementDef)object;
		String label = ed.getName();
		/* 10/10: adding descriptions on the tree nodes can be too verbose; it can be seen in the properties view
		if ((ed.getDescription() != null) && (!ed.getDescription().equals("")))
			label = label + " : " + ed.getDescription();
		*/
		return label == null || label.length() == 0 ?
			"" : label;
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(ElementDef.class)) {
			case MapperPackage.ELEMENT_DEF__EXPANDED:
			case MapperPackage.ELEMENT_DEF__MAX_MULTIPLICITY:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
			case MapperPackage.ELEMENT_DEF__CHILD_ELEMENTS:
			case MapperPackage.ELEMENT_DEF__ATTRIBUTE_DEFS:
			case MapperPackage.ELEMENT_DEF__IMPORT_MAPPING_SET:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * modified by RW to remove the option of adding Elements or Attributes to an Element
	 * <!-- end-user-doc -->
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);
		
		/*
		newChildDescriptors.add
			(createChildParameter
				(MapperPackage.Literals.ELEMENT_DEF__CHILD_ELEMENT_DEFS,
				 MapperFactory.eINSTANCE.createElement()));

		newChildDescriptors.add
			(createChildParameter
				(MapperPackage.Literals.ELEMENT_DEF__ATTRIBUTE_DEFS,
				 MapperFactory.eINSTANCE.createAttribute()));
		*/
	}
	
	/**
	 * Override for drag and drop command to attach an object mapping to the Element
	 * (does not work)
	protected Command createDragAndDropCommand(EditingDomain domain,
            				Object owner,
            				float location,
            				int operations,
            				int operation,                                          
            				Collection<?> collection)
	{
		Object draggedObject = collection.iterator().next();
		String draggedClass = draggedObject.getClass().getName();
		System.out.println("Dragged object class: " + draggedClass);
		
		ObjMapping om = MapperFactory.eINSTANCE.createObjMapping();
		om.setMappedClass("Unknown");
		Vector<ObjMapping> mappings = new Vector<ObjMapping>();
		mappings.addElement(om);
		
		// this seems to return true
		boolean can = new AddCommand(domain, (EObject)owner,
				MapperPackage.Literals.NODE_DEF__OBJECT_MAPPINGS,
				mappings).canExecute();
		System.out.println("Executable: " + can);

		
		// try to substitute an ObjMapping for the object actually being dropped
		if ((can) && (owner instanceof Element))
		{
			// does not work if the original drag and drop is not enabled 
			return new AddCommand(domain, (EObject)owner,
					MapperPackage.Literals.NODE_DEF__OBJECT_MAPPINGS,
					mappings);

			// does not work if the original drag and drop is not enabled
			return super.createDragAndDropCommand(domain,
					owner,location , operation, operations, mappings); 
		}
		else return UnexecutableCommand.INSTANCE;		
	}
	 */

}
