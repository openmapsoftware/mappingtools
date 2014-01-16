package com.openMap1.mapper.userConverters;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

//un-comment to test if the package is accessible
// import org.apache.derby.jdbc.ClientDriver;
// import com.mysql.jdbc.Driver;


/**
 * class to make a connection to a Relational Database
 * 
 * Uses the start of the jdbc connect string to recognise what jdbc driver to use; and currently 
 * only recognises a small number of drivers.
 * 
 * Modify this class to recognise other jdbc connect strings, and to load the appropriate driver;
 * or (if necessary) to ask the user for a user name and password.
 * 
 * @author robert
 *
 */
public class DBConnect {

	/**
	 * @return jdbc connect string, as set in the constructor; usually begins with 'jdbc:'
	 */
	public String connectString() {return connectString;}
	private String connectString;

	private String userName;
	private String password;
	private String schema;
	
	private boolean tracing = false;
	
	/**
	 * @return the database connection which this instance returns to the calling program 
	 */
	public Connection con() {return con;}
	private Connection con;
	
	/**
	 * @return true if a connection has successfully been made to the database
	 */
	public boolean connected() {return connected;}
	private boolean connected= false;
		
    /**
     *  names of DBMS that can be connected to, with their
     * allowed starts of jdbc connect strings, and the required driver classes.
     * Extend this list to use other jdbc drivers
     * */
    public static String[][] jdbcParam() {return jdbcParam;}
    private static String[][] jdbcParam =
    {
        {"jdbc:odbc:","sun.jdbc.odbc.JdbcOdbcDriver","odbc"},
        {"jdbc:jtds:sqlserver:","net.sourceforge.jtds.jdbc.Driver","SQL Server"},
        {"jdbc:interbase:","interbase.interclient.Driver","Interbase"},
        {"jdbc:oracle:thin:","oracle.jdbc.driver.OracleDriver","Oracle"},
        {"jdbc:Cache:","com.intersys.jdbc.CacheDriver","InterSystems"},
        {"jdbc:mysql:","com.mysql.jdbc.Driver","mySQL"},
        {"jdbc:derby://","org.apache.derby.jdbc.ClientDriver","Derby Network Server"},
        {"jdbc:derby:","org.apache.derby.jdbc.EmbeddedDriver","Embedded Derby"}
    };
    
	/**
	 * @param connectString url to connect to the database
	 * @param userName user name for the database; may be "", but not null 
	 * @param password password for the database; may be "", but not null 
	 */
    public DBConnect(String connectString, String userName, String password, String schema)
	{
		this.connectString = connectString;
		this.userName = userName;
		this.password = password;
		this.schema = schema;
	}

	/**
	 * Called before the database connection is used, to 
	 * set up the database connection which will later be passed  back 
	 * to the using program by method con()  
	 * @return true if the connection was made successfully
	 * @throws Exception if anything goes wrong
	 */
    public boolean connect() throws Exception
	  {
    	
	      connected = false;
	      boolean driverFound = false;
	      boolean addToConnectString = false;
	      String jdbcPrefixes = ""; // for fault-fixing only
	      try
	      {
	    	  // stop on the first driver that matches the start of the connect string
	          for (int driver = 0; driver < jdbcParam.length; driver++) if (!driverFound)
	          {
	              String jdbcPrefix = jdbcParam[driver][0];
	              String driverClassName = jdbcParam[driver][1];
	              
	              if (driver > 0) jdbcPrefixes = jdbcPrefixes + ", ";
	              jdbcPrefixes = jdbcPrefixes + "'" + jdbcPrefix + "'";

	              if (connectString.startsWith(jdbcPrefix))
	              {
                	  Class.forName(driverClassName);
 	                  driverFound = true;
 	                  if (addUserNameAndPasswordToUrl(jdbcPrefix)) addToConnectString = true;
	              }
	          }
	          if (!driverFound)
	          {
	              throw new Exception(
	                  "Can only handle jdbc connections with URLs starting " + jdbcPrefixes);
	          }
	          
	          String fullConnectString = connectString;
	          if (addToConnectString) fullConnectString = makeFullConnectString(connectString,userName,password);
	          // message("jdbc connect string: " + fullConnectString);
	          
	          trace("connect string: " + fullConnectString);
	          trace("user name: " + userName);
	          trace("password: " + password);
	          con = DriverManager.getConnection(fullConnectString,userName,password);
	          if ((schema != null) && (!schema.equals("")))
	        	  con.createStatement().execute("alter session set current_schema=" + schema);
	          connected = true;
	      }
	      catch (SQLException ex)
			    {throw new Exception("Failure opening database connection: " + ex.getMessage());}
	      catch (java.lang.Exception ex)
		  {
	    	  	ex.printStackTrace();
	    	  	throw new Exception("non-SQL Exception making database connection: " + ex.getMessage());
	      }
	      return connected;
	  }
    
    /**
     * only for Apache Derby, you need to add the username and password to the connection url
     * @param jdbcPrefix
     * @return true if the database is apache derby
     */
    private boolean addUserNameAndPasswordToUrl(String jdbcPrefix)
    {
    	return (jdbcPrefix.equals("jdbc:derby:"));
    }
    
    /**
     * only for Apache Derby, you need to add the user name and password to the connection url
     * @param jdbcPrefix
     * @return the full connect String
     */
    private String makeFullConnectString(String connectString,String userName,String password)
    {
    	String fullConnectString = connectString;
    	if ((userName != null) && (!userName.equals("")))
    		fullConnectString = fullConnectString + ";user=" + userName;
    	if ((password != null) && (!password.equals("")))
    		fullConnectString = fullConnectString + ";password=" + password;    	
    	return fullConnectString;
    }
    
    private void message(String s) {System.out.println(s);}
    
    private void trace(String s) {if (tracing) message(s);}

}
