package com.openMap1.mapper.views;

/**
 * Interface which any view class must implement
 * if its rows are to be sortable on some columns
 * 
 * @author robert
 *
 */
public interface SortableView {
	/**
	 * Use the locally cached results to show them again - with a new sort order
	 */
	public void showResultAgain();
	
	/**
	 * return the row sorter for the view
	 */
	public RowSorter rowSorter();

}
