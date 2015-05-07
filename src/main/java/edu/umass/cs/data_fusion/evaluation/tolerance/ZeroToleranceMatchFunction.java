package edu.umass.cs.data_fusion.evaluation.tolerance;


import edu.umass.cs.data_fusion.data_structures.Attribute;

public class ZeroToleranceMatchFunction implements ToleranceMatchFunction {
    @Override
    public boolean isMatch(Attribute predicted, Attribute gold) {
        return predicted.equals(gold);
    }

    @Override
    public String toleranceDescription() {
        return "(+/-0.0)";
    }
}
