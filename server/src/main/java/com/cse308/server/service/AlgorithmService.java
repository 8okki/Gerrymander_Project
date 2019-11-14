/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse308.server.service;

import com.cse308.server.gerrymander.Algorithm;
import com.cse308.server.gerrymander.State;
import com.cse308.server.gerrymander.enums.StateName;
import com.cse308.server.hibernate.dao.StateDao;
import org.springframework.stereotype.Service;

/**
 *
 * @author Jakob
 */

@Service
public class AlgorithmService {
    Algorithm algo = new Algorithm();
    
    public State initState(StateName stateName){
        return this.algo.initState(stateName);
    }
    
    public void runPhase0(float blocThreshold, float voteThreshold){
        this.algo.runPhase0(blocThreshold, voteThreshold);
    }
}
