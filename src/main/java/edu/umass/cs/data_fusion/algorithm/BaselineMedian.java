package edu.umass.cs.data_fusion.algorithm;

import edu.umass.cs.data_fusion.data_structures.*;
import edu.umass.cs.data_fusion.util.Functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BaselineMedian extends Algorithm{


    public BaselineMedian() {
        super("baseline-median");
    }

    @Override
    public ArrayList<Result> execute(RecordCollection recordCollection) {
        System.out.println(infoString(recordCollection));
        ArrayList<Result> results = new ArrayList<Result>();
        for (Entity entity: recordCollection.getEntities()) {
            Result res = new Result(this.source,entity);
            Set<String> attributeNames = recordCollection.getAttributes(entity);
            List<Record> recordsForEntity = recordCollection.getRecords(entity);
            for (String attrName : attributeNames) {
                List<Attribute> attributes = allValuesForAttribute(recordsForEntity, attrName);
                AttributeType type = Attribute.getType(attributes);
                AttributeDataType datatype = Attribute.getDataType(attributes);
                if (type == AttributeType.CONTINUOUS) {
                    if (datatype == AttributeDataType.FLOAT) {
                        List<Float> values = new ArrayList<Float>(attributes.size());
                        for (Attribute attr: attributes) {
                            values.add(( (FloatAttribute) attr).getFloatValue());
                        }
                        float median = Functions.getMedian(values);
                        res.addAttribute(new FloatAttribute(attrName,median,AttributeType.CONTINUOUS));
                    } else {
                        System.out.println("[BaselineMean] Sorry only continuous Float attributes are support with this fusion algorithm.");
                    }
                }
            }
            results.add(res);
        }
        return results;
    }
}
