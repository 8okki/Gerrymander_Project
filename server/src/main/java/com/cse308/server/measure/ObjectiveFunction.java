package com.cse308.server.measure;

import com.cse308.server.models.Cluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;


public class ObjectiveFunction {

    // List of measures to be used for this measure function
    private List<Measure> measures;
    private double totalWeight;

    // an activation function that is run, piecewise on each measure cost, before the weight is applied.
    // this is typically x-> 1-(1-x)^2, in accordance with Matt's original code.
    private Function<Double, Double> activationFunction;

    public ObjectiveFunction(List<Measure> measures, Function<Double, Double> activationFunction) {
        this.measures = measures;
        for(Measure measure : measures)
            totalWeight += measure.getWeight();
        this.activationFunction = activationFunction;
    }
    public ObjectiveFunction(List<Measure> measures) {
        this(measures, (x) -> 1 - Math.pow((1 - x), 2));
    }

    public double calculateObj(Set<Cluster> clusters){
        for(Measure measure : measures)
            measure.setScore(0);

        double obj = 0;
        for (Cluster cluster : clusters){
            double score = 0;
            for(Measure measure : measures) {
                double value  = measure.calculateMeasure(cluster);
                double weight = measure.getWeight();
                score += (activationFunction.apply(value) * weight) / totalWeight;
                measure.setScore(measure.getScore() + value);
            }
            cluster.setScore(score);
            obj += score;
        }

        for(Measure measure : measures)
            measure.setScore(measure.getScore() / clusters.size());
        obj = obj / clusters.size();

        return obj;
    }
}
