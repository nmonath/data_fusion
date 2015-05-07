package edu.umass.cs.data_fusion.algorithm;


import edu.umass.cs.data_fusion.data_structures.*;
import edu.umass.cs.data_fusion.util.Functions;
import edu.umass.cs.data_fusion.util.math.AbsoluteWeightedDeviation;
import edu.umass.cs.data_fusion.util.math.AttributeLossFunction;
import edu.umass.cs.data_fusion.util.math.ZeroOneLoss;

import java.util.*;

public class CRH extends Algorithm {

    protected double delta = 5;
    
    final protected int MAX_ITERATIONS = 1000;
    final protected int MIN_ITERATIONS = 10;


    public CRH() {
        super("CRH");
    }

    protected AttributeLossFunction categoricalLoss = new ZeroOneLoss();

    // EntityId \t AttrName => LossFn
    protected Map<String, AttributeLossFunction> lossFunctionMap;
    
    protected Map<String,AttributeLossFunction> initializeLossFunctionMap(RecordCollection recordCollection) {
        System.out.println("[CRH] Initializing loss functions for all entity-attribute pairs.");

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
                    map.put(lossFunctionMapKey(e,attrName), categoricalLoss);
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
                        map.put(lossFunctionMapKey(e,attrName), new AbsoluteWeightedDeviation((float) Math.sqrt(var) +  0.1f)); // add 0.1 as in CRH to prevent div by 0
                    }
                }
            }
            numCompleted++;
        }
        return map;
    }

    protected String lossFunctionMapKey(Entity e, String attrName) {
        return e.getIdentifier() + "\t" + attrName;
    }
    protected String lossFunctionMapKey(Entity e, Attribute a) {
        return lossFunctionMapKey(e,a.getName());
    }
    
    protected String weightsMapKey(Source s, String attrName) {
        return s.getName();
    }
    
    public Map<Entity,Record> initializePredictions(RecordCollection recordCollection) {
        Map<Entity,Record> prediction = new HashMap<Entity, Record>(recordCollection.getEntitiesCount());
        
        // Initialize categorical attributes to majority vote value
        // Initialize continuous attributes to the median & record the standard deviation

        Set<Entity> entities = recordCollection.getEntities();
        int numCompleted = 0;
        int numTotalEntites = recordCollection.getEntitiesCount();
        String countString;

        for (Entity e: entities) {
            if (numCompleted % 100 == 0) {
                countString = "[CRH] Initialized values for " + numCompleted + " of " + numTotalEntites + " entities.";
                System.out.println(countString);
            }
            Record initialPrediction = new Record(source,e);
            Set<String> attributeNames = recordCollection.getAttributes(e);
            ArrayList<Record> recordsForE = recordCollection.getRecords(e);
            for (String attrName : attributeNames) {

                Map<Attribute,Integer> count = getCount(recordsForE, attrName);
                AttributeType type = Attribute.getType(count.keySet());
                if (type == AttributeType.CATEGORICAL) {
                    initialPrediction.addAttribute(getMajorityVote(count));
                } else {
                    AttributeDataType dataType = Attribute.getDataType(count.keySet());
                    if (dataType == AttributeDataType.FLOAT) {
                        ArrayList<Float> floats = new ArrayList<Float>(count.size());
                        for (Attribute attr : count.keySet()) {
                            float value = ((FloatAttribute) attr).getFloatValue();
                            for (int i = 0; i < count.get(attr); i++)
                                floats.add(value);
                        }
                        float median = Functions.getMedian(floats);
                        initialPrediction.addAttribute(new FloatAttribute(attrName,median,type));
                    }
                }
            }
            prediction.put(e,initialPrediction);
            numCompleted++;
        }
        if (numCompleted % 100 == 0) {
            countString = "[CRH] Initialized values for " + numCompleted + "of " + numTotalEntites + " entities.";
            System.out.println(countString);
        }
        System.out.println("\n[CRH] Initialization complete.");
        return prediction;
    }
    
    public Map<String,Float> initializeWeights(RecordCollection recordCollection) {
        Set<Source> sources = recordCollection.getSources();
        Map<String,Float> weights = new HashMap<String, Float>(sources.size());
        int numSources = sources.size();
        for (Source s: sources) {
            weights.put(weightsMapKey(s,""),1.0f/numSources);
        }
        return weights;
    }

    
    public Map<String,Float> updateWeights(Map<Entity,Record> prediction, RecordCollection recordCollection) {
        Map<String,Float> categoricalLosses = new HashMap<String, Float>();
        Map<String,Float> continuousLosses = new HashMap<String, Float>();
        
        int numComplete = 0;
        int numSources = recordCollection.getSourcesCount();
        String updateStr = "[CRH] Computed losses for " + numComplete + " of " + numSources+  " sources";
        
        
        for (Source source: recordCollection.getSources() ) {
            System.out.println(updateStr);
            // initialize 
            categoricalLosses.put(source.getName(),0.0f);
            continuousLosses.put(source.getName(),0.0f);
            
            int numberOfCategoricalAttributes = 0;
            int numberOfContinuousAttributes = 0;
            
            for (Record record : recordCollection.getRecords(source)) {
                Record currentPrediction = prediction.get(record.getEntity());
                for (Attribute predAttr: currentPrediction.getAttributes().values()) {
                    if (record.hasAttribute(predAttr.getName())) {
                        Attribute recordAttr = record.getAttribute(predAttr.getName());
                        if (predAttr.getType() == AttributeType.CATEGORICAL && recordAttr.getType() == AttributeType.CATEGORICAL) {
                            categoricalLosses.put(source.getName(), categoricalLosses.get(source.getName()) + lossFunctionMap.get(lossFunctionMapKey(record.getEntity(), predAttr)).loss(recordAttr, predAttr));
                            numberOfCategoricalAttributes += 1;
                        } else if (predAttr.getType() == AttributeType.CONTINUOUS && recordAttr.getType() == AttributeType.CONTINUOUS) {
                            continuousLosses.put(source.getName(), continuousLosses.get(source.getName()) + lossFunctionMap.get(lossFunctionMapKey(record.getEntity(),predAttr)).loss(recordAttr, predAttr));
                            numberOfContinuousAttributes += 1;
                        } else {
                            System.out.println("[CRH] ERROR: Something has gone wrong, attributes with the same name have different types.");
                        }
                    }
                }
            }
            
            if (numberOfCategoricalAttributes > 0)
                categoricalLosses.put(source.getName(), categoricalLosses.get(source.getName())/numberOfCategoricalAttributes);
            if (numberOfContinuousAttributes > 0)
                continuousLosses.put(source.getName(), continuousLosses.get(source.getName())/numberOfContinuousAttributes);
            numComplete += 1;
            updateStr = "[CRH] Computed losses for " + numComplete + " of " + numSources+  " sources";
        }
        System.out.println(updateStr);
        return toWeights(categoricalLosses,continuousLosses);
    }
    
    protected <T> Map<T,Float> toWeights(Map<T,Float> categorical,Map<T,Float> continuous) {
        normalizeByMax(normalizeBySum(categorical));
        normalizeByMax(normalizeBySum(continuous));
        Map<T,Float> weights = new HashMap<T, Float>();
        for (T s: categorical.keySet())
            weights.put(s,categorical.get(s));
        for (T s : continuous.keySet()) {
            if (!weights.containsKey(s))
                continuous.put(s,0.0f);
            weights.put(s,continuous.get(s) + weights.get(s));
        }
        for (T s : weights.keySet()) {
            weights.put(s,weights.get(s) +  + 0.00001f); // Prevent a weight ever being 0
        }
        normalizeByMax(weights);
        Map<T,Float> newWeights = new HashMap<T, Float>(weights.size());
        for (T s : weights.keySet())
            newWeights.put(s,-1.0f * (float) Math.log(weights.get(s)) + 0.00001f);
        return newWeights;
    }
    
    private <T> Map<T,Float> normalizeBySum(Map<T,Float> map) {
        ArrayList<Float> floats = new ArrayList<Float>(map.values());
        float sum = Functions.sum(floats);
        if (sum > 0.0) {
            for (T e: map.keySet()) {
                map.put(e, map.get(e)/sum);
            }
        }
        return map;
    }

    private <T> Map<T,Float> normalizeByMax(Map<T,Float> map) {
        ArrayList<Float> floats = new ArrayList<Float>(map.values());
        float max = Functions.maxFloats(floats);
        if (max > Float.MIN_VALUE) {
            for (T e: map.keySet()) {
                map.put(e, map.get(e)/max);
            }
        }
        return map;
    }

    public Map<Entity, Record> updatePrediction(Map<Entity,Record> prediction, Map<String,Float> weights, RecordCollection recordCollection) {

        Map<Entity, Record> nextPrediction = new HashMap<Entity, Record>(prediction.size());
        Set<Entity> entities = recordCollection.getEntities();
        int numCompleted = 0;
        int numTotalEntities = recordCollection.getEntitiesCount();
        String countString;
        
        for (Entity e : entities) {
            if (numCompleted % 100 == 0) {
                countString = "[CRH] Updated predictions for " + numCompleted + " of " + numTotalEntities + " entities.";
                System.out.println(countString);
            }
            Record newPrediction = new Record(source, e);
            Set<String> attributeNames = recordCollection.getAttributes(e);
            ArrayList<Record> recordsForE = recordCollection.getRecords(e);
            for (String attrName : attributeNames) {

                Map<Attribute, Float> count = getWeightedCount(recordsForE, attrName, weights);
                AttributeType type = Attribute.getType(count.keySet());
                
                if (type == AttributeType.CATEGORICAL) {
                    newPrediction.addAttribute(getMajorityWeightedVote(count));
                } else if (type == AttributeType.CONTINUOUS) {
                    AttributeDataType dataType = Attribute.getDataType(count.keySet());
                    if (dataType == AttributeDataType.FLOAT)
                        newPrediction.addAttribute(new FloatAttribute(attrName, getWeightedMedian(recordsForE, attrName, weights),AttributeType.CONTINUOUS));
                    else
                        System.err.println("[CRH] No support for non-float continuous attributes, cannot predict values.");
                } else {
                    System.err.println("[CRH][updatePrediction] Unknown type for attributes");
                }
            }
            nextPrediction.put(e,newPrediction);
            numCompleted++;
        }
        System.out.println("[CRH] Finished updating predictions");
        return nextPrediction;
    }
    
    public float getWeightedMedian(List<Record> records, String attrName, Map<String, Float> weights) {

        List<Float> values = new ArrayList<Float>();
        List<Float> wghts = new ArrayList<Float>();
        for (Record r: records) {
            if (r.hasAttribute(attrName)) {
                values.add(((FloatAttribute) r.getAttribute(attrName)).getFloatValue());
                wghts.add(weights.get(weightsMapKey(r.getSource(), attrName)));
            }
        }
        return Functions.weightedMedian(wghts,values);
    }

    public float objectiveFunction(Map<Entity,Record> prediction, Map<String,Float> weights, RecordCollection recordCollection) {
        float objective = 0.0f;
        for (Entity entity : recordCollection.getEntities()) {
            
            Record predictedRecord = prediction.get(entity);
            
            for (Record record : recordCollection.getRecords(entity)) {
                for (String attrName : record.getAttributes().keySet()) { // TODO: It's unclear to me from the paper if we penalize for missing attr values.

                    Attribute predictedAttr = predictedRecord.getAttribute(attrName);
                    Attribute thisRecordAttr = record.getAttribute(attrName);

                    objective += weights.get(weightsMapKey(record.getSource(),attrName)) * lossFunctionMap.get(lossFunctionMapKey(entity, predictedAttr)).loss(predictedAttr, thisRecordAttr);

                }
                
            }
        }
        return objective;
    }



    /**
     * Each source has an associated weight, rather than giving each source a 1 vote
     * when it provides a value, the weight of the vote is the source's associated weight.
     * @param records - the list of data records
     * @param attrName - the name of the attribute
     * @param weights - the source weights
     * @return - the weighted count of values of the attribute with the given name.
     */
    public HashMap<Attribute, Float> getWeightedCount(List<Record> records, String attrName, Map<String,Float> weights) {
        HashMap<Attribute,Float> votes = new HashMap<Attribute, Float>();
        for (Record r: records) {
            if (r.hasAttribute(attrName)) {
                Attribute a = r.getAttribute(attrName);
                if (!votes.containsKey(a)) {
                    votes.put(a,0.0f);
                }
                votes.put(a,votes.get(a)+ weights.get(weightsMapKey(r.getSource(),attrName)));
            }
        }
        return votes;
    }

    /**
     * Selects the value of the attribute with the highest weighted vote
     * @param votes - the votes from getWeightedCount
     * @return - the highest weighted vote
     */
    public Attribute getMajorityWeightedVote(Map<Attribute,Float> votes) {
        float max = Float.MIN_VALUE;
        Attribute maxAttr = null;
        for (Attribute a : votes.keySet()) {
            float aCount = votes.get(a);
            if (aCount > max) {
                max = aCount;
                maxAttr = a;
            }
        }
        return maxAttr;
    }
    
    
    @Override
    public ArrayList<Result> execute(RecordCollection recordCollection) {
        System.out.println(infoString(recordCollection));
        lossFunctionMap = initializeLossFunctionMap(recordCollection);
        
        Map<Entity,Record> predictedTruth = initializePredictions(recordCollection);
        Map<String,Float> weights = initializeWeights(recordCollection);
        
        boolean converged = false;
        int numIters = 0;

        String iterString;
        
        float prevObjective;
        float objective = 0.0f;
        float change;

        while ((!converged && numIters < MAX_ITERATIONS) || numIters < MIN_ITERATIONS) {
            
            iterString = "[CRH] Number of completed iterations  " + numIters;
            System.out.println(iterString);

            Map<String,Float> prevWeights = new HashMap<String, Float>();
            prevWeights.putAll(weights);
            weights = updateWeights(predictedTruth,recordCollection);
            predictedTruth = updatePrediction(predictedTruth,weights,recordCollection);
            numIters++;
            
            prevObjective = objective;
            objective = objectiveFunction(predictedTruth,weights,recordCollection);
            change = Math.abs(objective - prevObjective);
            System.out.println("[CRH] Objective Function Score: " + objective + ". Change: " + change);
            converged = change < delta;
        }
        if (converged)
            System.out.println("[CRH] Convergence condition met.");
        if (numIters > MAX_ITERATIONS)
            System.out.println("[CRH] Max Iterations condition met.");
        
        // Give final result
        ArrayList<Result> results = new ArrayList<Result>();
        for (Record r : predictedTruth.values()) {
            results.add(new Result(r.getSource(),r.getEntity(),r.getAttributes()));
        }
        return results;
    }
    
}
