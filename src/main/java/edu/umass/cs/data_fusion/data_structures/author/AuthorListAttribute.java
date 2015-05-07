package main.java.edu.umass.cs.data_fusion.data_structures.author;


import main.java.edu.umass.cs.data_fusion.data_structures.Attribute;
import main.java.edu.umass.cs.data_fusion.data_structures.AttributeDataType;
import main.java.edu.umass.cs.data_fusion.data_structures.AttributeType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthorListAttribute extends Attribute {

    private Set<AuthorName> authors;
    
    public AuthorListAttribute(String name, String rawValue, AttributeType type) {
        super(name, rawValue);
        this.type = type;
        authors = getAuthorsGreedy(rawValue);
        dataType = AttributeDataType.AUTHOR_LIST;
    }

    public Set<AuthorName> getAuthors() {
        return authors;
    }

    public String addDelimeters(String listOfNames) {
        listOfNames = listOfNames.trim();
        Pattern punc = Pattern.compile("\\p{Punct}");
        // If there are no delimeters in the str
        boolean noDelim = !punc.matcher(listOfNames).find();
        if (!noDelim) {
            rawValue = listOfNames;
            return listOfNames;
        } else {
            String[] split = listOfNames.split("\\s");
            // The main question here is where to split up into groups of 1, 2 or 3
            // Here is what we do:
            // 1. If there are an odd number of names, delim each one
            // 2. If there are a multiple of 2 names, delim each as 1st and last
            if (split.length % 2 == 0) {
                StringBuffer result = new StringBuffer(listOfNames.length());
                for (int i = 0; i < split.length; i += 2) {
                    result.append(split[i]).append(" ").append(split[i+1]).append(" | ");
                }
                return result.toString();
            } else if (split.length > 3)  {
                StringBuffer result = new StringBuffer(listOfNames.length());
                for (int i = 0; i < split.length; i += 1) {
                    result.append(split[i]).append(" | ");
                }
                rawValue = result.toString();
                return result.toString();
            } else {
                rawValue = listOfNames;
                return rawValue;
            }
                
        }
    }
    
    
    public Set<AuthorName> getAuthorsGreedy(String rawString) {
        
        boolean done = false;
        String string = rawString;
        Set<AuthorName> names = new HashSet<AuthorName>();
        
        while (!done && string.length() > 0) {
            AuthorNameRegex[] regexes = {new AuthorNameRegex1(), new AuthorNameRegex2(), new AuthorNameRegex3(), 
                    new AuthorNameRegex4(), new AuthorNameRegex5(), new AuthorNameRegex6(), new AuthorNameRegex7(),
                    new AuthorNameRegex8(), new AuthorNameRegex9(), new AuthorNameRegex10(), new AuthorNameRegex11()};
            for (AuthorNameRegex reg : regexes) {
                reg.match(string);
            }
            Arrays.sort(regexes);
            AuthorName name = regexes[0].getName();
            if (name != null) {
                names.add(name);
                string = regexes[0].updatedString();
            } else 
                done = true;
        }
        return names;
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuffer = new StringBuilder();
        Iterator<AuthorName> authorNameIterator = authors.iterator();
        if (authorNameIterator.hasNext())
            stringBuffer.append(authorNameIterator.next().toString());
        while(authorNameIterator.hasNext())
            stringBuffer.append(", ").append(authorNameIterator.next());
        return stringBuffer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AuthorListAttribute) && ((AuthorListAttribute) obj).authors.equals(this.authors);
    }
    
    private abstract class AuthorNameRegex implements Comparable<AuthorNameRegex> {
        protected Pattern pattern;
        protected Matcher matcher;
        protected int start;
        protected int length;
        protected String string;
        final protected static int MAX_STRING_LENGTH = 1000000;
        
        public AuthorNameRegex(String regex) {
            pattern = Pattern.compile(regex);
        }
        
        public void match(String str) {
            assert str.length() < MAX_STRING_LENGTH;
            this.string = str.trim().toLowerCase();
            matcher = pattern.matcher(string);
            if (matcher.find()) {
                start = matcher.start();
                length = matcher.end() - start;
            } else {
                start = string.length();
                length = 0;
            }
        }
        
        public void clear() {
            matcher = null;
            start = Integer.MAX_VALUE;
            length = 0;
        }
        @Override
        public int compareTo(AuthorNameRegex o1) {
            return (this.start - o1.start)*MAX_STRING_LENGTH + (o1.length - this.length); // Sort ascending by starting position, break ties by picking the longer one
        }
        
        abstract public AuthorName getName();
        
        public String updatedString() {
            return string.substring(start + length);
        }
    }
    
    private class AuthorNameRegex1 extends AuthorNameRegex {
        public AuthorNameRegex1() {
            super("\\b[a-z\\-]+(\\b)?,(\\s)?[a-z\\-]+\\s[a-z\\-](\\.)?\\b");
        }
        
        public AuthorName getName() {
            if (start < string.length()) {
                String res = string.substring(start, start + length);
                String[] splt = res.split(",");
                String last = splt[0];
                String[] splt2 = splt[1].trim().split("\\s");
                String first = splt2[0];
                String mi = splt2[1].substring(0, 1);
                AuthorName name = new AuthorName();
                name.addName(AuthorNameType.LAST, last);
                name.addName(AuthorNameType.FIRST, first);
                name.addName(AuthorNameType.MIDDLE_INIT, mi);
                return name;
            } 
            return null;
        }
    }
    
    private class AuthorNameRegex2 extends AuthorNameRegex {
        
        public AuthorNameRegex2() {
            super("\\b[a-z\\-]+(\\b)?,(\\s)?[a-z\\-]+\\s[a-z\\-]+\\b");
        }
        
        public AuthorName getName() {
            if (start < string.length()) {
                String res = string.substring(start, start + length);
                String[] splt = res.split(",");
                String last = splt[0];
                String[] splt2 = splt[1].trim().split("\\s");
                String first = splt2[0];
                String middle = splt2[1];
                AuthorName name = new AuthorName();
                name.addName(AuthorNameType.LAST, last);
                name.addName(AuthorNameType.FIRST, first);
                name.addName(AuthorNameType.MIDDLE, middle);
                return name;
            }
            return null;
        }
    }
    
    private class AuthorNameRegex3 extends AuthorNameRegex {
        
        public AuthorNameRegex3() {
            super("\\b[a-z\\-]+(\\b)?,(\\s)?[a-z\\-]+\\b");
        }

        public AuthorName getName() {
            if (start < string.length()) {
                String res = string.substring(start, start + length);
                String[] splt = res.split(",");
                String last = splt[0];
                String first = splt[1].trim();
                AuthorName name = new AuthorName();
                name.addName(AuthorNameType.LAST, last);
                name.addName(AuthorNameType.FIRST, first);
                return name;
            }
            return null;
        }
    }
    
    private class AuthorNameRegex4 extends AuthorNameRegex {
        
        public AuthorNameRegex4() {
            super("\\b[a-z\\-]+\\s[a-z\\-](\\.)?\\s[a-z\\-]+\\b");
        }
        
        public AuthorName getName() {
            if (start < string.length()) {

                String res = string.substring(start, start + length);
                String[] splt = res.split("\\s");
                AuthorName name = new AuthorName();
                name.addName(AuthorNameType.FIRST, splt[0]);
                name.addName(AuthorNameType.MIDDLE_INIT, splt[1].substring(0, 1));
                name.addName(AuthorNameType.LAST, splt[2]);
                return name;
            }
            return null;
        }
    }
    
    private class AuthorNameRegex5 extends AuthorNameRegex {
        
        public AuthorNameRegex5() {
            super("\\b[a-z\\-]+\\s[a-z\\-]+\\s[a-z\\-]+");
        }
        
        public AuthorName getName() {
            if (start < string.length()) {
                String res = string.substring(start, start + length);
                String[] splt = res.split("\\s");
                AuthorName name = new AuthorName();
                name.addName(AuthorNameType.FIRST, splt[0]);
                name.addName(AuthorNameType.MIDDLE, splt[1]);
                name.addName(AuthorNameType.LAST, splt[2]);
                return name;
            }
            return null;
        }
        
    }
    
    private class AuthorNameRegex6 extends AuthorNameRegex {
        
        public AuthorNameRegex6() {
            super("\\b[a-z\\-]+\\s[a-z\\-]+");
        }
        
        public AuthorName getName() {
            if (start < string.length()) {
                String res = string.substring(start, start + length);
                String[] splt = res.split("\\s");
                AuthorName name = new AuthorName();
                name.addName(AuthorNameType.FIRST, splt[0]);
                name.addName(AuthorNameType.LAST, splt[1]);
                return name;
            }
            return null;
        }
    }
    
    private class AuthorNameRegex7 extends AuthorNameRegex {
        
        public AuthorNameRegex7() {
            super("\\b[a-z\\-](\\.)?\\s[a-z\\-]+");
        }
        
        public AuthorName getName() {
            if (start < string.length()) {
                String res = string.substring(start, start + length);
                String[] splt = res.split("\\s");
                AuthorName name = new AuthorName();
                name.addName(AuthorNameType.FIRST, splt[0].substring(0, 1));
                name.addName(AuthorNameType.LAST, splt[1]);
                return name;
            }
            return null;
        }
    }
    
    private class AuthorNameRegex8 extends AuthorNameRegex {
        
        public AuthorNameRegex8() {
            super("\\b[a-z\\-][\\s\\.](\\s)?[a-z\\-][\\s\\.](\\s)?([a-z\\-](\\.)?)?\\s[a-z\\-]+\\b");
        }
        
        public AuthorName getName() {
            if (start < string.length()) {
                String res = string.substring(start, start + length).replace(".", " ");
                String[] splt = res.split("\\s+");
                AuthorName name = new AuthorName();
                name.addName(AuthorNameType.FIRST, splt[0].substring(0, 1));
                name.addName(AuthorNameType.MIDDLE_INIT, splt[1].substring(0, 1));
                name.addName(AuthorNameType.LAST, splt[2]);
                return name;
            }
            return null;
        }
    }

    private class AuthorNameRegex9 extends AuthorNameRegex {
        
        public AuthorNameRegex9() {
            super("\\b[a-z\\-]+\\b");
        }
        
        public AuthorName getName() {
            if (start < string.length()) {
                String res = string.substring(start, start + length);
                AuthorName name = new AuthorName();
                name.addName(AuthorNameType.LAST, res);
                return name;
            }
            return null;
        }
    }

    private class AuthorNameRegex10 extends AuthorNameRegex {

        public AuthorNameRegex10() {
            super("\\b[a-z\\-]+(\\b)?,(\\s)?[a-z\\-][\\s\\.](\\s)?[a-z\\-][\\s\\.](\\s)?\\b");
        }

        public AuthorName getName() {
            if (start < string.length()) {
                String res = string.substring(start, start + length);
                String[] splt = res.split(",");
                String last = splt[0];
                String[] splt2 = splt[1].replace("."," ").trim().split("\\s+");
                String first = splt2[0];
                String middle = splt2[1];
                AuthorName name = new AuthorName();
                name.addName(AuthorNameType.LAST, last);
                name.addName(AuthorNameType.FIRST, first.substring(0, 1));
                name.addName(AuthorNameType.MIDDLE_INIT, middle.substring(0, 1));
                return name;
            }
            return null;
        }
    }

    private class AuthorNameRegex11 extends AuthorNameRegex {
        public AuthorNameRegex11() {
            super("\\b[a-z\\-]+(\\b)?,(\\s)?[a-z\\-](\\.)?\\s[a-z\\-]+\\b");
        }

        public AuthorName getName() {
            if (start < string.length()) {
                String res = string.substring(start, start + length);
                String[] splt = res.split(",");
                String last = splt[0];
                String[] splt2 = splt[1].trim().split("\\s");
                String first = splt2[0].substring(0,1);
                String mi = splt2[1];
                AuthorName name = new AuthorName();
                name.addName(AuthorNameType.LAST, last);
                name.addName(AuthorNameType.FIRST, first);
                name.addName(AuthorNameType.MIDDLE, mi);
                return name;
            }
            return null;
        }
    }
}
