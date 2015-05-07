package main.java.edu.umass.cs.data_fusion.util.math;


import main.java.edu.umass.cs.data_fusion.data_structures.*;
import main.java.edu.umass.cs.data_fusion.util.Functions;

import java.util.*;

public class InfoTheory {
    
    
    public static Map<Entity,Map<String,Float>> getEntropies(RecordCollection collection) {
        Map<Entity,Map<String,Float>> entropies = new HashMap<Entity, Map<String, Float>>();
        Set<String> attrNames = collection.getAttributes();
        for (Entity e : collection.getEntities()) {
            entropies.put(e, new HashMap<String, Float>());
            for (String attrName : attrNames) {
                
                List<Attribute> attributes = collection.allValuesForAttribute(e,attrName);
                if (attributes.size() > 0) {
                    if (attributes.get(0).getDataType() == AttributeDataType.FLOAT && attributes.get(0).getType() == AttributeType.CONTINUOUS) {
                        List<Float> floats = new ArrayList<Float>();
                        for (Attribute a : attributes) {
                            floats.add(( (FloatAttribute) a).getFloatValue());
                        }
                        entropies.get(e).put(attrName,mSpacingsEntropy(floats));
                    } else {
                        entropies.get(e).put(attrName,discreteEntropy(attributes));
                    }
                } else {
                    entropies.get(e).put(attrName,0.0f);
                }
            }
        }
        return entropies;
    }
    
    public static float discreteEntropy(List<Attribute> attributes) {
        Map<Attribute,Float> probabilities = new HashMap<Attribute, Float>();
        for (Attribute a : attributes) {
            if (!probabilities.containsKey(a))
                probabilities.put(a, 0.0f);
            probabilities.put(a, probabilities.get(a) + 1.0f);
        }
        float entropy = 0.0f;
        for (Attribute a : probabilities.keySet()) {
            float p = probabilities.get(a)/attributes.size();
            entropy += log2(p) * p;
        }
        return -entropy;
    }
    
    public static float mSpacingsEntropy(List<Float> input) {
        
        if (input.size() == 1 || input.size() == 2 )
            return 0.0f;

        List<Float> xs = addSmallNoise(input);
        
        
        int N = xs.size();
        int m = 1;//(int) Math.round(Math.sqrt(N));
        
        //ent = (1/(n-m))*sum(log2(((n+1)/m)*(xs(m+1:end) - xs(1:end-m))));
        Collections.sort(xs);
        float entropy = 0.0f;
        
        ArrayList<Float> tmp = new ArrayList<Float>(N-m);
        
        float nPlus1OverM = (N+1)/m;
        
        for (int i = 0; i < N-m; i++) {
            entropy +=  log2(Math.max(0.1f, nPlus1OverM * (xs.get(i + m) - xs.get(i))));
        }
        
        entropy = entropy * (1.0f / (N - m ));

        return entropy;
    }
    
    
    public static List<Float> addSmallNoise(List<Float> nums) {
        List<Float> noise = new ArrayList<Float>(nums.size());
        
        Random r = new Random(999);
        for (Float f : nums) {
            noise.add(f);
            //noise.add(f + (float) r.nextGaussian()*0.01f);
        }
        return noise;
    }
    
    public static float log2(float x) {
        return (float) (Math.log(x) / Math.log(2));
    }
    
    public static List<Float> log2(List<Float> vec) {
        List<Float> vec2 = new ArrayList<Float>(vec.size());
        for (Float f : vec) {
            vec2.add(log2(f));
        }
        return vec2;
    }
    
    public static float samplingEntropyEstimate(List<Float> samples, int numSamplesToUse, float sigma) {
        List<Float> samples2 = sampleNPDE(samples, sigma, numSamplesToUse);
        List<Float> p = denseEst(samples, samples2, sigma);
        float entropy = -1 * Functions.mean(log2(p));
        return entropy;
    }
    
    private static float sqrt2pi =  (float) Math.sqrt(2*Math.PI);
    
    public static float exp(float x, float mu, float sigma) {
        return (float) ((1/(sqrt2pi * sigma)) * Math.exp(-Math.pow((x - mu),2)/ (2 * sigma * sigma)));
    }
    
    public static List<Float> denseEst(List<Float> xs, List<Float> x, float sigma) {
        
        List<Float> p = new ArrayList<Float>(xs.size());
        for (Float f : xs) {
            float value = 0.0f;
            for (Float ff : x) {
                value += exp(f,ff,sigma);
            }
            p.add(value/x.size());
        }
        return p;
    }
    
    public static List<Float> sampleNPDE(List<Float> samples, float sigma, int numSamples) {
        Random r = new Random();
        List<Integer> dataPointsToSample = new ArrayList<Integer>(numSamples);
        List<Float> gaussianSamples = new ArrayList<Float>(numSamples);
        for (int i = 0; i < numSamples; i++) {
            dataPointsToSample.add(r.nextInt(samples.size()));
            gaussianSamples.add((float) r.nextGaussian());
        }
        List<Float> samples2 = new ArrayList<Float>(numSamples);
        for (int i = 0; i < numSamples; i++) {
            samples2.add( samples.get(dataPointsToSample.get(i)) + sigma * gaussianSamples.get(i));
        }
        return samples2;
            

    }

    
    public static void main(String[] args) {
        
        List<Float> data = new ArrayList<Float>();
        data.add(10.0f);
        data.add(100.0f);
        data.add(10.0f);
        data.add(24.0f);
        data.add(54.0f);
        data.add(66.0f);
        data.add(22.0f);
        data.add(10.0f);
        data.add(10.0f);
        data.add(10.0f);
        data.add(21.0f);
        data.add(100.0f);
        data.add(14.0f);
        data.add(12.0f);
        data.add(16.0f);
        System.out.println(mSpacingsEntropy(data));
    }
    
}
