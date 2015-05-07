package main.java.edu.umass.cs.data_fusion.experiment.baseline;


import main.java.edu.umass.cs.data_fusion.algorithm.SpecificSource;
import main.java.edu.umass.cs.data_fusion.data_structures.Pair;
import main.java.edu.umass.cs.data_fusion.data_structures.RecordCollection;
import main.java.edu.umass.cs.data_fusion.data_structures.Source;
import main.java.edu.umass.cs.data_fusion.experiment.BookExperiment;
import main.java.edu.umass.cs.data_fusion.experiment.Experiment;
import main.java.edu.umass.cs.data_fusion.load.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TopSourcesPerDataset {
    
    public static void bestSourcesPerDataset(boolean useSlack, LoadTSVFile loader, File outputDir, RecordCollection input, RecordCollection gold) {

        Set<Source> sources = input.getSources();
        
        List<Pair<String, Double>> errorRate = new ArrayList<Pair<String, Double>>(sources.size());
        List<Pair<String, Double>> mnad = new ArrayList<Pair<String, Double>>(sources.size());
        
        for (Source s : sources) {
            System.out.println("[TopSourcesPerDataset] Evaluating source " + s);
            Experiment exp = new Experiment(new SpecificSource(s.getName()),useSlack,loader,input,gold, new File(outputDir, s.getName()));
            exp.run();
            errorRate.add(new Pair<String, Double>(s.getName(), exp.getErrorRate()));
            mnad.add(new Pair<String,Double>(s.getName(),exp.getMnad()));
        }
        printReport(errorRate,outputDir, "errorRate.txt");
        printReport(mnad,outputDir, "mnad.txt");

    }

    public static void bestSourcesPerDatasetBooks(File outputDir, RecordCollection input, RecordCollection gold) {

        Set<Source> sources = input.getSources();

        List<Pair<String, Double>> accuracy = new ArrayList<Pair<String, Double>>(sources.size());

        for (Source s : sources) {
            System.out.println("[TopSourcesPerDataset] Evaluating source " + s);
            BookExperiment exp = new BookExperiment(new SpecificSource(s.getName()),new LoadBooks(),input,gold, new File(outputDir, s.getName()));
            exp.run();
            accuracy.add(new Pair<String, Double>(s.getName(), exp.getAccuracy()));
        }
        printReport(accuracy,outputDir, "accuracy.txt");
    }

    public static void printReport(List<Pair<String,Double>> results, File outputDir, String filename) {
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
    
    public static void runOnBooks() {
        LoadTSVFile loader = new LoadBooks();
        RecordCollection collection = loader.load(new File(new File("data", "book"), "book.txt"));
        RecordCollection gold = loader.loadGold(new File(new File("data", "book"), "book_golden.txt"));
        File outputDir = new File("top_sources", "book");
        bestSourcesPerDatasetBooks(outputDir, collection, gold);
    }
    
    public static void runOnJuly7Stock() {
        LoadStocks loader = new LoadStocks();
        RecordCollection collection = loader.load(new File(new File("data","clean_stock"),"stock-2011-07-07.txt"));
        RecordCollection gold = loader.loadGold(new File(new File("data","nasdaq_truth"),"stock-2011-07-07-nasdaq-com.txt"));
        File outputDir = new File("top_sources", "july7");
        bestSourcesPerDataset(true, loader, outputDir, collection, gold);
    }

    public static void runOnFullStock() {
        LoadStocks loader = new LoadStocks();
        RecordCollection collection = loader.load(new File(new File("data","stock"),"clean_stock_rawdata"));
        RecordCollection gold = loader.loadGold(new File(new File("data","stock"),"nasdaq_truth_golddata"));
        File outputDir = new File("top_sources", "full_stock");
        bestSourcesPerDataset(true,loader,outputDir,collection,gold);
    }
    
    public static void runOnWeather() {
        LoadWeather loader = new LoadWeather();
        RecordCollection collection = loader.load(new File(new File("data", "weather"), "weather_data_set.txt"));
        RecordCollection gold = loader.loadGold(new File(new File("data", "weather"), "weather_ground_truth.txt"));
        File outputDir = new File("top_sources", "weather");
        bestSourcesPerDataset(false, loader, outputDir, collection, gold);
    }
    
    public static void runOnAdult() {
        LoadAdult loader = new LoadAdult();
        RecordCollection collection = loader.load(new File(new File("data", "adult"), "adult_noisy.tsv"));
        RecordCollection gold = loader.loadGold(new File(new File("data", "adult"), "adult_gold.tsv"));
        File outputDir = new File("top_sources", "adult");
        bestSourcesPerDataset(false, loader, outputDir, collection, gold);
    }
    
    public static void runOnCredit() {
        LoadCreditApproval loader = new LoadCreditApproval();
        RecordCollection collection = loader.load(new File(new File("data", "credit"), "crx_noisy.tsv"));
        RecordCollection gold = loader.loadGold(new File(new File("data", "credit"), "crx.tsv"));
        File outputDir = new File("top_sources", "credit");
        bestSourcesPerDataset(false, loader, outputDir, collection, gold);
    }
    
    
    public static void main(String[] args) {
        runOnBooks();
        System.gc();
        runOnJuly7Stock();
        System.gc();
        runOnFullStock();
        System.gc();
        runOnWeather();
        System.gc();
        runOnAdult();
        System.gc();
        runOnCredit();
        System.gc();
    }

}
