package main.java.edu.umass.cs.data_fusion.algorithm;

import main.java.edu.umass.cs.data_fusion.data_structures.*;

import java.util.*;

public class ModifiedCRH extends CRH {

    @Override
    public Map<String,Float> initializeWeights(RecordCollection recordCollection) {
        Set<Source> sources = recordCollection.getSources();
        Map<String,Float> weights = new HashMap<String, Float>(sources.size());
        int numSources = sources.size();
        Set<String> attributes = recordCollection.getAttributes();
        int denominator = numSources * attributes.size();
        for (Source s: sources) {
            for (String attrName : attributes)
                weights.put(weightsMapKey(s, attrName),1.0f/denominator);
        }
        return weights;
    }


    @Override
    protected String weightsMapKey(Source source, String attrName) {
        return source.toString() + "\t" + attrName;
    }

    @Override
    public Map<String,Float> updateWeights(Map<Entity,Record> prediction, RecordCollection recordCollection) {
        return updateWeights(prediction, recordCollection, recordCollection.getAttributes());
    }

    public Map<String,Float> updateWeights(Map<Entity,Record> prediction, RecordCollection recordCollection, Set<String> allAttributes) {
        Map<String,Float> categoricalLosses = new HashMap<String, Float>();
        Map<String,Float> continuousLosses = new HashMap<String, Float>();

        int numComplete = 0;
        int numSources = recordCollection.getSourcesCount();
        String updateStr = "[ModifiedCRH] Computed losses for " + numComplete + " of " + numSources+  " sources";


        for (Source source: recordCollection.getSources() ) {
            System.out.println(updateStr);
            // initialize
            Map<String,Integer> numberOfCategoricalAttributes = new HashMap<String, Integer>();
            Map<String,Integer> numberOfContinuousAttributes = new HashMap<String, Integer>();

            for (String attrName : allAttributes) {
                categoricalLosses.put( weightsMapKey(source, attrName), 0.0f);
                continuousLosses.put( weightsMapKey(source, attrName), 0.0f);
                numberOfCategoricalAttributes.put(attrName,0);
                numberOfContinuousAttributes.put(attrName,0);
            }

            for (Record record : recordCollection.getRecords(source)) {
                Record currentPrediction = prediction.get(record.getEntity());
                for (Attribute predAttr: currentPrediction.getAttributes().values()) {
                    if (record.hasAttribute(predAttr.getName())) {
                        Attribute recordAttr = record.getAttribute(predAttr.getName());
                        String attrName = predAttr.getName();
                        String key = weightsMapKey(source, predAttr.getName());
                        if (predAttr.getType() == AttributeType.CATEGORICAL && recordAttr.getType() == AttributeType.CATEGORICAL) {
                            categoricalLosses.put(key, categoricalLosses.get(key) + lossFunctionMap.get(lossFunctionMapKey(record.getEntity(), predAttr)).loss(recordAttr, predAttr));
                            numberOfCategoricalAttributes.put(attrName, numberOfCategoricalAttributes.get(attrName) + 1);
                        } else if (predAttr.getType() == AttributeType.CONTINUOUS && recordAttr.getType() == AttributeType.CONTINUOUS) {
                            continuousLosses.put(key, continuousLosses.get(key) + lossFunctionMap.get(lossFunctionMapKey(record.getEntity(),predAttr)).loss(recordAttr, predAttr));
                            numberOfContinuousAttributes.put(attrName, numberOfContinuousAttributes.get(attrName) + 1);
                        } else {
                            System.out.println("[ModifiedCRH] ERROR: Something has gone wrong, attributes with the same name have different types.");
                        }
                    }
                }
            }
            for (String attrName : allAttributes) {
                String key = weightsMapKey(source, attrName);
                if (numberOfCategoricalAttributes.get(attrName) > 0)
                    categoricalLosses.put(key, categoricalLosses.get(key) / numberOfCategoricalAttributes.get(attrName));
                if (numberOfContinuousAttributes.get(attrName) > 0)
                    continuousLosses.put(key, continuousLosses.get(key) / numberOfContinuousAttributes.get(attrName));
            }
            numComplete += 1;
            updateStr = "[ModifiedCRH] Computed losses for " + numComplete + " of " + numSources+  " sources";
        }
        System.out.println(updateStr);
        return toWeights(categoricalLosses,continuousLosses);
    }
}
