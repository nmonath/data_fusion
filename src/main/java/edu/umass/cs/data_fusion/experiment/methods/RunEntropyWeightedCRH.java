package main.java.edu.umass.cs.data_fusion.experiment.methods;

import main.java.edu.umass.cs.data_fusion.algorithm.EntropyWeightedCRH;
import main.java.edu.umass.cs.data_fusion.experiment.Experiment;
import main.java.edu.umass.cs.data_fusion.experiment.ExperimentSetups;

import java.io.File;

public class RunEntropyWeightedCRH {
    
    public static void main(String[] args) {

        // Books
        Experiment books = ExperimentSetups.getBookExperiment(new EntropyWeightedCRH(), new File(new File("output", "EntropyWeightedCRH"), "books"));
        books.run();
        books = null;
        System.gc();

        // Full Stock
        Experiment stocks = ExperimentSetups.getFullStockExperiment(new EntropyWeightedCRH(), true, new File(new File("output", "EntropyWeightedCRH"), "full_stocks"));
        stocks.run();
        stocks = null;
        System.gc();

        // July 7
        Experiment july7 = ExperimentSetups.getJulySeventhStockExperiment(new EntropyWeightedCRH(), true, new File(new File("output", "EntropyWeightedCRH"), "july7"));
        july7.run();
        july7 = null;
        System.gc();

        // Weather
        Experiment weather = ExperimentSetups.getWeatherExperiment(new EntropyWeightedCRH(), new File(new File("output", "EntropyWeightedCRH"), "weather"));
        weather.run();
        weather = null;
        System.gc();

        // Adult
        Experiment adult = ExperimentSetups.getAdultExperiment(new EntropyWeightedCRH(), new File(new File("output", "EntropyWeightedCRH"), "adult"));
        adult.run();
        adult = null;
        System.gc();

        // Credit
        Experiment credit = ExperimentSetups.getCreditApprovalExperiment(new EntropyWeightedCRH(), new File(new File("output", "EntropyWeightedCRH"), "credit"));
        credit.run();
        credit = null;
        System.gc();
        
    }
}
