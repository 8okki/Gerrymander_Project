package com.cse308.server.measure;

import com.cse308.server.models.Cluster;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public class DefaultMeasure implements MeasureFunction {

    // a map that gives a function to generate the "weights" for a given measure, usually just a constant function f(Cluster) = C.
    private Map<Measure, Function<Cluster, Double>> weightFunctions;

    // an activation function that is run, piecewise on each measure cost, before the weight is applied.
    // this is typically x-> 1-(1-x)^2, in accordance with Matt's original code.
    private Function<Double,Double> activationFunction;


    // creates a DefaultMeasure object with the given set of Measure, Weight tuples and vanilla activation function.
    public static DefaultMeasure DefaultMeasureWithWeights(Map<Measure, Double> weights) {
        DefaultMeasure newMeasureSet = new DefaultMeasure(new HashMap<>());
        newMeasureSet.updateConstantWeights(weights);
        return newMeasureSet;
    }
    public void updateConstantWeights(Map<Measure,Double> weights) {
        this.weightFunctions = new HashMap<>();
        for (Measure measure : weights.keySet()) {
            this.weightFunctions.put(measure, (cluster) -> weights.get(measure));
        }
    }

    public DefaultMeasure(Map<Measure, Function<Cluster, Double>> weightFunctions, Function<Double, Double> activationFunction) {
        this.weightFunctions = weightFunctions;
        this.activationFunction = activationFunction;
    }
    public DefaultMeasure(Map<Measure, Function<Cluster, Double>> weightFunctions) {
        this(weightFunctions, (x) -> 1 - Math.pow((1 - x), 2));
    }

    @Override
    public double calculateMeasure(Cluster cluster) {
        double sum = 0;
        for (Map.Entry<Measure, Function<Cluster,Double>> entry: weightFunctions.entrySet()) {
            Measure measure = entry.getKey();
            Function<Cluster, Double> weightFunction = entry.getValue();

            double value  = measure.calculateMeasure(cluster);
            double weight = weightFunction.apply(cluster);

            sum += activationFunction.apply(value) * weight;
        }
        return sum;
    }
}
