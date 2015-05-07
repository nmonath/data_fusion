package edu.umass.cs.data_fusion.data_structures;


/**
 * A representation of what kind of values the attribute stores, i.e.
 * what datatype the values are stored in. E.g. string, float, etc
 */
public enum AttributeDataType {
    STRING,FLOAT,AUTHOR_LIST;
    
    public static AttributeDataType fromString(String str) {
        if (str.equalsIgnoreCase("string"))
            return STRING;
        else if (str.equalsIgnoreCase("float"))
            return FLOAT;
        else if (str.equalsIgnoreCase("author_list"))
            return AUTHOR_LIST;
        else
            return null;
    }
}
