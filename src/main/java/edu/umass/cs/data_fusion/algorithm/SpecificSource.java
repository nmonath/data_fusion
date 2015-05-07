package edu.umass.cs.data_fusion.algorithm;


import edu.umass.cs.data_fusion.data_structures.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Used to get the evaluation score of a particular source * 
 */
public class SpecificSource extends Algorithm {

    Source sourceToUse;

    public SpecificSource(String sourceName) {
        super("Specific Source (" + sourceName + ")");
        sourceToUse = new Source(sourceName);
    }

    @Override
    public ArrayList<Result> execute(RecordCollection recordCollection) {
        System.out.println(infoString(recordCollection));
        ArrayList<Result> results = new ArrayList<Result>();
        Set<Entity> seenEntities = new HashSet<Entity>();
        
        for (Record r : recordCollection.getRecords(sourceToUse)) {
            if (!seenEntities.contains(r.getEntity())) {
                Result res = new Result(this.source,r.getEntity());
                for (Attribute a : r.getAttributes().values())
                    res.addAttribute(a);
                results.add(res);
                seenEntities.add(r.getEntity());
            } else  {
                System.out.println("[SpecificSource] Source " + sourceToUse + " provided two records for entity " + r.getEntity() + " one version was chosen arbitrarily.");
            }
        }
        return results;
    }
}
