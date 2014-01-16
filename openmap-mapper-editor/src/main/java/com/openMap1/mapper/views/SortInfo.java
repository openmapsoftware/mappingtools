package com.openMap1.mapper.views;

import java.util.Comparator;

/**
 * information used in sorting any tabular view.
 * 
 * @author robert
 *
 */

public class SortInfo {

	/** index 0..N of the column being sorted */
	public int columnIndex() {return columnIndex;}
	private int columnIndex;

	/** to compare String values of cells in various ways */
	public Comparator<String> comparator() {return comparator;}
	private Comparator<String> comparator;

	/** if true , the sort is descending in this column */
	public boolean descending() {return descending;}
	private boolean descending;
	
	/** toggle between ascending and descending */
	public void reverse() {descending = !descending;}
	
	/**
	 * 
	 * @param columnIndex index 0..N of the column being sorted 
	 * @param comparator to compare String values of cells in various ways
	 */
	public SortInfo (int columnIndex, Comparator<String> comparator)
	{
		this.columnIndex = columnIndex;
		this.comparator = comparator;
		descending = false; // initially, and ascending sort on the column
	}

}
