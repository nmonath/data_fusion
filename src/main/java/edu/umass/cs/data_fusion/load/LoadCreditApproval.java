package edu.umass.cs.data_fusion.load;


import edu.umass.cs.data_fusion.data_structures.AttributeDataType;
import edu.umass.cs.data_fusion.data_structures.AttributeType;
import edu.umass.cs.data_fusion.dataset_creation.LoadUCIDatasetForDatasetCreation;


public class LoadCreditApproval extends LoadUCIDataset {

    public static String[] names = {
            "A1", "A2","A3",
            "A4", "A5","A6",
            "A7", "A8", "A9",
            "A10", "A11", "A12",
            "A13", "A14", "A15",
            "A16"
    };

    public static AttributeDataType[] dataTypes = {
            AttributeDataType.STRING,AttributeDataType.FLOAT,AttributeDataType.FLOAT,
            AttributeDataType.STRING,AttributeDataType.STRING,AttributeDataType.STRING,
            AttributeDataType.STRING,AttributeDataType.FLOAT,AttributeDataType.STRING,
            AttributeDataType.STRING,AttributeDataType.FLOAT,AttributeDataType.STRING,
            AttributeDataType.STRING,AttributeDataType.FLOAT,AttributeDataType.FLOAT,
            AttributeDataType.STRING
    };

    public static AttributeType[] types = {
            AttributeType.CATEGORICAL,AttributeType.CONTINUOUS,AttributeType.CONTINUOUS,
            AttributeType.CATEGORICAL,AttributeType.CATEGORICAL,AttributeType.CATEGORICAL,
            AttributeType.CATEGORICAL,AttributeType.CONTINUOUS,AttributeType.CATEGORICAL,
            AttributeType.CATEGORICAL,AttributeType.CONTINUOUS,AttributeType.CATEGORICAL,
            AttributeType.CATEGORICAL,AttributeType.CONTINUOUS,AttributeType.CONTINUOUS,
            AttributeType.CATEGORICAL
    };

    public LoadCreditApproval() {
        super(names,dataTypes,types);
    }
}
