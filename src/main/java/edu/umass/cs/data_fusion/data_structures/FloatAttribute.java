package edu.umass.cs.data_fusion.data_structures;

/**
 * A Float Attribute is instantiated version of the Attribute parent class 
 * in which the values of the attribute are floating point numbers. Despite 
 * the fact that floating point numbers can represent the Reals, a continuous 
 * set, a FloatAttribute can still be a categorical Attribute, meaning that it 
 * can take on one of a finite set of floating point numbers. 
 */
public class FloatAttribute extends Attribute {
	protected float floatValue;
	
	protected float normalizedValue;


	/**
	 * Constructor for the Float Attribute class. Uses deterministic regex rules
	 * to parse the string and convert it to a float. * 
	 * @param name - the name of the attribute
	 * @param rawValue - the number string as taken from the raw data
	 * @param type - whether the attribute is categorical or continuous
	 */
	public FloatAttribute(String name, String rawValue, AttributeType type) {
		super(name, rawValue);
		this.dataType = AttributeDataType.FLOAT;
		this.floatValue = stringToFloat(rawValue);
		this.type = type;
	}

	/**
	 * Same as other constructor, but sets type to be continuous by default. * 
	 * @param name - name of attribute
	 * @param rawValue - value as taken from the raw data
	 */
	public FloatAttribute(String name, String rawValue) {
		this(name,rawValue,AttributeType.CONTINUOUS);
	}

	/**
	 * Creates a new float attribute with the given float as its value* 
	 * @param name - name of attribute
	 * @param value - float value of the attribute
	 * @param type - categorical or continuous
	 */
	public FloatAttribute(String name, float value, AttributeType type) {
		super(name,String.format("%g",value));
		this.floatValue = value;
		this.type = type;
		this.dataType = AttributeDataType.FLOAT;
		
	}

	/**
	 * Returns the float value of the attribute * 
	 * @return
	 */
	public float getFloatValue() {
        return this.floatValue;
    }
	
	public void setFloatValue(float newVal) {
		this.floatValue = newVal;
	}

	/**
	 * Returns true iff the raw string was correctly parsed into a float value
	 * @return  
	 */
	public boolean isValidFloat() {
		return (floatValue != Float.MAX_VALUE);
	}
	
	//tested with:
//	new String[]{ "2.03", "+0.06", "-3,5%", "123,453", "624.431mil", 
//			"19.99m", "$31.13", "(2.03%)", "31.23 usd", "$624.43m", "19,995,000", 
//			"19.99 mil", "$ -0.27", "open: 30.45", "last trade: 31.23", 
//			"119.24k", ".04", "$4.88 b", "5b", "$ 4,860,718,800", 
//			"4.81 bil", "4.747926253e9"};

	/**
	 * Converts the given raw string into a floating point value for the attribute
	 * @param rawValue - the string from the data
	 * @return the parse float or Float.MAX_VALUE if it cannot be parsed
	 */
	public float stringToFloat(String rawValue) {
		float returnFloat = Float.MAX_VALUE; //gets returned in case string could not be parsed correctly
		
		//to handle percent, million, billion and k
		float multiplyer = 1;
		
		String processed = rawValue.trim().toLowerCase();
		processed = processed.replace(" ", "");
		processed = processed.replace("$", "");
		processed = processed.replace(",", "");
		processed = processed.replace("usd", "");
		processed = processed.replace("(", "");
		processed = processed.replace(")", "");
		
		//find mil, m, b, k, % and set miltiplyer. also delete it from string
		if(processed.matches("(.*)([0-9]+)(mil)(.*)")) {
			//delete mil and update multiplier
			processed = processed.replaceAll("([0-9]+)(mil)", "$1"); 
			multiplyer = 1000000;
		}
		else if(processed.matches("(.*)([0-9]+)(m)(.*)")) {
			processed = processed.replaceAll("([0-9]+)(m)", "$1"); 
			multiplyer = 1000000;
		}
		else if(processed.matches("(.*)([0-9]+)(bil)(.*)")) {
			processed = processed.replaceAll("([0-9]+)(bil)", "$1"); 
			multiplyer = 1000000000;
		}
		else if(processed.matches("(.*)([0-9]+)(b)(.*)")) {
			processed = processed.replaceAll("([0-9]+)(b)", "$1"); 
			multiplyer = 1000000000;
		}
		else if(processed.matches("(.*)([0-9]+)(k)(.*)")) {
			processed = processed.replaceAll("([0-9]+)(k)", "$1"); 
			multiplyer = 1000;
		}
		else if(processed.matches("(.*)([0-9]+)(%)(.*)")) {
			processed = processed.replaceAll("([0-9]+)(%)", "$1"); 
			multiplyer = 0.01f;
		}
		
		//remove any other characters before the number
		processed = processed.replaceAll("([^\\.\\d]*)([0-9eE\\.]+)", "$2"); 
		
		//remove any other characters after the number
		processed = processed.replaceAll("([0-9eE\\.]+)(\\D*)", "$1"); 
		
		try { 
			returnFloat = Float.parseFloat(processed);
			returnFloat *= multiplyer;
	    }
	    catch (NumberFormatException nfe) { 
	        //System.err.println("Invalid input " + processed); 
	    }
		
		return returnFloat;
	}

	/**
	 * The toString method returns a formatted version of the float* 
	 * @return
	 */
    @Override
    public String toString() {
        return String.format("%g", floatValue);
    }

	// TODO: Is this ok?
	/**
	 *  The hash code combines the name and formatted string of the float together and hashes that value
	 * @return
	 */
    @Override
    public int hashCode() {
        return (this.name + this.toString()).hashCode();
    }

	/**
	 * Returns true iff the two attrs have the same name and have the same float value 
	 * @param obj - other attribute
	 * @return
	 */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof FloatAttribute) && this.name.equals(((FloatAttribute) obj).getName()) && this.floatValue == ((FloatAttribute) obj).getFloatValue();
    }

	/**
	 * Allows storage of a normalized version of the floating point number  
	 * Useful if one applies normalization to the data and wants to maintain
	 * both the original and normalized values 
	 * @param normalizedValue
	 */
	public void setNormalizedValue(float normalizedValue){
		this.normalizedValue = normalizedValue;
	}

	/**
	 * Returns the stored normalized version of the float * 
	 * @return
	 */
	public float getNormalizedValue() {
		return normalizedValue;
	}

}
