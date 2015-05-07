package main.java.edu.umass.cs.data_fusion.dataset_creation;

import main.java.edu.umass.cs.data_fusion.data_structures.RecordCollection;
import main.java.edu.umass.cs.data_fusion.data_structures.Source;
import main.java.edu.umass.cs.data_fusion.data_structures.SyntheticSource;
import main.java.edu.umass.cs.data_fusion.load.LoadAdult;
import main.java.edu.umass.cs.data_fusion.load.LoadCreditApproval;
import main.java.edu.umass.cs.data_fusion.load.LoadPimaIndiansDiabetes;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class CreatePimaIndiansDiabetesDataset {

    public static void main(String[] args) {

        LoadPimaIndiansDiabetes loader = new LoadPimaIndiansDiabetes();
        RecordCollection collection = loader.load(new File(new File("data", "pima-indians-diabetes"), "pima-indians-diabetes.tsv"));

        // Modification code
        CreateSyntheticDataset createSyntheticDataset = new CreateSyntheticDataset();

        Random random = new Random(0);

        //synthetic data sources
        ArrayList<SyntheticSource> sources = new ArrayList<SyntheticSource>();
        for (int i = 0; i < 50; i ++ ) {
            double theta = random.nextDouble()*0.2 + 0.8;
            double sigma = random.nextDouble()*2.0;
            sources.add(new SyntheticSource(String.format("synth_%d_%g_%g",i,theta,sigma), theta,sigma));
        }


        RecordCollection noisyData = createSyntheticDataset.createModifiedDataset(collection, sources);

        noisyData.writeToTSVFile(new File(new File("data", "pima-indians-diabetes"), "pima-indians-diabetes_noisy.tsv"),loader.getOrderedAttributeNames());


    }

}
