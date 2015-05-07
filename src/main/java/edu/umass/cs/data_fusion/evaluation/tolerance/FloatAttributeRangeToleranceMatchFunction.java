package edu.umass.cs.data_fusion.evaluation.tolerance;


import edu.umass.cs.data_fusion.data_structures.Attribute;
import edu.umass.cs.data_fusion.data_structures.FloatAttribute;

public class FloatAttributeRangeToleranceMatchFunction implements ToleranceMatchFunction {
    
    private float tolerance;
    
    public FloatAttributeRangeToleranceMatchFunction(float tolerance) {
        this.tolerance = tolerance;
    }


    @Override
    public boolean isMatch(Attribute predicted, Attribute gold) {
        return predicted instanceof FloatAttribute 
                && gold instanceof FloatAttribute 
                && Math.abs(((FloatAttribute) predicted).getFloatValue() - ((FloatAttribute) gold).getFloatValue()) <= tolerance;
    }

    @Override
    public String toleranceDescription() {
        return String.format("(+/-%g)",  tolerance);
    }
}
