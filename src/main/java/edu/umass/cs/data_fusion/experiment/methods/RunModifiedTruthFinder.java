package main.java.edu.umass.cs.data_fusion.experiment.methods;

import main.java.edu.umass.cs.data_fusion.algorithm.BaselineMedian;
import main.java.edu.umass.cs.data_fusion.algorithm.ModifiedTruthFinder;
import main.java.edu.umass.cs.data_fusion.algorithm.TruthFinder;
import main.java.edu.umass.cs.data_fusion.experiment.Experiment;
import main.java.edu.umass.cs.data_fusion.experiment.ExperimentSetups;

import java.io.File;


public class RunModifiedTruthFinder {

    public static void main(String[] args) {

        // Books
        Experiment books = ExperimentSetups.getBookExperiment(new ModifiedTruthFinder(), new File(new File("output", "modified-tf"), "books"));
        books.run();
        books = null;
        System.gc();

        // Full Stock
        Experiment stocks = ExperimentSetups.getFullStockExperiment(new ModifiedTruthFinder(), true, new File(new File("output", "modified-tf"), "full_stocks"));
        stocks.run();
        stocks = null;
        System.gc();

        // July 7
        Experiment july7 = ExperimentSetups.getJulySeventhStockExperiment(new ModifiedTruthFinder(), true, new File(new File("output", "modified-tf"), "july7"));
        july7.run();
        july7 = null;
        System.gc();

        // Weather
        Experiment weather = ExperimentSetups.getWeatherExperiment(new ModifiedTruthFinder(), new File(new File("output", "modified-tf"), "weather"));
        weather.run();
        weather = null;
        System.gc();

        // Adult
        Experiment adult = ExperimentSetups.getAdultExperiment(new ModifiedTruthFinder(), new File(new File("output", "modified-tf"), "adult"));
        adult.run();
        adult = null;
        System.gc();

        // Credit
        Experiment credit = ExperimentSetups.getCreditApprovalExperiment(new ModifiedTruthFinder(), new File(new File("output", "modified-tf"), "credit"));
        credit.run();
        credit = null;
        System.gc();

//        // Pima Indians
//        Experiment pima = ExperimentSetups.getPimaIndiansDiabetesExperiments(new ModifiedTruthFinder(), new File(new File("output", "modified-tf"), "pima-indians-diabetes"));
//        pima.run();
//        pima = null;
//        System.gc();
    }
}
