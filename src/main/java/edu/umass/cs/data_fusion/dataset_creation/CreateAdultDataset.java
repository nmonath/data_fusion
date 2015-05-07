package edu.umass.cs.data_fusion.dataset_creation;

import edu.umass.cs.data_fusion.data_structures.RecordCollection;
import edu.umass.cs.data_fusion.data_structures.Source;
import edu.umass.cs.data_fusion.data_structures.SyntheticSource;
import edu.umass.cs.data_fusion.load.LoadAdult;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class CreateAdultDataset {
    
    public static void main(String[] args) {

        
        
        // Note LoadAdult != LoadAdultForDatasetCreation
        // LoadAdult loads files in the Luna Dong format
        // LoadAdultForDatasetCreation loads files in UCI format
        // the adult_gold.tsv file and other files in the data directory are in the Luna Dong format
        LoadAdult loader = new LoadAdult();
        RecordCollection collection = loader.load(new File(new File("data", "adult"), "adult_gold.tsv"));
        
        // Modification code
        CreateSyntheticDataset createSyntheticDataset = new CreateSyntheticDataset();
        
        Random random = new Random(0);
        
        //synthetic data sources
        ArrayList<SyntheticSource> sources = new ArrayList<SyntheticSource>();
        for (int i = 0; i < 10; i ++ ) {
            double theta = random.nextDouble();
            double sigma = random.nextDouble()*2.0;
            sources.add(new SyntheticSource(String.format("synth_%d_%g_%g",i,theta,sigma), theta,sigma));
        }
        
        
//    	SyntheticSource source1 = new SyntheticSource("synth1", 0.1, 1);
//    	SyntheticSource source2 = new SyntheticSource("synth2", 1, 3);
//    	sources.add(source1);
//    	sources.add(source2);
    	
        RecordCollection noisyData = createSyntheticDataset.createModifiedDataset(collection, sources);
        
        noisyData.writeToTSVFile(new File(new File("data", "adult"), "adult_noisy.tsv"),loader.getOrderedAttributeNames());
        
        
    }
    
}
