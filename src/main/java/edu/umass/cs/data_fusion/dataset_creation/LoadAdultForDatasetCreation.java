package edu.umass.cs.data_fusion.dataset_creation;

import edu.umass.cs.data_fusion.data_structures.AttributeDataType;
import edu.umass.cs.data_fusion.data_structures.AttributeType;

public class LoadAdultForDatasetCreation extends LoadUCIDatasetForDatasetCreation {
    
    public static String[] names = {
            "age", "workclass","fnlwgt",
            "education", "education-num","marital-status",
            "occupation", "relationship", "race", 
            "sex", "capital-gain", "capital-loss", 
            "hours-per-week", "native-country", "income-class"
    };
    
    public static AttributeDataType[] dataTypes = {
            AttributeDataType.FLOAT,AttributeDataType.STRING,AttributeDataType.FLOAT,
            AttributeDataType.STRING,AttributeDataType.FLOAT,AttributeDataType.STRING,
            AttributeDataType.STRING, AttributeDataType.STRING,AttributeDataType.STRING,
            AttributeDataType.STRING, AttributeDataType.FLOAT,AttributeDataType.FLOAT,
            AttributeDataType.FLOAT, AttributeDataType.STRING, AttributeDataType.STRING
    };
    
    public static AttributeType[] types = {
            AttributeType.CONTINUOUS,AttributeType.CATEGORICAL,AttributeType.CONTINUOUS,
            AttributeType.CATEGORICAL,AttributeType.CATEGORICAL,AttributeType.CATEGORICAL,
            AttributeType.CATEGORICAL, AttributeType.CATEGORICAL,AttributeType.CATEGORICAL,
            AttributeType.CATEGORICAL, AttributeType.CONTINUOUS,AttributeType.CONTINUOUS,
            AttributeType.CONTINUOUS, AttributeType.CATEGORICAL, AttributeType.CATEGORICAL
    };

    public LoadAdultForDatasetCreation() {
        super(",", "?",names,dataTypes,types);
    }

}
