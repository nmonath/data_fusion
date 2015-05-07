package edu.umass.cs.data_fusion.load;

import edu.umass.cs.data_fusion.algorithm.TruthFinder;
import edu.umass.cs.data_fusion.data_structures.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class LoadStocks extends LoadTSVFile {

    public static String[] names = {
    	"Percent Change", "Last Trading Price", "Open Price", 
    	"Change $", "Volume", "Today's High", 
    	"Today's Low", "Previous Close", "52wk High", 
    	"52wk Low", "Shares Outstanding", "P/E", 
    	"Market Cap", "Yield", "Dividend", 
    	"EPS"};
    // similarity
    public static AttributeDataType[] dataTypes = {
			AttributeDataType.FLOAT, AttributeDataType.FLOAT, AttributeDataType.FLOAT,
			AttributeDataType.FLOAT, AttributeDataType.FLOAT, AttributeDataType.FLOAT, 
			AttributeDataType.FLOAT, AttributeDataType.FLOAT, AttributeDataType.FLOAT,
			AttributeDataType.FLOAT, AttributeDataType.FLOAT, AttributeDataType.FLOAT,
			AttributeDataType.FLOAT, AttributeDataType.FLOAT, AttributeDataType.FLOAT, 
			AttributeDataType.FLOAT};
    // evaluation
	public static AttributeType[] types = {
			AttributeType.CATEGORICAL, AttributeType.CATEGORICAL, AttributeType.CATEGORICAL,
			AttributeType.CATEGORICAL, AttributeType.CONTINUOUS, AttributeType.CATEGORICAL,
			AttributeType.CATEGORICAL, AttributeType.CATEGORICAL, AttributeType.CATEGORICAL,
			AttributeType.CATEGORICAL, AttributeType.CONTINUOUS, AttributeType.CATEGORICAL,
			AttributeType.CONTINUOUS, AttributeType.CATEGORICAL, AttributeType.CATEGORICAL,
			AttributeType.CATEGORICAL};

//    public static AttributeType[] dataTypes = {
//    	AttributeType.STRING,AttributeType.STRING,AttributeType.STRING,
//    	AttributeType.STRING,AttributeType.STRING,AttributeType.STRING,
//   	AttributeType.STRING,AttributeType.STRING,AttributeType.STRING,
//    	AttributeType.STRING,AttributeType.STRING,AttributeType.STRING,
//    	AttributeType.STRING,AttributeType.STRING,AttributeType.STRING,
//    	AttributeType.STRING};
    public LoadStocks() {
        super(names,dataTypes,types);
    }
    
    public static void testRegex()
    {
    	System.out.println("start");
    	String pathToFile = "/Users/Manuel/Documents/Development/github/DBProject/dataset/";
    	
    	String[] filesToTest = {
        	"clean_stock/stock-2011-07-01.txt",
        	"clean_stock/stock-2011-07-04.txt",
        	"clean_stock/stock-2011-07-05.txt",
        	"clean_stock/stock-2011-07-06.txt",
        	"clean_stock/stock-2011-07-07.txt",
        	"clean_stock/stock-2011-07-08.txt",
        	"clean_stock/stock-2011-07-11.txt",
        	"clean_stock/stock-2011-07-12.txt",
        	"clean_stock/stock-2011-07-13.txt",
        	"clean_stock/stock-2011-07-14.txt",
        	"clean_stock/stock-2011-07-15.txt",
        	"clean_stock/stock-2011-07-18.txt",
        	"clean_stock/stock-2011-07-19.txt",
        	"clean_stock/stock-2011-07-20.txt",
        	"clean_stock/stock-2011-07-21.txt",
        	"clean_stock/stock-2011-07-22.txt",
        	"clean_stock/stock-2011-07-25.txt",
        	"clean_stock/stock-2011-07-26.txt",
        	"clean_stock/stock-2011-07-27.txt",
        	"clean_stock/stock-2011-07-28.txt",
        	"clean_stock/stock-2011-07-29.txt",
        	"nasdaq_truth2/stock-2011-07-01-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-04-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-05-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-06-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-07-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-08-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-11-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-12-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-13-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-14-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-15-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-18-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-19-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-20-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-21-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-22-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-25-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-26-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-27-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-28-nasdaq-com.txt",
        	"nasdaq_truth2/stock-2011-07-29-nasdaq-com.txt"};
    	
    	
    	for(int i = 0; i < filesToTest.length; i++) {
    		LoadStocks loader = new LoadStocks();
            RecordCollection collection = loader.load(new File(pathToFile + filesToTest[i]));
            

    		System.out.println("===== testing " + filesToTest[i] 
    				+ " r: " + collection.getRecordsCount() 
    				+ ", s: " + collection.getSourcesCount() 
    				+ ", e: " + collection.getEntitiesCount());
            ArrayList<Record> records = collection.getRecords();
            
            //vocs has not issued dividends in more than 1 year.		 1.0
            int j = 0;
            int print = 200; //print every XX attribute
            boolean printRegularValues = false;
            for(Record record : records)
            {
            	HashMap<String,Attribute> attrs = record.getAttributes();
            	for(Attribute attr : attrs.values())
            	{
            		if(attr.getClass() == FloatAttribute.class)
            		{
            			FloatAttribute fAttr = (FloatAttribute) attr;
            			if(!fAttr.isValidFloat())
            			{
    	        			System.out.println("f " + fAttr.getName() + "\t\t "
    	            				+ fAttr.getRawValue() + "\t\t " 
    	        					+ fAttr.getFloatValue() + "\t\t " 
    	        					+ fAttr.isValidFloat());
            			}
            			else if(j == print && printRegularValues)
            			{
            				System.out.println("_f " + fAttr.getName() + "\t\t "
    	            				+ fAttr.getRawValue() + "\t\t " 
    	        					+ fAttr.getFloatValue() + "\t\t " 
    	        					+ fAttr.isValidFloat());
            				j = 0;
            			}
            			
            		}
            		else if(attr.getClass() == StringAttribute.class)
            		{
            			StringAttribute sAttr = (StringAttribute) attr;
            			System.out.println("s " + sAttr.getName() + "\t\t "
                				+ sAttr.getRawValue() + "\t\t ");
            		}
            		j++;
            	}
            }
    	}
    	
    }
    
    public static void main(String[] args) {
        LoadStocks loader = new LoadStocks();
        RecordCollection collection = loader.load(new File("/Users/Manuel/Documents/Development/github/DBProject/dataset/clean_stock/stock-2011-07-01.txt"));
        TruthFinder tf = new TruthFinder();
        RecordCollection cleaner = tf.convert(tf.execute(collection));
        cleaner.writeToTSVFile(new File("outputTest.txt"),names);
        
        testRegex();
    }
    
    
    
}
