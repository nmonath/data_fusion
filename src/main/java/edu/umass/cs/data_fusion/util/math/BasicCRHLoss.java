package main.java.edu.umass.cs.data_fusion.util.math;


import main.java.edu.umass.cs.data_fusion.data_structures.Attribute;
import main.java.edu.umass.cs.data_fusion.data_structures.FloatAttribute;

public class BasicCRHLoss implements AttributeLossFunction {

    public ZeroOneLoss categoricalLoss = new ZeroOneLoss();
    public AbsoluteWeightedDeviation continuousLoss = new AbsoluteWeightedDeviation(0);
    
    @Override
    public String getName() {
        return "BasicCRHLoss";
    }

    @Override
    public float loss(Attribute one, Attribute two) {
        return (one instanceof FloatAttribute) && (two instanceof FloatAttribute) ? continuousLoss.loss(one,two) : categoricalLoss.loss(one,two);
    }
}
