package com.cse308.server.result;

import java.util.List;
import java.util.Set;

public class Phase2Result implements Result {

    double before;
    double after;
    List<Phase1Result> districtResults;
    Set<String> changedPrecincts;

    public Phase2Result(double before, double after, List<Phase1Result> districtResults, Set<String> changedPrecincts){
        this.before = before;
        this.after = after;
        this.districtResults = districtResults;
        this.changedPrecincts = changedPrecincts;
    }

}
