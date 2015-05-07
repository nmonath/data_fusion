package main.java.edu.umass.cs.data_fusion.dataset_creation;

import main.java.edu.umass.cs.data_fusion.data_structures.AttributeDataType;
import main.java.edu.umass.cs.data_fusion.data_structures.AttributeType;


public class LoadPimaIndiansDiabetesForDatasetCreation extends LoadUCIDatasetForDatasetCreation {

    public static String[] names = {
            "NumberOfTimesPregnant", "PlasmaGlucoseConcentration","DiastolicBloodPressure",
            "TricepsSkinFoldThickness", "SerumInsulin","BodyMassIndex",
            "DiabetesPedigreeFunction", "Age", "Diabetic"
    };

    public static AttributeDataType[] dataTypes = {
            AttributeDataType.STRING,AttributeDataType.FLOAT,AttributeDataType.FLOAT,
            AttributeDataType.FLOAT,AttributeDataType.FLOAT,AttributeDataType.FLOAT,
            AttributeDataType.FLOAT,AttributeDataType.FLOAT,AttributeDataType.FLOAT,
            AttributeDataType.FLOAT,AttributeDataType.FLOAT,AttributeDataType.FLOAT,
            AttributeDataType.FLOAT, AttributeDataType.STRING, AttributeDataType.STRING
    };

    public static AttributeType[] types = {
            AttributeType.CATEGORICAL,AttributeType.CONTINUOUS,AttributeType.CONTINUOUS,
            AttributeType.CONTINUOUS,AttributeType.CONTINUOUS,AttributeType.CONTINUOUS,
            AttributeType.CONTINUOUS,AttributeType.CONTINUOUS,AttributeType.CONTINUOUS,
            AttributeType.CONTINUOUS,AttributeType.CATEGORICAL,AttributeType.CATEGORICAL,
    };

    public LoadPimaIndiansDiabetesForDatasetCreation() {
        super(",", "?",names,dataTypes,types);
    }
}
