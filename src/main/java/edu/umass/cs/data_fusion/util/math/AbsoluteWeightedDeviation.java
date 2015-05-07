package main.java.edu.umass.cs.data_fusion.util.math;

import main.java.edu.umass.cs.data_fusion.data_structures.Attribute;
import main.java.edu.umass.cs.data_fusion.data_structures.FloatAttribute;

public class AbsoluteWeightedDeviation implements AttributeLossFunction {

    private float standardDeviation;
    public AbsoluteWeightedDeviation(float standardDeviation) {
        this.standardDeviation = standardDeviation;
    }
    
    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }
    
    @Override
    public float loss(Attribute one, Attribute two) {
        return Math.abs( ((FloatAttribute) one).getFloatValue() - ((FloatAttribute) two).getFloatValue()) / standardDeviation;
    }
}
