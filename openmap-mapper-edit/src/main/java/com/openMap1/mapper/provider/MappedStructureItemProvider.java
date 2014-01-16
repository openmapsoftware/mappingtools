/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.provider;



import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

// imports needed for RW added code
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.ecore.EObject;
import com.openMap1.mapper.commands.SetTopElementTypeCommand;
import com.openMap1.mapper.commands.SetTopElementNameCommand;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;

/**
 * This is the item provider adapter for a {@link com.openMap1.mapper.MappedStructure} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class MappedStructureItemProvider
	extends ItemProviderAdapter
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
	public MappedStructureItemProvider(AdapterFactory adapterFactory) {
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

			addNamePropertyDescriptor(object);
			addUMLModelURLPropertyDescriptor(object);
			addStructureTypePropertyDescriptor(object);
			addStructureURLPropertyDescriptor(object);
			addTopElementTypePropertyDescriptor(object);
			addTopElementNamePropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Name feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addNamePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_MappedStructure_name_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_MappedStructure_name_feature", "_UI_MappedStructure_type"),
				 MapperPackage.Literals.MAPPED_STRUCTURE__NAME,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the UML Model URL feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addUMLModelURLPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_MappedStructure_uMLModelURL_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_MappedStructure_uMLModelURL_feature", "_UI_MappedStructure_type"),
				 MapperPackage.Literals.MAPPED_STRUCTURE__UML_MODEL_URL,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Structure Type feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addStructureTypePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_MappedStructure_structureType_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_MappedStructure_structureType_feature", "_UI_MappedStructure_type"),
				 MapperPackage.Literals.MAPPED_STRUCTURE__STRUCTURE_TYPE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Structure URL feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addStructureURLPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_MappedStructure_structureURL_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_MappedStructure_structureURL_feature", "_UI_MappedStructure_type"),
				 MapperPackage.Literals.MAPPED_STRUCTURE__STRUCTURE_URL,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Top Element Type feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addTopElementTypePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_MappedStructure_topElementType_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_MappedStructure_topElementType_feature", "_UI_MappedStructure_type"),
				 MapperPackage.Literals.MAPPED_STRUCTURE__TOP_ELEMENT_TYPE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Top Element Name feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addTopElementNamePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_MappedStructure_topElementName_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_MappedStructure_topElementName_feature", "_UI_MappedStructure_type"),
				 MapperPackage.Literals.MAPPED_STRUCTURE__TOP_ELEMENT_NAME,
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
			childrenFeatures.add(MapperPackage.Literals.MAPPED_STRUCTURE__ROOT_ELEMENT);
			childrenFeatures.add(MapperPackage.Literals.MAPPED_STRUCTURE__MAPPING_PARAMETERS);
			childrenFeatures.add(MapperPackage.Literals.MAPPED_STRUCTURE__PARAMETER_CLASSES);
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
	 * This returns MappedStructure.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/MappedStructure"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	@Override
	public String getText(Object object) {
		MappedStructure ms = (MappedStructure)object;
		return ("Mappings:  " + ms.getStructureFileName() + " => " + ms.getClassModelFileName());
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

		switch (notification.getFeatureID(MappedStructure.class)) {
			case MapperPackage.MAPPED_STRUCTURE__NAME:
			case MapperPackage.MAPPED_STRUCTURE__UML_MODEL_URL:
			case MapperPackage.MAPPED_STRUCTURE__STRUCTURE_TYPE:
			case MapperPackage.MAPPED_STRUCTURE__STRUCTURE_URL:
			case MapperPackage.MAPPED_STRUCTURE__TOP_ELEMENT_TYPE:
			case MapperPackage.MAPPED_STRUCTURE__TOP_ELEMENT_NAME:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
			case MapperPackage.MAPPED_STRUCTURE__ROOT_ELEMENT:
			case MapperPackage.MAPPED_STRUCTURE__MAPPING_PARAMETERS:
			case MapperPackage.MAPPED_STRUCTURE__PARAMETER_CLASSES:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
				(MapperPackage.Literals.MAPPED_STRUCTURE__ROOT_ELEMENT,
				 MapperFactory.eINSTANCE.createElementDef()));

		newChildDescriptors.add
			(createChildParameter
				(MapperPackage.Literals.MAPPED_STRUCTURE__MAPPING_PARAMETERS,
				 MapperFactory.eINSTANCE.createGlobalMappingParameters()));

		newChildDescriptors.add
			(createChildParameter
				(MapperPackage.Literals.MAPPED_STRUCTURE__PARAMETER_CLASSES,
				 MapperFactory.eINSTANCE.createParameterClass()));
	}

	/**
	 * Return the resource locator for this item provider's resources.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return MapperEditPlugin.INSTANCE;
	}
	
	/**
	 * Added by RW
	 * Override the command to set the properties "Top Element Type" 
	 * and "Top Element Name" so that after setting the property,
	 * a top element of the correct type is added (with  the correct structure)
	 */
	public Command createSetCommand(EditingDomain domain,
										EObject owner,
										EStructuralFeature feature,
										Object value)
	{
		// special case when the property 'Top Element Type' has just been set
		if (feature == MapperPackage.Literals.MAPPED_STRUCTURE__TOP_ELEMENT_TYPE)
		{
			if ((owner instanceof MappedStructure)&& (value instanceof String)) // they should be
					return new SetTopElementTypeCommand(domain,owner,feature,value);
		}
		// special case when the property 'Top Element Name' has just been set
		if (feature == MapperPackage.Literals.MAPPED_STRUCTURE__TOP_ELEMENT_NAME)
		{
			if ((owner instanceof MappedStructure)&& (value instanceof String)) // they should be
					return new SetTopElementNameCommand(domain,owner,feature,value);
		}
		// do the usual set command for all other properties
		return super.createSetCommand(domain, owner, feature, value);
	}
	

}
