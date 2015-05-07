package edu.umass.cs.data_fusion.dataset_creation;


import edu.umass.cs.data_fusion.data_structures.RecordCollection;

import java.io.File;

public class WritePimaIndiansDiabetesGoldDataset {

    public static void main(String[] args) {

        LoadPimaIndiansDiabetesForDatasetCreation loader = new LoadPimaIndiansDiabetesForDatasetCreation();

        RecordCollection data = loader.load(new File(new File("uci_data", "pima-indians-diabetes"), "pima-indians-diabetes.data"));

        data.writeToTSVFile( new File(new File("data","pima-indians-diabetes"), "pima-indians-diabetes.tsv"),loader.getOrderedAttributeNames());


    }
}
