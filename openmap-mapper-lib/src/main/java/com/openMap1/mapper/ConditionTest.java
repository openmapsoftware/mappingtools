/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

import com.openMap1.mapper.ConditionTest;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Condition Test</b></em>',
 * and utility methods for working with them.
 * 
 * This lists all the tests that can be applied between values 
 * when evaluating mapping conditions.
 * The most commonly used is '=', but inequalities and 'contains' comparisons are
 * also used sometimes.
 * <!-- end-user-doc -->
 * @see com.openMap1.mapper.MapperPackage#getConditionTest()
 * @model
 * @generated
 */
public enum ConditionTest implements Enumerator {
	/**
	 * The '<em><b>EQUALS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #EQUALS_VALUE
	 * @generated
	 * @ordered
	 */
	EQUALS(0, "EQUALS", "="),

	/**
	 * The '<em><b>GT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #GT_VALUE
	 * @generated
	 * @ordered
	 */
	GT(1, "GT", ">"),

	/**
	 * The '<em><b>LT</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LT_VALUE
	 * @generated
	 * @ordered
	 */
	LT(2, "LT", "<"), /**
	 * The '<em><b>CONTAINS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * Test that one string contains another as a substring
	 * <!-- end-user-doc -->
	 * @see #CONTAINS_VALUE
	 * @generated
	 * @ordered
	 */
	CONTAINS(3, "CONTAINS", "contains"), /**
	 * The '<em><b>CONTAINEDBY</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * Test that oone string is contained as a substring of another.
	 * <!-- end-user-doc -->
	 * @see #CONTAINEDBY_VALUE
	 * @generated
	 * @ordered
	 */
	CONTAINEDBY(4, "CONTAINEDBY", "containedBy"), /**
	 * The '<em><b>CONTAINSASWORD</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONTAINSASWORD_VALUE
	 * @generated
	 * @ordered
	 */
	CONTAINSASWORD(5, "CONTAINSASWORD", "containsAsWord"), /**
	 * The '<em><b>CONTAINEDBYASWORD</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * Test that one stribng contains another as a word, i.e 
	 * spearated by spaces from the rest.
	 * <!-- end-user-doc -->
	 * @see #CONTAINEDBYASWORD_VALUE
	 * @generated
	 * @ordered
	 */
	CONTAINEDBYASWORD(6, "CONTAINEDBYASWORD", "containedByAsWord"), /**
	 * The '<em><b>NOT EQUALS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #NOT_EQUALS_VALUE
	 * @generated
	 * @ordered
	 */
	NOT_EQUALS(7, "NOT_EQUALS", "!=");

	/**
	 * The '<em><b>EQUALS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>EQUALS</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #EQUALS
	 * @model literal="="
	 * @generated
	 * @ordered
	 */
	public static final int EQUALS_VALUE = 0;

	/**
	 * The '<em><b>GT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>GT</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #GT
	 * @model literal=">"
	 * @generated
	 * @ordered
	 */
	public static final int GT_VALUE = 1;

	/**
	 * The '<em><b>LT</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>LT</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LT
	 * @model literal="<"
	 * @generated
	 * @ordered
	 */
	public static final int LT_VALUE = 2;

	/**
	 * The '<em><b>CONTAINS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>CONTAINS</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTAINS
	 * @model literal="contains"
	 * @generated
	 * @ordered
	 */
	public static final int CONTAINS_VALUE = 3;

	/**
	 * The '<em><b>CONTAINEDBY</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>CONTAINEDBY</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTAINEDBY
	 * @model literal="containedBy"
	 * @generated
	 * @ordered
	 */
	public static final int CONTAINEDBY_VALUE = 4;

	/**
	 * The '<em><b>CONTAINSASWORD</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>CONTAINSASWORD</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTAINSASWORD
	 * @model literal="containsAsWord"
	 * @generated
	 * @ordered
	 */
	public static final int CONTAINSASWORD_VALUE = 5;

	/**
	 * The '<em><b>CONTAINEDBYASWORD</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>CONTAINEDBYASWORD</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONTAINEDBYASWORD
	 * @model literal="containedByAsWord"
	 * @generated
	 * @ordered
	 */
	public static final int CONTAINEDBYASWORD_VALUE = 6;

	/**
	 * The '<em><b>NOT EQUALS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>NOT EQUALS</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #NOT_EQUALS
	 * @model literal="!="
	 * @generated
	 * @ordered
	 */
	public static final int NOT_EQUALS_VALUE = 7;

	/**
	 * An array of all the '<em><b>Condition Test</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final ConditionTest[] VALUES_ARRAY =
		new ConditionTest[] {
			EQUALS,
			GT,
			LT,
			CONTAINS,
			CONTAINEDBY,
			CONTAINSASWORD,
			CONTAINEDBYASWORD,
			NOT_EQUALS,
		};

	/**
	 * A public read-only list of all the '<em><b>Condition Test</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<ConditionTest> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Condition Test</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ConditionTest get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ConditionTest result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Condition Test</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ConditionTest getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			ConditionTest result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Condition Test</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ConditionTest get(int value) {
		switch (value) {
			case EQUALS_VALUE: return EQUALS;
			case GT_VALUE: return GT;
			case LT_VALUE: return LT;
			case CONTAINS_VALUE: return CONTAINS;
			case CONTAINEDBY_VALUE: return CONTAINEDBY;
			case CONTAINSASWORD_VALUE: return CONTAINSASWORD;
			case CONTAINEDBYASWORD_VALUE: return CONTAINEDBYASWORD;
			case NOT_EQUALS_VALUE: return NOT_EQUALS;
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
	private ConditionTest(int value, String name, String literal) {
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
	
} //ConditionTest
