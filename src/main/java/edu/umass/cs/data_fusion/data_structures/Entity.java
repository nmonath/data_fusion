package edu.umass.cs.data_fusion.data_structures;


import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Entities in this project correspond to keys in a relational database. 
 * That is each entity can be distinguished from each other entity. In the case
 * of stock data, an entity would refer to a particular company on a particular day. 
 * In the case of flight data, it would correspond to a particular flight on a
 * particular day
 */
public class Entity {

    private String identifier = "";

    public Entity(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Entity) && identifier.equals(((Entity) obj).identifier);
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "Entity(identifier: " + identifier + ")";
    }
}
