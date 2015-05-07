package edu.umass.cs.data_fusion.experiment.baseline;

import edu.umass.cs.data_fusion.algorithm.BaselineMean;
import edu.umass.cs.data_fusion.algorithm.BaselineMedian;
import edu.umass.cs.data_fusion.experiment.Experiment;
import edu.umass.cs.data_fusion.experiment.ExperimentSetups;

import java.io.File;

public class RunMedian {

    public static void main(String[] args) {


        // Full Stock
        Experiment fullStock = ExperimentSetups.getFullStockExperiment(new BaselineMedian(), true, new File(new File("output", "median"), "fullstock"));
        fullStock.run();
        fullStock = null;
        System.gc();

        // July 7 Stock
        Experiment july7 = ExperimentSetups.getJulySeventhStockExperiment(new BaselineMedian(), true, new File(new File("output", "median"), "july7"));
        july7.run();
        july7 = null;
        System.gc();

        // Weather
        Experiment weather = ExperimentSetups.getWeatherExperiment(new BaselineMedian(), new File(new File("output", "median"), "weather"));
        weather.run();
        weather = null;
        System.gc();

        // Adult
        Experiment adult = ExperimentSetups.getAdultExperiment(new BaselineMedian(), new File(new File("output", "median"), "adult"));
        adult.run();
        adult = null;
        System.gc();

        // Credit
        Experiment credit = ExperimentSetups.getCreditApprovalExperiment(new BaselineMedian(), new File(new File("output", "median"), "credit"));
        credit.run();
        credit = null;
        System.gc();

        // Pima Indians
//        Experiment pima = ExperimentSetups.getPimaIndiansDiabetesExperiments(new BaselineMedian(), new File(new File("output", "median"), "pima-indians-diabetes"));
//        pima.run();
//        pima = null;
//        System.gc();
    }
}
