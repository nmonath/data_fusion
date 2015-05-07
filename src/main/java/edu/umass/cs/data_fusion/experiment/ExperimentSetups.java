package edu.umass.cs.data_fusion.experiment;

import edu.umass.cs.data_fusion.data_structures.Algorithm;
import edu.umass.cs.data_fusion.load.LoadAdult;
import edu.umass.cs.data_fusion.load.LoadBooks;
import edu.umass.cs.data_fusion.load.LoadCreditApproval;
import edu.umass.cs.data_fusion.load.LoadStocks;
import edu.umass.cs.data_fusion.load.LoadPimaIndiansDiabetes;
import edu.umass.cs.data_fusion.load.LoadWeather;

import java.io.File;

public class ExperimentSetups {
    
    public static Experiment getStockExperiment(Algorithm algorithm, boolean evaluationWithSlack, File inputFile,File goldFile,File outputDir) {
        return new Experiment(algorithm,evaluationWithSlack,new LoadStocks(),inputFile,goldFile,outputDir);
    }
    
    public static Experiment getJulySeventhStockExperiment(Algorithm algorithm, boolean evaluationWithSlack, File outputDir) {
        return getStockExperiment(algorithm,evaluationWithSlack, new File(new File("data","clean_stock"),"stock-2011-07-07.txt"), new File(new File("data","nasdaq_truth"),"stock-2011-07-07-nasdaq-com.txt"),outputDir);
    }
    
    public static Experiment getFullStockExperiment(Algorithm algorithm, boolean evaluationWithSlack, File outputDir) {
        return getStockExperiment(algorithm,evaluationWithSlack, new File(new File("data","stock"),"clean_stock_rawdata"), new File(new File("data","stock"),"nasdaq_truth_golddata"),outputDir);
        
    }
    public static Experiment getWeatherExperiment(Algorithm algorithm, File outputDir) {
        return new Experiment(algorithm,false,new LoadWeather(),new File(new File("data", "weather"), "weather_data_set.txt"),new File(new File("data", "weather"), "weather_ground_truth.txt"),outputDir);
    }
    
    public static Experiment getBookExperiment(Algorithm algorithm, File outputDir) {
        return new BookExperiment(algorithm,new LoadBooks(),new File(new File("data", "book"), "book.txt"),new File(new File("data", "book"), "book_golden.txt"),outputDir);
    }
    
    public static Experiment getAdultExperiment(Algorithm algorithm, File outputDir) {
        return new Experiment(algorithm,false, new LoadAdult(), new File(new File("data", "adult"), "adult_noisy.tsv"),new File(new File("data", "adult"), "adult_gold.tsv"),outputDir);
    }

    public static Experiment getCreditApprovalExperiment(Algorithm algorithm, File outputDir) {
        return new Experiment(algorithm,false, new LoadCreditApproval(), new File(new File("data", "credit"), "crx_noisy.tsv"),new File(new File("data", "credit"), "crx.tsv"),outputDir);
    }

    public static Experiment getPimaIndiansDiabetesExperiments(Algorithm algorithm, File outputDir) {
        return new Experiment(algorithm,false, new LoadPimaIndiansDiabetes(), new File(new File("data", "pima-indians-diabetes"), "pima-indians-diabetes_noisy.tsv"),new File(new File("data", "pima-indians-diabetes"), "pima-indians-diabetes.tsv"),outputDir);
    }
    
}
