package main.java.edu.umass.cs.data_fusion.evaluation.tolerance;


import main.java.edu.umass.cs.data_fusion.data_structures.Attribute;

public interface ToleranceMatchFunction {
    
    public boolean isMatch(Attribute predicted, Attribute gold);
    
    public String toleranceDescription();
}
