package main.java.edu.umass.cs.data_fusion.evaluation;


import main.java.edu.umass.cs.data_fusion.data_structures.Entity;
import main.java.edu.umass.cs.data_fusion.data_structures.Record;
import main.java.edu.umass.cs.data_fusion.data_structures.RecordCollection;
import main.java.edu.umass.cs.data_fusion.data_structures.author.AuthorListAttribute;

import java.util.*;

public class EvaluateBookDataset {
    
    private double accuracy;
    
    public EvaluateBookDataset() {
        accuracy = 0.0;
    }

    public void calcAccuracy(RecordCollection predicted, RecordCollection gold) {
        double accuracy = 0.0;
        double denominator = 0.0;
        for (Entity entity : gold.getEntities()) {

            List<Record> goldRecords = gold.getRecords(entity);
            List<Record> predictedRecords = predicted.getRecords(entity);

            assert goldRecords.size() == 1;
            assert predictedRecords.size() == 1 || predictedRecords.size() == 0;
            
            Record goldRecord = goldRecords.get(0);

            AuthorListAttribute goldAuthorList = (AuthorListAttribute) goldRecord.getAttribute("Author(s)");

            if (predictedRecords.size() > 0) {

                Record predictedRecord = predictedRecords.get(0);
                AuthorListAttribute predAuthorList = ((AuthorListAttribute) predictedRecord.getAttribute("Author(s)"));

                if (predAuthorList != null && predAuthorList.equals(goldAuthorList))
                    accuracy+=1.0;
            }
            denominator+=1.0;
        }
        this.accuracy =  accuracy / denominator;
    }
    
    public double getAccuracy() {
        return accuracy;
    }

}
