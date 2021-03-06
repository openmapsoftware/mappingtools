/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.provider;

import com.openMap1.mapper.util.MapperAdapterFactory;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class MapperItemProviderAdapterFactory extends MapperAdapterFactory implements ComposeableAdapterFactory, IChangeNotifier, IDisposable {
	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;

	/**
	 * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

	/**
	 * This constructs an instance.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MapperItemProviderAdapterFactory() {
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
		supportedTypes.add(IItemPropertySource.class);
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.Annotations} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AnnotationsItemProvider annotationsItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.Annotations}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createAnnotationsAdapter() {
		if (annotationsItemProvider == null) {
			annotationsItemProvider = new AnnotationsItemProvider(this);
		}

		return annotationsItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.AssocEndMapping} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AssocEndMappingItemProvider assocEndMappingItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.AssocEndMapping}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createAssocEndMappingAdapter() {
		if (assocEndMappingItemProvider == null) {
			assocEndMappingItemProvider = new AssocEndMappingItemProvider(this);
		}

		return assocEndMappingItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.AssocMapping} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AssocMappingItemProvider assocMappingItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.AssocMapping}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createAssocMappingAdapter() {
		if (assocMappingItemProvider == null) {
			assocMappingItemProvider = new AssocMappingItemProvider(this);
		}

		return assocMappingItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.AttributeDef} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AttributeDefItemProvider attributeDefItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.AttributeDef}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createAttributeDefAdapter() {
		if (attributeDefItemProvider == null) {
			attributeDefItemProvider = new AttributeDefItemProvider(this);
		}

		return attributeDefItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.ClassDetails} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ClassDetailsItemProvider classDetailsItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.ClassDetails}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createClassDetailsAdapter() {
		if (classDetailsItemProvider == null) {
			classDetailsItemProvider = new ClassDetailsItemProvider(this);
		}

		return classDetailsItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.ConversionArgument} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ConversionArgumentItemProvider conversionArgumentItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.ConversionArgument}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createConversionArgumentAdapter() {
		if (conversionArgumentItemProvider == null) {
			conversionArgumentItemProvider = new ConversionArgumentItemProvider(this);
		}

		return conversionArgumentItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.CrossCondition} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CrossConditionItemProvider crossConditionItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.CrossCondition}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createCrossConditionAdapter() {
		if (crossConditionItemProvider == null) {
			crossConditionItemProvider = new CrossConditionItemProvider(this);
		}

		return crossConditionItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.ElementDef} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ElementDefItemProvider elementDefItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.ElementDef}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createElementDefAdapter() {
		if (elementDefItemProvider == null) {
			elementDefItemProvider = new ElementDefItemProvider(this);
		}

		return elementDefItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.FixedPropertyValue} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FixedPropertyValueItemProvider fixedPropertyValueItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.FixedPropertyValue}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createFixedPropertyValueAdapter() {
		if (fixedPropertyValueItemProvider == null) {
			fixedPropertyValueItemProvider = new FixedPropertyValueItemProvider(this);
		}

		return fixedPropertyValueItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.GlobalMappingParameters} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GlobalMappingParametersItemProvider globalMappingParametersItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.GlobalMappingParameters}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createGlobalMappingParametersAdapter() {
		if (globalMappingParametersItemProvider == null) {
			globalMappingParametersItemProvider = new GlobalMappingParametersItemProvider(this);
		}

		return globalMappingParametersItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.ImportMappingSet} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ImportMappingSetItemProvider importMappingSetItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.ImportMappingSet}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createImportMappingSetAdapter() {
		if (importMappingSetItemProvider == null) {
			importMappingSetItemProvider = new ImportMappingSetItemProvider(this);
		}

		return importMappingSetItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.JavaConversionImplementation} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected JavaConversionImplementationItemProvider javaConversionImplementationItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.JavaConversionImplementation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createJavaConversionImplementationAdapter() {
		if (javaConversionImplementationItemProvider == null) {
			javaConversionImplementationItemProvider = new JavaConversionImplementationItemProvider(this);
		}

		return javaConversionImplementationItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.LocalPropertyConversion} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalPropertyConversionItemProvider localPropertyConversionItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.LocalPropertyConversion}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createLocalPropertyConversionAdapter() {
		if (localPropertyConversionItemProvider == null) {
			localPropertyConversionItemProvider = new LocalPropertyConversionItemProvider(this);
		}

		return localPropertyConversionItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.MappedStructure} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MappedStructureItemProvider mappedStructureItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.MappedStructure}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createMappedStructureAdapter() {
		if (mappedStructureItemProvider == null) {
			mappedStructureItemProvider = new MappedStructureItemProvider(this);
		}

		return mappedStructureItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.ModelAssocFilter} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModelAssocFilterItemProvider modelAssocFilterItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.ModelAssocFilter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createModelAssocFilterAdapter() {
		if (modelAssocFilterItemProvider == null) {
			modelAssocFilterItemProvider = new ModelAssocFilterItemProvider(this);
		}

		return modelAssocFilterItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.ModelFilterSet} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModelFilterSetItemProvider modelFilterSetItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.ModelFilterSet}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createModelFilterSetAdapter() {
		if (modelFilterSetItemProvider == null) {
			modelFilterSetItemProvider = new ModelFilterSetItemProvider(this);
		}

		return modelFilterSetItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.ModelPropertyFilter} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModelPropertyFilterItemProvider modelPropertyFilterItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.ModelPropertyFilter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createModelPropertyFilterAdapter() {
		if (modelPropertyFilterItemProvider == null) {
			modelPropertyFilterItemProvider = new ModelPropertyFilterItemProvider(this);
		}

		return modelPropertyFilterItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.Namespace} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NamespaceItemProvider namespaceItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.Namespace}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createNamespaceAdapter() {
		if (namespaceItemProvider == null) {
			namespaceItemProvider = new NamespaceItemProvider(this);
		}

		return namespaceItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.NodeMappingSet} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NodeMappingSetItemProvider nodeMappingSetItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.NodeMappingSet}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createNodeMappingSetAdapter() {
		if (nodeMappingSetItemProvider == null) {
			nodeMappingSetItemProvider = new NodeMappingSetItemProvider(this);
		}

		return nodeMappingSetItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.Note} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NoteItemProvider noteItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.Note}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createNoteAdapter() {
		if (noteItemProvider == null) {
			noteItemProvider = new NoteItemProvider(this);
		}

		return noteItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.ObjMapping} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ObjMappingItemProvider objMappingItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.ObjMapping}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createObjMappingAdapter() {
		if (objMappingItemProvider == null) {
			objMappingItemProvider = new ObjMappingItemProvider(this);
		}

		return objMappingItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.ParameterClass} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ParameterClassItemProvider parameterClassItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.ParameterClass}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createParameterClassAdapter() {
		if (parameterClassItemProvider == null) {
			parameterClassItemProvider = new ParameterClassItemProvider(this);
		}

		return parameterClassItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.ParameterClassValue} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ParameterClassValueItemProvider parameterClassValueItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.ParameterClassValue}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createParameterClassValueAdapter() {
		if (parameterClassValueItemProvider == null) {
			parameterClassValueItemProvider = new ParameterClassValueItemProvider(this);
		}

		return parameterClassValueItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.PropMapping} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PropMappingItemProvider propMappingItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.PropMapping}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createPropMappingAdapter() {
		if (propMappingItemProvider == null) {
			propMappingItemProvider = new PropMappingItemProvider(this);
		}

		return propMappingItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.PropertyConversion} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PropertyConversionItemProvider propertyConversionItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.PropertyConversion}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createPropertyConversionAdapter() {
		if (propertyConversionItemProvider == null) {
			propertyConversionItemProvider = new PropertyConversionItemProvider(this);
		}

		return propertyConversionItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.ValueCondition} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ValueConditionItemProvider valueConditionItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.ValueCondition}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createValueConditionAdapter() {
		if (valueConditionItemProvider == null) {
			valueConditionItemProvider = new ValueConditionItemProvider(this);
		}

		return valueConditionItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.ValuePair} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ValuePairItemProvider valuePairItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.ValuePair}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createValuePairAdapter() {
		if (valuePairItemProvider == null) {
			valuePairItemProvider = new ValuePairItemProvider(this);
		}

		return valuePairItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link com.openMap1.mapper.XSLTConversionImplementation} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected XSLTConversionImplementationItemProvider xsltConversionImplementationItemProvider;

	/**
	 * This creates an adapter for a {@link com.openMap1.mapper.XSLTConversionImplementation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createXSLTConversionImplementationAdapter() {
		if (xsltConversionImplementationItemProvider == null) {
			xsltConversionImplementationItemProvider = new XSLTConversionImplementationItemProvider(this);
		}

		return xsltConversionImplementationItemProvider;
	}

	/**
	 * This returns the root adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComposeableAdapterFactory getRootAdapterFactory() {
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * This sets the composed adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
		this.parentAdapterFactory = parentAdapterFactory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object type) {
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter adapt(Notifier notifier, Object type) {
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object adapt(Object object, Object type) {
		if (isFactoryForType(type)) {
			Object adapter = super.adapt(object, type);
			if (!(type instanceof Class<?>) || (((Class<?>)type).isInstance(adapter))) {
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This removes a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void fireNotifyChanged(Notification notification) {
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null) {
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

	/**
	 * This disposes all of the item providers created by this factory. 
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void dispose() {
		if (annotationsItemProvider != null) annotationsItemProvider.dispose();
		if (assocEndMappingItemProvider != null) assocEndMappingItemProvider.dispose();
		if (assocMappingItemProvider != null) assocMappingItemProvider.dispose();
		if (attributeDefItemProvider != null) attributeDefItemProvider.dispose();
		if (classDetailsItemProvider != null) classDetailsItemProvider.dispose();
		if (conversionArgumentItemProvider != null) conversionArgumentItemProvider.dispose();
		if (crossConditionItemProvider != null) crossConditionItemProvider.dispose();
		if (elementDefItemProvider != null) elementDefItemProvider.dispose();
		if (fixedPropertyValueItemProvider != null) fixedPropertyValueItemProvider.dispose();
		if (globalMappingParametersItemProvider != null) globalMappingParametersItemProvider.dispose();
		if (importMappingSetItemProvider != null) importMappingSetItemProvider.dispose();
		if (javaConversionImplementationItemProvider != null) javaConversionImplementationItemProvider.dispose();
		if (localPropertyConversionItemProvider != null) localPropertyConversionItemProvider.dispose();
		if (mappedStructureItemProvider != null) mappedStructureItemProvider.dispose();
		if (modelAssocFilterItemProvider != null) modelAssocFilterItemProvider.dispose();
		if (modelFilterSetItemProvider != null) modelFilterSetItemProvider.dispose();
		if (modelPropertyFilterItemProvider != null) modelPropertyFilterItemProvider.dispose();
		if (namespaceItemProvider != null) namespaceItemProvider.dispose();
		if (nodeMappingSetItemProvider != null) nodeMappingSetItemProvider.dispose();
		if (noteItemProvider != null) noteItemProvider.dispose();
		if (objMappingItemProvider != null) objMappingItemProvider.dispose();
		if (parameterClassItemProvider != null) parameterClassItemProvider.dispose();
		if (parameterClassValueItemProvider != null) parameterClassValueItemProvider.dispose();
		if (propMappingItemProvider != null) propMappingItemProvider.dispose();
		if (propertyConversionItemProvider != null) propertyConversionItemProvider.dispose();
		if (valueConditionItemProvider != null) valueConditionItemProvider.dispose();
		if (valuePairItemProvider != null) valuePairItemProvider.dispose();
		if (xsltConversionImplementationItemProvider != null) xsltConversionImplementationItemProvider.dispose();
	}

}
