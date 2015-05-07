package main.java.edu.umass.cs.data_fusion.data_structures;

import java.util.*;


/**
 * A representation for a truth discovery algorithm *
 */
public abstract class Algorithm {

    /**
     * The name of the algorithm. * 
     */
    protected String name;

    /**
     * The source associated with this algorithm * 
     */
    protected Source source;

    /**
     * Instantiate a new algorithm with the given name * 
     * @param name - the name of the algorithm
     */
    protected Algorithm(String name) {
        this.name = name;
        this.source = new Source(name);
    }

    /**
     * Retrieve the name of the algorithm * 
     * @return the name of the algorithm
     */
    public String getName() {
        return name;
    }

    /**
     * Implemented differently by each Algorithm. The RecordCollection contains information 
     * about a number of entities from a variety of sources. This method consolidates the records
     * such that the result will contain a single record for each entity in the record collection.
     * @param recordCollection - the inputted data to clean.
     * @return - the cleaned data with one record per entity.
     */
    public abstract ArrayList<Result> execute(RecordCollection recordCollection);


    /**
     * Returns all of the sources appearing the list of records which provide the given
     * attribute value (regardless of which entity the value appears for). Note that 
     * if you want to find the sources providing a value for a particular entity. Then
     * use the method in RecordCollection getRecordsForEntity to get only those records 
     * for a given entity in constant time. 
     * @param records - the records to search through for sources
     * @param value - the value of interest
     * @return - the sources providing the given attribute value in the list of records
     */
    protected Set<Source> sourcesWithValue(List<Record> records, Attribute value)  {
        Set<Source> sourcesWithValue = new HashSet<Source>();
        String attrName = value.getName();
        for (Record r : records) {
            if (r.hasAttribute(attrName))
                if (r.getAttribute(attrName).equals(value))
                    sourcesWithValue.add(r.getSource());
        }
        return sourcesWithValue;
    }
    
    protected int numSourcesWithinEntity(List<Record> records)  {
        Set<Source> sourcesInEntity = new HashSet<Source>();
        
        for (Record r : records) {
        	sourcesInEntity.add(r.getSource());
        }
        return sourcesInEntity.size();
    }
    
    protected int numSourcesWithinEntityWithNonZeroTrustWorthiness(List<Record> records,HashMap<Source, Double> sourceTrustworthiness)  {
        Set<Source> sourcesInEntity = new HashSet<Source>();
        
        for (Record r : records) {
        	Source s = r.getSource();
        	if(sourceTrustworthiness.get(s)!=0.0)
        	   sourcesInEntity.add(s);
        }
        return sourcesInEntity.size();
    }
    
    
    /**
     * Returns all of the sources appearing the list of records which DO NOT provide the given
     * attribute value (regardless of which entity the value appears for). Note that 
     * if you want to find the sources providing a value for a particular entity. Then
     * use the method in RecordCollection getRecordsForEntity to get only those records 
     * for a given entity in constant time. 
     * @param records - the records to search through for sources
     * @param value - the value of interest
     * @return - the sources providing the given attribute value in the list of records
     */
    protected Set<Source> sourcesWithOutValue(List<Record> records, Attribute value)  {
        Set<Source> sourcesWithOutValue = new HashSet<Source>();
        String attrName = value.getName();
        for (Record r : records) {
            if (r.hasAttribute(attrName))
                if (!r.getAttribute(attrName).equals(value))
                	sourcesWithOutValue.add(r.getSource());
        }
        return sourcesWithOutValue;
    }

    /**
     * Returns all of the unique values given for the attribute with the passed in name 
     * which appear in the list of records.  
     * @param records - the list of data records
     * @param attributeName - the attribute of interest
     * @return - all of the unique values for the attribute with name attributeName in records
     */
    protected Set<Attribute> valuesForAttribute(List<Record> records, String attributeName) {
        Set<Attribute> valuesForAttribute = new HashSet<Attribute>();
        for (Record r: records) {
            Attribute attr = r.getAttribute(attributeName);
            if (attr != null)
                valuesForAttribute.add(attr);
        }
        return valuesForAttribute;
    }
    
    protected Set<Attribute> entityAttributeValues(List<Record> records, String attributeName) {
        Set<Attribute> valuesForAttribute = new HashSet<Attribute>();
        for (Record r: records) {
            Attribute attr = r.getAttribute(attributeName);
            Entity e = r.getEntity();
            if (attr != null)
                valuesForAttribute.add(attr);
        }
        return valuesForAttribute;
    }

    /**
     * Returns all of the values (including duplicates) given for the attribute with the passed in name
     * which appear in the list of records.
     * @param records - the list of data records
     * @param attributeName - the attribute of interest
     * @return - all of the unique values for the attribute with name attributeName in records
     */
    protected List<Attribute> allValuesForAttribute(List<Record> records, String attributeName) {
        List<Attribute> valuesForAttribute = new ArrayList<Attribute>(100);
        for (Record r: records) {
            Attribute attr = r.getAttribute(attributeName);
            if (attr != null)
                valuesForAttribute.add(attr);
        }
        return valuesForAttribute;
    }

    /**
     * Returns the total number of attribute values (i.e. the number of (entity,attribute) pairs) 
     * provided by a given source in the RecordCollection 
     * @param collection - the data collection
     * @param source - the source of interest
     * @return - the number of attribute values provided by the source
     */
    protected int numberOfValuesProvidedBySource(RecordCollection collection, Source source) {
        ArrayList<Record> recordsForSource = collection.getRecords(source);
        int numValuesForSource = 0;
        for (Record r : recordsForSource) {
            numValuesForSource += r.getAttributes().size();
        }
        return numValuesForSource;
    }
    
    
    protected HashMap<Entity,HashMap<String,HashSet<Attribute>>> valuesProvidedBySource(RecordCollection collection, Source source) {
        ArrayList<Record> recordsForSource = collection.getRecords(source);
        HashMap<Entity,HashMap<String,HashSet<Attribute>>> entityAttributePair = new HashMap<Entity,HashMap<String,HashSet<Attribute>>>();
        
        for (Record r : recordsForSource) {
        	Entity e = r.getEntity();
            entityAttributePair.put(r.getEntity(),new HashMap<String,HashSet<Attribute>>());
        	for (Map.Entry<String,Attribute> entityRecords : r.getAttributes().entrySet()) {
        		if(!entityAttributePair.get(e).containsKey(entityRecords.getKey()))
        		    entityAttributePair.get(e).put(entityRecords.getKey(),new HashSet<Attribute>());
        		entityAttributePair.get(e).get(entityRecords.getKey()).add(entityRecords.getValue());
        	}
            
        }
        return entityAttributePair;
    }

    /**
     * Moves Result records into a RecordCollection objection 
     * TODO: Should we consolidate Result into Record, maybe a supertype?
     * @param results - the list of results
     * @return - the RecordCollection containing the result.
     */
    public RecordCollection convert(ArrayList<Result> results) {
        ArrayList<Record> rec = new ArrayList<Record>(results.size());
        for (Result r : results) {
            rec.add(new Record(r.getSource(),r.getEntity(),r.getAttributes()));
        }
        return new RecordCollection(rec);
    }

    /**
     * A toString method which contains the name of the algorithm  
     * @return - string
     */
    public String toString() {
        return "Algorithm(" + name + ")";
    }

    /**
     * Displays useful information about the record collection
     * @param collection - collection of data
     * @return - info string
     */
    public String infoString(RecordCollection collection) {
        return "[Algorithm] Running " + this + " on RecordCollection with " + collection.getEntitiesCount() + " entities, each with ~" + collection.getAttributes().size() + " attributes.";
    }

    /**
     * For a particular attribute, returns a mapping from the values of the attributes to 
     * the number of times that value appears in the list of records 
     * @param records - the data records
     * @param attrName - the name of the attribute
     * @return - the mapping of values to number of appearances
     */
    public HashMap<Attribute, Integer> getCount(List<Record> records, String attrName) {
        HashMap<Attribute,Integer> votes = new HashMap<Attribute, Integer>();
        for (Record r: records) {
            if (r.hasAttribute(attrName)) {
                Attribute a = r.getAttribute(attrName);
                if (!votes.containsKey(a)) {
                    votes.put(a,0);
                }
                votes.put(a,votes.get(a)+1);
            }
        }
        return votes;
    }

    /**
     * Given a mapping of attribute values to their number of occurrences
     * returns the value with the most votes 
     * @param votes - takes a vote of which value occurs the most
     * @return - the most frequently occurring value
     */
    public Attribute getMajorityVote(Map<Attribute,Integer> votes) {
        int max = -1;
        Attribute maxAttr = null;
        for (Attribute a : votes.keySet()) {
            int aCount = votes.get(a);
            if (aCount > max) {
                max = aCount;
                maxAttr = a;
            }
        }
        return maxAttr;
    }


}