package main.java.edu.umass.cs.data_fusion.dataset_creation;


import main.java.edu.umass.cs.data_fusion.data_structures.RecordCollection;

import java.io.File;

public class WriterCreditApprovalGoldDataset {

    public static void main(String[] args) {

        LoadCreditApprovalForDatasetCreation loader = new LoadCreditApprovalForDatasetCreation();

        RecordCollection data = loader.load(new File(new File("uci_data", "credit-approval"), "crx.data"));

        data.writeToTSVFile( new File(new File("data","credit"), "crx.tsv"),loader.getOrderedAttributeNames());


    }
}
