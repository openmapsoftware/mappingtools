/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.provider;


import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.NodeMappingSet;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link com.openMap1.mapper.NodeMappingSet} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class NodeMappingSetItemProvider
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
	public NodeMappingSetItemProvider(AdapterFactory adapterFactory) {
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

		}
		return itemPropertyDescriptors;
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
			childrenFeatures.add(MapperPackage.Literals.NODE_MAPPING_SET__OBJECT_MAPPINGS);
			childrenFeatures.add(MapperPackage.Literals.NODE_MAPPING_SET__PROPERTY_MAPPINGS);
			childrenFeatures.add(MapperPackage.Literals.NODE_MAPPING_SET__ASSOCIATION_MAPPINGS);
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
	 * This returns NodeMappingSet.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/NodeMappingSet"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * changed to indicate the number of mappings under this node;
	 * but the number of mappings does not update dynamically as you add them.
	 * <!-- end-user-doc -->
	 */
	@Override
	public String getText(Object object) {
		NodeMappingSet nms = (NodeMappingSet)object;
		int so = nms.getObjectMappings().size();
		int sp = nms.getPropertyMappings().size();
		int sa = nms.getAssociationMappings().size();
		String label = "Mappings: ";
		// record nonzero mappings of each type
		if (so > 0) label = label + so + " object, ";
		if (sp > 0) label = label + sp + " property, ";
		if (sa > 0) label = label + sa + " link, ";
		// trim off the last ", "
		label = label.substring(0,label.length()-2);
		return label;
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

		switch (notification.getFeatureID(NodeMappingSet.class)) {
			case MapperPackage.NODE_MAPPING_SET__OBJECT_MAPPINGS:
			case MapperPackage.NODE_MAPPING_SET__PROPERTY_MAPPINGS:
			case MapperPackage.NODE_MAPPING_SET__ASSOCIATION_MAPPINGS:
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
				(MapperPackage.Literals.NODE_MAPPING_SET__OBJECT_MAPPINGS,
				 MapperFactory.eINSTANCE.createObjMapping()));

		newChildDescriptors.add
			(createChildParameter
				(MapperPackage.Literals.NODE_MAPPING_SET__PROPERTY_MAPPINGS,
				 MapperFactory.eINSTANCE.createPropMapping()));

		newChildDescriptors.add
			(createChildParameter
				(MapperPackage.Literals.NODE_MAPPING_SET__ASSOCIATION_MAPPINGS,
				 MapperFactory.eINSTANCE.createAssocMapping()));
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

}
