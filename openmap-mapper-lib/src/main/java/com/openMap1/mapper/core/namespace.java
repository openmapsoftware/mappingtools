package com.openMap1.mapper.core;

import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.Namespace;


//--------------------------------------------------------------------------------------
//                              Namespaces
//--------------------------------------------------------------------------------------

    /**
     * an XML namespace; wrapper class for the Mapper Model class Namespace
     */
	public class namespace
    {
        private String URI;
        private String prefix; // what comes before the ':', not including ':'
        // prefix = "" for the default namespace

        /**
         * constructor from string URI and prefix;
         * prefix = "" for the default namespace
         */
        public namespace(String prefix,String uri)
        {
            if (prefix == null)
                {GenUtil.message("Cannot form a namespace with a null prefix and URI '" + uri + "'.");}
            if (uri == null)
                {GenUtil.message("Cannot form a namespace with a null URI and prefix '" + prefix + "'.");}
            URI = uri;
            this.prefix = prefix;
        }
        
        /**
         * constructor from the mapper model Namespace object
         * @param ns the Namespace
         */
        public namespace(Namespace ns)
        {
        	prefix = ns.getPrefix();
        	URI = ns.getURL();
        }

        /**
         * @return String the namespace prefix, before the ':'
         */
        public String prefix() {return prefix;}
        
        /**
         * @return String the namespace URI
         */
        public String URI() {return URI;}

        /**
         *  name of the attribute used to declare the namespace
         * If prefix = null (illegal) returns null. */
        public String attributeName()
        {

            String res = null;
            if (prefix != null)
            {
                res = "xmlns";
                if (!prefix.equals(""))
                    {res = res + ":" + prefix;}
            }
            else GenUtil.message("Error: null prefix");
            return res;
        }
    }
