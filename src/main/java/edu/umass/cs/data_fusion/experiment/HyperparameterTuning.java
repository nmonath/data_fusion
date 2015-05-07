package main.java.edu.umass.cs.data_fusion.experiment;


import main.java.edu.umass.cs.data_fusion.algorithm.ModifiedTruthFinder;
import main.java.edu.umass.cs.data_fusion.algorithm.TruthFinder;
import main.java.edu.umass.cs.data_fusion.data_structures.Pair;
import main.java.edu.umass.cs.data_fusion.data_structures.RecordCollection;
import main.java.edu.umass.cs.data_fusion.load.LoadBooks;
import main.java.edu.umass.cs.data_fusion.load.LoadStocks;
import main.java.edu.umass.cs.data_fusion.load.LoadTSVFile;
import main.java.edu.umass.cs.data_fusion.load.LoadWeather;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HyperparameterTuning {
    
    
    private static String hyperparameterString(double initialTrustworthinesses, double deltas, double rho, double gamma) {
        return String.format("Initial Trustworthiness: %f, Delta: %f, Rho: %f, Gamma: %f ", initialTrustworthinesses, deltas, rho, gamma);
    }
    
    public static void tuneTruthFinder(boolean useModified, boolean evaluateWithSlack, LoadTSVFile loader, File outputDir, RecordCollection dataset, RecordCollection gold) {

        double[] initialTrustworthinesses = {0.5,0.7,0.8};
        double[] deltas = {0.001};
        double[] rhos = {0.4,0.5,0.6,0.7};
        double[] gammas = {0.1,0.01,0.001,0.0001};

        
        int numConfigs = initialTrustworthinesses.length * deltas.length * rhos.length * gammas.length;

        List<Pair<String,Double>> errorRate = new ArrayList<Pair<String, Double>>(numConfigs);
        List<Pair<String,Double>> mnad = new ArrayList<Pair<String, Double>>(numConfigs);

        int numConfigsComplete = 0;
        for (double init_trust : initialTrustworthinesses) {
            for (double delta : deltas ) {
                for (double rho : rhos) {
                    for (double gamma : gammas) {
                        System.out.println("[HyperParameterTuning] Completed " + numConfigsComplete + " of " + numConfigs + " configurations");
                        String config = hyperparameterString(init_trust,delta,rho,gamma);
                        System.out.println("[HyperParameterTuning] Evaluating: " + config);
                        TruthFinder truthFinder;
                        if (useModified)
                            truthFinder = new ModifiedTruthFinder(init_trust,delta,rho,gamma);
                        else
                            truthFinder= new TruthFinder(init_trust,delta,rho,gamma);
                        Experiment exp = new Experiment(truthFinder,evaluateWithSlack,loader,dataset,gold,new File(outputDir,config.replaceAll(" ", "_")));
                        exp.run();
                        errorRate.add(new Pair<String, Double>(config, exp.getErrorRate()));
                        mnad.add(new Pair<String,Double>(config,exp.getMnad()));
                        numConfigsComplete++;
                    }
                }
            }
        }

//        Collections.sort(errorRate, new Comparator<Pair<String, Double>>() {
//            @Override
//            public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
//                return (int) Math.ceil((o2.two - o1.two)*100);
//            }
//        });
//
//        Collections.sort(mnad, new Comparator<Pair<String, Double>>() {
//            @Override
//            public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
//                return (int) Math.ceil((o2.two - o1.two)*100);
//            }
//        });
        printHyperparameterReport(errorRate,outputDir, "errorrate.txt");
        printHyperparameterReport(mnad,outputDir, "mnad.txt");
    } 
    
    
    public static void printHyperparameterReport(List<Pair<String,Double>> results, File outputDir, String filename) {
        StringBuilder sb = new StringBuilder(1000);
        int i = 0;
        for (Pair<String, Double> pair : results) {
            sb.append(i + " | " + pair.two + " | " + pair.one + "\n");
            i++;
        }
        System.out.println(sb.toString());
        try {
            PrintWriter printWriter = new PrintWriter(new File(outputDir, filename));
            printWriter.println(sb.toString());
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void tuneTruthFinderOnJulySevenStock() {
        LoadStocks loader = new LoadStocks();
        RecordCollection collection = loader.load(new File(new File("data","clean_stock"),"stock-2011-07-07.txt"));
        RecordCollection gold = loader.loadGold(new File(new File("data","nasdaq_truth"),"stock-2011-07-07-nasdaq-com.txt"));
        File outputDir = new File("hyperparameters-normal-tf", "july7");
        tuneTruthFinder(false,true,loader,outputDir,collection,gold);
    }

    public static void tuneModifiedTruthFinderOnJulySevenStock() {
        LoadStocks loader = new LoadStocks();
        RecordCollection collection = loader.load(new File(new File("data","clean_stock"),"stock-2011-07-07.txt"));
        RecordCollection gold = loader.loadGold(new File(new File("data","nasdaq_truth"),"stock-2011-07-07-nasdaq-com.txt"));
        File outputDir = new File("hyperparameters-modified-tf", "july7");
        tuneTruthFinder(true,true,loader,outputDir,collection,gold);
    }


    public static void tuneTruthFinderOnBooks(boolean useModified, File outputDir, RecordCollection dataset, RecordCollection gold) {

        double[] initialTrustworthinesses = {0.5,0.7,0.8};
        double[] deltas = {0.001};
        double[] rhos = {0.4,0.5,0.6,0.7};
        double[] gammas = {0.1,0.01,0.001,0.0001};

        LoadBooks loader = new LoadBooks();

        int numConfigs = initialTrustworthinesses.length * deltas.length * rhos.length * gammas.length;

        List<Pair<String,Double>> accuracy = new ArrayList<Pair<String, Double>>(numConfigs);

        int numConfigsComplete = 0;
        for (double init_trust : initialTrustworthinesses) {
            for (double delta : deltas ) {
                for (double rho : rhos) {
                    for (double gamma : gammas) {
                        System.out.println("[HyperParameterTuning] Completed " + numConfigsComplete + " of " + numConfigs + " configurations");
                        String config = hyperparameterString(init_trust,delta,rho,gamma);
                        System.out.println("[HyperParameterTuning] Evaluating: " + config);
                        TruthFinder truthFinder;
                        if (useModified)
                            truthFinder = new ModifiedTruthFinder(init_trust,delta,rho,gamma);
                        else
                            truthFinder= new TruthFinder(init_trust,delta,rho,gamma);
                        BookExperiment exp = new BookExperiment(truthFinder,loader,dataset,gold,new File(outputDir,config.replaceAll(" ", "_")));
                        exp.run();
                        accuracy.add(new Pair<String, Double>(config, exp.getAccuracy()));
                        numConfigsComplete++;
                    }
                }
            }
        }

//        Collections.sort(accuracy, new Comparator<Pair<String, Double>>() {
//            @Override
//            public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
//                return (int) Math.ceil((o2.two - o1.two)*100);
//            }
//        });
        
        printHyperparameterReport(accuracy,outputDir, "accuracy.txt");
    }



    public static void tuneTruthFinderOnBooks() {

        LoadBooks loader = new LoadBooks();
        RecordCollection collection = loader.load(new File(new File("data", "book"), "book.txt"));
        RecordCollection gold = loader.loadGold(new File(new File("data", "book"), "book_golden.txt"));
        File outputDir = new File("hyperparameters-normal-tf", "book");
        tuneTruthFinderOnBooks(false,outputDir, collection, gold);
    }


    public static void tuneModifiedTruthFinderOnBooks() {

        LoadBooks loader = new LoadBooks();
        RecordCollection collection = loader.load(new File(new File("data", "book"), "book.txt"));
        RecordCollection gold = loader.loadGold(new File(new File("data", "book"), "book_golden.txt"));
        File outputDir = new File("hyperparameters-modified-tf", "book");
        tuneTruthFinderOnBooks(true,outputDir, collection, gold);
    }

    public static void tuneTruthFinderOnWeather() {
        LoadWeather loader = new LoadWeather();
        RecordCollection collection = loader.load(new File(new File("data", "weather"), "weather_data_set.txt"));
        RecordCollection gold = loader.loadGold(new File(new File("data", "weather"), "weather_ground_truth.txt"));
        File outputDir = new File("hyperparameters-normal-tf", "weather");
        tuneTruthFinder(false,false,loader,outputDir,collection,gold);
    }

    public static void tuneModifiedTruthFinderOnWeather() {
        LoadWeather loader = new LoadWeather();
        RecordCollection collection = loader.load(new File(new File("data", "weather"), "weather_data_set.txt"));
        RecordCollection gold = loader.loadGold(new File(new File("data", "weather"), "weather_ground_truth.txt"));
        File outputDir = new File("hyperparameters-modified-tf", "weather");
        tuneTruthFinder(true,false,loader,outputDir,collection,gold);
    }
    
    



}
