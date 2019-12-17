package com.cse308.server.result;

import java.util.List;
import java.util.Set;

public class Phase2Result implements Result {

    int mmBefore;
    int mmAfter;
    double scoreBefore;
    double scoreAfter;
    List<Phase1Result> districtResults;
    Set<String> changedPrecincts;

    public Phase2Result(double[] results, List<Phase1Result> districtResults, Set<String> changedPrecincts){
        this.mmBefore = (int) results[0];
        this.mmAfter = (int) results[0];
        this.scoreBefore = results[2];
        this.scoreAfter = results[3];
        this.districtResults = districtResults;
        this.changedPrecincts = changedPrecincts;
    }

}
