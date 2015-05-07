package edu.umass.cs.data_fusion.experiment;


import edu.umass.cs.data_fusion.data_structures.Algorithm;
import edu.umass.cs.data_fusion.data_structures.RecordCollection;
import edu.umass.cs.data_fusion.data_structures.Result;
import edu.umass.cs.data_fusion.evaluation.EvaluateBookDataset;
import edu.umass.cs.data_fusion.evaluation.EvaluationMetrics;
import edu.umass.cs.data_fusion.load.LoadTSVFile;
import edu.umass.cs.data_fusion.util.HTMLOutput;

import java.io.File;
import java.util.ArrayList;

public class BookExperiment extends Experiment {
    
    private double accuracy;

    public BookExperiment(Algorithm algorithm, LoadTSVFile loader, RecordCollection inputData, RecordCollection gold, File outputDir) {
        super(algorithm,false,loader,inputData,gold,outputDir);
    }

    public BookExperiment(Algorithm algorithm, LoadTSVFile loader, File inputFile, File goldFile, File outputDir) {
        this(algorithm, loader, loader.load(inputFile), loader.loadGold(goldFile), outputDir);
    }


    @Override
    public void run() {
        // Run the algorithm
        ArrayList<Result> results = algorithm.execute(inputData);
        RecordCollection resultsCollection = algorithm.convert(results);

        EvaluateBookDataset bookEval = new EvaluateBookDataset();
        bookEval.calcAccuracy(resultsCollection, gold);
        accuracy = bookEval.getAccuracy();
        System.out.println("Accuracy: " + bookEval.getAccuracy());

        // Just for the html
        EvaluationMetrics evaluator = new EvaluationMetrics(results,gold);

        // Write the output
        outputDir.mkdirs();
        HTMLOutput.writeHTMLOutput(loader.getOrderedAttributeNames(), resultsCollection, gold, new File(outputDir, "report.html").getAbsolutePath(), true, evaluator);
        resultsCollection.writeToTSVFile(new File(outputDir, "output.tsv"), loader.getOrderedAttributeNames());
        writeScoreFile(outputDir, "Accuracy: " + bookEval.getAccuracy());

    }

    public double getAccuracy() {
        return accuracy;
    }
}
