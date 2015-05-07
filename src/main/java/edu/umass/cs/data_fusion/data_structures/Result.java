package edu.umass.cs.data_fusion.data_structures;

import java.util.HashMap;
import java.lang.Override;

public class Result {
	
	private Source source;  
    private Entity entity;
    private HashMap<String,Attribute> attributes;
    private Double confidence;
    
    public Result(Source source,Entity entity){
    	this(source,entity,new HashMap<String, Attribute>(),0.0);
    }

    
    public Result(Source source, Entity entity, HashMap<String, Attribute> attributes) {
        this(source, entity, attributes,0.0);
    }

    
    public Result(Source source, Entity entity, Iterable<Attribute> attributes) {
        this(source, entity, attributes,0.0);
    }

     public Result(Source source, Entity entity,HashMap<String, Attribute> attributes, Double confidence) {
         this.source = source;
         this.entity = entity;
         this.attributes = attributes;
         this.confidence=confidence;
     }
     

     
     public Result(Source source, Entity entity, Iterable<Attribute> attributes, Double confidence) {
         this.source = source;
         this.entity = entity;
         this.attributes = new HashMap<String, Attribute>();
         for (Attribute a: attributes) {
             this.addAttribute(a);
         }
         this.confidence=confidence;
     }

     public void addAttribute(Attribute attr) {
         attributes.put(attr.getName(),attr);
     }
     
     public HashMap<String,Attribute> getAttributes() {
         return attributes;
     }
     
     public int getNumAttributes(){
     	return attributes.size();
     }
     
     public Double getConfidence() {
         return confidence;
     }
     
     public Source getSource(){
    	 return source;
     }
     
     public Entity getEntity(){
    	 return entity;
     }
     
     @Override
     public String toString() {
         StringBuilder sb = new StringBuilder(100);
         sb.append("Result(");
         sb.append(this.source.toString());
         sb.append(", ");
         sb.append(this.entity.toString());
         for (Attribute a : attributes.values()) {
             sb.append(", ");
             sb.append(a.toString());
         }
         sb.append(", ");
         sb.append(this.confidence.toString());
         sb.append(")");
         return sb.toString();
     }
     
     @Override
     public int hashCode() {
         return this.toString().hashCode();
     }

     @Override
     public boolean equals(Object obj) {
         return (obj instanceof Result) && this.source.equals(((Result) obj).getSource()) && this.entity.equals(((Result) obj).getEntity());
     }
}
