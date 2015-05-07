package main.java.edu.umass.cs.data_fusion.dataset_creation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import main.java.edu.umass.cs.data_fusion.data_structures.Attribute;
import main.java.edu.umass.cs.data_fusion.data_structures.AttributeType;
import main.java.edu.umass.cs.data_fusion.data_structures.Entity;
import main.java.edu.umass.cs.data_fusion.data_structures.FloatAttribute;
import main.java.edu.umass.cs.data_fusion.data_structures.Record;
import main.java.edu.umass.cs.data_fusion.data_structures.RecordCollection;
import main.java.edu.umass.cs.data_fusion.data_structures.Source;
import main.java.edu.umass.cs.data_fusion.data_structures.StringAttribute;
import main.java.edu.umass.cs.data_fusion.data_structures.SyntheticSource;

public class CreateSyntheticDataset {
	//for CONTINUOUS attributes get the possible values that the attribute can take 
	HashMap<String, ArrayList<String>> attrValues;
    
    // place holder code
    public RecordCollection createModifiedDataset(RecordCollection gold, ArrayList<SyntheticSource> sources) {
        // Perform modification
    	
    	RecordCollection returnCollection = null;
    	//get the possible attribute values for continuous values
		attrValues = getPossibleAttrValues(gold);
    	
//		//output the found attribute and possible values
//		for(Entry<String, ArrayList<String>> entry : attrValues.entrySet()) {
//			System.out.println("===== " + entry.getKey());
//			for(String val : entry.getValue()) {
//				System.out.println("    " + val);
//			}
//		}
    	
    	for(SyntheticSource source : sources) {
    		//modify data according to this source
    		RecordCollection tempCollection = modifyData(gold, source);
    		
    		if(returnCollection == null) {
    			//initialize rcord collections
    			returnCollection = tempCollection;
    		}
    		else
    		{
    			//merge recordcollections
    			ArrayList<Record> rec1 = tempCollection.getRecords();
    			ArrayList<Record> rec2 = returnCollection.getRecords();
    			ArrayList<Record> combined = new ArrayList<Record>();
    			combined.addAll(rec1);
    			combined.addAll(rec2);
    			returnCollection = new RecordCollection(combined);
    		}
    	}
    	
		return returnCollection;
    }
    
    
    //create a data set according to the probabilities in the source
    public RecordCollection modifyData(RecordCollection gold, SyntheticSource synth)
    {
    	ArrayList<Record> records = gold.getRecords();
    	ArrayList<Record> modifiedRecords = new ArrayList<Record>();
    	
    	
    	int i = 0;
    	int same = 0;
    	int change = 0;
    	for(Record r : records) {
    		Entity entity = r.getEntity();
    		HashMap<String, Attribute> attributes = r.getAttributes();
    		HashMap<String, Attribute> modifiedAttrs = new HashMap<String, Attribute>();

			int attr = 0;
    		for(Entry<String, Attribute> entry : attributes.entrySet()) {
    			if(entry.getValue() instanceof FloatAttribute) {
    				float curVal = ((FloatAttribute)entry.getValue()).getFloatValue();
    				float noisyVal = (float)getNoisyValue(curVal, synth.getSigma());
    				
    				FloatAttribute newFloatAttr = new FloatAttribute(entry.getKey(), noisyVal, AttributeType.CONTINUOUS);
    				modifiedAttrs.put(entry.getKey(), newFloatAttr);
    			}
    			
    			if(entry.getValue() instanceof StringAttribute) {
    				double rand = new Random().nextDouble();
    				
    				if(rand < synth.getChangeProb()) {
    					//select random value
    					change++;
    					String noisyVal = getRandomAttrValue(entry.getKey());
    					
    					StringAttribute newStringAttr = new StringAttribute(entry.getKey(), noisyVal, AttributeType.CATEGORICAL);
        				
    					modifiedAttrs.put(entry.getKey(), newStringAttr);
    				}
    				else {
    					//value stays the same
    					same++;
    					modifiedAttrs.put(entry.getKey(), entry.getValue());
    				}
    			}
    			
    			attr++;

    		}
    		
    		modifiedRecords.add(new Record(new Source(synth.getName()), entity, modifiedAttrs));

			i++;
    	}
    	System.out.println("changed: " + (change / (float)(change+same)));
    	
    	return new RecordCollection(modifiedRecords);
    }
    
    //TODO: normalize
    public static double getNoisyValue(double mean, double sigma) {
    	   Random rand = new Random();
    	   return mean + sigma * rand.nextGaussian();
    }
    
    //get a random value of a given attribute name
    public String getRandomAttrValue(String attrName) {
    	String returnString = "";
    	
    	ArrayList<String> possibleValues = attrValues.get(attrName);
    	
    	//get random value
    	returnString = possibleValues.get(new Random().nextInt(possibleValues.size()));
    	
    	return returnString;
    }
    
    //calculate the possible values that each attribute can take
    public HashMap<String, ArrayList<String>> getPossibleAttrValues(RecordCollection gold) {
    	ArrayList<Record> records = gold.getRecords();
    	
    	HashMap<String, ArrayList<String>> returnHashmap = new HashMap<String, ArrayList<String>>();
    	

    	for(Record r : records) {
    		HashMap<String, Attribute> attributes = r.getAttributes();

			int attr = 0;
    		for(Entry<String, Attribute> entry : attributes.entrySet()) {
    			//only look at continuouse values
    			if(entry.getValue() instanceof StringAttribute) {
    				String attrName = entry.getKey();
    				String value = ((StringAttribute)entry.getValue()).getStringValue();
    				
    				if(returnHashmap.containsKey(attrName))
    				{
    					if(!returnHashmap.get(attrName).contains(value))
    						 returnHashmap.get(attrName).add(value);
    				}
    				else
    				{
    					ArrayList<String> list = new ArrayList<String>();
    					list.add(value);
    					returnHashmap.put(attrName, list);
    				}
    			}
    		}
    	}
    	return returnHashmap;
    }
}
