package edu.umass.cs.data_fusion.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import edu.umass.cs.data_fusion.data_structures.*;


public class LoadWeather extends LoadTSVFile {

	private static String[] names = {"Weather (Categorical)", "Weather (Continuous)"};

	private static AttributeDataType[] dataTypes = {AttributeDataType.STRING,AttributeDataType.FLOAT};

	private static AttributeType[] types = {AttributeType.CATEGORICAL,AttributeType.CONTINUOUS};

	public LoadWeather() {
		super(names, dataTypes, types);
	}
	@Override
	public RecordCollection load(File file) {
		String line = "";
		try {
			ArrayList<Record>  records = new ArrayList<Record>(10000);
            
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
	        line = reader.readLine();
	        int lineNo = 0;
	        String lineCount = "Lines Read: " + lineNo;
			System.out.print(lineCount);
	        while (line != null) {
                String[] fields = line.split("\t");
                
                Record rec = new Record(lineNo, new Source(fields[2]), new Entity(fields[0]));

        		String s = fields[1];
        		if('w' == s.charAt(0))//this is categorical
        		{
        			String rawValue = s; // Keep the W to match output results
        			String name = fields[0];
        			StringAttribute strAttr = new StringAttribute(orderedAttributeNames[0], rawValue, AttributeType.CATEGORICAL);
                    rec.addAttribute(strAttr);

                    //System.out.print(name+"\t" +rawValue+"\t" );
        			
        		}
        		else //continuous
        		{
        			String name = fields[0];
        			String rawValue = fields[1];
					float value = Float.parseFloat(rawValue); // Since the FloatAttribute takes the abs value of the attributes & we don't want that here use the simple parseFloat
        			FloatAttribute floatAttr = new FloatAttribute(orderedAttributeNames[1], value, AttributeType.CONTINUOUS);
                    rec.addAttribute(floatAttr);

                    //System.out.print(name+"\t" +rawValue+"\t" );
        		}
                records.add(rec);

                line = reader.readLine();
				lineNo += 1;
				if (lineNo % 100 == 0) {
					for (int i = 0; i < lineCount.length(); i++)
						System.out.print("\b");
					lineCount = "Lines Read: " + lineNo;
					System.out.print(lineCount);
				}
                
	        }
            System.out.println("\n Done Loading.");
            return new RecordCollection(records);
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERROR READING LINE: " + line);
            e.printStackTrace();
        }
		return null;
	}

	@Override
	public RecordCollection loadGold(File file) {
		String line = "";
		try {
			ArrayList<Record>  records = new ArrayList<Record>(10000);

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			line = reader.readLine();
			int lineNo = 0;
			String lineCount = "Lines Read: " + lineNo;
			while (line != null) {
				String[] fields = line.split("\t");

				Record rec = new Record(lineNo, new Source("gold"), new Entity(fields[0]));

				String s = fields[1];
				if('w' == s.charAt(0))//this is categorical
				{
					String rawValue = s.trim();
					String name = fields[0];
					StringAttribute strAttr = new StringAttribute(orderedAttributeNames[0], rawValue, AttributeType.CATEGORICAL);
					rec.addAttribute(strAttr);

					//System.out.print(name+"\t" +rawValue+"\t" );

				}
				else //continuous
				{
					String name = fields[0];
					String rawValue = fields[1];
					float value = Float.parseFloat(rawValue);  // Since the FloatAttribute takes the abs value of the attributes & we don't want that here use the simple parseFloat
					FloatAttribute floatAttr = new FloatAttribute(orderedAttributeNames[1], value,AttributeType.CONTINUOUS);
					rec.addAttribute(floatAttr);

					//System.out.print(name+"\t" +rawValue+"\t" );
				}
				records.add(rec);

				line = reader.readLine();
				
				lineNo += 1;
				if (lineNo % 100 == 0) {
					for (int i = 0; i < lineCount.length(); i++)
						System.out.print("\b");
					lineCount = "Lines Read: " + lineNo;
					System.out.print(lineCount);
				}
			}
			System.out.println("\n Done Loading.");
			return new RecordCollection(records);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("ERROR READING LINE: " + line);
			e.printStackTrace();
		}
		return null;
		
		
	}

//	public static void main(String[] args) {
//		RecordCollection collection = load(new File("/Users/Manuel/Documents/Development/github/DBProject/dataset/CRH/data/weather_data_set.txt"));
//		TruthFinder tf = new TruthFinder();
//        RecordCollection cleaner = tf.convert(tf.execute(collection));
//        cleaner.writeToTSVFile(new File("outputTest.txt"));
//	}

}
