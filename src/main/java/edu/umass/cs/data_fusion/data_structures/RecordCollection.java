package main.java.edu.umass.cs.data_fusion.data_structures;


import java.io.*;
import java.util.*;

// I didn't see this on our list of things to implement, but I thought it was something we had talked about?
// I think it might be useful in implementing the algorithms.
public class RecordCollection {
    
    private ArrayList<Record> records;
    
    private HashMap<Source,ArrayList<Record>> source2records;
    
    private HashMap<Entity,ArrayList<Record>> entity2records;
    
    public RecordCollection(ArrayList<Record> records) {
        this.records = records;
        source2records = new HashMap<Source,ArrayList<Record>>();
        entity2records = new HashMap<Entity,ArrayList<Record>>();
        for (Record r: this.records) {
            if (!source2records.containsKey(r.getSource()))
                source2records.put(r.getSource(),new ArrayList<Record>()); //TODO: What should we set as an initial estimate for the size of this arraylist?
            source2records.get(r.getSource()).add(r);
            if (!entity2records.containsKey(r.getEntity()))
                entity2records.put(r.getEntity(),new ArrayList<Record>());
            entity2records.get(r.getEntity()).add(r);
        }
    }
    
    public ArrayList<Record> getRecords() {
        return records;
    }
    
    
    
    public ArrayList<Record> getRecords(Source source) {
        if (source2records.containsKey(source))
            return source2records.get(source);
        else
            return new ArrayList<Record>(); // TODO: Do we want to return an empty array list or null?
    }
    
    public ArrayList<Record> getRecords(Entity entity) {
        if (entity2records.containsKey(entity))
            return entity2records.get(entity);
        else
            return new ArrayList<Record>();// TODO: Do we want to return an empty array list or null?
    }
    
    public Set<Entity> getEntities() {
        return entity2records.keySet();
    }
    
    public Set<Source> getSources() {
        return  source2records.keySet();
    }
    
    public Set<String> getAttributes() {
        Set<String> attrs = new HashSet<String>();
        for (Entity e : getEntities()) {
            attrs.addAll(getAttributes(e));
        }
        return attrs;
    }
    
    public int getRecordsCount() {
    	return records.size();
    }
    
    public int getSourcesCount() {
    	return source2records.size();
    }
    
    public int getEntitiesCount() {
    	return entity2records.size();
    }

    public Set<String> getAttributes(Entity entity) {
        ArrayList<Record> correspondingRecords = getRecords(entity);
        Set<String> attributes = new HashSet<String>();
        for (Record r : correspondingRecords)
            for (String a: r.getAttributes().keySet())
                attributes.add(a);
        return attributes;
    }
    
    public Set<String> getAttributes(Source source) {
        ArrayList<Record> correspondingRecords = getRecords(source);
        Set<String> attributes = new HashSet<String>();
        for (Record r : correspondingRecords)
            for (String a: r.getAttributes().keySet())
                attributes.add(a);
        return attributes;
    }

    /**
     * The number of (entity,attr) pairs in the collection. * 
     * @return
     */
    public int getTotalNumberOfAttributes() {
        int num = 0;
        for (Entity e: entity2records.keySet()) {
	   for (Record r : entity2records.get(e)) {
             num += r.getAttributes().keySet().size();
	   }
        }
        return num;
    }

    /**
     * Return the number of attributes with a type in the appears given set
     * @param attributeTypes
     * @return
     */
    public int getNumberOfAttributes(Set<AttributeType> attributeTypes) {
        int num = 0;
        for (Entity e: entity2records.keySet()) {
            for (Record r : entity2records.get(e)) {
                for (Attribute a: r.getAttributes().values()) {
                    if (attributeTypes.contains(a.getType()))
                        num ++;
                }
            }
        }
        return num;
    }

    /**
     * Return the values for the given entity & attribute  
     * @param entity - the entity
     * @param attributeName - the name of the attribute 
     * @return
     */
    public Set<Attribute> valuesForAttribute(Entity entity, String attributeName) {
        Set<Attribute> valuesForAttribute = new HashSet<Attribute>();
        List<Record> recordsForEntity = getRecords(entity);
        for (Record r: recordsForEntity) {
            Attribute attr = r.getAttribute(attributeName);
            if (attr != null)
                valuesForAttribute.add(attr);
        }
        return valuesForAttribute;
    }

    /**
     * Returns a map from the string name of categorical attribute to the values in its domain.*
     * @return
     */
    public Map<String,Set<Attribute>> getAttributeDomains() {
        Set<String> attributes = this.getAttributes();
        Map<String,Set<Attribute>> domains = new HashMap<String, Set<Attribute>>();
        for (String attrName : attributes) {
            Set<Attribute> domain = new HashSet<Attribute>();
            for (Entity e : this.getEntities()) {
                domain.addAll(valuesForAttribute(e,attrName));
            }
            domains.put(attrName, domain);
        }
        return domains;
    }

    public List<Attribute> allValuesForAttribute(Entity entity, String attributeName) {
        List<Record> records1 = getRecords(entity);
        List<Attribute> valuesForAttribute = new ArrayList<Attribute>(100);
        for (Record r: records1) {
            Attribute attr = r.getAttribute(attributeName);
            if (attr != null)
                valuesForAttribute.add(attr);
        }
        return valuesForAttribute;
    }


    public void writeToTSVFile(File file) {
        try {
            file.getParentFile().mkdirs();
            PrintWriter out = new PrintWriter(file,"UTF-8");
            for (Record r : records) {
                StringBuilder sb = new StringBuilder(100);
                sb.append(r.getSource().getName());
                sb.append("\t");
                sb.append(r.getEntity().getIdentifier());
                ArrayList<Attribute> attrs = new ArrayList<Attribute>(r.getAttributes().values());
                Collections.sort(attrs);
                for (Attribute a : attrs) {
                    sb.append("\t");
                    sb.append(a.getRawValue());
                }
                out.println(sb.toString());
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void writeToTSVFile(File file, String[] attributeOrdering) {
        try {
            file.getParentFile().mkdirs();
            PrintWriter out = new PrintWriter(file,"UTF-8");
            Collections.sort(records, new Comparator<Record>() {
                @Override
                public int compare(Record o1, Record o2) {
                    return o1.getEntity().getIdentifier().compareTo(o2.getEntity().getIdentifier());
                }
            });
            for (Record r : records) {
                StringBuilder sb = new StringBuilder(100);
                sb.append(r.getSource().getName());
                sb.append("\t");
                sb.append(r.getEntity().getIdentifier());
                for (String attrName : attributeOrdering) {
                    sb.append("\t");
                    if (r.hasAttribute(attrName))
                        sb.append(r.getAttribute(attrName));
                }
                out.println(sb.toString());
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
}
