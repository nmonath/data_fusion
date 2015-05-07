package edu.umass.cs.data_fusion.load;

import edu.umass.cs.data_fusion.data_structures.AttributeDataType;
import edu.umass.cs.data_fusion.data_structures.AttributeType;


public class LoadPimaIndiansDiabetes extends LoadUCIDataset {

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

    public LoadPimaIndiansDiabetes() {
        super(names,dataTypes,types);
    }
}
