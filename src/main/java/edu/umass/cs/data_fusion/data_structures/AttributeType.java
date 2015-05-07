package edu.umass.cs.data_fusion.data_structures;


/**
 * Used mostly for evaluation purposes, denotes whether the
 * attribute can take on a finite or infinite number of values
 */
public enum AttributeType {
    CONTINUOUS,CATEGORICAL;

    public static AttributeType fromString(String str) {
        if (str.equalsIgnoreCase("continuous"))
            return CONTINUOUS;
        else if (str.equalsIgnoreCase("categorical"))
            return CATEGORICAL;
        else
            return null;
    }
}
