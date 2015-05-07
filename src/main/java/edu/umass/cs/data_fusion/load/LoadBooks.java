package edu.umass.cs.data_fusion.load;

import edu.umass.cs.data_fusion.data_structures.AttributeDataType;
import edu.umass.cs.data_fusion.data_structures.AttributeType;

public class LoadBooks extends LoadTSVFile{
    public static String[] names = {"Title", "Author(s)"};
    public static AttributeDataType[] dataTypes = {AttributeDataType.STRING,AttributeDataType.AUTHOR_LIST};
    public static AttributeType[] types = {AttributeType.CATEGORICAL,AttributeType.CATEGORICAL};
    
    public LoadBooks() {
        super(names,dataTypes,types);
    }
}
