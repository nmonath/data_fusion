package edu.umass.cs.data_fusion.algorithm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.umass.cs.data_fusion.data_structures.*;
import edu.umass.cs.data_fusion.util.Functions;


public class ThreeEstimate extends Algorithm{

    private double initialTrustworthiness;
    private double delta;
    private double lamda;
    private double epsilon;
    
    final private double MAX_ITERATIONS = 1000;
    final private double MIN_ITERATIONS = 10;
	
    public ThreeEstimate(double initialTrustworthiness, double delta, double lamda,double epsilon) {
        super("ThreeEstimates");
        this.initialTrustworthiness = initialTrustworthiness;
        this.delta = delta;
        this.lamda = lamda;
        this.epsilon = epsilon;
    }
    
    public ThreeEstimate() {
        super("ThreeEstimates");
        this.initialTrustworthiness = 0.8; 
        this.delta = 0.001;
        this.lamda = 0.9;
        this.epsilon = 0.1;
    }
	
	public ArrayList<Result> execute(RecordCollection collection) {
		
		HashMap<Source, Double> previousTrustworthiness = new HashMap<Source, Double>();
	    HashMap<Entity,HashMap<String,HashMap<Attribute,Double>>> errorFactor = new HashMap<Entity, HashMap<String,HashMap<Attribute,Double>>>();
		Set<Source> sources = collection.getSources();
		Set<Entity> entities = collection.getEntities();
		
		//initialization
        for (Source s : sources) {
        	previousTrustworthiness.put(s,initialTrustworthiness);
        }
        for (Entity entity : entities) {
        	errorFactor.put(entity, new HashMap<String, HashMap<Attribute, Double>>());
        	Set<String> attributeNames = collection.getAttributes(entity);
        	ArrayList<Record> recordsForEntity = collection.getRecords(entity);
        	
            for (String attributeName : attributeNames) {
            	errorFactor.get(entity).put(attributeName, new HashMap<Attribute, Double>());
            	Set<Attribute> valuesForGivenAttribute = valuesForAttribute(recordsForEntity, attributeName);
            	for (Attribute v : valuesForGivenAttribute) {
            		errorFactor.get(entity).get(attributeName).put(v, epsilon);
            	}
            	
            }
        	
        }
        
        HashMap<Entity,HashMap<String,HashMap<Attribute,Double>>> confidence = new HashMap<Entity, HashMap<String,HashMap<Attribute,Double>>>();
        boolean hasConverged = false;
        int numIterations = 0;
        String iterationString;
        //while (numIterations < MIN_ITERATIONS) {
        while(numIterations < MIN_ITERATIONS){
            iterationString = "[3 Estimates] Number of completed iterations: " + numIterations;
            System.out.println(iterationString);
        	// confidence value calculation
        	ArrayList<Double> confvalues  = new ArrayList<Double>();
        	for (Entity entity : entities) {
        		confidence.put(entity, new HashMap<String, HashMap<Attribute, Double>>());
        	    Set<String> attributeNames = collection.getAttributes(entity);
        	    ArrayList<Record> recordsForEntity = collection.getRecords(entity);
        	    int numSourcesInEntities = numSourcesWithinEntity(recordsForEntity);
        	    numSourcesInEntities = Math.max(1, numSourcesInEntities);
                for (String attributeName : attributeNames) {

                	confidence.get(entity).put(attributeName, new HashMap<Attribute, Double>());
                	Set<Attribute> valuesForGivenAttribute = valuesForAttribute(recordsForEntity, attributeName);
                    for (Attribute v : valuesForGivenAttribute) {
                        double pos = 0.0;
                        double neg = 0.0;
                    	Set<Source> sourcesWithValue = sourcesWithValue(recordsForEntity, v);
                    	
                    	for (Source s : sourcesWithValue) {
                    		pos += (1-(previousTrustworthiness.get(s) * errorFactor.get(entity).get(attributeName).get(v)));
                    	}
                        for (Source s : sources) {
                            if (!sourcesWithValue.contains(s)) {
                            	neg +=previousTrustworthiness.get(s) * errorFactor.get(entity).get(attributeName).get(v);
                            }
                        }
                        
                        double conf = (pos+neg)/(double)numSourcesInEntities;
                        confidence.get(entity).get(attributeName).put(v,conf);
                        confvalues.add(conf);
                        //System.out.println("conf "+conf);
                    }
                }
            }
        	
        	// normalize confidence values
        	confidence = normalize(confvalues,confidence,this.lamda);
        	
        	
        	//error factor value calculation
        	ArrayList<Double> errorvalues  = new ArrayList<Double>();
        	for (Entity entity : entities) {
        		ArrayList<Record> recordsForEntity = collection.getRecords(entity);
        		int norm = numSourcesWithinEntityWithNonZeroTrustWorthiness(recordsForEntity,previousTrustworthiness);
        		norm = Math.max(norm,1);
        		Set<String> attributeNames = collection.getAttributes(entity);
        		for (String attributeName : attributeNames) {
        			Set<Attribute> valuesForGivenAttribute = valuesForAttribute(recordsForEntity, attributeName);
        			for (Attribute v : valuesForGivenAttribute) {
                        double pos = 0.0;
                        double neg = 0.0;
                        Set<Source> sourcesWithValue = sourcesWithValue(recordsForEntity, v);
                    	for (Source s : sourcesWithValue) {
                    		if(previousTrustworthiness.get(s)!=0){
                    		    pos += (1-(confidence.get(entity).get(attributeName).get(v)))/previousTrustworthiness.get(s);
                    		}
                    	}
                        for (Source s : sources) {
                            if (!sourcesWithValue.contains(s)) {
                            	if(previousTrustworthiness.get(s)!=0){
                            	   neg += (confidence.get(entity).get(attributeName).get(v))/previousTrustworthiness.get(s);
                            	}
                            }
                        }
                        double error = (pos+neg)/(double)norm;
                        errorFactor.get(entity).get(attributeName).put(v,error);
                        errorvalues.add(error);
                        //System.out.println("Error "+error);
                   }
        		}
        	}
        	
        	//normalize error factor values
        	errorFactor = normalize(errorvalues,errorFactor, this.lamda);
        	
        	//source trustworthiness value calculation
        	HashMap<Source, Double> newsourceTrustworthiness = new HashMap<Source, Double>();
        	ArrayList<Double> trustWorthinessvalues  = new ArrayList<Double>();
        	for(Source s: sources){
        		double pos = 0.0;
        		double neg = 0.0;
        		int norm = 0;
        		HashMap<Entity,HashMap<String,HashSet<Attribute>>>  valuesFromSource = valuesProvidedBySource(collection,s);
        		for (Map.Entry<Entity,HashMap<String,HashSet<Attribute>>> entityRecords : valuesFromSource.entrySet()) {
        			Entity e = entityRecords.getKey();
        			for(Map.Entry<String,HashSet<Attribute>> attributes:entityRecords.getValue().entrySet()){
        				String attributeName = attributes.getKey();
        				
        				for(Attribute a : attributes.getValue()){
        				   double error = errorFactor.get(e).get(attributeName).get(a);
        				   if(error!=0.0){
        					   pos += (1-confidence.get(e).get(attributeName).get(a))/error;
        				   }
        				   
        				}
        			}
        			
        			ArrayList<Record> recordsForEntity = collection.getRecords(e);
        			Set<String> attributeNames = collection.getAttributes(e);
        			for (String attributeName : attributeNames) {
            			Set<Attribute> valuesForGivenAttribute = valuesForAttribute(recordsForEntity, attributeName);
            			for (Attribute v : valuesForGivenAttribute) {
            				
            				Set<Source> sourcesWithValue = sourcesWithValue(recordsForEntity, v);
            				double error = errorFactor.get(e).get(attributeName).get(v);
            				if(error!=0){
            					norm+=1;
            				}
            				if(!sourcesWithValue.contains(s) && error!=0.0){
            					neg += confidence.get(e).get(attributeName).get(v)/error ;
            				}
            			}
        			}
        			
        			
        		}
        		norm = Math.max(1, norm);
        		double trustworthiness = (pos+neg)/(double)norm;
        		newsourceTrustworthiness.put(s,trustworthiness);
        		trustWorthinessvalues.add(trustworthiness);
        		//System.out.println("Trustworthiness "+trustworthiness);
        	}
        	
        	//normalize trustworthiness
        	newsourceTrustworthiness = normalizetrustworthiness(trustWorthinessvalues,newsourceTrustworthiness,this.lamda);
        	
        	//test for convergence
        	hasConverged = converged(previousTrustworthiness,newsourceTrustworthiness,delta);
            if (hasConverged)
                System.out.println("[3 Estimates] Convergence condition met.");
            previousTrustworthiness = newsourceTrustworthiness;
            numIterations += 1;
	
        }
        
        if (!hasConverged)
            System.out.println("[3 Estimates] Max Iterations condition met.");

        System.out.println("[3 Estimates] Algorithm complete, assigning values to each attribute.");
        
        
        ArrayList<Result> results = new ArrayList<Result>(entities.size());
        for (Entity entity: entities) {
            Result res = new Result(this.source, entity);
            Set<String> attributeNames = collection.getAttributes(entity);
            
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
        System.out.println("[3 Estimates] Done.");
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
    
    
	
    public static HashMap<Entity,HashMap<String,HashMap<Attribute,Double>>> normalize(ArrayList<Double> listValues, HashMap<Entity,HashMap<String,HashMap<Attribute,Double>>> hashValues, double lambda){
        
    	double min = Functions.min(listValues);
        double max = Functions.max(listValues);
        
        for (Map.Entry<Entity,HashMap<String,HashMap<Attribute,Double>>> entityRecords : hashValues.entrySet()) {
            Entity entityName = entityRecords.getKey();
            for( Map.Entry<String,HashMap<Attribute,Double>> attributeValues :entityRecords.getValue().entrySet()){
            	String attributeName = attributeValues.getKey();
            	for(Map.Entry<Attribute,Double> attrVal:attributeValues.getValue().entrySet()){
            		Double oldValue = attrVal.getValue();
            		Double x1 = 0.0;
            		Double diff = max-min;
            		if(diff == 0.0) {
            		    x1 = (oldValue - min);
            		}
            		else{
            		    x1 = (oldValue - min)/(max-min);
            		}
            		long x2 = Math.round(x1);
            		Double newValue =lambda*x1 + (1-lambda)*x2;
            		hashValues.get(entityName).get(attributeName).put(attrVal.getKey(), newValue);
            	}
            }
        }
        
        
    	return hashValues;
    }
    
    
    public static HashMap<Source,Double> normalizetrustworthiness(ArrayList<Double> listValues,HashMap<Source,Double> hashValues, double lamda){
        
    	double min = Functions.min(listValues);
        double max = Functions.max(listValues);
        
        for (Map.Entry<Source,Double> entityRecords : hashValues.entrySet()) {
            Source sourceName = entityRecords.getKey();
            Double oldValue = entityRecords.getValue();
            Double x1 = (oldValue - min)/(max-min);
            long x2 = Math.round(x1);
            Double newValue =lamda*x1 + (1-lamda)*x2;
            hashValues.put(sourceName,newValue);
        }
        
        
    	return hashValues;
    }
	
}
