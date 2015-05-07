package edu.umass.cs.data_fusion.algorithm;

 
import edu.umass.cs.data_fusion.data_structures.*;

import java.util.*;

// Modified to include source/attribute pairs
public class ModifiedTruthFinder extends TruthFinder {

    private double initialTrustworthiness;
    private double delta;
    private double rho;
    private double gamma;

    final private double MAX_ITERATIONS = 20;
    final private double MIN_ITERATIONS = 5;

    private Source source = new Source(this.getName());

    public ModifiedTruthFinder() {
        super("ModifiedTruthFinder");
        this.initialTrustworthiness = 0.5;
        this.delta = 0.001;
        this.rho = 0.6;
        this.gamma = 0.000100;
    }

    public ModifiedTruthFinder(double initialTrustworthiness, double delta, double rho, double gamma) {
        super("ModifiedTruthFinder");
        this.initialTrustworthiness = initialTrustworthiness;
        this.delta = delta;
        this.rho = rho;
        this.gamma = gamma;
    }

    public ArrayList<Result> execute(RecordCollection collection) {

        /*
        The range of values of the similarity function is very sensitive for performance. 
        Since the range of the stock values dramatically differs from attribute to attribute
        we perform max-min scaling of attribute values.
         */
        normalizeAttributeValues(collection);
        
        /*
        Each unique <Entity, AttributeValue> pair is assigned a confidence. Note that if two sources
        present the same value for an attribute this pair will have the same confidence for both sources
        and so we only store it once. 
         */
        HashMap<Entity, HashMap<String, HashMap<Attribute, Double>>> confidence = new HashMap<Entity, HashMap<String, HashMap<Attribute, Double>>>();
        
        /*
        The algorithm works iteratively checking for convergence by measuring the cosine similarity 
        between the trustworthiness scores (stored in an arbitrarily ordered vector) 
        of the current round and the previous round
         */
        HashMap<Pair<Source, String>, Double> previousTrustworthiness = new HashMap<Pair<Source, String>, Double>();


        /*
        The sources which provide the data items  
         */
        Set<Source> sources = collection.getSources();


        /*
         Initialize source trustworthiness
         */
        for (Source s : sources)
            for (Record r : collection.getRecords(s))
                for (String attrName : r.getAttributes().keySet())
                    previousTrustworthiness.put(new Pair<Source, String>(s, attrName), initialTrustworthiness);
        

        /*
        If the algorithm has converged
         */
        boolean converged = false;
        
        /*
        The number of iterators completed so far
         */
        int numIterations = 0;

        String iterationString;
        
        /*
        Repeat this process until convergence or the number of iterations exceeds the limit
         */
        while ((!converged && numIterations < MAX_ITERATIONS) || numIterations < MIN_ITERATIONS) {

            iterationString = "[ModifiedTruthFinder] Number of completed iterations: " + numIterations;
            System.out.println(iterationString);
            
            /*
            The algorithm operates over pairs of <Entity, Attribute>
             */
            for (Entity entity : collection.getEntities()) {
                
                /*
                Reset the confidence values for this entity; confidence values are calculated at each iteration.
                 */
                confidence.put(entity, new HashMap<String, HashMap<Attribute, Double>>());
                
                /*
                All of the attribute names of the entities attributes
                 */
                Set<String> attributeNames = collection.getAttributes(entity);
                
                /*
                All of the records corresponding to this entity
                 */
                ArrayList<Record> recordsForEntity = collection.getRecords(entity);
                
                
                /*
                Iterate over each attributeName for this entity
                 */
                for (String attributeName : attributeNames) {
                    
                    /*
                    Reset the confidence values for this attribute
                     */
                    confidence.get(entity).put(attributeName, new HashMap<Attribute, Double>());
                    
                    /*
                    A hash map storing the sigma values for each attribute value as described in the paper
                     */
                    HashMap<Attribute, Double> sigma = new HashMap<Attribute, Double>();
                    
                    /*
                    All of the different values for a given named attribute
                     */
                    Set<Attribute> valuesForGivenAttribute = valuesForAttribute(recordsForEntity, attributeName);
                    
                    /*
                    Calculated the sigma for each value 
                     */
                    for (Attribute v : valuesForGivenAttribute) {
                        // sigma_v <- - sum_{s in SourcesProvidingV} ln(1-Trustworthiness(s)) 
                        Set<Source> sourcesWithValue = sourcesWithValue(recordsForEntity, v);
                        double sigma_v = 0.0;
                        for (Source s : sourcesWithValue) {
                            sigma_v += -Math.log(1 - previousTrustworthiness.get(new Pair<Source, String>(s, v.getName()))); // TODO: I think it will slow us down to instantiate an object for each lookup
                        }
                        sigma.put(v, sigma_v);
                    }
                    
                    /*
                    Calculate the sigmaStar and confidence for each attribute value
                     */
                    for (Attribute v : valuesForGivenAttribute) {
                        // sigmaStar_v <- sigma_v + rho * (sum_{v' in valuesForGivenAttribute, v' != v} sigma_v' * (similarity(v,v')))
                        double sigmaStar_v = sigma.get(v);
                        for (Attribute vPrime : valuesForGivenAttribute) {
                            if (!v.equals(vPrime)) {
                                sigmaStar_v += rho * sigma.get(vPrime) * similarity(v, vPrime);
                            }
                        }
                        // confidence_v <- 1 / (1 + exp(-gamma*sigmaStar_v)
                        double confidence_v = 1 / (1 + Math.exp(-gamma * sigmaStar_v));
                        if (Double.isNaN(confidence_v))
                            System.out.println("[ModifiedTruthFinder] confidence is nan for attribute " + v + " sigma(v) " + sigma.get(v));
                        confidence.get(entity).get(attributeName).put(v, confidence_v);
                    }
                }
            }
            
            /*
            Calculate the trustworthiness values of the source-attribute pairs for this round.
             */
            HashMap<Pair<Source, String>, Double> currentTrustworthiness = new HashMap<Pair<Source, String>, Double>();
            for (Source s : sources) {

                // trustworthiness(s,a) <- sum_{v in ValuesGivenBySource} confidence(v)/NumberOfValuesGivenBySource
                ArrayList<Record> recordsForSource = collection.getRecords(s);

                // attr name -> num appears or trust
                Map<String, Integer> numValuesForAttr = new HashMap<String, Integer>();
                Map<String, Double> trustworthiness = new HashMap<String, Double>();

                for (Record r : recordsForSource) {
                    for (Attribute a : r.getAttributes().values()) {
                        String aName = a.getName();
                        double trust = confidence.get(r.getEntity()).get(a.getName()).get(a);
                        if (!trustworthiness.containsKey(aName)) {
                            trustworthiness.put(a.getName(), 0.0);
                        }
                        if (!numValuesForAttr.containsKey(aName)) {
                            numValuesForAttr.put(aName, 0);
                        }
                        trustworthiness.put(aName, trustworthiness.get(aName) + trust);
                        numValuesForAttr.put(aName, numValuesForAttr.get(aName) + 1);
                    }
                }
                for (String attrName : numValuesForAttr.keySet()) {
                    if (numValuesForAttr.get(attrName) == 0)
                        System.out.println("[ModifiedTruthFinder] Divided by Zero");
                    currentTrustworthiness.put(new Pair<Source, String>(s, attrName), trustworthiness.get(attrName) / numValuesForAttr.get(attrName));
                }
            }
            /*
            Check for convergence
             */
            converged = converged(previousTrustworthiness, currentTrustworthiness, delta);
            if (converged)
                System.out.println("[ModifiedTruthFinder] Convergence condition met.");
            previousTrustworthiness = currentTrustworthiness;
            numIterations += 1;
        }

        if (!converged)
            System.out.println("[ModifiedTruthFinder] Max Iterations condition met.");

        System.out.println("[ModifiedTruthFinder] Algorithm complete, assigning values to each attribute.");
        /*
        Assign a final value to each attribute of each entity  
         */
        Set<Entity> entities = collection.getEntities();
        ArrayList<Result> results = new ArrayList<Result>(entities.size());
        for (Entity entity : entities) {
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
        System.out.println("[TruthFinder] Done.");
        return results;
    }

}

