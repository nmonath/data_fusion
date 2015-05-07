package main.java.edu.umass.cs.data_fusion.data_structures.author;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class AuthorName {

    public HashMap<String,AuthorNameType> spelling2type = new HashMap<String, AuthorNameType>();
    public HashMap<AuthorNameType,String> type2spelling = new HashMap<AuthorNameType, String>();
    
    public void addName(AuthorNameType type, String spelling) {
        if (type == AuthorNameType.FIRST || type == AuthorNameType.LAST) {
            spelling2type.put(spelling, type);
            type2spelling.put(type, spelling);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<AuthorNameType> types = type2spelling.keySet().iterator();
        if (types.hasNext()) {
            AuthorNameType next = types.next();
            sb.append(next.toString()).append(":").append(type2spelling.get(next));
        }
        while (types.hasNext()) {
            AuthorNameType next = types.next();
            sb.append(", ").append(next.toString()).append(":").append(type2spelling.get(next));
        }
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AuthorName) && this.toString().equals(((AuthorName) obj).toString());
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public static double implication(Set<AuthorName> f1, Set<AuthorName> f2) {
        double base_sim = 0.5;
        Set<AuthorName> f1Authors = new HashSet<AuthorName>();
        f1Authors.addAll(f1);
        Set<AuthorName> f2Authors = new HashSet<AuthorName>();
        f2Authors.addAll(f2);

        // Align two
        double correctness = 0.0;
        for (AuthorName name : f1Authors) {
            AuthorName matchedName = null;
            double bestScore = 0.0;
            for (AuthorName predName : f2Authors) {
                double score = getScore(predName, name);
                if (score > bestScore) {
                    bestScore = score;
                    matchedName = predName;
                }
            }
            if (matchedName != null) {
                f2Authors.remove(matchedName);
                correctness += bestScore;
            }
        }
        double denominator = Math.max(1.0, f1Authors.size());
        return correctness/denominator;

    }

    
    public static double accuracy(Set<AuthorName> predictedSet, Set<AuthorName> goldSet){
        Set<AuthorName> predAuthors = new HashSet<AuthorName>();
        predAuthors.addAll(predictedSet);
        Set<AuthorName> goldAuthors = new HashSet<AuthorName>();
        goldAuthors.addAll(goldSet);
        
        // Align two
        double correctness = 0.0;
        for (AuthorName name : goldAuthors) {
            AuthorName matchedName = null;
            double bestScore = 0.0;
            for (AuthorName predName : predAuthors) {
                double score = getScoreExact(predName, name);
                if (score > bestScore) {
                    bestScore = score;
                    matchedName = predName;
                }
            }
            if (matchedName != null) {
                predAuthors.remove(matchedName);
                correctness += bestScore;
            }
        }
        double denominator = Math.max(1.0,Math.max(predAuthors.size(),goldAuthors.size()));
        return correctness/denominator;
    }


    public static double getScoreExact(AuthorName predicted, AuthorName gold) {
        double score = 0.0;
        // Last name
        if (predicted.type2spelling.containsKey(AuthorNameType.LAST) && gold.type2spelling.containsKey(AuthorNameType.LAST)) {
            score += predicted.type2spelling.get(AuthorNameType.LAST).equalsIgnoreCase(gold.type2spelling.get(AuthorNameType.LAST)) ? 0.5 : 0.0;
        }
        // First name -- exact math
        if (predicted.type2spelling.containsKey(AuthorNameType.FIRST) && gold.type2spelling.containsKey(AuthorNameType.FIRST)) {
            score += predicted.type2spelling.get(AuthorNameType.FIRST).equalsIgnoreCase(gold.type2spelling.get(AuthorNameType.FIRST)) ? 0.5 : 0.0;
        }
        return (score < 1.0) ? 0.0 : 1.0;
    }

    public static double getScore(AuthorName predicted, AuthorName gold) {
        double score = 0.0;
        // Last name
        if (predicted.type2spelling.containsKey(AuthorNameType.LAST) && gold.type2spelling.containsKey(AuthorNameType.LAST)) {
            score += predicted.type2spelling.get(AuthorNameType.LAST).equalsIgnoreCase(gold.type2spelling.get(AuthorNameType.LAST)) ? 0.5 : 0.0;
        }
//        // Middle name -- just require that we get the initial correct.
//        if (predicted.type2spelling.containsKey(AuthorNameType.MIDDLE_INIT) && gold.type2spelling.containsKey(AuthorNameType.MIDDLE_INIT)) {
//            score += gold.type2spelling.get(AuthorNameType.MIDDLE_INIT).toLowerCase().charAt(0) ==  predicted.type2spelling.get(AuthorNameType.MIDDLE_INIT).toLowerCase().charAt(0) ? 1.0/6.0 : 0.0;
//        } else if (predicted.type2spelling.containsKey(AuthorNameType.MIDDLE_INIT) && gold.type2spelling.containsKey(AuthorNameType.MIDDLE)) {
//            score += gold.type2spelling.get(AuthorNameType.MIDDLE).toLowerCase().charAt(0) ==  predicted.type2spelling.get(AuthorNameType.MIDDLE_INIT).toLowerCase().charAt(0) ? 1.0/6.0 : 0.0;
//        } else if (predicted.type2spelling.containsKey(AuthorNameType.MIDDLE) && gold.type2spelling.containsKey(AuthorNameType.MIDDLE_INIT)) {
//            score += gold.type2spelling.get(AuthorNameType.MIDDLE_INIT).toLowerCase().charAt(0) ==  predicted.type2spelling.get(AuthorNameType.MIDDLE).toLowerCase().charAt(0) ? 1.0/6.0 : 0.0;
//        } else if (predicted.type2spelling.containsKey(AuthorNameType.MIDDLE) && gold.type2spelling.containsKey(AuthorNameType.MIDDLE)) {
//            score += predicted.type2spelling.get(AuthorNameType.MIDDLE).equalsIgnoreCase(gold.type2spelling.get(AuthorNameType.MIDDLE)) ? 1.0/6.0 : 0.0;
//        }
        // First name -- exact math
        if (predicted.type2spelling.containsKey(AuthorNameType.FIRST) && gold.type2spelling.containsKey(AuthorNameType.FIRST)) {
            score += predicted.type2spelling.get(AuthorNameType.FIRST).equalsIgnoreCase(gold.type2spelling.get(AuthorNameType.FIRST)) ? 0.5 : 0.0;
        } else if (predicted.type2spelling.containsKey(AuthorNameType.FIRST_INIT) && gold.type2spelling.containsKey(AuthorNameType.FIRST_INIT)) {
            score += predicted.type2spelling.get(AuthorNameType.FIRST_INIT).equalsIgnoreCase(gold.type2spelling.get(AuthorNameType.FIRST_INIT)) ? 0.5 : 0.0;
        }
        return score;
    }
    
}
