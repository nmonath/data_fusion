package edu.umass.cs.data_fusion.data_structures;


import java.lang.Object;
import java.lang.Override;
import java.lang.String;

public class Source {

    private String name = "";
    
    public Source(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return  (obj instanceof Source) && name.equals(((Source)obj).name);
    }
    
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Source(name: " + name + ")";
    }
}
