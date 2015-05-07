package edu.umass.cs.data_fusion.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.umass.cs.data_fusion.data_structures.Algorithm;
import edu.umass.cs.data_fusion.data_structures.Attribute;
import edu.umass.cs.data_fusion.data_structures.Entity;
import edu.umass.cs.data_fusion.data_structures.RecordCollection;
import edu.umass.cs.data_fusion.data_structures.Record;
import edu.umass.cs.data_fusion.data_structures.Result;
import edu.umass.cs.data_fusion.data_structures.Source;
import edu.umass.cs.data_fusion.util.Functions;



public class TwoEstimates extends Algorithm {
	private double initialTrustworthiness;
    private double lambda;
    private double delta;

    final private double MAX_ITERATIONS = 1000;
    final private double MIN_ITERATIONS = 10;

    private Source source = new Source(this.getName());
    
    public TwoEstimates() {
        super("TwoEstimates");
        this.initialTrustworthiness = 0.8;
        this.delta = 0.001;
        this.lambda = 0.1;
    }
    
    public TwoEstimates(double initialTrustworthiness, double delta, double lambda) {
        super("TwoEstimates");
        this.initialTrustworthiness = initialTrustworthiness;
        this.delta = delta;
        this.lambda = lambda;
    }
    
    @Override
    public ArrayList<Result> execute(RecordCollection collection) {

        HashMap<Entity,HashMap<String,HashMap<Attribute,Double>>> confidence = new HashMap<Entity, HashMap<String,HashMap<Attribute,Double>>>();
        
        HashMap<Source, Double> previousTrustworthiness = new HashMap<Source, Double>();

        Set<Source> sources = collection.getSources();
        
        //init
        for (Source s : sources) {
            previousTrustworthiness.put(s,initialTrustworthiness);
        }
        
        boolean converged = false;
        
        int numIterations = 0;
        
        String iterationString;
        
        
        while ((!converged && numIterations < MAX_ITERATIONS) || numIterations < MIN_ITERATIONS) {
        	
        	iterationString = "[2-Estimates] Number of completed iterations: " + numIterations;
        	System.out.println(iterationString);
        	
        	// confidence value calculation
        	ArrayList<Double> confvalues  = new ArrayList<Double>();
        	
        	//iterate entities
            for (Entity entity : collection.getEntities()) {
                
                confidence.put(entity, new HashMap<String, HashMap<Attribute, Double>>());
                               
                Set<String> attributeNames = collection.getAttributes(entity);
                
                ArrayList<Record> recordsForEntity = collection.getRecords(entity);
                
        	    int numSourcesInEntities = numSourcesWithinEntity(recordsForEntity);
                
                //iterate attrs
                for (String attributeName : attributeNames) {
                    
                    confidence.get(entity).put(attributeName, new HashMap<Attribute, Double>());
                    
                    HashMap<Attribute, Double> pos = new HashMap<Attribute, Double>();
                    
                    HashMap<Attribute, Double> neg = new HashMap<Attribute, Double>();
                                        
                    Set<Attribute> valuesForGivenAttribute = valuesForAttribute(recordsForEntity, attributeName);
                    
                    
                    
                    //Calculated the pos and neg for each value                      
                    for (Attribute v : valuesForGivenAttribute) {
                        // pos <- sum_{s in SourcesProvidingV} (1-Trustworthiness(s)) 
                        // neg <- sum_{s in SourcesNOTProvidingV} (Trustworthiness(s))
                        // conf <- (neg+pos) / | Sources of this entity |
                        Set<Source> sourcesWithValue = sourcesWithValue(recordsForEntity, v);
                        Set<Source> sourcesWithOutValue = sourcesWithOutValue(recordsForEntity, v);
                        double posV = 0.0;
                        double negV = 0.0;
                        for (Source s : sourcesWithValue) {
                        	posV += (1 - previousTrustworthiness.get(s));
                        }
                        for (Source s : sourcesWithOutValue) {
                        	negV += previousTrustworthiness.get(s);
                        }
                        
                        pos.put(v, posV);
                        neg.put(v, negV);

                        //calculate new confidence
                        //TODO: correct? assuming that no source provides duplicates
                        int nbrSourcesOfEntity = recordsForEntity.size();
                        
                        double conf = (negV + posV) / (double)numSourcesInEntities;
                        confidence.get(entity).get(attributeName).put(v, conf);
                        confvalues.add(conf);
                    }
                    
                }
            }
            
            //normalize
        	confidence = ThreeEstimate.normalize(confvalues,confidence, this.lambda);
            
            
            //calc this round trustworthyness
            HashMap<Source, Double> currentTrustworthiness = new HashMap<Source, Double>();
        	ArrayList<Double> currentTrustworthinessValues  = new ArrayList<Double>();
            for (Source s : sources) {
            	ArrayList<Record> recordsForSource = collection.getRecords(s);
            	
                //calculate
                // pos <- sum_{v in ValuesGivenBySource} (1 - confidence(v))
                // neg <- sum_{v in ValuesNOTGivenBySource} (confidence(v))
            	double pos = 0.0;
            	double neg = 0.0;
            	int nbrFacts = 0;
            	for (Record r : recordsForSource) {
                    for (Attribute a: r.getAttributes().values()) {
                    	//is attr of source same as confidence attr?
                    
                    	//yes
                    	if(a.equals(confidence.get(r.getEntity()).get(a.getName())))
                    	{
                    		pos += (1 - confidence.get(r.getEntity()).get(a.getName()).get(a));
                    	}
                    	else
                    	{
                    		//no
                    		neg += confidence.get(r.getEntity()).get(a.getName()).get(a);
                    	}
                    	nbrFacts++;
                    }
                }
            	
            	//calculate new T_s values
            	double curTrust = (pos+neg)/(double)nbrFacts;
            	currentTrustworthiness.put(s,curTrust);
            	currentTrustworthinessValues.add(curTrust);
            }
            
            //normalize 
            currentTrustworthiness = ThreeEstimate.normalizetrustworthiness(currentTrustworthinessValues,currentTrustworthiness,this.lambda);
            previousTrustworthiness = currentTrustworthiness;
            
            //converged?
            converged = converged(previousTrustworthiness,currentTrustworthiness,delta);
            if (converged)
                System.out.println("[2-Estimates] Convergence condition met.");
            numIterations += 1;
        }
        
        //get final values
        Set<Entity> entities = collection.getEntities();
        ArrayList<Result> results = new ArrayList<Result>(entities.size());
        for (Entity entity: entities) {
            Result res = new Result(this.source, entity);
            Set<String> attributeNames = collection.getAttributes(entity);
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
                if (bestAttr != null)
                    res.addAttribute(bestAttr);
            }
            results.add(res);
        }
        System.out.println("[2-Estimates] Done.");
        return results;
    }
    
    
    public double L1dist(HashMap<Source,Double> previous, HashMap<Source,Double> current) {
        assert previous.keySet().containsAll(current.keySet());
        double[] previousVec = new double[previous.size()];
        double[] currentVec = new double[previous.size()];
        int i = 0;
        for (Source s: previous.keySet()) {
            previousVec[i] = previous.get(s);
            currentVec[i] = current.get(s);
            i += 1;
        }
        double dd = Functions.L1dist(previousVec, currentVec);
        return dd;
    }
	
	public boolean converged(HashMap<Source,Double> previous, HashMap<Source,Double> current, double delta) {
        return L1dist(previous,current) < delta;
    }
    
}
