package com.cse308.server.algorithm;

import com.cse308.server.models.Cluster;

/**
 *
 * @author Mavericks
 */
public interface MeasureFunction {

    double calculateMeasure(Cluster cluster);

}