/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.service;

import com.cse308.server.algorithm.Algorithm;
import com.cse308.server.measure.Measure;
import com.cse308.server.models.State;
import com.cse308.server.enums.Demographic;
import com.cse308.server.enums.StateName;
import com.cse308.server.result.DistrictInfo;
import com.cse308.server.result.Phase1Result;
import com.cse308.server.result.Phase2Result;
import com.cse308.server.result.VoteBlocResult;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mavericks
 */

@Service
public class AlgorithmService {
    @Autowired
    Algorithm algo;
    
    public State initState(StateName stateName){
        return this.algo.initState(stateName);
    }

    public void initGeometry() { this.algo.initGeometry(); }

    public void initNeighbors() {
        this.algo.initNeighbors();
    }

    public List<VoteBlocResult> runPhase0(float blocThreshold, float voteThreshold){
        return this.algo.runPhase0(blocThreshold, voteThreshold);
    }
    
    public List<DistrictInfo> runPhase1(float demographicMinimum, float demographicMaximum, List<Demographic> demographics, int targetDistrictNum){
        return this.algo.runPhase1(demographicMinimum, demographicMaximum, demographics, targetDistrictNum);
    }

    public List<DistrictInfo> runPhase1Incremental(float demographicMinimum, float demographicMaximum, List<Demographic> demographics, int targetDistrictNum){
       return this.algo.runPhase1Incremental(demographicMinimum, demographicMaximum, demographics, targetDistrictNum);
    }
    
    public boolean isPhase1Done(){
        return this.algo.isPhase1Done();
    }
    
    public Phase2Result runPhase2(List<Measure> measures) {
        return this.algo.runPhase2(measures);
    }
}
