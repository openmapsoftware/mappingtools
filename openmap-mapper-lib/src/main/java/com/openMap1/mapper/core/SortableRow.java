package com.openMap1.mapper.core;

import java.util.Vector;

public interface SortableRow {

	//types for comparators
	public static int STRING = 0;
	public static int NUMBER = 1;
	
	/**
	 * for an object to serve as a sortable row for class RowSorter,
	 * it must be able to present the cell contents as a Vector.
	 * @return
	 */
	public Vector<?> rowVector();
	

}
