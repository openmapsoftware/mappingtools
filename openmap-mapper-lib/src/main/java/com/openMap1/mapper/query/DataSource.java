package com.openMap1.mapper.query;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.resources.IProject;


import com.openMap1.mapper.reader.ReaderFactory;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.userConverters.DBConnect;
import com.openMap1.mapper.util.SystemMessageChannel;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.converters.CSV_Wrapper;
import com.openMap1.mapper.converters.V2Converter;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.Namespace;
import com.openMap1.mapper.StructureType;

import org.eclipse.core.resources.IFile;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;

import java.awt.Color;

import org.w3c.dom.Element;

/**
 * The data source for a query or translation test consists
 * of a mapping set, an instance mapped by the mapping set (or a relational database), and 
 * the class model to which it is mapped.
 * 
 * Each data source is a row of the Data Source View, and may be active or not.
 * 
 * @author robert
 *
 */

public class DataSource  implements MatchSource{

    private XOReader reader; // XML reader using MDL
    public XOReader getReader() throws MapperException
    {
    	if (reader == null) throw new MapperException("Data source has no reader defined");
    	return reader;
    }

    private Element rootNode; // root node of the DOM
    public Element getRootNode() {return rootNode;}
    public void setRootNode(Element el) {rootNode = el;}
    
    private URI mappingSetURI;
    public String mappingSetURIString() {return mappingSetURI.toString();}
    
    /**
     * @return true if the data source is currently active, i.e has
     * been checked in the data source view , and so may be used in queries and translations
     * BEWARE - not immediately updated when the box is checked - only later on
     * getActiveDataSources - i.e the flag here is not much use yet.
     */
    public boolean isActive() {return isActive;}
    private boolean isActive;
    /**
     * tell the data source if it is active or not
     * Note that this is not called immediately on a checking the box event ,as it should be
     * @param act
     */
    public void setIsActive(boolean act) {isActive = act;}
    
    /**
     * @return true if the data source is selected in the data source view
     */
    public boolean isSelected() {return isSelected;}
    private boolean isSelected = false;

    /**
     * tell the data source if it is selected or not
     * @param selected
     */
    public void setIsSelected(boolean selected) {isSelected = selected;}

    public String mappingSetName() 
    {
    	String sn = "";
    	StringTokenizer st = new StringTokenizer(mappingSetURIString(),"/\"");
    	while (st.hasMoreTokens()) sn = st.nextToken();
    	return sn;
    }

    /**
     * @return the mapping set file name without extension
     */
    public String mappingSetNameRoot() 
    {
    	StringTokenizer st = new StringTokenizer(mappingSetName(),".");
    	return st.nextToken();
    }

    private String sourceType = null;
    public String sourceType() {return sourceType;}
    

    public String instanceFileName() 
    {
    	String sn = "";
    	StringTokenizer st = new StringTokenizer(instanceURIString,"/\"");
    	while (st.hasMoreTokens()) sn = st.nextToken();
    	return sn;
    }

    /** codes for data sources are now allocated in
     * sequence 'A', 'B' etc, depending only on the position in the data source view. */
    public String getCode() {return shortCode;}
    public void setCode(String name) {shortCode = name;}
    private String shortCode = "A";
    
    /**
     * @return short name of the data source, for us in the matching web service
     */
    public String getShortName() {return shortName;}
    private String shortName = "";
    public void setShortName(String shortName) {this.shortName = shortName;}

    private String instanceURIString = "";
    public String instanceURIString()
    {
    	return instanceURIString;
    }
    
    /**
     * @return the IFile containing the instance; will need
     * to be extended for relational sources with a query defined
     */
    public IFile getInstanceFile() {return EclipseFileUtil.getFile(instanceURIString);}

    private String classModelURIString = "";
    public String classModelURIString() {return classModelURIString;}
    
    /**
     * @return the project containing the class model of this data source
     */
    public IProject getProject() {return FileUtil.getProject(classModelURIString);}


    public String classModelName() 
    {
    	String sn = "";
    	StringTokenizer st = new StringTokenizer(classModelURIString,"/\"");
    	while (st.hasMoreTokens()) sn = st.nextToken();
    	return sn;
    }
    
    /**
     * 
     * @return the file extension ('.xml' or '.txt') for the file type of this
     * data source outside any wrapper transformations
     * @throws MapperException
     */
    public String getExtension() throws MapperException
    {
		return (getReader().ms().getExtensions()[0]).substring(1);    	
    }
    
    private MappedStructure mappedStructure;
    
    /**
     * @return the mapped structure created when the data source was created,
     * or at the last call of refresh()
     * @throws MapperException
     */
    public MappedStructure getMappedStructure() throws MapperException
    {
    	if (mappedStructure != null) return mappedStructure;
    	return getFreshMappedStructure();
    }
    
    /**
     * 
     * @return the MappedStructure of this data source, refreshed in case the mappings have 
     * been changed since the data source was created
     */
    public MappedStructure getFreshMappedStructure() throws MapperException
    {
    	String location = FileUtil.absoluteLocation(mappingSetURIString());
    	try {
    		mappedStructure =  FileUtil.getMappedStructure(location);   		
    	}
    	catch (Exception ex) {throw new MapperException("Cannot open mapping set: " + ex.getMessage());}
    	return mappedStructure;
    }
    
    // for relational data sources
    
    public Connection con() {return con;}
    private Connection con = null;
    public void setConnection(Connection con) {this.con = con;}
    
    public void connect(String userName, String password) throws MapperException
    {
    	if (!isRelational()) throw new MapperException("Data Source " + getCode() + " is not Relational");
    	DBConnect connector = new DBConnect(instanceURIString,userName,password,null);
    	try
    	{
        	connector.connect();
        	con = connector.con();    		
    	}
    	catch (Exception ex) 
    		{throw new MapperException("Failed to open relational database for data source " + getCode() + ": " + ex.getMessage());}
    }
    
    public boolean isConnected() {return (con != null);}
    
    /**
     * @return true if this is a relational data source
     */
    public boolean isRelational()
    {
    	boolean relational = false;
    	try {relational = getMappedStructure().getStructureType() == StructureType.RDBMS;}
    	catch (Exception ex) {relational = false;}
    	return relational;
    }
    
    private String sqlText = "";
    public String getSqlText() {return sqlText;}
    public void setSQLText(String text) {sqlText = text;}
    
    /**
     * @return true if this data source uses the FHIR mapping class
     */
    public boolean isFHIRSource() 
    {
    	boolean isFHIR = false;
    	try
    	{
        	if ((getMappedStructure() != null) 
        			&& (getMappedStructure().getMappingParameters() != null)
        			&& ("com.openMap1.mapper.fhir.FHIRMapper".equals(getMappedStructure().getMappingParameters().getMappingClass()))) 
        			isFHIR = true;
    		
    	}
    	catch (Exception ex) {}    	
    	return isFHIR;
    }
    
    /**
     * @return true if this data source connects to a FHIR server
     */
    public boolean isNetworkedFHIRSource()
    {
    	return ((isFHIRSource()) && (instanceURIString != null) && (instanceURIString.startsWith("http")));
    }
    
    /**
     * @return the FHIR query which this data source evaluates against its FHIR server
     */
    public String fhirSearch() {return fhirSearch;}
    private String fhirSearch = null;
    
    public void setFhirSearch(String search) {fhirSearch = search;}
    
    
    //-----------------------------------------------------------------------------------------
    //                              constructors and refresh
    //-----------------------------------------------------------------------------------------

    /**
     * Constructor when making this data source for the first time - various checks have been
     * made in DataSourceView
     * @param reader
     * @param mappingSetURI
     * @param rootNode
     * @param sourceType
     * @param instanceURI
     */
    public DataSource(XOReader reader, URI mappingSetURI, Element rootNode, String sourceType, String instanceURIString)
    {
        this.reader = reader;
        this.mappingSetURI = mappingSetURI;
        this.rootNode = rootNode;
        this.sourceType = sourceType;
        this.instanceURIString = instanceURIString;
        classModelURIString = reader.ms().getUMLModelURL();
    }
    
    /**
     * constructor when reconstructing a Data Source Set from an inter-session memento or
     * a saved view file
     * @param mappingSetURI
     * @param sourceType
     * @param instanceURI
     */
    public DataSource(URI mappingSetURI, String sourceType, String instanceURIString) throws MapperException
    {
        this.mappingSetURI = mappingSetURI;
        this.instanceURIString = instanceURIString;
        this.sourceType = sourceType;
        /* try to refresh the data source so as to find the class model uri at least;  but if 
         * you cannot find the xml instance or connect to the database, leave that to be sovled when
         * the data source is used in a query, match or translation */
        try{refresh();}
        catch (Exception ex) {}
    }
    
    /**
     * constructor from an XML element in a match file.
     * @param sourceEl
     * @throws MapperException
     */
    public DataSource(Element sourceEl, String rootPath, boolean inEclipse) throws MapperException
    {
        sourceType = sourceEl.getAttribute("sourceType");
        setCode(sourceEl.getAttribute("code"));
        setShortName(sourceEl.getAttribute("shortName"));
    	if (inEclipse)
    	{
            mappingSetURI = URI.createURI(sourceEl.getAttribute("mappingSet"));
            instanceURIString = sourceEl.getAttribute("instance");

            /* try to refresh the data source so as to find the class model uri at least;  but if 
             * you cannot find the xml instance or connect to the database, leave that to be solved when
             * the data source is used in a query, match or translation */
            try{refresh();}
            catch (Exception ex) {}
    	}
    	else
    	{
    		String mappingPath = FileUtil.absoluteLocation(rootPath, sourceEl.getAttribute("mappingSet"));
    		String instancePath = FileUtil.absoluteLocation(rootPath, sourceEl.getAttribute("instance"));
    		String classModelPath = FileUtil.absoluteLocation(rootPath, sourceEl.getAttribute("classModel"));
			reader = ReaderFactory.makeReader(mappingPath, classModelPath, instancePath);
    	}
    }
    
    /**
     * constructs a very thin data source, which only contains query results,
     * consisting of only one record extracted from another data source of query results
     * @param fullDataSource
     * @param recNo
     * @throws MapperException
     */
    public DataSource(MatchSource fullDataSource, int recNo)  throws MapperException
    {
    	if ((recNo < 0)|(recNo > fullDataSource.resultSize()))  
    		throw new MapperException("Record number " + recNo + " is out of range when creating a one-record dsata source for matching");
    	
    	// same code as its parent data source
    	setCode(fullDataSource.getCode());

    	// result vector is initially empty; add one row to it
    	result.add(fullDataSource.getRow(recNo));
    	alreadyMatched.add(new Boolean(false));
    	
    	// set it to have the same column headers
    	setColumnHeaders(fullDataSource.getColumnHeaders());
    	
    }
    
    
    /**
     * refresh this data source for a new query or translation test,
     * in case the mappings or the XML instance have been altered since 
     * the data source was made
     */
    public void refresh() throws MapperException
    {
    	// refresh the mapped structure, in case the mappings have been changed
		mappedStructure = getFreshMappedStructure();
		classModelURIString = mappedStructure.getUMLModelURL();
		
		if (isNetworkedFHIRSource())
		{
			if (fhirSearch == null) throw new MapperException("No FHIR search defined for data source " + getCode());
			// get the FHIR XML from the search, or throw an exception
			FHIRClient client = new FHIRClient(instanceURIString());
			rootNode = client.getFHIRResponse(fhirSearch);
		}

		// refresh the root node of an XML Instance source, in case it has changed
		else if ((mappedStructure.getStructureType() == StructureType.XSD)|
				(mappedStructure.getStructureType() == StructureType.V2)) try
		{
			rootNode = mappedStructure.getXMLRoot(instanceURIString);
			if (rootNode == null) throw new MapperException("Cannot open XML instance at " + instanceURIString);
		}
		catch(Exception ex)  {ex.printStackTrace(); throw new MapperException("Terminated; unknown fault opening XML source");}

		// there is no refresh for a relational data source; connecting to the source is done outside this class

		reader = mappedStructure.getXOReader(rootNode, null, new SystemMessageChannel());
    }
    
    /**
     * used to step through a large csv data source (called in QueryExecutor)
     * @throws MapperException
     */
    public void renewDOM() throws MapperException
    {
    	/* each time this is called, it gets the same mapped structure instance, 
    	 * with the same csv wrapper class instance, and uses the csv wrapper class
    	 * to step through a limited number of lines in the csv file, and make them into a DOM. */
		rootNode = getMappedStructure().getXMLRoot(instanceURIString);
		if (rootNode == null) throw new MapperException("Cannot open XML instance at " + instanceURIString); 
		reader.setRoot(rootNode);
    }
    
    /**
     * make a specific test database behave as if it needs a specific user name and password;
     * (which it does not actually, being mySQL seen through odbc).
     * For that database url, throw an exception if the user name and password are not correct
     * @param dbURL the test database 
     * @param userName
     * @param password
     * @throws MapperException
     */
    private void testPassword(String dbURL, String userName, String password) throws MapperException
    {
    	String TEST_URL="jdbc:odbc:patientDB";
    	String TEST_USERNAME="root";
    	String TEST_PASSWORD="kfr746";
    	
    	// message("Testing credentials for database '" + dbURL + "'");
    	
    	if (TEST_URL.equals(dbURL))
    	{
    		if (!TEST_USERNAME.equals(userName)) throw new MapperException("Invalid test user name '" + userName + "'");
    		if (!TEST_PASSWORD.equals(password)) throw new MapperException("Invalid test password '" + password + "'");
    	}
    }
    
    
    //-----------------------------------------------------------------------------------------
    //                          query-related and match-related functionality
    //-----------------------------------------------------------------------------------------

    // results from the queryExecutor; used by the matcher
    private Vector<Vector<CellContent>> result = new Vector<Vector<CellContent>>();
    public Vector<Vector<CellContent>> result() {return result;}
    public int resultSize() {return result.size();}
    
    public Vector<CellContent> getRow(int row) {return (result.get(row));}
    
    private Vector<Integer> resultCount = null;
    
    // records matched in previous matches. index as for result vector
    private Vector<Boolean> alreadyMatched = new Vector<Boolean>();
    public Vector<Boolean> alreadyMatched() {return alreadyMatched;}
    public void setMatched(int i, boolean isMatched) 
    {
    		alreadyMatched.remove(i);
    		alreadyMatched.insertElementAt(new Boolean(isMatched), i);
    }
    public boolean isMatched(int i) {return alreadyMatched.get(i).booleanValue();}
    
    public void setAllUnMatched()
    {
    	alreadyMatched = new Vector<Boolean>();
    	for (int i = 0; i < result().size(); i++) alreadyMatched.add(new Boolean(false));
    }

    /*  Each 'row' is a Vector of cellContents, in the right order for columns.
    The key is a concatenated string of upper case cell contents, for fast matching
    with results from other sources. */
    private Hashtable<String, Vector<CellContent>> keyedResults = new Hashtable<String, Vector<CellContent>>();
    public Hashtable<String, Vector<CellContent>> keyedResults() {return keyedResults;}
    
    private Hashtable<String,Integer> keyedResultCounts = new Hashtable<String,Integer>();
    public Integer getResultCount(String key) {return keyedResultCounts.get(key);}

    /* count the rows in the column sorted results,
     before they are reduced by collating with other sources. */
    private int originalRowCount = 0;
    public void setOriginalRowCount() {originalRowCount = keyedResults.size();}
    public int getOriginalRowCount() {return originalRowCount;}

    private boolean hasResult = false;
    public boolean hasResult() {return hasResult;}

    
    private Vector<String[]> columnHeaders;
    public Vector<String[]> getColumnHeaders() {return columnHeaders;}
    
    public void setColumnHeaders(Vector<String[]> columnHeaders) {this.columnHeaders = columnHeaders;};

    // re-initialise the raw results and the column sorted  results
    public void unsetResult()
    {
        result = new Vector<Vector<CellContent>>();
        resultCount = null;
        alreadyMatched = new Vector<Boolean>();
        keyedResults = new Hashtable<String, Vector<CellContent>>();
        hasResult = false;
    }

    // set up the displayable results
    public void setResult(Vector<Vector<String[]>> res, Vector<Integer> resCount)
    {
        result = new Vector<Vector<CellContent>>();
        for (int i = 0; i < res.size(); i++) result.add(makeDisplayRow(res.get(i)));
        resultCount = null;
        if (resCount != null) resultCount = resCount;
        // when a result first comes from a query, none of the records have been matched
        for (int i = 0; i < res.size(); i++) alreadyMatched.add(new Boolean(false));
        setKeyedResults();
        hasResult = true;
    }
    
    /**
     * @return number of rows in this data source that have not been matched.
     */
    public int countUnMatched()
    {
    	int unMatched = 0;
    	for (int i = 0; i < alreadyMatched.size(); i++) if (!alreadyMatched.get(i).booleanValue()) unMatched++;
    	return unMatched;
    }

    /*  From a Vector of results, where each row is a Vector of String
    arrays in correct column order, produce a Hashtable of results,
    where each row is a Vector of cellContents.
    The key is a concatenated string of upper case cell contents. */
    private void setKeyedResults()
    {
        keyedResults = new Hashtable<String, Vector<CellContent>>();
        keyedResultCounts = new Hashtable<String,Integer>();
        for (int i = 0; i < result.size(); i++)
        {
            Vector<CellContent> displayRow = result.get(i);
            String key = rowKey(displayRow);
            keyedResults.put(key,displayRow);
            if (resultCount != null) keyedResultCounts.put(key, resultCount.get(i));
        }
    }
    
    /**
     * 
     * @param row row returned from the QueryExecutor
     * @return displayable row of CellContent objects.
     */
    public Vector<CellContent> makeDisplayRow(Vector<String[]> row)
    {
        Vector<CellContent> displayRow = new Vector<CellContent>();
        for (int j = 0; j < row.size(); j++)
            displayRow.add(new CellContent(row.get(j)[2],Color.black));
     	return displayRow;
    }
    
    /**
     * 
     * @return column headers - currently unqualified class name, followed by property name;
     * which is unsatisfactory for small data type classes
     */
    public Vector<CellContent> getHeaderRow()
    {
        Vector<CellContent> colSortedRow = new Vector<CellContent>();
        for (int j = 0; j < columnHeaders.size(); j++)
        {
            String[] f = columnHeaders.get(j);
            String header = f[0] + "." + f[1];
            colSortedRow.addElement(new CellContent(header,Color.black));
        }
    	return colSortedRow;    	
    }

    /* a string key for each row, to match on all columns in the row
    Key is converted to upper case, so case is ignored in row matching. */
    private String rowKey(Vector<CellContent> row)
    {
        String key = "";
        for (int i = 0; i < row.size(); i++)
        {
            CellContent cc = row.elementAt(i);
            key = key + "£$" + cc.getText().toUpperCase();
        }
        return key;
    }
    
    //-----------------------------------------------------------------------------------------
    //                 used for segmenting very large RDBMS and CSV queries and matches
    //-----------------------------------------------------------------------------------------
    
    
    
    public boolean isCSVSource()
    {
    	boolean isCSV = false;
    	try
    	{
    		MapperWrapper wrap = getMappedStructure().getWrapper();
    		isCSV = ((wrap != null) && (wrap instanceof CSV_Wrapper));
    	}
    	catch (Exception ex) {}
    	return isCSV;
    }
    
    //-----------------------------------------------------------------------------------------
    //                          odds and ends
    //-----------------------------------------------------------------------------------------
    
    /**
     * @return true if this is a data source for a V2.XML message
     */
    public boolean isV2DataSource()
    {
    	boolean isV2 = false;
    	if (reader.ms().getMappingParameters() != null)
    		for(Iterator<Namespace> it = reader.ms().getMappingParameters().getNameSpaces().iterator();it.hasNext();)
    			if (it.next().getURL().equals(V2Converter.V2_NAMESPACE_URI)) isV2 = true;
    	return isV2;
    }
    

}
