/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.service;

import com.cse308.server.gerrymander.Algorithm;
import com.cse308.server.gerrymander.State;
import com.cse308.server.gerrymander.enums.Demographic;
import com.cse308.server.gerrymander.enums.StateName;
import com.cse308.server.gerrymander.result.VoteBlocResult;
import com.cse308.server.hibernate.dao.StateDao;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mavericks
 */

@Service
public class AlgorithmService {
    Algorithm algo = new Algorithm();
    
    public State initState(StateName stateName){
        return this.algo.initState(stateName);
    }
    
    public List<VoteBlocResult> runPhase0(float blocThreshold, float voteThreshold){
        return this.algo.runPhase0(blocThreshold, voteThreshold);
    }
    
    public void runPhase1(List<Demographic> demographics, float demographicMinimum, float demographicMaximum){
        this.algo.runPhase1(demographics, demographicMinimum, demographicMaximum);
    }
}
