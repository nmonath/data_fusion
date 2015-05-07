package main.java.edu.umass.cs.data_fusion.util.math;

import main.java.edu.umass.cs.data_fusion.data_structures.Attribute;
import main.java.edu.umass.cs.data_fusion.data_structures.FloatAttribute;

public class WeightedAbsoluteWeightedDeviation implements AttributeLossFunction {

    private float standardDeviation;
    private float K;
    public WeightedAbsoluteWeightedDeviation(float standardDeviation, float K) {
        this.standardDeviation = standardDeviation;
        this.K = K;
    }

    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public float loss(Attribute one, Attribute two) {
        return K*Math.abs( ((FloatAttribute) one).getFloatValue() - ((FloatAttribute) two).getFloatValue()) / standardDeviation;
    }
}
