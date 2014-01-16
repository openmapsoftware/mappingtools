package com.openMap1.mapper.query;

// 

/**
 * This class is  used  in class RDBToXML, 
 * An instance denotes a table name, a column name, and the value
 */

public class ResultCell
{
        public String tableName;
        public String columnName;
        public String value;

        public ResultCell(String tn, String cn, String val)
        {
            tableName = tn;
            columnName = cn;
            value = val;
        }
}

