package com.cse308.server.measure;

import com.cse308.server.models.Cluster;

import java.util.List;
import java.util.function.Function;


public class DefaultMeasure implements MeasureFunction {

    // List of measures to be used for this measure function
    private List<Measure> measures;

    // an activation function that is run, piecewise on each measure cost, before the weight is applied.
    // this is typically x-> 1-(1-x)^2, in accordance with Matt's original code.
    private Function<Double, Double> activationFunction;

    public DefaultMeasure(List<Measure> measures, Function<Double, Double> activationFunction) {
        this.measures = measures;
        this.activationFunction = activationFunction;
    }
    public DefaultMeasure(List<Measure> measures) {
        this(measures, (x) -> 1 - Math.pow((1 - x), 2));
    }

    @Override
    public double calculateMeasure(Cluster cluster) {
        double sum = 0;
        for (Measure measure : measures) {
            double value  = measure.calculateMeasure(cluster);
            double weight = measure.weight;
            sum += activationFunction.apply(value) * weight;
        }
        return sum;
    }
}
