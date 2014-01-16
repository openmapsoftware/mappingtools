package com.openMap1.mapper.query;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.DBStructure;
import com.openMap1.mapper.util.GenUtil;



import java.util.*;

/**
 * Class which parses query text. 
 * Abstract superclass of the one implementing class:
 *  - QueryParserImpl_Ecore, which requires only an Ecore model
 * 
 * @author robert
 *
 */

public abstract class QueryParserImpl_Super implements QueryParser
{
	
	/**
	 * @return list of all query classes
	 */
	public Vector<QueryClass> queryClasses() {return queryClasses;}
	protected Vector<QueryClass> queryClasses = new Vector<QueryClass>();
		
	/**
	 * @return vector of  WriteFields to be written out
	 */
	public Vector<WriteField> writeFields() {return writeFields;}
    protected Vector<WriteField> writeFields = new Vector<WriteField>(); 
    

    /**
     * vector of conditions on properties
     */
    public Vector<QueryCondition> conditions() {return conditions;}
    protected Vector<QueryCondition> conditions = new Vector<QueryCondition>();

    
    /**
     * @return hashtable of link associations, 
     * keyed by association name
     */
    public Hashtable<String, LinkAssociation> linkAssociations() {return linkAssociations;}
    protected Hashtable<String, LinkAssociation> linkAssociations = new Hashtable<String, LinkAssociation>();


    public String queryText;
    
    protected Vector<String[]> errors;
    
    protected String code;
    
    protected boolean tracing;
    
    protected boolean isMappedReader = false;

    
//------------------------------------------------------------------------------------
//                             Constructor
//------------------------------------------------------------------------------------
    
    public QueryParserImpl_Super(Vector<String[]> errors, boolean tracing) 
    {
    	this.errors = errors;
    	this.tracing = tracing;    	
    }


//------------------------------------------------------------------------------------
//                             Access methods for results  - in subclass
//------------------------------------------------------------------------------------





//-----------------------------------------------------------------------------------------
//                   Main methods for parsing query text
//-----------------------------------------------------------------------------------------


    /** parse query text and return true if it is OK. */
    public boolean parse(String query) 
    {
        boolean res = false;
        trace("Parsing '" + query + "'");
        
        // add spaces  around '=', '<', and '>' if needed
        String spacedQuery = addSpaces(query);

        // strip the query text into 'words', separated by spaces commas or newlines, which may contain '.'
        Vector<String> words = GenUtil.stripWords(spacedQuery);

        // words before any 'where'
        Vector<String> before = beforeWhere(words);
        trace("words before 'where':" + before.size());

        // words after any 'where'
        Vector<String> after = afterWhere(words);
        trace("words after 'where':" + after.size());

    	// parse query conditions first so that classes involved in conditions come early in the natural query strategy
        trace("parsing conditions");
        try {res = parseConditions(after);}
        catch (MapperException ex) {errorMessage(ex.getMessage());}

        // classes involved only in write conditions may be non-core
        trace("parsing write fields");
        if (!(parseWriteFields(before))) {errorMessage("Problem in write fields; parse abandoned.");}

        return res;
    }
    
    /*
     * add spaces around special symbols, in case the user has not
     */
    private String addSpaces(String query)
    {
    	String[] symbols = {"=","<",">"};
    	StringTokenizer st = new StringTokenizer(query,"=><",true);
    	String newQuery = "";
    	while (st.hasMoreTokens())
    	{
    		String next = st.nextToken();
    		if (GenUtil.inArray(next, symbols)) newQuery = newQuery + " " + next + " ";
    		else newQuery = newQuery + next;
    	}
    	return newQuery;
    }

    private boolean parseWriteFields(Vector<String> before)
    {
         boolean writeFieldsOK = true;
        // loop over write fields
        for (int pos = 1; pos < before.size(); pos++)  if (writeFieldsOK)
        {
                /* for fields which are 'class.role.property' this has the side effect
                of storing the associations without duplicates. 
                'false' means the QueryClasses involved are not necessarily core to the query*/
        	    Vector<WriteField> wf = makeWriteFields(qWord(before,pos),false);
        	    for (int i =0; i < wf.size(); i++) writeFields.add(wf.get(i));
        }
        if (writeFields.size() == 0)
            {errorMessage("No valid properties defined to write out in the query.");writeFieldsOK = false;}
        return writeFieldsOK;
    }


    /**
     * 
     * @param after
     * @return
     */
    private boolean parseConditions(Vector<String> after) throws MapperException
    {
        boolean res = true;
        int pos = 0; // current position in the Vector of words after 'where'
        int len = after.size();
        String w1,w2,w3;

        /* both property conditions and link associations take up three words;
        stop when there are less than three words left, or if there has been a failure */
        while ((pos + 2 < len) && res)
        {
            w1 = qWord(after, pos);
            w2 = qWord(after, pos + 1);
            w3 = qWord(after, pos + 2);
            if (hasFullStop(w1)) // condition on a property; RHS can be a constant or another property
            {
            	QueryCondition qc = null;
            	// 'true' means the QueryClasses involved must be core to the query
            	Vector<WriteField> wfs = makeWriteFields(w1,true); // side-effect; stores any associations implied by role names
            	if (wfs.size() != 1) throw new MapperException("Cannot parse LHS of condition '" + w1 + "'");
            	WriteField wf1 = wfs.get(0);
                if (wf1.valid()) // property must be represented in  the ecore model
                {
                     if (!OKRelation(w2)) res = false;
                    if (hasFullStop(w3)) // RHS is another property value
                    {
                    	Vector<WriteField> wft = makeWriteFields(w3,true); // side-effect; stores any associations implied by role names
                    	if (wft.size() != 1) throw new MapperException("Cannot parse RHS of condition '" + w1 + "'");
                    	WriteField wf3 = wft.get(0);
                        if (wf3.valid()) // property must be represented in the Ecore model
                        {
                            qc = new QueryCondition(wf1.queryClass(),wf1.propName(),w2,wf3.queryClass(),wf3.propName(),this);
                        }
                        else res = false;
                    }
                    else // RHS is a constant
                    {
                        qc = new QueryCondition(wf1.queryClass(),wf1.propName(),w2,w3,this);
                    }
                    conditions.add(qc);
                    res = res & checkAnd(after,pos); //if there is any next word, check it is 'and'
                    pos = pos + 4; // move on beyond the condition and 'and'
                }
                else {res = false;}  //writeField has already issued a warning
            } // end of 'condition on a property' case

            else // link association between objects/classes
            {
            	pos = handleLinkAssociation(w1,w2,w3,after,pos);
            	if (pos < 0) res = false;
           }
        }
        return res;
    }
    

    abstract Vector<WriteField> makeWriteFields(String fName, boolean isCore);

    /**
     * 
     * @param w1
     * @param w2
     * @param w3
     * @param after
     * @param pos
     * @return
     */
    abstract int handleLinkAssociation(String w1, String w2, String w3,Vector<String> after, int pos);




//---------------------------------------------------------------------------------
//                      Mundane methods supporting parsing
//---------------------------------------------------------------------------------

    /** word i of a Vector of words.
    This may be 'display' (added unknown to the user to the start of his query)
    or class.column or class.role.column, with any number of roles
    (for output, or as part of a condition)
    or one word of 'class association class'
    or 'where' or 'and'  */
    private String qWord(Vector<String> words,int i)
    {
        String res = null;
        if (i < words.size()) res = words.elementAt(i);
        trace("query word " + i + " = '" + res + "'");
        return res;
    }

    // Vector of all words before 'where' , excluding 'where'
    private Vector<String> beforeWhere(Vector<String> fullQuery)
    {
        Vector<String> res = new Vector<String>();
        boolean whereFound = false;
        for (int i = 0; i < fullQuery.size(); i++)
        {
            String word = fullQuery.elementAt(i);
            if (word.equalsIgnoreCase("where")) whereFound = true;
            if (!whereFound) res.addElement(word);
        }
        return res;
    }

    // Vector of all words after 'where' , excluding 'where'
    private Vector<String> afterWhere(Vector<String> fullQuery)
    {
        Vector<String> res = new Vector<String>();
        boolean whereFound = false;
        for (int i = 0; i < fullQuery.size(); i++)
        {
            String word = (String)fullQuery.elementAt(i);
            if (whereFound) res.addElement(word);
            if (word.equalsIgnoreCase("where")) whereFound = true;
        }
        return res;
    }

    /* make the parts of a word separated by '.' into elements of a Vector,
    with the first part as element 0 of the Vector.
    So 'fred' becomes ('fred'), 'fred.joe' becomes ('fred','joe'), etc. */
    protected Vector<String> stopSeparated(String word)
    {
        Vector<String> res = new Vector<String>();
        boolean fullStopFound = false;
        // first character is i=0, but expect no full stops at either end of the word
        for (int i = 1; i < word.length()-1; i++)
        {
            // stop at the first '.'
            if ((word.charAt(i) == '.') && (!fullStopFound))
            {
                String start = word.substring(0,i); // keeps the first i characters
                res.addElement(start);
                Vector<String> endWords = stopSeparated(word.substring(i+1)); // chops off the first (i+1) characters
                for (int j = 0; j < endWords.size(); j++)
                    {res.addElement(endWords.elementAt(j));}
                fullStopFound = true;
            }
        }
        if (!fullStopFound) res.addElement(word);
        return res;
    }

    protected String rejoined(Vector<String> subFields)
    {
        String rj = "";
        for (int i = 0; i < subFields.size(); i++)
        {
            rj = rj + subFields.elementAt(i);
            if (i < (subFields.size() - 1)) rj = rj + ".";
        }
        return rj;
    }

    /* fullRole is (role)class.
    Return the 'role' part. */
    protected String bareRole(String fullRole)
    {
        String res = "";
        int fb = GenUtil.firstOfChar(')',fullRole);
        if (fb > 1) res = fullRole.substring(1,fb);
        return res;
    }

    /* fullRole is (role)class.
    Return the 'class' part */
    protected String definedClass(String fullRole)
    {
        String res = null;
        int fb = GenUtil.firstOfChar(')',fullRole);
        if (fb > 0) res = fullRole.substring(fb+1,fullRole.length());
        return res;
    }



    /* true if a vector resulting from writeField implies that the property
    is represented in the XML. */
    protected boolean represented(Vector<String> v)
        {return (v.elementAt(2).equals("present"));}

    /** check if a name has an internal full stop */
    private boolean hasFullStop(String fName)
    {
        int i;
        boolean res = false;
        for (i = 1; i < fName.length()-1; i++)
        {
            if (fName.charAt(i) == '.')
                {res = true;}
        }
        return res;
    }

    protected boolean checkAnd(Vector<String> after, int pos)
    {
        boolean res = true;
        if (((pos + 3) < after.size()) && !(qWord(after,pos+3).equalsIgnoreCase("and")))
        {
            res = false;
            errorMessage("The word immediately following any condition must be "
                + "'and', not '" + qWord(after,pos+3) + "'");
        }
        return res;
    }

    /** true if test is an allowed form of a relation to test.
    Otherwise writes an error message. */
    private boolean OKRelation(String test)
    {
        boolean res=false;
        String[] arithRelations = {"=",">",">=","<","<="};
        if (inArray(test, arithRelations)) {res = true;}
        else if (test.equalsIgnoreCase("contains")) {res = true;}
        else if (test.equalsIgnoreCase("notContains")) {res = true;}
        else if (test.equalsIgnoreCase("startsWith")) {res = true;}
        else if (test.equalsIgnoreCase("in")) {res = true;}
        else if (test.equalsIgnoreCase("notIn")) {res = true;}
        else if (test.equalsIgnoreCase("before")) {res = true;}
        else if (test.equalsIgnoreCase("after")) {res = true;}
        else {errorMessage("Cannot recognise condition test '" + test
            + "'; allowed tests are =, >, >=, <, <=, contains, notContains, startsWith, in, notIn, before, after");}
        return res;
    }

//------------------------------------------------------------------------------------
//                             Bits & bobs
//------------------------------------------------------------------------------------

	/** check if a string is in an array, checking all elements */
	private boolean inArray(String arg, String[] array)
	{
	    int i,len;
	    boolean res = false;
	    len = array.length;
	    if (len > 0) for ( i = 0; i < len; i++)
	        {if (arg.equals(array[i])) res = true;}
	    return res;
	}


    protected void errorMessage(String s) 
    {
    	String[] errorRow = new String[2];
    	errorRow[0] = code;
    	errorRow[1] = s;
    	errors.add(errorRow);

    	System.out.println(s);
    }

    protected void message(String s) 
    {
    	System.out.println(s);
    }
    
    protected void trace(String s) {if (tracing) message(s);}

	
	/**
	 * 
	 * @param removePackageNames
	 * @return pairs [className, propertyName] for column headers
	 */
    public Vector<String[]> getColumnHeaders(boolean removePackageNames)
	{
		Vector<String[]> headers = new Vector<String[]>();

		// set other new column headers, reusing columns or making new ones
		for (int c = 0; c < writeFields.size(); c++)
		{
			String[] header = new String[2];
			
			// first part of the header is the class name, or if there is an association chain, the last link of it
			WriteField field = writeFields.get(c);
			QueryClass queryClass = field.queryClass();
			
			// case where there is no association chain;  maybe remove the package name from the class name
			header[0] = queryClass.className(); 
			StringTokenizer st = new StringTokenizer(header[0],".");
			if ((removePackageNames) && (st.countTokens() == 2))
			{
				st.nextToken();
				header[0] = st.nextToken();
			}
			
			// case where there is an association chain
			String chain = queryClass.assocChain();
			if ((chain != null) && (chain.length() > 1))
			{
				StringTokenizer links = new StringTokenizer(chain, ".");
				while (links.hasMoreTokens()) {header[0] = links.nextToken();}
			}

			// second part of the header is just the field name
			header[1] = field.propName();
			headers.add(header);
		}
		
		return headers;		
	}
    
    
    //--------------------------------------------------------------------------------------
    //				Generating SQL queries against relational data sources
    //--------------------------------------------------------------------------------------
    
    /**
     * 
     * @param code the code of a relational data source
     * @return a Vector of SQLQuery objects to populate the XML needed to answer the query;
     * currently there is only one SQL Query needed
     * @throws MapperException if the data source does not have relational structure
     */
    public Vector<SQLQuery> makeSQLQueries(String code, DBStructure database) throws MapperException
    {
    	SQLQuery oneQuery = new SQLQuery(database);
    	
    	/* note all the tables, columns and SQL conditions that need to be added to the query, 
    	 * so it delivers the smallest XML tree to answer the query. */

    	for (int i = 0; i < queryClasses.size(); i++)
    		queryClasses.get(i).buildQuery(oneQuery, code);
    	
    	for (int i = 0; i < conditions.size(); i++)
    		conditions.get(i).buildQuery(oneQuery, code);
    	
    	for (Enumeration<LinkAssociation> en = linkAssociations.elements();en.hasMoreElements();)
    		en.nextElement().buildQuery(oneQuery, code);
    	
    	for (int i = 0; i < writeFields.size(); i++)
    		writeFields.get(i).buildQuery(oneQuery, code);

    	Vector<SQLQuery> result = new Vector<SQLQuery>();
    	result.add(oneQuery);
    	return result;
    }



}
