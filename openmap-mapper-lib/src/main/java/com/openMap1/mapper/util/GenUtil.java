package com.openMap1.mapper.util;

import java.text.Collator;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.namespace;

/**
 * A collection of general utilities, all static.
 * 
 * @author robert
 *
 */
public class GenUtil {
	
	public static void message(String s) {System.out.println(s);}

    /** return true if the string s is anywhere in the vector v of strings */
    public static boolean inVector(String s, Vector<String> v)
    {
        int i;
        boolean res = false;
        for (i = 0; i < v.size(); i++)
        {
            if (s.equals(v.elementAt(i))) res = true;
        }
        return res;
    }
    
    /**
     * 
     * @param s a string
     * @param v a Vector of strings
     * @return the number of occurrences of s in v
     */
    public static int countOccurrences(String s, Vector<String> v)
    {
    	int count = 0;
        for (int i = 0; i < v.size(); i++)
        {
            if (s.equals(v.elementAt(i))) count++;
        }
        return count;
    }
    

    /** check if a string is in an array of strings, checking all elements */
    public static boolean inArray(String arg, String[] array)
    {
        int i,len;
        boolean res = false;
        len = array.length;
        if (len > 0) for ( i = 0; i < len; i++)
            {if (arg.equals(array[i])) res = true;}
        return res;
    }

    /**
     * returns true if s1 contains s2 as a substring anywhere
     */
    public static boolean contains(String s1, String s2)
    {
        int offset;
        boolean res = false;
        if (s1.length() > s2.length() - 1)
            for (offset = 0; offset < s1.length() - s2.length() + 1; offset++)
                if (s1.startsWith(s2,offset)) {res = true;}
        return res;
    }
    
    /**
     * change the initial letter of a string to lower case
     * @param s
     * @return
     */
    public static String initialLowerCase(String s)
    {
    	String initial = s.substring(0,1).toLowerCase();
    	return initial + s.substring(1);
    }
    
    /**
     * change the initial letter of a string to upper case
     * @param s
     * @return
     */
    public static String initialUpperCase(String s)
    {
    	String initial = s.substring(0,1).toUpperCase();
    	return initial + s.substring(1);
    }

    /* returns true if s1 contains s2 as a substring bounded by spaces or commas*/
    public static boolean containsAsWord(String s1, String s2)
    {
        Vector<String> words = stripWords(s1);
        return inVector(s2,words);
    }


    /** from a string s, strip out words separated by spaces or commas or new lines. */
    public static Vector<String> stripWords(String s)
    {
       String sTemp; // disposable copy
       String word;
       char c;
       char singleQuote = '\'';
       char newLine = '\n';
       char returnChar = '\r';
       Vector<String> res = new Vector<String>();

       sTemp = s.substring(0); // make a straight copy of s
       while (sTemp.length() > 0)
       {
           c = sTemp.charAt(0);
           if ((c == ' ')|(c == ',')|(c == newLine)|(c == returnChar))
           {
               sTemp = sTemp.substring(1); // strip off the first space
           }
           else if ((c == singleQuote)|(c == '"'))
           {
               word = stringBetween(sTemp,c); // string between quotes
               sTemp = sTemp.substring(word.length()+2); //strip off string and quotes
               res.addElement(word);
           }
           else
           {
               word = firstWord(sTemp);
               sTemp = sTemp.substring(word.length()); // strip off word
               res.addElement(word);
           }
       }
       return res;
    }


    /* the first character of s is c. Find the string between that and the
    next occurrence of c, or the end of s. */
    private static String stringBetween(String s, char c)
    {
       int i;
       char cp;
       String res = s.substring(1); // in case no repeat of c is found
       boolean found = false; // no repeat of c found yet

       for (i = 1; i < s.length(); i++) // don't look for c in 1st char
       {
           cp = s.charAt(i);
           if ((!found) && (c == cp)) // first repeat of c only
               {found = true; res = s.substring(1,i);}
       }
       return res;
    }

    /* return the position of the first occurrence of character c
    in string s, or -1 if it does not occur.*/
    public static int firstOfChar(char c, String s)
    {
       int res = -1;
       boolean found = false;
       for (int i = 0; i < s.length(); i++) if (!found)
           if (s.charAt(i) == c)
           {
               res = i;
               found = true;
           }
       return res;
    }


    /** find the first word in s before any space or comma or new line.
    The first character of s must not be a space or comma. */
    private static String firstWord(String s)
    {
       int i;
       char c;
       char newLine = '\n';
       char returnChar = '\r';
       String res = s; // in case no blank is found
       boolean found = false; // no blank found yet

       for (i = 1; i < s.length(); i++) // don't look for blank in 1st char
       {
           c = s.charAt(i);
           if ((!found) && ((c == ' ')|(c == ',')|(c == newLine)|(c == returnChar))) // first blank or comma or newline only
               {found = true; res = s.substring(0,i);}
       }
       return res;
    }

     
     /**
      * Called on catching some Exception that was really expected never to happen.
      * Writes the exception message, then throws a null pointer Exception.
      * @param ex the exception
      * @param methodName method where the exception was caught.
      */
     public static void surprise(Exception ex, String methodName)
     {
    	 System.out.println("Surprise Exception in method '" + methodName 
    			 + "': " + ex.getMessage());
		 for (int i = 0; i < ex.getStackTrace().length; i++) System.out.println(ex.getStackTrace()[i].toString());
     }
     
     /**
      * replace both the two line break characters by spaces
      * @param s
      * @return
      */
     public static String replaceLineBreaksBySpaces(String s)
     {
    	 char space = ' ';
    	 char[] replaced = new char[s.length()];
    	 for (int i = 0; i < s.length();i++)
    	 {
    		 char c = s.charAt(i);
    		 if (c==13) replaced[i] = space;
    		 else if (c==10) replaced[i] = space;
    		 else replaced[i] = c;
    	 }
    	 String ss = new String(replaced);
    	 return ss;
     }

     
     //----------------------------------------------------------------------------------------------------------
     //   default MDL namespaces
     //----------------------------------------------------------------------------------------------------------

private static String meNamespaceURI = "http://www/myCo.com/dModel.daml";
private static String meNamespacePrefix = "me";

/**
* default MDL namespace for MDL mapping files, given by:
* <p>
* prefix = "me"; URI = "http://www/myCo.com/dModel.daml"
*
* @return namespace
*/
public static namespace defaultMDLNamespace() {return new namespace(meNamespacePrefix,meNamespaceURI);}


/**
* MDL documents typically start with:
* <p>
* <schema-adjunct target="http://www/myCo.com/mySchema.xsd" xmlns:me="http://www/myCo.com/dModel.daml">
* <p>
* This string defines the 'target' attribute.
*
* @return String
*/
public static String MDLTargetNamespaceURI() {return ("http://www/myCo.com/mySchema.xsd");}

/**
 * convert a sequence of words into 'java-like' form -
 * remove the gaps between words
 * and make the initial letter of all words except the first upper case.
 */
 public static String gaplessForm(String name)
 {
     int i;
     char[] next = new char[1];
     boolean gap = false;
     String result = "";
     String inc;

     for (i = 0; i < name.length(); i++)
     {
         if (name.charAt(i) == ' ')
             {gap = true;}
         else
             {
                 next[0] = name.charAt(i);
                 inc = new String(next);
                 if (gap) {inc = inc.toUpperCase();}
                 gap = false;
                 result = result + inc;
             }
     }
     return result;
 }

 // concatenate the strings in a Vector, separated by a separator string
 public static String concatenate(Vector<String> v, String separator)
 {
     String res = "";
     for (int i = 0; i < v.size(); i++)
     {
           res = res + v.elementAt(i);
           if (i < v.size() - 1) res = res + separator;
     }
     return res;
 }


 // true if a CM link can lead at most to one element
 public static boolean uniqueLink(String CMLink)
     {return (!((contains(CMLink,"[1:*]"))|         // one or more
                (contains(CMLink,"li"))|         // list
                (contains(CMLink,"[0:*]"))));}      // zero or more


 // true if a CM link implies an element is optional
 public static boolean optional(String CMLink)
     {return ((contains(CMLink,"[0:1]"))|      // optional
             (contains(CMLink,"chc"))|       // choice
             (contains(CMLink,"[0:*]")));}     // zero or more


 // return a single String from a Vector of Strings, spaced by " "
 public static String singleString(Vector<String> strings)
 {
     String single = "";
     for (int i = 0; i < strings.size(); i++)
     {
         if (i > 0) single = single + " ";
         single = single + strings.elementAt(i);
     }
     return single;
 }
 
 /**
  * 
  * @param stringTable
  * @return a single string of the keys speparated by spaces
  */
 public static String singleKeyString(Hashtable<String,?> stringTable)
 {
     String single = "";
     for (Enumeration<String> en = stringTable.keys(); en.hasMoreElements();)
    	 single = single + en.nextElement() + " ";
     return single;
 }
 
 //---------------------------------------------------------------------------------
 //                         Vector copying methods
 //---------------------------------------------------------------------------------
 
 
 public static Vector<String> vStringCopy(Vector<String> v)
 {
	 Vector<String> res = new Vector<String>();
	 for (Iterator<String> it = v.iterator();it.hasNext();) res.add(it.next());
	 return res;
 }
 
 //---------------------------------------------------------------------------------
 //                         parsing lines of csv files
 //---------------------------------------------------------------------------------
 
 /**
  * convert a line of a csv file into a Vector of Strings.
  * The components of the Vector are separated by comma in the line, 
  * and may be expressions in double quotes (containing commas which are not treated as separators)
  * @param line line of a csv file
  * @return the parts of the line
  */
 public static Vector<String> parseCSVLine(String line) throws MapperException
 {
	 Vector<String> parsedLine = new Vector<String>();
	 
	 // for storing the contents of double quotes, which may contain commas
	 Hashtable<String,String> quoteContents = new Hashtable<String,String>();
	 // the keys used to store contents of double quotes are unique (overkill - they cannot be confused with real text)
	 String uniquePrefix = "£$_";
			 
	// pull out and store the contents of double quotes
	 StringTokenizer sQuote = new StringTokenizer(line,"\"",true);
	 String converted = "";
	 int suffix = 0;
	 boolean inDoubleQuotes = false;
	 while (sQuote.hasMoreTokens())
	 {
		 String next = sQuote.nextToken();
		 // deal with double quote marks
		 if (next.equals("\"")) 
		 {
			 // two double quotes in succession; store an empty string with a key
			 if (inDoubleQuotes)
			 {
				 String key = uniquePrefix + suffix;
				 suffix++;
				 quoteContents.put(key,"");
				 converted = converted + key;
			 }
			 
			 inDoubleQuotes = !inDoubleQuotes; // update state in/out of quotes
			 converted = converted + "\""; // retain the quote marks
		 }
		 // a non-empty string inside double quotes; save it by key and consume the next double quote
		 else if (inDoubleQuotes)
		 {
			 // save the string by key
			 String key = uniquePrefix + suffix;
			 suffix++;
			 quoteContents.put(key, next);
			 converted = converted + key;
			 
			 // consume the following double quote mark
			 sQuote.nextToken();
			 inDoubleQuotes = false;
			 converted = converted + "\""; // retain the final quote mark
		 }
		 // a non-empty string not in double quotes (may contain separator commas)
		 else if (!inDoubleQuotes)
		 {
			 converted = converted + next;
		 }
	 }
	 
		/* must retain commas for the case when two commas are separated by nothing 
		 * If there are N columns, there should be (N-1) comma separators. */	
		StringTokenizer sComma = new StringTokenizer(converted,",", true);
		
		int col = 0;
		String value = "";
		while (sComma.hasMoreTokens())
		{
			value = sComma.nextToken();
			// encountered an initial comma, or a comma after a comma; interpret the value as empty
			if (value.equals(",")) 
			{
				parsedLine.add("");
				col++;
			}
			// encountered a non-empty value; consume the next comma after it, and check if the value is in quotes
			else
			{
				// consume the next comma, if there is one
				String comma = ",";
				if (sComma.hasMoreTokens()) comma = sComma.nextToken();
				if (!comma.equals(",")) throw new MapperException("'" + comma + "' is not a ',' at position " + col + " in " + line);

				// deal with keys for values, in double quotes
				if ((value.startsWith("\"")) && (value.endsWith("\""))) 
				{
					value = value.substring(1,value.length() - 1); // strip off quotes from the key
					if (value.startsWith(uniquePrefix)) // now the value should be one of the keys to quoteContents
					{
						String contents = quoteContents.get(value);
						if (contents == null) 
							throw new MapperException("Cannot find stored value at position "  + col + " of line " + line 
									+ " by key '" + value + "' in converted line '" + converted + "'" );
						parsedLine.add(contents);
						col++;
					}
					else throw new MapperException("Unexpected form of key '" + value + "' at position "  + col + " of line " + line);
				}
				// deal with unquoted values between commas
				else
				{
					parsedLine.add(value);
					col++;
				}
			}
		}

		// a final ',' implies an empty value after the comma, which was not added in the loop above
		if (converted.endsWith(",")) parsedLine.add("");
		 
	 
	 return parsedLine;
 }

 
 //---------------------------------------------------------------------------------
 //                         memory management
 //---------------------------------------------------------------------------------
 
 public static void writeMemory()
 {
	 Runtime rt = Runtime.getRuntime();
	 System.out.println("Total " + rt.totalMemory() + "\tMax " 
			 + rt.maxMemory() + "\tFree " + rt.freeMemory());
 }
 
 //---------------------------------------------------------------------------------
 //                         sorting strings
 //---------------------------------------------------------------------------------
 
 
 public static Iterator<String> sortedStringKeys(Hashtable<String,?> table)
 {
	 Collection<String> coll = new TreeSet<String>(Collator.getInstance());
	 for (Enumeration<String> en = table.keys();en.hasMoreElements();) coll.add(en.nextElement());
	 return coll.iterator();	 
 }
 
 //---------------------------------------------------------------------------------------------
 //            user messages - copied from WorkBenchUtil
 //----------------------------------------------------------------------------------------------
 
	/**
	 * show an informative message with an OK button to continue
	 * @param title
	 * @param message
	 */
	public static void showMessage(String title, String message)
	{
		MessageDialog.openInformation(
				getShell(),
				title,
				message);		
	}

	/**
	 * @return a shell foe messages and dialogues
	 */
	public static Shell getShell()
	{
		return PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
	}
 
 
}
