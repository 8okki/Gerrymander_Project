package com.cse308.server.result;

import java.util.List;

public class Phase2Result implements Result {

    double before;
    double after;
    List<Phase1Result> districtResults;

    public Phase2Result(double before, double after, List<Phase1Result> districtResults){
        this.before = before;
        this.after = after;
        this.districtResults = districtResults;
    }

}
