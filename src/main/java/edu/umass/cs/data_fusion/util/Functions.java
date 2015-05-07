package edu.umass.cs.data_fusion.util;


import java.util.*;

public class Functions {

    public static double dot(double[] x, double[] y) {
        assert x.length == y.length;
        double res = 0.0;
        for (int i = 0; i < x.length; i++)
            res += x[i] * y[i];
        return res;
    }

    public static double L1Norm(double[] x) {
        double res = 0.0;
        for (int i = 0; i < x.length; i++)
            res += Math.abs(x[i]);
        return res;
    }

    public static double L2Norm(double[] x) {
        double res = 0.0;
        for (int i = 0; i < x.length; i++)
            res += Math.pow(x[i], 2);
        return Math.sqrt(res);
    }

    public static double cosine(double[] x, double[] y) {
        assert x.length == y.length;
        return dot(x, y) / (L2Norm(x) * L2Norm(y));
    }

    public static double L1dist(double[] x, double[] y) {
        assert x.length == y.length;
        double dist = 0.0;
        for (int i = 0; i < x.length; i++) {
            dist += Math.abs(x[i] - y[i]);
        }
        return dist;
    }
    
    public static double L2dist(double[] x, double[] y) {
        assert x.length == y.length;
        double dist = 0.0;
        for (int i = 0; i < x.length; i++) {
            dist += Math.pow(x[i]-y[i],2);
        }
        dist = Math.sqrt(dist);
        return dist;
    }

    /**
     * Based on Algorithm presented in: http://people.cs.clemson.edu/~bcdean/dp_practice/dp_8.swf
     * Computes the editDistance between the two strings. The minimum number of characters to delete, insert or swap
     * to convert one string into the other.
     *
     * @param one
     * @param two
     * @return
     */
    public static int editDistance(String one, String two) {

        int[][] dist = new int[2][two.length() + 1];
        for (int j = 0; j <= two.length(); j++) {
            dist[0][j] = j;
        }

        int replacementCost = 0;
        for (int i = 1; i <= one.length(); i++) {
            dist[1][0] = i;
            for (int j = 1; j <= two.length(); j++) {
                replacementCost = 0;
                if (one.charAt(i - 1) != two.charAt(j - 1)) {
                    replacementCost = 1;
                }
                //                             Deletion Cost    Insertion Cost      Replacement/Use Symbol Cost
                dist[1][j] = Math.min(Math.min(dist[0][j] + 1, dist[1][j - 1] + 1), dist[0][j - 1] + replacementCost);
            }
            System.arraycopy(dist[1], 0, dist[0], 0, dist[0].length);
        }
        return dist[1][two.length()];
    }
    
    //TODO: Implement the linear time algorithm w/o sorting
    public static float getMedian(List<Float> floats) {
        Collections.sort(floats);
        // If odd, take the middle element
        // otherwise take the average of the two middle elements. Is this right?
        int len = floats.size();
        if (len % 2 == 1) {
            return floats.get( (int) Math.ceil(len/2) );
        } else {
            return (floats.get( (int) Math.ceil(len/2) - 1 ) + floats.get( (int) Math.ceil(len/2) ))/2.0f;
        }
    }
    
    public static float mean(List<Float> floats) {
        float mu = 0.0f;
        for (float f: floats) {
            mu += f;
        }
        return mu/floats.size();
    }
    
    public static float variance(List<Float> floats, float mu) {
        float sigma_sqd = 0.0f;
        for (float f: floats) {
            sigma_sqd += Math.pow(f - mu, 2);
        }
        return sigma_sqd/floats.size();
    }

    public static float variance(List<Float> floats) {
        return variance(floats,mean(floats));
    }
    
    public static float minFloats(List<Float> floats) {
        float min = Float.MAX_VALUE;
        for (Float f: floats) {
            if (f < min)
                min =f;
        }
        return min;
    }
    
    public static double min(List<Double> doubles) {
        double min = Double.MAX_VALUE;
        for (Double f: doubles) {
            if (f < min)
                min =f;
        }
        return min;
    }

    public static float maxFloats(List<Float> floats) {
        float max = Float.MIN_VALUE;
        for (Float f: floats) {
            if (f > max)
                max =f;
        }
        return max;
    }
    
    public static double max(List<Double> doubles) {
        double max = Double.MIN_VALUE;
        for (Double f: doubles) {
            if (f > max)
                max =f;
        }
        return max;
    }
    
    public static float sum(List<Float> floats) {
        float sum = 0.0f;
        for (Float f: floats) {
            sum += f;
        }
        return sum;
    }
    public static float sum(float[] floats, int start, int end) {
        float sum = 0.0f;
        for (int i = start; i < end; i++) {
            sum += floats[i];
        }
        return sum;
    }
    
    public static float weightedMedian(List<Float> weights, final List<Float> values) {
        
        List<Integer> idx = new ArrayList<Integer>(weights.size());
        for (int i = 0 ; i < weights.size(); i++) {
            idx.add(i);
        }
        
        float sum = sum(weights);
        Collections.sort(idx, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (values.get(o1).equals(values.get(o2)))
                    return 0;
                else 
                    return (values.get(o1).compareTo(values.get(o2)));
            }
        });

        float[] sorted = new float[values.size()];
        for (int i = 0; i < values.size();i++) {
            sorted[i] = values.get(idx.get(i));
        }
        
        float[] normalized = new float[values.size()];
        for (int i = 0; i < values.size();i++) {
            normalized[i] = weights.get(idx.get(i))/sum;
        }
        
        
        for (int i = 0; i < normalized.length; i++) {
            float sumTo_i = sum(normalized, 0, i + 1);
            if (sumTo_i >= 0.5) {
                return sorted[i];
            }
            
        }
        System.out.println("[weightedMedian] Something went wrong, returning NaN.");
        return Float.NaN;
    }
    
    public static <T> Set<T> setMinus(Set<T> one, Set<T> two) {
        Set<T> oneCopy = new HashSet<T>();
        oneCopy.addAll(one);
        oneCopy.removeAll(two);
        return oneCopy;
    }

    
    public static double logSumExp(List<Double> doubles) {
        
        if (doubles.size() == 0)
            return Double.MIN_VALUE;
        
        if (doubles.size() == 1)
            return doubles.get(0);
        
        double max = max(doubles);
        double min = min(doubles);
        double c = (max > Math.abs(min)) ? max : min;
        double res = 0.0;
        for (Double d : doubles) {
            res += Math.exp(d - c);
        }
        return c +  Math.log(res);
    }
    
    public static void main(String[] args) {
        List<Float> weights = new ArrayList<Float>();
        weights.add(0.9f);
        weights.add(0.4f);
        weights.add(0.3f);
        weights.add(0.8f);
        weights.add(0.3f);
        weights.add(0.1f);
        weights.add(0.8f);
        List<Float> values = new ArrayList<Float>();
        values.add(100.0f);
        values.add(1.0f);
        values.add(122.0f);
        values.add(133.0f);
        values.add(12f);
        values.add(44f);
        values.add(333f);
        System.out.println("wm " + weightedMedian(weights, values));
    }
    
}
