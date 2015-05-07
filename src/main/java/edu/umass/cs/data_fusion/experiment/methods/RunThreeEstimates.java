package edu.umass.cs.data_fusion.experiment.methods;
import java.io.File;

import edu.umass.cs.data_fusion.experiment.Experiment;
import edu.umass.cs.data_fusion.experiment.ExperimentSetups;
import edu.umass.cs.data_fusion.algorithm.ThreeEstimate;


public class RunThreeEstimates {
	public static void main(String[] args) {
		
		//July 7 stock 
		Experiment ThreeEstOneDayStock = ExperimentSetups.getJulySeventhStockExperiment(new ThreeEstimate(), true, new File("output_stock_july7"));
        ThreeEstOneDayStock.run();
        
		// full stock
		Experiment ThreeEstFullStock = ExperimentSetups.getFullStockExperiment(new ThreeEstimate(), true,new File("fullstock_output"));
		ThreeEstFullStock.run();
        
        //weather
		Experiment ThreeEstWeather = ExperimentSetups.getWeatherExperiment(new ThreeEstimate(), new File("output_weather"));
		ThreeEstWeather.run();
		
		// book
		Experiment ThreeEstBook = ExperimentSetups.getBookExperiment(new ThreeEstimate(), new File("output_book"));
		ThreeEstBook.run();
		
		// Adult dataset
		Experiment ThreeEstAdult = ExperimentSetups.getAdultExperiment(new ThreeEstimate(), new File("output_adult"));
		ThreeEstAdult.run();
		
		// Credit
		Experiment ThreeEstCredit = ExperimentSetups.getCreditApprovalExperiment(new ThreeEstimate(), new File("output_credit"));
		ThreeEstCredit.run();
		
		//Pima Indians Diabetes 
		Experiment ThreeEstDiabetes = ExperimentSetups.getPimaIndiansDiabetesExperiments(new ThreeEstimate(), new File("output_diabetes"));
		ThreeEstDiabetes.run();

	}
      
}
