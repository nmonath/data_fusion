package main.java.edu.umass.cs.data_fusion.experiment.baseline;

import main.java.edu.umass.cs.data_fusion.algorithm.Upperbound;
import main.java.edu.umass.cs.data_fusion.experiment.Experiment;
import main.java.edu.umass.cs.data_fusion.experiment.ExperimentSetups;
import main.java.edu.umass.cs.data_fusion.load.*;

import java.io.File;

public class RunUpperbound {

    public static void main(String[] args) {

        // Books
        LoadTSVFile loader = new LoadBooks();
        Experiment books = ExperimentSetups.getBookExperiment(new Upperbound(loader.loadGold(new File(new File("data", "book"), "book_golden.txt"))), new File(new File("output", "upperbound"), "books"));
        books.run();
        books = null;
        System.gc();
        
        // Weather
        loader = new LoadWeather();
        Experiment weather = ExperimentSetups.getWeatherExperiment(new Upperbound(loader.loadGold(new File(new File("data", "weather"), "weather_ground_truth.txt"))), new File(new File("output", "upperbound"), "weather"));
        weather.run();
        weather = null;
        System.gc();
        
        // Adult 
        loader = new LoadAdult();
        Experiment adult = ExperimentSetups.getAdultExperiment(new Upperbound(loader.loadGold(new File(new File("data", "adult"), "adult_gold.tsv"))), new File(new File("output", "upperbound"), "adult"));
        adult.run();
        adult = null;
        System.gc();

        // Credit
        loader = new LoadCreditApproval();
        Experiment credit = ExperimentSetups.getCreditApprovalExperiment(new Upperbound(loader.loadGold(new File(new File("data", "credit"), "crx.tsv"))), new File(new File("output", "upperbound"), "credit"));
        credit.run();
        credit = null;
        System.gc();
    
    }
}
