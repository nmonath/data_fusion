package edu.umass.cs.data_fusion.data_structures.author;

// Using an idea from: http://stackoverflow.com/questions/6667243/using-enum-values-as-string-literals
public enum AuthorNameType {
    FIRST ("FIRST"),
    FIRST_INIT ("FIRST_INIT"),
    MIDDLE ("MIDDLE"),
    MIDDLE_INIT ("MIDDLE_INIT"),
    LAST ("LAST"),
    UNKNOWN ("UNKNOWN");

    private final String name;

    private AuthorNameType(String s) {
        name = s;
    }
    
    public String toString(){
        return name;
    }

}