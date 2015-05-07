package main.java.edu.umass.cs.data_fusion.algorithm;

import main.java.edu.umass.cs.data_fusion.data_structures.*;
import main.java.edu.umass.cs.data_fusion.util.Functions;
import main.java.edu.umass.cs.data_fusion.util.math.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EntropyWeightedCRH extends CRH {

    protected Map<String,AttributeLossFunction> initializeLossFunctionMap(RecordCollection recordCollection) {
        System.out.println("[EntropyWeightedCRH] Initializing loss functions for all entity-attribute pairs.");
        
        Map<Entity, Map<String,Float>> entropies = InfoTheory.getEntropies(recordCollection);
        
        Map<String, AttributeLossFunction> map = new HashMap<String, AttributeLossFunction>();

        Set<Entity> entities = recordCollection.getEntities();
        int numCompleted = 0;
        int numTotalEntities = recordCollection.getEntitiesCount();
        String countString;

        for (Entity e : entities) {
            if (numCompleted % 100 == 0) {
                countString = "[CRH] Initialized loss function for " + numCompleted + " of " + numTotalEntities + " entities.";
                System.out.println(countString);
            }
            Set<String> attributeNames = recordCollection.getAttributes(e);
            ArrayList<Record> recordsForE = recordCollection.getRecords(e);
            for (String attrName : attributeNames) {

                Map<Attribute, Integer> count = getCount(recordsForE, attrName);
                AttributeType type = Attribute.getType(count.keySet());
                if (type == AttributeType.CATEGORICAL) {
                    float entropy =  entropies.get(e).get(attrName);
                    if (entropy <= 0.0f)
                        entropy = 1.0f;
                    map.put(lossFunctionMapKey(e,attrName), new ZeroKLoss(  1.0f / entropy));
                } else {
                    AttributeDataType dataType = Attribute.getDataType(count.keySet());
                    if (dataType == AttributeDataType.FLOAT) {
                        ArrayList<Float> floats = new ArrayList<Float>(count.size());
                        for (Attribute attr : count.keySet()) {
                            float value = ((FloatAttribute) attr).getFloatValue();
                            for (int i = 0; i < count.get(attr); i++)
                                floats.add(value);
                        }
                        float var = Functions.variance(floats);
                        float entropy =  entropies.get(e).get(attrName);
                        if (entropy <=0.0f)
                            entropy = 1.0f;
                        map.put(lossFunctionMapKey(e,attrName), new WeightedAbsoluteWeightedDeviation((float) Math.sqrt(var) +  0.1f,  entropy)); // add 0.1 as in CRH to prevent div by 0
                    }
                }
            }
            numCompleted++;
        }
        return map;
    }
}
