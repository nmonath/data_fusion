package edu.umass.cs.data_fusion.algorithm;


import edu.umass.cs.data_fusion.data_structures.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Upperbound extends Algorithm {
    
    RecordCollection gold;
    
    public Upperbound(RecordCollection gold) {
        super("Upperbound");
        this.gold = gold;
    }
    
    public Upperbound(List<Result> gold) {
        super("Upperbound");
        ArrayList<Record> records = new ArrayList<Record>(gold.size());
        for (Result g : gold) {
            records.add(new Record(source,g.getEntity(),g.getAttributes()));
        }
        this.gold = new RecordCollection(records);
    }
    
    
    @Override
    public ArrayList<Result> execute(RecordCollection recordCollection) {
        
        ArrayList<Result> results = new ArrayList<Result>(recordCollection.getEntitiesCount());
        
        // For each gold entity
        for (Entity entity: gold.getEntities()) {
            
            // find the records we have in our set about that entity
            List<Record> recordsForEntity = recordCollection.getRecords(entity);
            
            // Find the gold record about the entity
            List<Record> goldRecords = gold.getRecords(entity);
            if (goldRecords.size() != 1)
                System.err.println("WARNING: More than one gold record for entity" + entity);
            Record goldRecord = goldRecords.get(0);

            // make a new result record for the entity
            Result resultForEntity = new Result(source,entity);
            // For each attribute that we have a value for in our collection about that entity
            for (String attributeName : recordCollection.getAttributes(entity)) {
                
                // find the set of values we have for that attribute 
                Set<Attribute> valuesForAttribute = valuesForAttribute(recordsForEntity, attributeName);
                
                // if the gold record has this attribute
                if (goldRecord.hasAttribute(attributeName)) {
                    // if the gold attribute appears in one of the source's values
                    if (valuesForAttribute.contains(goldRecord.getAttribute(attributeName)))
                        resultForEntity.addAttribute(goldRecord.getAttribute(attributeName));
                }
            }
            results.add(resultForEntity);
        }
        return results;
    }
}
