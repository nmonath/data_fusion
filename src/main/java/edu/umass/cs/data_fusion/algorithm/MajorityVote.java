package main.java.edu.umass.cs.data_fusion.algorithm;


import main.java.edu.umass.cs.data_fusion.data_structures.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MajorityVote extends Algorithm {

    public MajorityVote() {
        super("MajorityVote");
    }



    @Override
    public ArrayList<Result> execute(RecordCollection recordCollection) {
        // Print an info message about the algorithm
        System.out.println(infoString(recordCollection));
        Set<Entity> entities = recordCollection.getEntities();
        ArrayList<Result> resultingTable = new ArrayList<Result>(entities.size());
        int numCompleted = 0;
        int numTotalEntites = recordCollection.getEntitiesCount();
        String countString = "Number of completed entities: " + numCompleted + "/" + numTotalEntites;
        System.out.print(countString);
        for (Entity e: entities) {
            
            if (numCompleted % 100 == 0) {
                for (int i = 0; i < countString.length();i++)
                    System.out.print("\b");
                countString = "Number of completed entities: " + numCompleted + "/" + numTotalEntites;
                System.out.print(countString);
            }
            
            Result result = new Result(source,e);
            Set<String> attributeNames = recordCollection.getAttributes(e);
            ArrayList<Record> recordsForE = recordCollection.getRecords(e);
            for (String attrName : attributeNames) {
                result.addAttribute(getMajorityVote(getCount(recordsForE,attrName)));
            }
            resultingTable.add(result);
            numCompleted++;
        }
        if (numCompleted % 100 == 0) {
            for (int i = 0; i < countString.length();i++)
                System.out.print("\b");
            countString = "Number of completed entities: " + numCompleted + "/" + numTotalEntites;
            System.out.print(countString);
        }
        System.out.println("\nDone running algorithm.");
        return resultingTable;
    }
}
