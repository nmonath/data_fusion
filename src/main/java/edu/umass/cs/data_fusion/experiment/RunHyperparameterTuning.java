package edu.umass.cs.data_fusion.experiment;


public class RunHyperparameterTuning {
    
    
    public static void main(String[] args) {

        // Books
        HyperparameterTuning.tuneTruthFinderOnBooks();
        System.gc();
        HyperparameterTuning.tuneModifiedTruthFinderOnBooks();
        System.gc();

        // July7
        HyperparameterTuning.tuneTruthFinderOnJulySevenStock();
        System.gc();
        HyperparameterTuning.tuneModifiedTruthFinderOnJulySevenStock();
        System.gc();

        // Weather
        HyperparameterTuning.tuneTruthFinderOnWeather();
        System.gc();
        HyperparameterTuning.tuneModifiedTruthFinderOnWeather();
        System.gc();
    }
}
