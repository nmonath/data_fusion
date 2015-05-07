package edu.umass.cs.data_fusion.algorithm;


import edu.umass.cs.data_fusion.data_structures.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MLE extends Algorithm {

    final private double beta1;
    final private double r;
    final private double delta;
    
    final private int MAX_ITERATIONS = 10;
    final private int MIN_ITERATIONS = 10;

    private Source source = new Source(this.getName());

    public MLE () {
        this(0.5,0.5,1.0);
    }
    
    public MLE (double beta1, double r, double delta) {
        super("MLE");
        this.beta1 = beta1;
        this.r = r;
        this.delta = delta;
    }

    @Override
    public ArrayList<Result> execute(RecordCollection recordCollection) {
        System.out.println(infoString(recordCollection));
        HashMap<Source, Double> f = new HashMap<Source, Double>();
        HashMap<Source, Double> aprev = new HashMap<Source, Double>();
        HashMap<Source, Double> bprev = new HashMap<Source, Double>();
        HashMap<Source, Integer> numberOfValuesGivenBySource = new HashMap<Source, Integer>();
        Set<Source> sources = recordCollection.getSources();
        
        int numberOfTotalValues = 0;
        for (Source s : sources) {
            int numValForS = numberOfValuesProvidedBySource(recordCollection, s);
            numberOfValuesGivenBySource.put(s,numValForS);
            f.put(s, (double) numValForS);
            numberOfTotalValues += numValForS;
        }
        for (Source s : sources) {
            f.put(s, f.get(s)/numberOfTotalValues);
            aprev.put(s, r * f.get(s) / beta1);
            bprev.put(s, (1 - r) * f.get(s) / (1 - beta1));
        }

        HashMap<Entity,HashMap<String,HashMap<Attribute,Double>>> confidence = new HashMap<Entity, HashMap<String,HashMap<Attribute,Double>>>();
        
        double C_sum = 0.0;
        boolean converged = false;
        int numIterations = 0;
        String iterationString;



        while ((!converged && numIterations < MAX_ITERATIONS) || numIterations < MIN_ITERATIONS) {
            
            // E Step
            for (Entity entity : recordCollection.getEntities()) {

                iterationString = "[MLE] Number of completed iterations: " + numIterations;
                System.out.println(iterationString);
                
                confidence.put(entity, new HashMap<String, HashMap<Attribute, Double>>());

                Set<String> attributeNames = recordCollection.getAttributes(entity);
                ArrayList<Record> recordsForEntity = recordCollection.getRecords(entity);

                for (String attributeName : attributeNames) {
                    double a_v = 1.0;
                    double b_v = 1.0;
                    confidence.get(entity).put(attributeName, new HashMap<Attribute, Double>());

                    Set<Attribute> valuesForGivenAttribute = valuesForAttribute(recordsForEntity, attributeName);
                    for (Attribute v : valuesForGivenAttribute) {
                        Set<Source> sourcesWithValue = sourcesWithValue(recordsForEntity, v);
                        for (Source s : sourcesWithValue) {
                            a_v *= aprev.get(s);
                            b_v *= bprev.get(s);
                        }
                        for (Source s : sources) {
                            if (!sourcesWithValue.contains(s)) {
                                a_v *= (1 - aprev.get(s));
                                b_v *= (1 - bprev.get(s));
                            }
                        }
                        double c_v = (a_v * beta1) / (a_v * beta1 + b_v * (1 - beta1));
                        confidence.get(entity).get(attributeName).put(v, c_v);
                        C_sum += c_v;
                    }
                }
            }

            HashMap<Source, Double> anext = new HashMap<Source, Double>();
            HashMap<Source, Double> bnext = new HashMap<Source, Double>();
            // M Step
            for (Source s : sources) {
                double C_s_sum = 0.0;
                
                ArrayList<Record> recordsForSource = recordCollection.getRecords(s);
                for (Record r : recordsForSource) {
                    for (Attribute attr : r.getAttributes().values()) {
                        C_s_sum += confidence.get(r.getEntity()).get(attr.getName()).get(attr);
                    }
                }
                anext.put(s, C_s_sum / C_sum);
                bnext.put(s, (numberOfValuesGivenBySource.get(s) - C_s_sum) / (numberOfTotalValues - C_sum));
            }
            converged = hasConverged(aprev, bprev, anext, bnext, delta);
            if (converged)
                System.out.println("[MLE] Convergence condition met");

            numIterations += 1;
        }
        
        /*
        Assign a final value to each attribute of each entity  
         */
        Set<Entity> entities = recordCollection.getEntities();
        ArrayList<Result> results = new ArrayList<Result>(entities.size());
        for (Entity entity: entities) {
            Result res = new Result(this.source, entity);
            Set<String> attributeNames = recordCollection.getAttributes(entity);
            // The value for each attribute it is the one with the highest confidence
            for (String attributeName : attributeNames) {
                double max = Double.MIN_VALUE;
                Attribute bestAttr = null;
                for (Attribute a : confidence.get(entity).get(attributeName).keySet()) {
                    double conf = confidence.get(entity).get(attributeName).get(a);
                    if (conf > max) {
                        max = conf;
                        bestAttr = a;
                    }
                }
                if (max > 0.5)
                    res.addAttribute(bestAttr);
            }
            results.add(res);
        }
        
        return results;
    }

    // TODO: How is this defined?
    private boolean hasConverged(HashMap<Source, Double> aprev, HashMap<Source, Double> bprev, HashMap<Source, Double> anext, HashMap<Source, Double> bnext, double delta) {
        return (TruthFinder.L1dist(aprev,anext) + TruthFinder.L1dist(bprev,bnext)) < delta;
    }
}
