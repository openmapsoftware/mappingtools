package com.openMap1.mapper.views;

import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IFolderLayout;

/**
 * defines the mappper perspective - the set of views and editors used by the  mapping tools.
 * @author robert
 *
 */

public class MapperPerspectiveFactory implements IPerspectiveFactory{
	
	private static final String CLASS_MODEL_VIEW_ID = "com.openMap1.mapper.views.ClassModelView";
	private static final String ATTRIBUTE_VIEW_ID = "com.openMap1.mapper.views.AttributeView";
	private static final String ASSOCIATION_VIEW_ID = "com.openMap1.mapper.views.AssociationView";
	private static final String DATA_SOURCE_VIEW_ID = "com.openMap1.mapper.views.DataSourceView";
	private static final String QUERY_RESULT_VIEW_ID = "com.openMap1.mapper.views.QueryResultView";
	private static final String TRANSLATION_ISSUE_VIEW_ID = "com.openMap1.mapper.views.TranslationIssueView";
	private static final String TRANSLATION_SUMMARY_VIEW_ID = "com.openMap1.mapper.views.TranslationSummaryView";
	private static final String MAPPINGS_VIEW_ID = "com.openMap1.mapper.views.MappingsView";
	private static final String DEBUG_VIEW_ID = "com.openMap1.mapper.views.DebugView";
	private static final String DEBUG_INSTANCE_VIEW_ID = "com.openMap1.mapper.views.DebugInstanceView";
	
	@SuppressWarnings("deprecation")
	public void createInitialLayout(IPageLayout layout)
	{
		// get the editor area
		String editorArea = layout.getEditorArea();
		
		// put the resource navigator view on the left
		// layout.addView(IPageLayout.ID_RES_NAV, IPageLayout.LEFT, 0.2f, editorArea);
		
		// make a folder to the left and add views to it
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.3f, editorArea);
		left.addView(ASSOCIATION_VIEW_ID);
		left.addView(IPageLayout.ID_RES_NAV);
		
		// make a folder at the bottom and add views to it
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.65f, editorArea);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(DATA_SOURCE_VIEW_ID);
		bottom.addView(QUERY_RESULT_VIEW_ID);
		bottom.addView(ATTRIBUTE_VIEW_ID);
		bottom.addView(TRANSLATION_ISSUE_VIEW_ID);
		bottom.addView(MAPPINGS_VIEW_ID);
		bottom.addView(DEBUG_VIEW_ID);
			
		// make a folder at the right and add views to it
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.3f, editorArea);
		right.addView(CLASS_MODEL_VIEW_ID);
		right.addView(TRANSLATION_SUMMARY_VIEW_ID);
		right.addView(DEBUG_INSTANCE_VIEW_ID);
		
		
	}

}
