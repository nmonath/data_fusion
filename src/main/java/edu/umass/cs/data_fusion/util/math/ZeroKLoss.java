package edu.umass.cs.data_fusion.util.math;

import edu.umass.cs.data_fusion.data_structures.Attribute;

public class ZeroKLoss implements AttributeLossFunction {

    final private String name = "ZeroKLoss";
    private float K;
    public ZeroKLoss(float K) {
        this.K = K;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float loss(Attribute one, Attribute two) {
        return (one.equals(two)) ? 0 : K;
    }
}
