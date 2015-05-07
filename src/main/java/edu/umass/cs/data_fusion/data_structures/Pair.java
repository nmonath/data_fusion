package main.java.edu.umass.cs.data_fusion.data_structures;

/**
 * A handy way of representing an ordered  pair of two objects. 
 * @param <T> type of the first object
 * @param <U> type of the second object
 */
public class Pair<T,U> {

    /**
     * The first object in the pair * 
     */
    final public T one;

    /**
     * The second object in the pair * 
     */
    final public U two;

    /**
     * Constructor for pair sets the two objects to the given values * 
     * @param one - the first item
     * @param two - the second item 
     */
    public Pair(T one, U two) {
        this.one = one;
        this.two = two;
    }

    /**
     * A pair is equal to another pair their first objects are equal
     * and their second objects are equal  
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Pair) && (one.equals(((Pair) obj).one) && two.equals(((Pair) obj).two));
    }

    /**
     * Based on the Scala hashcode function for tuples *
     * http://stackoverflow.com/questions/5866720/hashcode-in-case-classes-in-scala
     * @return
     */
    @Override
    public int hashCode() {
        // Based on Scala's implementation of hashcodes for tuples? TODO: Is this ok?
        int hashCode = 2;
        if (one != null)
            hashCode = hashCode * 41 + one.hashCode();
        if (two != null)
            hashCode = hashCode * 41 + two.hashCode();
        return hashCode;
    }
    
    @Override
    public String toString() {
        return "Pair(" + one.toString() + ", " + two.toString() + ")";
    }
}
