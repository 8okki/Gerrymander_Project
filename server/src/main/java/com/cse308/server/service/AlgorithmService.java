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
import com.cse308.server.result.VoteBlocResult;
import java.util.List;

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

    public List<VoteBlocResult> runPhase0(float blocThreshold, float voteThreshold){
        return this.algo.runPhase0(blocThreshold, voteThreshold);
    }
    
    public void runPhase1(List<Demographic> demographics, float demographicMinimum, float demographicMaximum, int targetDistrictNum){
        this.algo.runPhase1(demographics, demographicMinimum, demographicMaximum, targetDistrictNum);
    }

    public void runPhase2(List<Measure> measures) {
        this.algo.runPhase2(measures);
    }
}
