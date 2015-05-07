package main.java.edu.umass.cs.data_fusion.dataset_creation;

import main.java.edu.umass.cs.data_fusion.data_structures.RecordCollection;

import java.io.File;

public class WriteAdultGoldDataset {
    
    
    public static void main(String[] args) {
        
        LoadAdultForDatasetCreation loader = new LoadAdultForDatasetCreation();
        
        RecordCollection data = loader.load(new File(new File("uci_data", "adult"), "adult.data"));
        
        data.writeToTSVFile( new File(new File("data","adult"), "adult_gold.tsv"),loader.getOrderedAttributeNames());

        
    }
    
    
}
