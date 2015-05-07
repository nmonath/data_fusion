package edu.umass.cs.data_fusion.data_structures;


import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Random;

/**
 * A data structure corresponding to a row in a relational database. Each record has an associated
 * entity and the entity has a number of attribute associated with it. Each row also has a source 
 * associated with the row. This source could be the website providing the data or an algorithm if 
 * the row appears in the output of an algorithm. 
 */
public class Record {

    /**
     * A unique id for the record * 
     */
    private int id;

    /**
     * The source providing the record * 
     */
    private Source source;

    /**
     * The entity this record is about *
     */
    private Entity entity;

    /**
     * The attributes of this record's entity * 
     */
    private HashMap<String,Attribute> attributes;

    /**
     * Constructor for record. Inits record w/ random id number 
     * and no attributes* 
     * @param source
     * @param entity
     */
    public Record(Source source, Entity entity) {
        this(new Random().nextInt(), source,entity,new HashMap<String, Attribute>());
    }


    /**
     * Initializes Record with given id number and with no attributes
     * @param id
     * @param source
     * @param entity
     */
    public Record(int id, Source source, Entity entity) {
       this(id, source,entity,new HashMap<String, Attribute>());
    }

    /**
     * Constructor which initializes all of the fields of the Record
     * @param id
     * @param source
     * @param entity
     * @param attributes
     */
    public Record(int id, Source source, Entity entity, HashMap<String, Attribute> attributes) {
        this.id = id;
        this.source = source;
        this.entity = entity;
        this.attributes = attributes;
    }

    /**
     * Constructor which initializes all fields by id number and gives the record a random id 
     * @param source
     * @param entity
     * @param attributes
     */
    public Record(Source source, Entity entity, HashMap<String, Attribute> attributes) {
        this(new Random().nextInt(), source, entity, attributes);
    }

    /**
     * Assigns a random id number to the record, adds each attribute to the hash map  
     * @param source
     * @param entity
     * @param attributes
     */
    public Record(Source source, Entity entity, Iterable<Attribute> attributes) {
        this(new Random().nextInt(), source, entity, attributes);
    }

    /**
     * Creates a new record with all fields specified, each attribute is added to the hash map
     * @param id
     * @param source
     * @param entity
     * @param attributes
     */
    public Record(int id,Source source, Entity entity, Iterable<Attribute> attributes) {
        this.id = id;
        this.source = source;
        this.entity = entity;
        this.attributes = new HashMap<String, Attribute>();
        for (Attribute a: attributes) {
            this.addAttribute(a);
        }
    }


    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * TODO: Is this definition correct?
     * Two records are equal if they have the same source and entity? 
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Record) && this.id == ((Record) obj).getId() && this.source.equals(((Record) obj).getSource()) && this.entity.equals(((Record) obj).getEntity());
    }

    /**
     * Returns the id of the record 
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the source of the record
     */
    public Source getSource() {
        return source;
    }

    /**
     * Returns the entity of the record  
     * @return
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Adds the attribute to the record. If the record already has an attribute with the 
     * same name, the old value will be overwritten 
     * @param attr - the new attribute
     */
    public void addAttribute(Attribute attr) {
        attributes.put(attr.getName(),attr);
    }

    /**
     * Returns all of the attributes of the record * 
     * @return
     */
    public HashMap<String,Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Returns the number of attributes the record has * 
     * @return
     */
    public int getNumAttributes(){
    	return attributes.size();
    }

    /**
     * Checks to see if the record has an attribute with the given name 
     * @param attrName
     * @return
     */
    public boolean hasAttribute(String attrName) { return attributes.containsKey(attrName);}

    /**
     * Returns the attribute the record has with the given name or null if the record 
     * doesn't store the attribute with this name
     * @param attrName
     * @return
     */
    public Attribute getAttribute(String attrName) { return attributes.get(attrName);}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("Record(");
        sb.append(this.source.toString());
        sb.append(", ");
        sb.append(this.entity.toString());
        for (Attribute a : attributes.values()) {
            sb.append(", ");
            sb.append(a.toString());
        }
        sb.append(")");
        return sb.toString();
    }
}
