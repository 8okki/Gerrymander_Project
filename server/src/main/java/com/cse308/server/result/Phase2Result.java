package com.cse308.server.result;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Phase2Result implements Result {

    int mmBefore;
    int mmAfter;
    double objBefore;
    double objAfter;
    Map scores;
    List<DistrictInfo> districts;

    public Phase2Result(int[] MMs, double[] objs, Map scores, List<DistrictInfo> districts){
        this.mmBefore = MMs[0];
        this.mmAfter = MMs[1];
        this.objBefore = objs[0];
        this.objAfter = objs[1];
        this.scores = scores;
        this.districts = districts;
    }

}
