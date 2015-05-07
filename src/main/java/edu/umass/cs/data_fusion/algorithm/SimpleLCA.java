package edu.umass.cs.data_fusion.algorithm;

import edu.umass.cs.data_fusion.data_structures.*;
import edu.umass.cs.data_fusion.data_structures.Entity;
import edu.umass.cs.data_fusion.util.Functions;

import java.util.*;

public class SimpleLCA extends Algorithm {
    
    private double initial_trustworthiness;
    private double beta_1;
    private double delta;
    
    private static final int MAX_ITERATIONS = 100;
    private static final int MIN_ITERATIONS = 10;

    public SimpleLCA() {
        this(0.9,0.5,0.01);
    }

    public SimpleLCA(double initial_trustworthiness, double beta_1, double delta) {
        super("SimpleLCA");
        this.initial_trustworthiness = initial_trustworthiness;
        this.beta_1 = beta_1;
        this.delta = delta;
    }
    
    protected String confidenceMapKey(Entity entity, Attribute attribute) {
        return confidenceMapKey(entity, attribute.getName());
    }

    protected String confidenceMapKey(Entity entity, String attribute) {
        return entity.toString() + "\t" + attribute;
    }
    
    @Override
    public ArrayList<Result> execute(RecordCollection recordCollection) {
        System.out.println(infoString(recordCollection));
        
        Map<Source,Double> trustworthiness = new HashMap<Source, Double>();
        Map<String, Map<Attribute, Double>> confidences = new HashMap<String, Map<Attribute, Double>>();
        
        // Initialize source trusworthiness
        Set<Source> sources = recordCollection.getSources();
        for (Source s : sources) {
            trustworthiness.put(s,Math.log(initial_trustworthiness));
        }

        boolean converged = false;
        int numIterations = 0;
        
        String iterationString;
        
        while ((!converged && numIterations < MAX_ITERATIONS) || numIterations > MIN_ITERATIONS) {

            iterationString = "[SimpleLCA] Number of completed iterations: " + numIterations;
            System.out.println(iterationString);
            
            // E - Step
            System.out.println("[SimpleLCA] Expectation Step");
            confidences = new HashMap<String, Map<Attribute, Double>>();

            double C_sum = 0.0;
            Set<Entity> entities = recordCollection.getEntities();
            Set<String> attrNames = recordCollection.getAttributes();


            int numEntitiesComplete = 0;
            int numEntities = entities.size();
            int printFreq = (int) Math.min(100,numEntities/10);
            
            List<Double> confValues = new ArrayList<Double>(1000);
            
            for (Entity entity : entities) {
                
                if (numEntitiesComplete % printFreq == 0) {
                    System.out.println("[SimpleLCA] Completed " + numEntitiesComplete + " of " + numEntities + " entities");
                }

                List<Record> recordsForEntity = recordCollection.getRecords(entity);

                for (String attrName : attrNames) {
                    confidences.put(confidenceMapKey(entity,attrName), new HashMap<Attribute, Double>());
                    Set<Attribute> values = valuesForAttribute(recordsForEntity, attrName);
                    double numValues = values.size();
                    for (Attribute val : values) {
                        Set<Source> sourcesWithValue = sourcesWithValue(recordsForEntity, val);
                        Set<Source> sourcesWithoutValue = Functions.setMinus(sources, sourcesWithValue);
                        double C_v = Math.log(beta_1);
                        for (Source s : sourcesWithValue) {
                            //C_v *= trustworthiness.get(s);
                            C_v += trustworthiness.get(s);
                        }
                        for (Source s : sourcesWithoutValue) {
                            //C_v *= (1 - trustworthiness.get(s)) / (numValues - 1);
                            double exp = Math.exp(-trustworthiness.get(s));
                            double log =  Math.log(exp-1);
                            C_v += log + trustworthiness.get(s);
                        }
                        confValues.add(C_v);
                        confidences.get(confidenceMapKey(entity,attrName)).put(val, C_v);
                    }
                }
                
                numEntitiesComplete++;
            }
            double log_C_sum = Functions.logSumExp(confValues);
            System.out.println("[SimpleLCA] Completed " + numEntitiesComplete + " of " + numEntities + " entities");
            System.out.println("[SimpleLCA] Normalizing confidence values");
            for (Entity entity : entities) {
                for (String attrName : attrNames) {
                    for (Attribute attr : confidences.get(confidenceMapKey(entity,attrName)).keySet()) {
                        double C_v = confidences.get(confidenceMapKey(entity,attrName)).get(attr) - log_C_sum;
                        confidences.get(confidenceMapKey(entity,attrName)).put(attr, C_v);
                    }
                }
            }
            System.out.println("[SimpleLCA] Expectation Step Complete.");


            // M - Step
            System.out.println("[SimpleLCA] Maximization Step");
            
            Map<Source, Double> nextTrustworthiness = new HashMap<Source, Double>();
            
            int numSourcesComplete = 0;
            int numSources = sources.size();
            printFreq = (int) Math.min(100,numSources/10);
            
            for (Source s : sources) {
                
                if (numSourcesComplete % printFreq == 0) {
                    System.out.println("[SimpleLCA] Completed " + numSourcesComplete + " of " + numSources + " sources");
                }
                
                List<Record> recordsForSource = recordCollection.getRecords(s);
                double T_s = 0.0;
                List<Double> values = new ArrayList<Double>(1000);
                double denominator = 0.0;
                for (Record r : recordsForSource) {
                    Entity entity = r.getEntity();
                    for (Attribute attr : r.getAttributes().values()) {
                        values.add(confidences.get(confidenceMapKey(entity, attr)).get(attr));
                        denominator += 1;
                    }
                }
                T_s = Functions.logSumExp(values) - Math.log(denominator);
                nextTrustworthiness.put(s, T_s);
                
                numSourcesComplete++;
            }
            System.out.println("[SimpleLCA] Maximization Step Complete.");
            numIterations++;
            
            converged = converged(trustworthiness,nextTrustworthiness,delta);
            if (converged)
                System.out.println("[SimpleLCA] Convergence Condition Met.");
            // update the trustworthiness
            trustworthiness = nextTrustworthiness;
        }
        System.out.println("[SimpleLCA] EM Algorithm Complete. Collecting the results.");

       ArrayList<Result> results = new ArrayList<Result>(recordCollection.getEntitiesCount());
        
        for (Entity entity : recordCollection.getEntities()) {
            Result res = new Result(this.source,entity);
            for (String attrName : recordCollection.getAttributes()) {
                double bestConfidence = Double.MIN_VALUE;
                Attribute bestAttribute = null;
                Map<Attribute,Double> submap = confidences.get(confidenceMapKey(entity,attrName));
                for (Attribute attr : submap.keySet()) {
                    double C_v = submap.get(attr);
                    if (C_v > bestConfidence) {
                        bestConfidence = C_v;
                        bestAttribute = attr;
                    }
                }
                if (bestAttribute != null)
                    res.addAttribute(bestAttribute);
            }
        }
        System.out.println("[SimpleLCA] Done collecting the results.");
        return results;
    }


    public static <T> double L1dist(Map<T,Double> previous, Map<T,Double> current) {
        assert previous.keySet().containsAll(current.keySet());
        double[] previousVec = new double[previous.size()];
        double[] currentVec = new double[previous.size()];
        int i = 0;
        for (T s: previous.keySet()) {
            previousVec[i] = previous.get(s);
            try {
                currentVec[i] = current.get(s);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(s.toString());
            }
            i += 1;
        }
        double dd = Functions.L1dist(previousVec, currentVec);
        System.out.println("[TruthFinder] L1 Distance: " + dd);
        return dd;
    }

    public static <T> boolean converged(Map<T,Double> previous, Map<T,Double> current, double delta) {
        return L1dist(previous,current) < delta;
    }
    
}
