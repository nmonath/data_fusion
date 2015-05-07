package edu.umass.cs.data_fusion.data_structures;

public class SyntheticSource extends Source {
	
	//standard deviation for CONTINUOUS attributes
	private double sigma;
	
	//probability of alteration for CATEGORICAL attrs [0 ... 1]
	private double changeProb;

	public SyntheticSource(String name) {
		super(name);
		
		this.changeProb = 0.1;
		this.sigma = 1;
	}
	
	public SyntheticSource(String name, double changeProb, double sigma) {
		super(name);
		
		this.changeProb = changeProb;
		this.sigma = sigma;
	}

	public double getSigma() {
		return sigma;
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	public double getChangeProb() {
		return changeProb;
	}

	public void setChangeProb(double changeProb) {
		this.changeProb = changeProb;
	}

	@Override
    public String toString() {
        return "Source(name: " + this.getName()  + ", sigma: " + 
        		this.sigma + ", changeProb: " + this.changeProb + ")";
    }
}
