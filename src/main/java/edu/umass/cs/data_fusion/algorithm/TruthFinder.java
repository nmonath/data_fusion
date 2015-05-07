package edu.umass.cs.data_fusion.algorithm;

 
import edu.umass.cs.data_fusion.data_structures.*;
import edu.umass.cs.data_fusion.data_structures.author.AuthorListAttribute;
import edu.umass.cs.data_fusion.data_structures.author.AuthorName;
import edu.umass.cs.data_fusion.util.Functions;

import java.util.*;

public class TruthFinder extends Algorithm{
    
    private double initialTrustworthiness;
    private double delta;
    private double rho;
    private double gamma;

    final private double MAX_ITERATIONS = 20;
    final private double MIN_ITERATIONS = 5;

    private Source source = new Source(this.getName());


    public TruthFinder(String name) {
        super(name);
        this.initialTrustworthiness = 0.5; // TODO: Grid search these values
        this.delta = 0.001;
        this.rho = 0.3;
        this.gamma = 0.1;
    }
    
    public TruthFinder() {
        super("TruthFinder");
        //Initial Trustworthiness: 0.500000, Delta: 0.001000, Rho: 0.700000, Gamma: 0.010000
        this.initialTrustworthiness = 0.5;
        this.delta = 0.001;
        this.rho = 0.7;
        this.gamma = 0.01;
    }
    
    public TruthFinder(double initialTrustworthiness, double delta, double rho, double gamma) {
        super("TruthFinder");
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
        HashMap<Entity,HashMap<String,HashMap<Attribute,Double>>> confidence = new HashMap<Entity, HashMap<String,HashMap<Attribute,Double>>>();
        
        /*
        The algorithm works iteratively checking for convergence by measuring the cosine similarity 
        between the trustworthiness scores (stored in an arbitrarily ordered vector) 
        of the current round and the previous round
         */
        HashMap<Source, Double> previousTrustworthiness = new HashMap<Source, Double>();


        /*
        The sources which provide the data items  
         */
        Set<Source> sources = collection.getSources();
        
        /*
         Initialize source trustworthiness
         */
        for (Source s : sources) {
            previousTrustworthiness.put(s,initialTrustworthiness);
        }

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

            iterationString = "[TruthFinder] Number of completed iterations: " + numIterations;
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
                            sigma_v += -Math.log(1 - previousTrustworthiness.get(s));
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
                            if (sigma.get(vPrime) == null)
                                System.out.println("null");
                            
                            if (!v.equals(vPrime)) {
                                sigmaStar_v += rho * sigma.get(vPrime) * similarity(v, vPrime);
                            }
                        }
                        // confidence_v <- 1 / (1 + exp(-gamma*sigmaStar_v)
                        double confidence_v = 1 / (1 + Math.exp(-gamma * sigmaStar_v));
                        confidence.get(entity).get(attributeName).put(v, confidence_v);
                    }
                }
            }
            /*
            Calculate the trustworthiness values of the sources for this round.
             */
            HashMap<Source, Double> currentTrustworthiness = new HashMap<Source, Double>();
            for (Source s : sources) {
                // trustworthiness(s) <- sum_{v in ValuesGivenBySource} confidence(v)/NumberOfValuesGivenBySource
                ArrayList<Record> recordsForSource = collection.getRecords(s);
                int numValuesForSource = 0;
                double trustworthiness = 0.0;
                for (Record r : recordsForSource) {
                    for (Attribute a: r.getAttributes().values()) {
                        trustworthiness += confidence.get(r.getEntity()).get(a.getName()).get(a);
                        numValuesForSource += 1;
                    }
                }
                currentTrustworthiness.put(s,trustworthiness/numValuesForSource);
            }
            /*
            Check for convergence
             */
            converged = converged(previousTrustworthiness,currentTrustworthiness,delta);
            
            if (converged)
                System.out.println("[TruthFinder] Convergence condition met.");
            previousTrustworthiness = currentTrustworthiness;
            numIterations += 1;
        }

        if (!converged)
            System.out.println("[TruthFinder] Max Iterations condition met.");

        System.out.println("[TruthFinder] Algorithm complete, assigning values to each attribute.");
        /*
        Assign a final value to each attribute of each entity  
         */
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
        System.out.println("[TruthFinder] Done.");
        return results;
    }


    /**
     * This function deserves some serious thought. Right now this definition seems to 
     * give results comparable to the Li et al 2014 paper, but not as good as Dong et al 2012  
     * @param attr1
     * @param attr2
     * @return
     */
    public double similarity(Attribute attr1, Attribute attr2) {
        if (attr1 instanceof FloatAttribute && attr2 instanceof FloatAttribute) {
            float one = ((FloatAttribute) attr1).getNormalizedValue();
            float two = ((FloatAttribute) attr2).getNormalizedValue();
            return -Math.abs(one - two);
        }else if (attr1 instanceof StringAttribute && attr2 instanceof StringAttribute)
            return -1.0*Functions.editDistance(((StringAttribute) attr1).getStringValue(),((StringAttribute) attr2).getStringValue());
        else if (attr1 instanceof AuthorListAttribute && attr2 instanceof AuthorListAttribute)
            return AuthorName.accuracy(((AuthorListAttribute) attr1).getAuthors(), ((AuthorListAttribute) attr2).getAuthors());
        return 0.0;
    }
    
    public double cosineSim(HashMap<Source,Double> previous, HashMap<Source,Double> current) {
        assert previous.keySet().containsAll(current.keySet());
        double[] previousVec = new double[previous.size()];
        double[] currentVec = new double[previous.size()];
        int i = 0;
        for (Source s: previous.keySet()) {
            previousVec[i] = previous.get(s);
            currentVec[i] = current.get(s);
            i += 1;
        }
        double cos = Functions.cosine(previousVec, currentVec);
        System.out.println("[TruthFinder] Cosine similarity: " + cos);
        return cos;
    }

    public static <T> double L1dist(HashMap<T,Double> previous, HashMap<T,Double> current) {
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
    
    // NOTE: While the paper suggests the cosine distance btw the source trustworthiness vectors 
    // is used as a criteria, I did not understand how it would work. They give a delta of 0.01%
    // but what is this a percent of?? It is unclear to me. So instead I use L1dist and threshold less than delta.
    public static <T> boolean converged(HashMap<T,Double> previous, HashMap<T,Double> current, double delta) {
        double dist =  L1dist(previous,current);
        return dist < delta || Double.isNaN(dist);
    }

    // performs the min-max scaling.
    public void normalizeAttributeValues(RecordCollection collection) {
        for (Entity entity : collection.getEntities()) {
            Set<String> attributeNames = collection.getAttributes(entity);
            ArrayList<Record> recordsForEntity = collection.getRecords(entity);
            for (String attrName : attributeNames) {
                ArrayList<Float> values  = new ArrayList<Float>(recordsForEntity.size());
                for (Record r: recordsForEntity) {
                    Attribute attr = r.getAttribute(attrName);
                    if (attr != null) {
                        if (attr instanceof FloatAttribute)
                            values.add(((FloatAttribute) attr).getFloatValue());
                    }
                }
                
                float min = Functions.minFloats(values);
                float max = Functions.maxFloats(values);
                
                //float mu = Functions.mean(values);
                //float var = Functions.variance(values,mu);
                for (Record r: recordsForEntity) {
                    Attribute attr = r.getAttribute(attrName);
                    if (attr != null) {
                        if (attr instanceof FloatAttribute)
                            if (max - min == 0)
                                ((FloatAttribute) attr).setNormalizedValue( 0.0f );
                            else
                                ((FloatAttribute) attr).setNormalizedValue( (((FloatAttribute) attr).getFloatValue() - min) / (max - min)  ) ;
                    }
                }
            }
        }
    }
    
}
