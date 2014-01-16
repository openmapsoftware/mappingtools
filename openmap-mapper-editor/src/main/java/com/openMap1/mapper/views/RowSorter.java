package com.openMap1.mapper.views;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;

import com.openMap1.mapper.query.CellContent;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.core.SortableRow;

import org.w3c.dom.Element;

/**
 * helper class used by any tabular view whose rows may need to be sorted.
 * @author robert
 *
 */
public class RowSorter  extends ViewerSorter{
	

	// comparators, visible to viewers	
	
	public static Comparator<String> comparatorForType(int sortType)
	{
		Comparator<String> comp = null;
		if (sortType == SortableRow.STRING) comp = stringComparator;
		if (sortType == SortableRow.NUMBER) comp = numberComparator;
		return comp;
	}

	
	/**
	 * alphabetic comparator for two Strings
	 */
	public static Comparator<String> stringComparator = new Comparator<String>(){
		public int compare(String s1, String s2)
		{return (s1.compareTo(s2));}
	};

	
	/**
	 * Comparator for two strings that are to be compared as numbers.
	 * Any String that cannot be interpreted as a number is taken as zero
	 * for this comparison.
	 */
	public static Comparator<String> numberComparator = new Comparator<String>(){
		public int compare(String s1, String s2)
		{
			int result = 0;
			double n1 = 0;
			double n2 = 0;
			try {n1 = new Double(s1).doubleValue();} catch (Exception ex) {}
			try {n2 = new Double(s2).doubleValue();} catch (Exception ex) {}
			if ((n1 - n2) > 0.0) result = 1;
			if ((n1 - n2) < 0.0) result = -1;
			return result;
		}
	};

	protected TableViewer viewer;
	
	// sort and comparison information for all columns in the query result
	protected Vector<SortInfo> infos;
	
	// those columns that have been selected for comparison in the sort
	protected Vector<SortInfo> selectedSorters;
	public Vector<SortInfo> selectedSorters() {return selectedSorters;}
	
	protected int previousColumnSelected = -1;
	
	/* keep track of the SelectionAdapter given to each TableColumn,
	 * so you can remove it before giving the column another one */
	protected Hashtable<Integer,SelectionAdapter> selectionAdapters;
	
	protected SortableView sortView;
	
	//-------------------------------------------------------------------------------
	//               constructor, and initialisation after any query
	//-------------------------------------------------------------------------------
	
	
	public RowSorter(TableViewer viewer, SortableView sortView)
	{
		this.viewer = viewer;
		this.sortView = sortView;
		
		/* Need to keep track of selection adapters on columns throughout 
		 * all queries in the lifetime of the Query Result view  - 
		 * do not re-initialise this for each new query result. */
		selectionAdapters = new Hashtable<Integer,SelectionAdapter>();
	}
	
	/**
	 * Create selection listeners for all sortable columns, so that mouse clicks on those columns
	 * wil define a sort order
	 * @param columns
	 * @param comparators
	 */
	public void initialiseForResult(Vector<TableColumn> columns, Vector<Comparator<String>> comparators)
	{
		previousColumnSelected = -1;
		infos = new Vector<SortInfo>();
		selectedSorters = new Vector<SortInfo>();
		for (int i = 0; i < columns.size(); i++)
		{
			SortInfo si = new SortInfo(i,comparators.elementAt(i));
			infos.add(si);
			createSelectionListener(columns.elementAt(i),si);
		}		
	}

	/* keep track of the SelectionAdapter given to each TableColumn,
	 * so you can remove it before giving the column another one */
	
	protected void createSelectionListener(final TableColumn column, final SortInfo info)
	{
		SelectionAdapter sa = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {sortUsing(info);}
		};
		SelectionAdapter oldSa = selectionAdapters.get(new Integer(info.columnIndex()));
		if (oldSa != null) column.removeSelectionListener(oldSa);
		column.addSelectionListener(sa);
		selectionAdapters.put(new Integer(info.columnIndex()),sa);
	}
	
	//-------------------------------------------------------------------------------
	//                               comparing rows
	//-------------------------------------------------------------------------------

	/**
	 * Work through the columns defined in selectedSorters, comparing on 
	 * each column until you get a decision
	 */
	public int compare(Viewer viewer, Object r1, Object r2)
	{
		int result = 0;
		Vector<?> row1 = new Vector<Object>();
		Vector<?> row2 = new Vector<Object>();
		if ((r1 instanceof Vector<?>)  && (r2 instanceof Vector<?>))
		{
			row1 = (Vector<?>)r1;
			row2 = (Vector<?>)r2;
		}
		else if ((r1 instanceof SortableRow)  && (r2 instanceof SortableRow))
		{
			row1 = ((SortableRow)r1).rowVector();
			row2 = ((SortableRow)r2).rowVector();
		}
		else {System.out.println("Rows of sorted table do not implement SortableRow");}
		
		if  ((row1.size() == row2.size()) && (selectedSorters != null))
		{
			// break off looking at further columns as soon as any column gives a decision
			for (int i = 0; i < selectedSorters.size(); i++) if (result == 0)
			{
				SortInfo si = selectedSorters.get(i);
				int col = si.columnIndex();
				if ((col > -1) && (col < row1.size()))
				{
					Object obj1 = row1.get(col);
					Object obj2 = row2.get(col);
					result = compareCell(obj1,obj2,si);
				}
			}
		}
		return result;
	}
	
	// compare the String contents of two cells, according to the comparator used for the column
	private int compareCell(Object obj1, Object obj2, SortInfo si)
	{
		int compare = 0;

		if ((obj1 instanceof CellContent) && (obj2 instanceof CellContent))
		{
			CellContent cell1 = (CellContent)obj1;
			CellContent cell2 = (CellContent)obj2;
			compare = si.comparator().compare(cell1.getText(), cell2.getText());
		}

		else if ((obj1 instanceof String) && (obj2 instanceof String))
		{
			compare = si.comparator().compare((String)obj1, (String)obj2);
		}

		if (si.descending()) compare = -compare;
		return compare;
	}
	
	//-------------------------------------------------------------------------------
	//                 telling the sorter how to compare rows
	//-------------------------------------------------------------------------------
	
	// maintain the Vector selectedSorters to do the sort implied by the sequence of column selections
	protected void sortUsing(SortInfo info)
	{
		/* if this column was the previous column selected, 
		 * the new selection only reverses the ascending/descending choice of the main sort */
		if (previousColumnSelected == info.columnIndex())
			selectedSorters.get(0).reverse();

		/* otherwise make the selected column head of the selected sorters, and 
		 * remove it from any lower position to ensure it is not duplicated. */ 
		else
		{
			selectedSorters.insertElementAt(info, 0);
			for (int s = 1; s < selectedSorters.size();s++)
			{
				SortInfo sr = selectedSorters.elementAt(s);
				if (sr.columnIndex() == info.columnIndex()) 
					selectedSorters.remove(s);
			}
		}

		// remember that this column has just been selected, so its sort can be reversed on the next selection
		previousColumnSelected = info.columnIndex();

		// do the sort
		viewer.refresh();
		sortView.showResultAgain();
	}
	
	
	/**
	 * for testing purposes, restore the selection of sort columns from 
	 * a saved query result view
	 * @param sortEl the <Sort> element of a saved query result view
	 */
	public void restoreSortColumns(Element sortEl)
	{
		selectedSorters = new Vector<SortInfo>();
		for (Iterator<Element> sc = XMLUtil.namedChildElements(sortEl,"Column").iterator();sc.hasNext();)
		try
		{
			Element column = sc.next();
			int index = new Integer(column.getAttribute("index")).intValue();
			SortInfo colSort = infos.get(index);
			if (column.getAttribute("descending").equals("true")) colSort.reverse();
			selectedSorters.add(colSort);
		}
		catch (Exception ex) {GenUtil.surprise(ex,"RowSorter.restoreSortColumns");}
	}
	

}
