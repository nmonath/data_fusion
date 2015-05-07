package edu.umass.cs.data_fusion.experiment.methods;


import edu.umass.cs.data_fusion.algorithm.CRH;
import edu.umass.cs.data_fusion.algorithm.ModifiedTruthFinder;
import edu.umass.cs.data_fusion.algorithm.TruthFinder;
import edu.umass.cs.data_fusion.experiment.Experiment;
import edu.umass.cs.data_fusion.experiment.ExperimentSetups;

import java.io.File;

public class RunTruthFinder {

    public static void main(String[] args) {

        // Books
        Experiment books = ExperimentSetups.getBookExperiment(new TruthFinder(), new File(new File("output", "normal-tf"), "books"));
        books.run();
        books = null;
        System.gc();

        // Full Stock
        Experiment stocks = ExperimentSetups.getFullStockExperiment(new TruthFinder(), true, new File(new File("output", "normal-tf"), "full_stocks"));
        stocks.run();
        stocks = null;
        System.gc();

        // July 7
        Experiment july7 = ExperimentSetups.getJulySeventhStockExperiment(new TruthFinder(), true, new File(new File("output", "normal-tf"), "july7"));
        july7.run();
        july7 = null;
        System.gc();

        // Weather
        Experiment weather = ExperimentSetups.getWeatherExperiment(new TruthFinder(), new File(new File("output", "normal-tf"), "weather"));
        weather.run();
        weather = null;
        System.gc();

        // Adult
        Experiment adult = ExperimentSetups.getAdultExperiment(new TruthFinder(), new File(new File("output", "normal-tf"), "adult"));
        adult.run();
        adult = null;
        System.gc();

        // Credit
        Experiment credit = ExperimentSetups.getCreditApprovalExperiment(new TruthFinder(), new File(new File("output", "normal-tf"), "credit"));
        credit.run();
        credit = null;
        System.gc();


        // Pima Indians
//        Experiment pima = ExperimentSetups.getPimaIndiansDiabetesExperiments(new TruthFinder(), new File(new File("output", "normal-tf"), "pima-indians-diabetes"));
//        pima.run();
//        pima = null;
//        System.gc();

    }
}
