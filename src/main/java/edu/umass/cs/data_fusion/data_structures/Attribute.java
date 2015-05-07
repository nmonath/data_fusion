package edu.umass.cs.data_fusion.data_structures;


import java.lang.String;

/**
 * An attribute is a property that is stored about an entity. It can be thought of as a column in a 
 * relational database. Each attribute has a domain stored in its AttributeDataType which represents 
 * the space of values the value of the attribute lies in. Each attribute also has an AttributeType 
 * which is mostly used for purposes of evaluation or calculating values of a loss function. The 
 * AttributeType represents whether the semantics of the attribute make it a continuous or a categorical 
 * attribute. Note that an attribute may have an AttributeDataType of Float meaning its value is some 
 * floating point number, but may have AttributeType of categorical, meaning that it can take on a value
 * of one of a fixed set of floating point numbers.
 */
public class Attribute implements Comparable {

    /**
     * The name of the attribute, a unique identifier * 
     */
    protected String name;

    /**
     * The value of the attribute as grep'd from the web * 
     */
    protected String rawValue;

    /**
     * The domain of the attribute value, i.e. Strings, Floats, etc *  
     */
    protected AttributeDataType dataType;
    
    /**
     * The size of the space of the domain, e.g. categorical or continuous * 
     */
    protected AttributeType type;

    /**
     * Default constructor for Attributes, sets the name and rawValue *
     * @param name - the name of the attr
     * @param rawValue - the value from the web
     */
    public Attribute(String name, String rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    /**
     * Comparator for attributes, attributes with the same name are 
     * sorted by the ordering of their values, and attributes with 
     * different names are sorted by their name 
     * @param o - the other attribute
     * @return - partial order of the attrs
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof Attribute) {
            Attribute other = (Attribute) o;
            if (this.name.equals(other.getName()))
                return this.rawValue.compareTo(other.getRawValue());
            else
                return this.name.compareTo(other.getName());
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Returns the name of the attribute* 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value of the attribute as it was grep'd from the web * 
     * @return rawValue
     */
    public String getRawValue() {
        return rawValue;
    }

    /**
     * A hash code for the attr. It is the hash code of the name and rawValue *
     * @return
     */
    @Override
    public int hashCode() {
        return (this.name + this.rawValue).hashCode();
    }

    /**
     * Checks equality between attributes. Equality is determined by the name
     * and rawvalue  
     * @param obj - other attribute
     * @return true iff the attrs are equal
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Attribute) && this.name.equals(((Attribute) obj).getName()) && this.rawValue.equals(((Attribute) obj).getRawValue()) && this.dataType.equals(((Attribute) obj).getDataType());
    }

    /**
     * Returns the data type / domain of the attribute* 
     * @return dataType
     */
	public AttributeDataType getDataType(){
	    return dataType;
	}

    /**
     * Returns the type / evaluation type / size of domain of the attribute * 
     * @return type
     */
    public AttributeType getType() {return type;}

    /**
     * Given a group of attributes, check if they all have the same data type, 
     * if they do, return that type, else return null
     * @param attributes - the group of attributes
     * @return - the datatype of the attrs
     */
    public static AttributeDataType getDataType(Iterable<Attribute> attributes) {

        boolean first = true;
        AttributeDataType attributeDataType = null;
        for (Attribute a : attributes) {
            AttributeDataType next = a.getDataType();
            if (first)
                attributeDataType = next;
            else if(!next.equals(attributeDataType)) {
                System.err.println("[Attribute.getDataType] ERROR: the collection of attributes passed into getDataType do not have a common data type.");
                return null;
            }
            first = false;
        }
        return attributeDataType;
    }

    /**
     * Given a group of attributes, check if they all have the same type,
     * if they do, return that type, else return null
     * @param attributes - the group of attributes
     * @return - the type of the attrs
     */
    public static AttributeType getType(Iterable<Attribute> attributes) {

        boolean first = true;
        AttributeType attributeType = null;
        for (Attribute a : attributes) {
            AttributeType next = a.getType();
            if (first)
                attributeType = next;
            else if(!next.equals(attributeType)) {
                System.err.println("[Attribute.getType] ERROR: the collection of attributes passed into getType do not have a common data type.");
                return null;
            }
            first = false;
        }
        return attributeType;
    }
}
