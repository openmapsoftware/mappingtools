package com.openMap1.mapper.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LicenceDialog extends Dialog{
	
	private String licenceText;
	private String licenceTitle;
	
	private static String[] XSLT_LICENCE_PARAS = {
			"1.	Licence",
			"The XSLT generation software and documentation is the property of Open Mapping Software ltd. (OMS). Upon signature of this agreement and payment of the agreed fee, you have certain rights to use the software during the Licence Period. The Licence Period shall begin on the date of your initial installation of a copy of the software and shall last until one year from the date of the initial installation. ",
			"This licence governs any modifications to the software that are provided by OMS.",
			"During the Licence Period you may:",
			"-	Use one copy of the software on a single computer,",
			"-	Make one copy a the software for back up or archival purposes",
			"-	Use the software on a network provided you have a licenced copy of the software for each computer that can access the software via the network",
			"",
			"During the Licence Period you are not allowed to:",
			"-	Transfer the licence to another computer without the express permission of OMS, which will not be unreasonably withheld",
			"-	Attempt to discover the source code of the software",
			"-	Use the software in any way not permitted by this Licence Agreement",
			"",
			"2.	Software Piracy",
			"You agree that OMS may incorporate measures to prevent unlicensed or illegal use of the software.",
			"",
			"3.	Intellectual Property Rights",
			"OMS shall retain all Intellectual Property Rights relating to documents and software provided by OMS. ",
			"",
			"4.	Limitation of Liability",
			"Subject always to the exclusions below, the liability of OMS to the Client, whether in contract, negligence, other tort, by way of indemnity or otherwise arising out of or in connection with this Agreement shall be subject to the financial limits set out in this Clause  as follows:",
			"the liability for all defaults resulting direct loss of or damage to the tangible property of the Client shall in no event exceed the value of the licence per event or series of connected events;",
			"the aggregate liability for all other defaults arising (but excluding any liability governed by the previous paragraph) in relation to the  software shall in no event exceed 125% of the total amount paid by the Client under this Agreement.",
			"Notwithstanding the foregoing: OMS does not exclude or restrict liability for:",
			"death or personal injury caused by its negligence or that of its employees;",
			"fraud or fraudulent misrepresentation; or",
			"breach of obligations as to the transfer of title of any goods where title is intended to pass; and",
			"OMS shall not be liable to the Client for:",
			"any loss of data, profits, revenue, contracts or anticipated savings; or",
			"any consequential or indirect loss or damage however caused.",
			"",
			"5.	Warranty",
			"OMS does not warrant that the software will meet your requirements or that it will be error free. However within the Licence Period OMS will remedy any errors at its own expense. Any requests for modifications shall be subject to a separate agreement between the parties.",
			"The above warranty is exclusive and in lieu of all other warranties, either express or implied, including the implied warranties of satisfactory quality and fitness for a particular purpose. OMS does not give any warranty in relation to non-infringement of Intellectual Property Rights.",
			"",
			"6.	Law and Jurisdiction",
			"This Agreement is subject to English law. If any part of these Conditions are found to be unenforceable by a court, the rest are unaffected.",
			"This Agreement constitutes the entire Agreement between the parties.",
	};
	
	private static String MATCHER_LICENCE = "In return for use of the Fast Matching plugin, I promise to pay Open Mapping Software on demand loads of money and never to ....";
	
	public static String DECLINED = "declined";
	
	private static byte[] newLine = {13,10};
	
	private static String validKey = "XA1PG67Q32K5";

	
	public static String XSLT_licence_text() 
	{
		String licence = "";
		String nl = new String(newLine);
		for (int i = 0; i < XSLT_LICENCE_PARAS.length; i++)
		{
			licence = licence + XSLT_LICENCE_PARAS[i] + nl;
		}
		return licence;
	}
	
	public static String matcher_licence_text() {return MATCHER_LICENCE;}
	
	private FormData formData;
	
	private String result = DECLINED;

	public LicenceDialog(Shell parent, String licenceTitle,  String licenceText) {
		super(parent, SWT.PRIMARY_MODAL);
		this.licenceText = licenceText;
		this.licenceTitle = licenceTitle;
	}

	public Object open () 
	{
		Shell parent = getParent();
        final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText(licenceTitle);
        shell.setBounds(100, 100, 500, 240);
        shell.setLayout(new FormLayout());
        
        // text area for entering licence key, top right
        final Text licenceKeyValue = new Text(shell,SWT.COLOR_WHITE|SWT.BORDER);
        formData = new FormData();
        formData.right = new FormAttachment(100,-5);
        formData.left = new FormAttachment(60,0); // 40% of the dialog box width for the key
        formData.top = new FormAttachment(0,5);
        licenceKeyValue.setLayoutData(formData);

        // Label for entering the licence key, at the top and to the left of the text area
        Label enterKeyLabel = new Label(shell,0);
        enterKeyLabel.setText("Enter licence key: ");
        formData = new FormData();
        formData.right = new FormAttachment(licenceKeyValue,-10);
        formData.top = new FormAttachment(0,5);
        enterKeyLabel.setLayoutData(formData);
 
        // Label for licence text, below the label for entering the key
        Label licenceTextLabel = new Label(shell,0);
        licenceTextLabel.setText("Licence Agreement");
        formData = new FormData();
        formData.left = new FormAttachment(0,5);
        formData.top = new FormAttachment(enterKeyLabel,5);
        licenceTextLabel.setLayoutData(formData);

        // Decline button, placed bottom right
        Button declineButton = new Button(shell, SWT.PUSH);
        declineButton.setText("Decline");
        formData = new FormData();
        formData.right = new FormAttachment(100,-10);
        formData.bottom = new FormAttachment(100,-5);
        declineButton.setLayoutData(formData);
        
        // if the user declines the licence terms, set the returned key to "declined" and close
        declineButton.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent event) {
        		result = DECLINED;
        		shell.dispose();
        	}
        });

        // Accept button, placed to the left of the decline button
        Button acceptButton = new Button(shell, SWT.PUSH);
        acceptButton.setText("Accept");
        formData = new FormData();
        formData.right = new FormAttachment(declineButton,-10);
        formData.bottom = new FormAttachment(100,-5);
        acceptButton.setLayoutData(formData);

        // if the user accepts the licence terms, close and return the licence key he entered
        acceptButton.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent event) {
        		result = licenceKeyValue.getText();
        		shell.dispose();
        	}
        });

        // agreement statement, to the left of the Accept button
        Label haveReadLabel = new Label(shell,SWT.TOP);
        haveReadLabel.setText("I have read the licence agreement, and agree to its terms: ");
        formData = new FormData();
        formData.right = new FormAttachment(acceptButton,-10);
        formData.bottom = new FormAttachment(100,-10);
        haveReadLabel.setLayoutData(formData);
                
        // Licence text, placed above the two buttons and below the licence text label
        // SWT.COLOR_WHITE has no effect. 
        Text text = new Text(shell, SWT.MULTI|SWT.COLOR_WHITE|SWT.BORDER|SWT.READ_ONLY|SWT.WRAP|SWT.V_SCROLL);
        Color white = new Color(null, 255, 255, 255);
        text.setBackground(white);
        text.setText(licenceText);
         //text.s
        formData = new FormData();
        formData.top = new FormAttachment(licenceTextLabel,5);
        formData.bottom = new FormAttachment(declineButton,-5);
        formData.left = new FormAttachment(0,5);
        formData.right = new FormAttachment(100,-5);
        text.setLayoutData(formData);
 
        shell.open();
        Display display = parent.getDisplay();
        while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) display.sleep();
        }
        
        // return the licence key the user entered, unless he declined (in which case, return 'declined')
        return result;
	}
	
	/**
	 * 
	 * @param xslKey
	 * @return true if it is a valid licence key
	 */
	public static boolean isValidXSLKey(String xslKey)
	{
		if (xslKey == null) return false;
		if (xslKey.equals(DECLINED)) return false;
		if (xslKey.equals("")) return false;
		if (xslKey.equals(validKey))return true;
		return false;
	}
}


