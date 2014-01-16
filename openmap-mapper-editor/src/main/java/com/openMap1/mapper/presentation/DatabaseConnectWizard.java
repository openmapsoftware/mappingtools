package com.openMap1.mapper.presentation;

import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.openMap1.mapper.userConverters.DBConnect;



/**
 * This wizard gets three Strings from the user: a jdbc connect string, 
 * and possibly a user name and password.
 */
public class DatabaseConnectWizard extends Wizard implements INewWizard {

	/**
	 * This is the page to get (jdbc string, user name , password, schema name)
	 */
	protected DatabaseConnectionInformationPage dbConnectInfoPage;
	public DatabaseConnectionInformationPage getDbConnectInfoPage() 
		{return dbConnectInfoPage;}

	protected IWorkbench workbench;

	protected IStructuredSelection selection;
	
	private DBConnect dbConnect;
	public DBConnect dbConnect() {return dbConnect;}
	
	private boolean jdbcStringIsFixed = false;
	private String prefixedjdbcString;
	
	/**
	 * can be called after the widgets are disposed
	 * @return the user name
	 */
	public String getUserName() {return userName;}
	private String userName = "";
	
	/**
	 * can be called after the widgets are disposed
	 * @return the password
	 */
	public String getPassword() {return password;}
	private String password ="";
	
	public String getSchemaName() {return schemaName;}
	private String schemaName;
	
	public boolean finishedSuccessfully() {return finishedSuccessfully;}
	private boolean finishedSuccessfully = false;


	/**
	 * This just records the information.
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		setWindowTitle("Database Connection");
		setDefaultPageImageDescriptor(ExtendedImageRegistry.INSTANCE.getImageDescriptor(MapperEditorPlugin.INSTANCE.getImage("full/wizban/NewMapper")));
	}
	
	
	/**
	 * to set the jdbc connect string to a value that cannot be altered
	 * @param jdbcString
	 */
	public void fixJDBCString (String jdbcString)
	{
		jdbcStringIsFixed = true;
		prefixedjdbcString = jdbcString;
	}

	/**
	 * When a connect string has been entered, try to connect to it.
	 * If it fails, tell the user why in the page title, and let him retry
	 */
	public boolean performFinish() {
		try{
			userName = localGetUserName();
			password = localGetPassword();
			schemaName = localGetSchemaName();

			String schema = null;
			if ((schemaName != null) && (schemaName.length() > 0))	 schema = schemaName;		
			dbConnect = new DBConnect(localGetJDBCString(),localGetUserName(),localGetPassword(),schema);
			finishedSuccessfully = dbConnect.connect();
			return finishedSuccessfully;
		}
		catch (Exception ex) 
		{
			dbConnectInfoPage.setDescription(ex.getMessage());
			return false;			
		}		
	}

	/**
	 * This is the page where the strings are requested.
	 */
	public class DatabaseConnectionInformationPage extends WizardPage {
		protected Text jdbcStringField;
		protected Text userNameField;
		protected Text passwordField;
		protected Text schemaNameField;
		protected Combo dbmsChoice;

		public DatabaseConnectionInformationPage(String pageId) {
			super(pageId);
		}

		/**
		 */
		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE); {
				GridLayout layout = new GridLayout();
				layout.numColumns = 1;
				layout.verticalSpacing = 12;
				composite.setLayout(layout);

				GridData data = new GridData();
				data.verticalAlignment = GridData.FILL;
				data.grabExcessVerticalSpace = true;
				data.horizontalAlignment = GridData.FILL;
				composite.setLayoutData(data);
			}

			Label dbmsTypeLabel = new Label(composite, SWT.LEFT);
			{
				dbmsTypeLabel.setText("DBMS Type");

				GridData data = new GridData();
				data.horizontalAlignment = GridData.FILL;
				dbmsTypeLabel.setLayoutData(data);
			}

			dbmsChoice = new Combo(composite, SWT.BORDER);
			{
				GridData data = new GridData();
				data.horizontalAlignment = GridData.FILL;
				data.grabExcessHorizontalSpace = true;
				dbmsChoice.setLayoutData(data);
			}
			
			setDBMSChoices(dbmsChoice);
			if (dbmsChoice.getItemCount() == 1) {
				dbmsChoice.select(0);
			}
			dbmsChoice.addModifyListener(fillInPrefix);

			Label jdbcStringLabel = new Label(composite, SWT.LEFT);
			{
				jdbcStringLabel.setText("Connection String");

				GridData data = new GridData();
				data.horizontalAlignment = GridData.FILL;
				jdbcStringLabel.setLayoutData(data);
			}

			jdbcStringField = new Text(composite, SWT.BORDER);
			{
				GridData data = new GridData();
				data.horizontalAlignment = GridData.FILL;
				data.grabExcessHorizontalSpace = true;
				jdbcStringField.setLayoutData(data);
				
				// where the dialog is only looking for a user name, password and maybe schema
				if (jdbcStringIsFixed)
				{
					jdbcStringField.setText(prefixedjdbcString);
					jdbcStringField.setEditable(false);
				}
			}

			jdbcStringField.addModifyListener(validator);

			Label userNameLabel = new Label(composite, SWT.LEFT);
			{
				userNameLabel.setText("User Name");

				GridData data = new GridData();
				data.horizontalAlignment = GridData.FILL;
				userNameLabel.setLayoutData(data);
			}
			userNameField = new Text(composite, SWT.BORDER);
			{
				GridData data = new GridData();
				data.horizontalAlignment = GridData.FILL;
				data.grabExcessHorizontalSpace = true;
				userNameField.setLayoutData(data);
			}

			Label passwordLabel = new Label(composite, SWT.LEFT);
			{
				passwordLabel.setText("Password");

				GridData data = new GridData();
				data.horizontalAlignment = GridData.FILL;
				passwordLabel.setLayoutData(data);
			}
			passwordField = new Text(composite, SWT.PASSWORD);
			{
				GridData data = new GridData();
				data.horizontalAlignment = GridData.FILL;
				data.grabExcessHorizontalSpace = true;
				passwordField.setLayoutData(data);
			}

			Label schemaNameLabel = new Label(composite, SWT.LEFT);
			{
				schemaNameLabel.setText("Schema name");

				GridData data = new GridData();
				data.horizontalAlignment = GridData.FILL;
				schemaNameLabel.setLayoutData(data);
			}
			
			schemaNameField = new Text(composite, SWT.LEFT);
			{
				GridData data = new GridData();
				data.horizontalAlignment = GridData.FILL;
				data.grabExcessHorizontalSpace = true;
				schemaNameField.setLayoutData(data);
			}

			setPageComplete(validatePage());
			setControl(composite);
		}

		/** when a DBMS has been selected in the DBMS choice Combo,
		 * set up the correct prefix for the jdbc connect string
		 */
		protected ModifyListener fillInPrefix =
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					jdbcStringField.setText(jdbcPrefix(dbmsChoice.getText()));
				}
			};

			
		/**
		 */
		protected ModifyListener validator =
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setPageComplete(validatePage());
				}
			};

		/** The page is valid as soon as something has been added to the jdbc prefix
		 *  for the chosen DBMS
		 */
		protected boolean validatePage() {
			boolean OK = ((getJDBCString() != null) && 
					(getJDBCString().length() > jdbcPrefix(dbmsChoice.getText()).length()));
			return OK;
		}

		/**
		 */
		public void setVisible(boolean visible) {
			super.setVisible(visible);
			if (visible) {
			}
		}

		/**
		 */
		public String getJDBCString() {
			return jdbcStringField.getText();
		}

		/**
		 */
		String getUserName() {
			return userNameField.getText();
		}
		
		String getPassword(){
			return passwordField.getText();			
		}
		
		String getSchemaName(){
			return schemaNameField.getText();			
		}
		
		private void setDBMSChoices(Combo dbmsChoice)
		{
			for (int i = 0; i < DBConnect.jdbcParam().length;i++)
			{
				String[] jdbcVals = DBConnect.jdbcParam()[i];
				try
				{
					// check the driver class is available
					Class.forName(jdbcVals[1]);
					// if so, add the DBMS name as a choice
					dbmsChoice.add(jdbcVals[2]);
				}
				/* If you cannot find the driver class to provide a jdbc API for this DBMS, 
				 * fail silently and do not add the DBMS to the list of choices. */
				catch (Exception ex) { /* System.out.println("Cannot load driver class " + jdbcVals[1]); */}
			}
		}
		
		/**
		 * @param dbmsName name of a DBMS, chosen by the user
		 * @return the correct prefix to the jdbc connect string for the chosen DBMS
		 */
		private String jdbcPrefix(String dbmsName)
		{
			String prefix = "";
			for (int i = 0; i < DBConnect.jdbcParam().length;i++)
			{
				String[] jdbcVals = DBConnect.jdbcParam()[i];
				if (jdbcVals[2].equals(dbmsName)) prefix = jdbcVals[0];
			}
			return prefix;
		}


	}

	/**
	 * The framework calls this to create the contents of the wizard.
	 */
		@Override
	public void addPages() {
		dbConnectInfoPage = new DatabaseConnectionInformationPage("Whatever2");
		dbConnectInfoPage.setTitle("Connect to a DBMS");
		dbConnectInfoPage.setDescription("Provide a jdbc connection string for the chosen DBMS");
		addPage(dbConnectInfoPage);
	}
		
		// these methods are private because they cannot be called after the widgets are disposed
		
		private String localGetJDBCString() 
		{
			if (dbConnectInfoPage == null) return null;
			return dbConnectInfoPage.getJDBCString();
		}
		
		private String localGetUserName() 
		{
			if (dbConnectInfoPage == null) return null;
			return dbConnectInfoPage.getUserName();
		}
		
		private String localGetPassword() 
		{
			if (dbConnectInfoPage == null) return null;
			return dbConnectInfoPage.getPassword();
		}
		
		private String localGetSchemaName() 
		{
			if (dbConnectInfoPage == null) return null;
			return dbConnectInfoPage.getSchemaName();
		}


}
