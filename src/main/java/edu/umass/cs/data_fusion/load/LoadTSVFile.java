package main.java.edu.umass.cs.data_fusion.load;

import main.java.edu.umass.cs.data_fusion.data_structures.*;
import main.java.edu.umass.cs.data_fusion.data_structures.author.AuthorListAttribute;

import java.io.*;
import java.util.ArrayList;

public class LoadTSVFile {


    protected String[] orderedAttributeNames;

    protected AttributeDataType[] attributeDataTypes;

    protected AttributeType[] attributeTypes;

    public LoadTSVFile(AttributeDataType[] attributeDataTypes, AttributeType[] attributeTypes) {
        orderedAttributeNames = new String[0];
        this.attributeDataTypes = attributeDataTypes;
        this.attributeTypes = attributeTypes;
    }
    
    public LoadTSVFile(String[] orderedAttributeNames, AttributeDataType[] attributeDataTypes, AttributeType[] attributeTypes) {
        this.orderedAttributeNames = orderedAttributeNames;
        this.attributeDataTypes = attributeDataTypes;
        this.attributeTypes = attributeTypes;
    }
    

    public RecordCollection load(File file) {
        String line = "";
        try {
            System.out.println("Loading file: " + file.getAbsolutePath());
            ArrayList<Record>  records = new ArrayList<Record>(10000); // TODO: Maybe there is a way to get the number of lines easily?
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
                    String[] fields = line.split("\t");
                    if (fields.length < 2) {
                        System.err.println("\nError reading file " + file.getName() + " malformed line: " + line);
                    } else {
                        Record rec = new Record(lineNo, new Source(fields[0]), new Entity(fields[1]));
                        for (int i = 2; i < fields.length; i++) {
                            int j = i - 2;
                            // Handle empty attributes
                            if (fields[i].length() > 0)
                                if (j < orderedAttributeNames.length) {
                                    Attribute attrToAdd = getAttributeFromString(orderedAttributeNames[j], fields[i], attributeDataTypes[j], attributeTypes[j]);
                                    if (attrToAdd != null)
                                        rec.addAttribute(attrToAdd);
                                } else {
                                    Attribute attrToAdd = getAttributeFromString(String.format("Attr%04d", j), fields[i], attributeDataTypes[j], attributeTypes[j]);
                                    if (attrToAdd != null)
                                        rec.addAttribute(attrToAdd);
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

    public RecordCollection loadGold(File file) {
        String line = "";
        try {
            ArrayList<Record>  records = new ArrayList<Record>(1000); // TODO: Maybe there is a way to get the number of lines easily?
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            line = reader.readLine();
            int lineNo = 0;
            while (line != null) {
                if (line.length() > 0) {
                    String[] fields = line.split("\t");
                    if (fields.length < 1) {
                        System.err.println("Error reading file " + file.getName() + " malformed line: " + line);
                    } else {
                        Record rec = new Record(lineNo, new Source("Gold"), new Entity(fields[0]));
                        for (int i = 1; i < fields.length; i++) {
                            int j = i - 1;
                            // Handle empty attributes
                            if (fields[i].length() > 0)
                                if (j < orderedAttributeNames.length) {
                                    Attribute attrToAdd = getAttributeFromString(orderedAttributeNames[j], fields[i], attributeDataTypes[j], attributeTypes[j]);
                                    if (attrToAdd != null)
                                        rec.addAttribute(attrToAdd);
                                } else {
                                    Attribute attrToAdd = getAttributeFromString(String.format("Attr%04d", j), fields[i], attributeDataTypes[j], attributeTypes[j]);
                                    if (attrToAdd != null)
                                        rec.addAttribute(attrToAdd);
                                }
                        }
                        records.add(rec);
                    }
                }
                line = reader.readLine();
                lineNo += 1;
            }
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
    

    protected Attribute getAttributeFromString(String name, String rawValue, AttributeDataType dataType, AttributeType type)  {
        switch (dataType) {
            case STRING: {
                return getStringAttributeFromString(name,rawValue,type);
            }
            case FLOAT: {
                return getFloatAttributeFromString(name,rawValue,type);
            }
            case AUTHOR_LIST:{
                return getAuthorListAttributeFromString(name,rawValue,type);
            }
        }
        return null;
    }
    
    protected Attribute getStringAttributeFromString(String name, String rawValue, AttributeType type) {
        return new StringAttribute(name,rawValue,type);
    }
    
    protected Attribute getFloatAttributeFromString(String name, String rawValue, AttributeType type) {
        FloatAttribute flt = new FloatAttribute(name,rawValue,type);
        return (flt.isValidFloat() ? flt : null); 
    }
    
    protected Attribute getAuthorListAttributeFromString(String name, String rawValue, AttributeType type) {
        AuthorListAttribute authorListAttribute = new AuthorListAttribute(name,rawValue,type);
        return (authorListAttribute.getAuthors().isEmpty()) ? null : authorListAttribute;
    }

    public String[] getOrderedAttributeNames() {
        return orderedAttributeNames;
    }
}
