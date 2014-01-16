package com.openMap1.mapper;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import com.openMap1.mapper.StructureType;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Structure Type</b></em>',
 * and utility methods for working with them.
 * 
 *  structure type is a type of data structure recognised by the mapper editor,
 *  Currently either 'XML' or 'RDBMS'.
 *  
 * <!-- end-user-doc -->
 * @see com.openMap1.mapper.MapperPackage#getStructureType()
 * @model
 * @generated
 */
public enum StructureType implements Enumerator {
	/**
	 * The '<em><b>XSD</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #XSD_VALUE
	 * @generated
	 * @ordered
	 */
	XSD(0, "XSD", "XSD"),

	/**
	 * The '<em><b>RDBMS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RDBMS_VALUE
	 * @generated
	 * @ordered
	 */
	RDBMS(1, "RDBMS", "RDBMS"),

	/**
	 * The '<em><b>V2</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #V2_VALUE
	 * @generated
	 * @ordered
	 */
	V2(2, "V2", "V2");

	/**
	 * The '<em><b>XSD</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>XSD</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #XSD
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int XSD_VALUE = 0;

	/**
	 * The '<em><b>RDBMS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>RDBMS</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RDBMS
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int RDBMS_VALUE = 1;

	/**
	 * The '<em><b>V2</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>V2</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #V2
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int V2_VALUE = 2;

	/**
	 * An array of all the '<em><b>Structure Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final StructureType[] VALUES_ARRAY =
		new StructureType[] {
			XSD,
			RDBMS,
			V2,
		};

	/**
	 * A public read-only list of all the '<em><b>Structure Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<StructureType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Structure Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static StructureType get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			StructureType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Structure Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static StructureType getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			StructureType result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Structure Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static StructureType get(int value) {
		switch (value) {
			case XSD_VALUE: return XSD;
			case RDBMS_VALUE: return RDBMS;
			case V2_VALUE: return V2;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private StructureType(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getValue() {
	  return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
	  return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //StructureType
