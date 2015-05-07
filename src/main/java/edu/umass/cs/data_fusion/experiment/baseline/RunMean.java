package edu.umass.cs.data_fusion.experiment.baseline;

import edu.umass.cs.data_fusion.algorithm.BaselineMean;
import edu.umass.cs.data_fusion.algorithm.MajorityVote;
import edu.umass.cs.data_fusion.experiment.Experiment;
import edu.umass.cs.data_fusion.experiment.ExperimentSetups;

import java.io.File;

public class RunMean {

    public static void main(String[] args) {


        // Full Stock
        Experiment fullStock = ExperimentSetups.getFullStockExperiment(new BaselineMean(),true, new File(new File("output","mean"), "fullstock"));
        fullStock.run();
        fullStock = null;
        System.gc();

        // July 7 Stock
        Experiment july7 = ExperimentSetups.getJulySeventhStockExperiment(new BaselineMean(), true, new File(new File("output", "mean"), "july7"));
        july7.run();
        july7 = null;
        System.gc();

        // Weather
        Experiment weather = ExperimentSetups.getWeatherExperiment(new BaselineMean(), new File(new File("output", "mean"), "weather"));
        weather.run();
        weather = null;
        System.gc();

        // Adult
        Experiment adult = ExperimentSetups.getAdultExperiment(new BaselineMean(), new File(new File("output", "mean"), "adult"));
        adult.run();
        adult = null;
        System.gc();

        // Credit
        Experiment credit = ExperimentSetups.getCreditApprovalExperiment(new BaselineMean(), new File(new File("output", "mean"), "credit"));
        credit.run();
        credit = null;
        System.gc();

//        // Pima Indians
//        Experiment pima = ExperimentSetups.getPimaIndiansDiabetesExperiments(new BaselineMean(), new File(new File("output", "mean"), "pima-indians-diabetes"));
//        pima.run();
//        pima = null;
//        System.gc();
    }
}
