package edu.umass.cs.data_fusion.algorithm;

import java.util.ArrayList;

import edu.umass.cs.data_fusion.data_structures.Algorithm;
import edu.umass.cs.data_fusion.data_structures.RecordCollection;
import edu.umass.cs.data_fusion.data_structures.Result;

public class Ensemble extends Algorithm{
	// the list of algorithms to be ensembled.
	ArrayList<Algorithm> algorithmList;
	// final algorithm to determine trustworthiness
	Algorithm finalAlgorithm;
	
	
    public Ensemble(ArrayList<Algorithm> algorithmList, Algorithm finalAlgorithm) {
        super("Ensemble");
        this.algorithmList  = algorithmList;
        this.finalAlgorithm = finalAlgorithm;
    }
    
    public ArrayList<Result> execute(RecordCollection collection) {
    	ArrayList<Result> results = new ArrayList<Result>();
    	for(Algorithm algorithm : algorithmList){
    		for(Result r : algorithm.execute(collection)){
    			results.add(r);
    		}
    		
    	}
    	RecordCollection finalCollection = finalAlgorithm.convert(results);
    	ArrayList<Result> finalResults = finalAlgorithm.execute(finalCollection);
    	
    	return finalResults;
    }
}
