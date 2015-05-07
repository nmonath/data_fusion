package edu.umass.cs.data_fusion.dataset_creation;

import edu.umass.cs.data_fusion.data_structures.*;
import edu.umass.cs.data_fusion.load.LoadTSVFile;

import java.io.*;
import java.util.ArrayList;

public class LoadUCIDatasetForDatasetCreation extends LoadTSVFile {

    private String delimeter;
    private String unknown;

    public LoadUCIDatasetForDatasetCreation(String delimeter, String unknown, String[] orderedAttributeNames, AttributeDataType[] attributeDataTypes, AttributeType[] attributeTypes) {
       super(orderedAttributeNames, attributeDataTypes, attributeTypes);
        this.delimeter = delimeter;
        this.unknown = unknown;
    }
    
    @Override
    public RecordCollection load(File file) {
        String line = "";
        try {
            System.out.println("Loading file: " + file.getAbsolutePath());
            ArrayList<Record> records = new ArrayList<Record>(10000); // TODO: Maybe there is a way to get the number of lines easily?
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            line = reader.readLine();
            int lineNo = 0;
            String lineCount = "Lines Read: " + lineNo;
            System.out.print(lineCount);
            while (line != null) {
                if (line.length() > 0) {
                    if (lineNo % 100 == 0) {
                        for (int i = 0; i < lineCount.length(); i++)
                            System.out.print("\b");
                        System.out.print(lineCount);
                    }
                    lineCount = "Lines Read: " + lineNo;
                    String[] fields = line.split(delimeter);
                    if (fields.length < 0) {
                        System.err.println("\nError reading file " + file.getName() + " malformed line: " + line);
                    } else {
                        Record rec = new Record(lineNo, new Source("Gold"), new Entity(String.format("%d",lineNo)));
                        for (int i = 0; i < fields.length; i++) {
                            if (fields[i].length() > 0) {
                                if (!fields[i].trim().equals(unknown)) {
                                    Attribute attrToAdd = getAttributeFromString(orderedAttributeNames[i], fields[i].trim(), attributeDataTypes[i], attributeTypes[i]);
                                    if (attrToAdd != null)
                                        rec.addAttribute(attrToAdd);
                                }
                            }
                        }
                        records.add(rec);
                    }
                }
                line = reader.readLine();
                lineNo += 1;
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
        return load(file);
    }
    
    // In this case we want just a simple parse of the decimal, not the full regex version
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
