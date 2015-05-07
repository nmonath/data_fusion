package edu.umass.cs.data_fusion.evaluation;


import edu.umass.cs.data_fusion.data_structures.*;
import edu.umass.cs.data_fusion.evaluation.tolerance.FloatAttributeRangeToleranceMatchFunction;
import edu.umass.cs.data_fusion.evaluation.tolerance.ToleranceDefaults;
import edu.umass.cs.data_fusion.evaluation.tolerance.ToleranceMatchFunction;
import edu.umass.cs.data_fusion.evaluation.tolerance.ZeroToleranceMatchFunction;
import edu.umass.cs.data_fusion.util.Functions;

import java.util.*;

public class EvaluationMetricsWithTolerance extends EvaluationMetrics {
    
    private Map<Pair<Entity,String>,ToleranceMatchFunction> toleranceMatchFunctionMap;

    public EvaluationMetricsWithTolerance(ArrayList<Result> results, RecordCollection gold, Map<Pair<Entity,String>,ToleranceMatchFunction> toleranceMatchFunctionMap){
        super(results, gold, 0.0, 0.0, 0.0,0.0, 0.0);
        this.toleranceMatchFunctionMap = toleranceMatchFunctionMap;
    }
    
    public EvaluationMetricsWithTolerance(ArrayList<Result> results, RecordCollection rawData, RecordCollection gold) {
        this(results,gold,getToleranceMatchingFunctions(rawData));
    }
    
    public boolean matches(Entity entity, Attribute predicted, Attribute gold) {
        return predicted.getName().equals(gold.getName()) &&
                toleranceMatchFunctionMap.get(new Pair<Entity, String>(entity, predicted.getName())).isMatch(predicted, gold);
    } 

    public static Map<Pair<Entity,String>,ToleranceMatchFunction> getToleranceMatchingFunctions(RecordCollection collection) {

        Map<Pair<Entity,String>,ToleranceMatchFunction> mapping = new HashMap<Pair<Entity, String>, ToleranceMatchFunction>();
        for (Entity entity: collection.getEntities()) {
            for (String attrName: collection.getAttributes(entity)) {
                Set<Attribute> values = collection.valuesForAttribute(entity,attrName);
                AttributeDataType attributeDataType = Attribute.getDataType(values);
                
                // Current implementation 
                //  STRING == exact match
                //  FLOAT == toleranceFactor * median(values)
                //  TIME == within ten minutes (TODO)
                if (attributeDataType.equals(AttributeDataType.STRING)) {
                    mapping.put(new Pair<Entity, String>(entity,attrName),new ZeroToleranceMatchFunction());
                } else if (attributeDataType.equals(AttributeDataType.FLOAT)) {
                    List<Float> floats = new ArrayList<Float>(values.size());
                    for (Attribute a : values) 
                        floats.add(((FloatAttribute) a).getFloatValue());
                    mapping.put(new Pair<Entity, String>(entity,attrName),new FloatAttributeRangeToleranceMatchFunction(ToleranceDefaults.FLOAT_TOLERANCE_FACTOR * Functions.getMedian(floats)));
                } else {
                   System.err.println("[EvaluationMetricsWithTolerance.getToleranceMatchingFunctions] No know tolerance matching function for attribute data type: " + attributeDataType);
                }
            }
        }
        return mapping;
    }
    
    public ToleranceMatchFunction getToleranceMatchFunction(Entity e, String attrName) {
        return toleranceMatchFunctionMap.get(new Pair<Entity,String>(e,attrName));
    }


}
