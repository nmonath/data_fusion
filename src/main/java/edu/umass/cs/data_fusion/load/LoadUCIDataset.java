package edu.umass.cs.data_fusion.load;

import edu.umass.cs.data_fusion.data_structures.*;

import java.io.File;

/**
 * This class is for loading the synthetic datasets that we create. *
 */
public class LoadUCIDataset extends LoadTSVFile{

    
    public LoadUCIDataset(String[] orderedAttributeNames, AttributeDataType[] attributeDataTypes, AttributeType[] attributeTypes) {
        super(orderedAttributeNames, attributeDataTypes, attributeTypes);
    }

    // The gold files that I created have "Gold" as the source so they are the same format as the 
    // other files. 
    @Override
    public RecordCollection loadGold(File file) {
        return load(file);
    }

    // Just use simple parse for speed?
    @Override
    protected Attribute getFloatAttributeFromString(String name, String rawValue, AttributeType type) {
        float floatValue = Float.MAX_VALUE;
        try {
            floatValue = Float.parseFloat(rawValue);
        } catch (NumberFormatException e) {
            System.out.println("\nCouldn't parse: " + rawValue);
        }
        if (floatValue == Float.MAX_VALUE)
            return null;
        FloatAttribute flt = new FloatAttribute(name,floatValue,type);
        return (flt.isValidFloat() ? flt : null);
    }
}
