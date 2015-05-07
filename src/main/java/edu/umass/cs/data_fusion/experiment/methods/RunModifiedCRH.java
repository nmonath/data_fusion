package main.java.edu.umass.cs.data_fusion.experiment.methods;


import main.java.edu.umass.cs.data_fusion.algorithm.CRH;
import main.java.edu.umass.cs.data_fusion.algorithm.ModifiedCRH;
import main.java.edu.umass.cs.data_fusion.experiment.Experiment;
import main.java.edu.umass.cs.data_fusion.experiment.ExperimentSetups;

import java.io.File;

public class RunModifiedCRH {

    public static void main(String[] args) {

        // Books
        Experiment books = ExperimentSetups.getBookExperiment(new ModifiedCRH(), new File(new File("output", "ModifiedCRH"), "books"));
        books.run();
        books = null;
        System.gc();

        // Full Stock
        Experiment stocks = ExperimentSetups.getFullStockExperiment(new ModifiedCRH(), true, new File(new File("output", "ModifiedCRH"), "full_stocks"));
        stocks.run();
        stocks = null;
        System.gc();

        // July 7
        Experiment july7 = ExperimentSetups.getJulySeventhStockExperiment(new ModifiedCRH(), true, new File(new File("output", "ModifiedCRH"), "july7"));
        july7.run();
        july7 = null;
        System.gc();

        // Weather
        Experiment weather = ExperimentSetups.getWeatherExperiment(new ModifiedCRH(), new File(new File("output", "ModifiedCRH"), "weather"));
        weather.run();
        weather = null;
        System.gc();

        // Adult
        Experiment adult = ExperimentSetups.getAdultExperiment(new ModifiedCRH(), new File(new File("output", "ModifiedCRH"), "adult"));
        adult.run();
        adult = null;
        System.gc();

        // Credit
        Experiment credit = ExperimentSetups.getCreditApprovalExperiment(new ModifiedCRH(), new File(new File("output", "ModifiedCRH"), "credit"));
        credit.run();
        credit = null;
        System.gc();
    }
}
